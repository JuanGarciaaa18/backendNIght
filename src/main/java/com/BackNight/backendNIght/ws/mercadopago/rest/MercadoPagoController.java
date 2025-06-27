package com.BackNight.backendNIght.ws.mercadopago.rest;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoPreferenceResponse;
import com.BackNight.backendNIght.ws.mercadopago.service.MercadoPagoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService; // Declared as final

    // This is the ONLY constructor, ensuring 'mercadoPagoService' is always initialized
    public MercadoPagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/create-mercadopago-preference")
    public ResponseEntity<?> createPreference(@RequestBody MercadoPagoCreatePreferenceRequest orderRequest) {
        // Your existing code
        try {
            // Corrected line: Use 'mercadoPagoService' (with capital 'P')
            String checkoutUrl = mercadoPagoService.createPaymentPreference(orderRequest);
            return new ResponseEntity<>(new MercadoPagoPreferenceResponse(checkoutUrl), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MPApiException e) {
            System.err.println("Mercado Pago API Error Response: " + e.getApiResponse().getContent());
            return new ResponseEntity<>("Error al comunicarse con Mercado Pago: " + e.getApiResponse().getContent(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MPException e) {
            System.err.println("Mercado Pago SDK Error: " + e.getMessage());
            return new ResponseEntity<>("Error interno al procesar el pago: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Error inesperado al crear preferencia de Mercado Pago: " + e.getMessage());
            return new ResponseEntity<>("Error interno del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}