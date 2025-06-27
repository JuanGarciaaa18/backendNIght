package com.BackNight.backendNIght.ws.mercadopago.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceRequestAutoReturn; // <<-- ¡Esta es la importación clave!
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;
// import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoItemRequest; // No es necesaria aquí

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String mercadoPagoAccessToken;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl; // e.g., http://localhost:3000

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        // Inicializa el SDK de Mercado Pago una sola vez
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String createPaymentPreference(MercadoPagoCreatePreferenceRequest orderRequest) throws MPException, MPApiException {
        System.out.println("DEBUG MercadoPagoService: frontendBaseUrl cargado como: '" + frontendBaseUrl + "'");

        // Validación básica (puedes agregar más validaciones aquí)
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
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(frontendBaseUrl.trim() + "/pago-exitoso") // Crea estas rutas en tu frontend
                .pending(frontendBaseUrl.trim() + "/pago-pendiente")
                .failure(frontendBaseUrl.trim() + "/pago-fallido")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(itemsMp)
                .backUrls(backUrls)
                .autoReturn(PreferenceRequestAutoReturn.NONE) // <<-- ¡Este es el cambio principal!
                // Puedes añadir más configuraciones aquí, como la información del pagador, etc.
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint(); // Retorna la URL de checkout
    }
}