package com.BackNight.backendNIght.ws.mercadopago.rest;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoPreferenceResponse;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoConfirmationRequest;
import com.BackNight.backendNIght.ws.mercadopago.service.MercadoPagoService;
import com.BackNight.backendNIght.ws.entity.Reserva;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/servicio")
public class MercadoPagoController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoController.class);

    private final MercadoPagoService mercadoPagoService;

    public MercadoPagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/create-mercadopago-preference")
    public ResponseEntity<?> createPreference(@RequestBody MercadoPagoCreatePreferenceRequest orderRequest) {
        log.info("Recibida solicitud POST en /servicio/create-mercadopago-preference desde el frontend.");
        log.debug("Cuerpo de la solicitud recibido: {}", orderRequest.toString());

        try {
            String checkoutUrl = mercadoPagoService.createPaymentPreference(orderRequest);
            log.info("Preferencia de pago creada exitosamente. URL de checkout: {}", checkoutUrl);
            return new ResponseEntity<>(new MercadoPagoPreferenceResponse(checkoutUrl), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear preferencia de pago: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (MPApiException e) {
            log.error("Error de la API de Mercado Pago al crear preferencia. Contenido de la respuesta: {}", e.getApiResponse().getContent(), e);
            return new ResponseEntity<>("Error al comunicarse con Mercado Pago: " + e.getApiResponse().getContent(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (MPException e) {
            log.error("Error del SDK de Mercado Pago al crear preferencia: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno al procesar el pago: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error("Error inesperado al crear preferencia de Mercado Pago: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor al procesar el pago.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/confirmar-reserva")
    public ResponseEntity<?> confirmReservation(@RequestBody MercadoPagoConfirmationRequest confirmationRequest) {
        log.info("Recibida solicitud POST en /servicio/confirmar-reserva para confirmar pago.");
        log.debug("Cuerpo de la solicitud recibido: {}", confirmationRequest.toString());

        try {
            Reserva reservaConfirmada = mercadoPagoService.confirmPaymentAndReservation(confirmationRequest);
            log.info("Reserva con ID {} confirmada exitosamente. Estado: {}", reservaConfirmada.getIdReserva(), reservaConfirmada.getEstadoPago());
            return new ResponseEntity<>(reservaConfirmada, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Error de validación al confirmar reserva: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("Error inesperado al confirmar reserva: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor al confirmar la reserva.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}