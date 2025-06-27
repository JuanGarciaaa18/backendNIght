package com.BackNight.backendNIght.ws.mercadopago.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
// ¡¡¡IMPORTANTE!!! Si usas versiones muy nuevas del SDK de MP,
// PreferenceRequestAutoReturn puede no existir o no ser necesaria.
// La hemos eliminado en este código, basándonos en los errores previos.
// import com.mercadopago.client.preference.PreferenceRequestAutoReturn;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger; // Importar el logger
import org.slf4j.LoggerFactory; // Importar el LoggerFactory

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    // Añadir un logger para depuración
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${mercadopago.access.token}")
    private String mercadoPagoAccessToken;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl; // e.g., http://localhost:3000 o https://tudominio.com

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        // Inicializa el SDK de Mercado Pago una sola vez
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String createPaymentPreference(MercadoPagoCreatePreferenceRequest orderRequest) throws MPException, MPApiException {
        // Usamos el logger en lugar de System.out.println para una mejor gestión en producción
        log.debug("frontendBaseUrl cargado como: '{}'", frontendBaseUrl);

        // Validación básica
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío.");
        }

        List<PreferenceItemRequest> itemsMp = orderRequest.getItems().stream().map(item ->
                PreferenceItemRequest.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .pictureUrl(item.getPictureUrl())
                        .quantity(item.getQuantity())
                        .currencyId(item.getCurrencyId())
                        .unitPrice(new BigDecimal(item.getUnitPrice()))
                        .build()
        ).collect(Collectors.toList());

        // Define las URLs de retorno para Mercado Pago
        // ¡ES CRÍTICO QUE frontendBaseUrl TENGA UN VALOR VÁLIDO Y ACCESIBLE!
        // Ejemplo: "http://localhost:3000" para desarrollo, o "https://tudominio.com" para producción
        String successUrl = frontendBaseUrl.trim() + "/pago-exitoso";
        String pendingUrl = frontendBaseUrl.trim() + "/pago-pendiente";
        String failureUrl = frontendBaseUrl.trim() + "/pago-fallido";

        log.debug("URL de éxito para Mercado Pago: '{}'", successUrl);
        log.debug("URL de pendiente para Mercado Pago: '{}'", pendingUrl);
        log.debug("URL de fallo para Mercado Pago: '{}'", failureUrl);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .pending(pendingUrl)
                .failure(failureUrl)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(itemsMp)
                .backUrls(backUrls)
                // ¡¡¡IMPORTANTE!!! Se elimina la línea .autoReturn(...)
                // El error indicaba que "back_url.success must be defined" a pesar de autoReturn.
                // Esto sugiere que autoReturn está entrando en conflicto o es redundante
                // cuando backUrls ya está completamente definido.
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint(); // Retorna la URL de checkout de Mercado Pago
    }
}