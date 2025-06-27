package com.BackNight.backendNIght.ws.mercadopago.rest;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoPreferenceResponse;
import com.BackNight.backendNIght.ws.mercadopago.service.MercadoPagoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // Importar el Logger
import org.slf4j.LoggerFactory; // Importar el LoggerFactory


@RestController
@RequestMapping("/servicio") // <-- ¡Ruta base que coincide con tu frontend!
@CrossOrigin(origins = "http://localhost:5173") // <-- ¡Asegúrate que este puerto sea el de tu frontend!
public class MercadoPagoController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoController.class); // Logger para el controlador

    private final MercadoPagoService mercadoPagoService;

    // Constructor para inyección de dependencias
    public MercadoPagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/create-mercadopago-preference") // <-- ¡Endpoint específico que coincide con tu frontend!
    public ResponseEntity<?> createPreference(@RequestBody MercadoPagoCreatePreferenceRequest orderRequest) {
        // Log para confirmar que la solicitud llega a este controlador
        log.info("Recibida solicitud POST en /servicio/create-mercadopago-preference desde el frontend.");

        try {
            // Llama al servicio para crear la preferencia en Mercado Pago
            String checkoutUrl = mercadoPagoService.createPaymentPreference(orderRequest);
            log.info("Preferencia de pago creada exitosamente. URL de checkout: {}", checkoutUrl);

            // Retorna la URL de checkout al frontend
            return new ResponseEntity<>(new MercadoPagoPreferenceResponse(checkoutUrl), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // Manejo de errores de validación (ej. carrito vacío)
            log.error("Error de validación al crear preferencia de pago: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (MPApiException e) {
            // Manejo de errores específicos de la API de Mercado Pago
            log.error("Error de la API de Mercado Pago al crear preferencia. Contenido de la respuesta: {}", e.getApiResponse().getContent(), e);
            return new ResponseEntity<>("Error al comunicarse con Mercado Pago: " + e.getApiResponse().getContent(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (MPException e) {
            // Manejo de errores generales del SDK de Mercado Pago
            log.error("Error del SDK de Mercado Pago al crear preferencia: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno al procesar el pago: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            // Manejo de cualquier otro error inesperado
            log.error("Error inesperado al crear preferencia de Mercado Pago: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor al procesar el pago.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}