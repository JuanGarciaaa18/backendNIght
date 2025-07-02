    package com.BackNight.backendNIght.ws.mercadopago.service;

    import com.mercadopago.MercadoPagoConfig;
    import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
    import com.mercadopago.client.preference.PreferenceClient;
    import com.mercadopago.client.preference.PreferenceItemRequest;
    import com.mercadopago.client.preference.PreferenceRequest;
    import com.mercadopago.exceptions.MPApiException;
    import com.mercadopago.exceptions.MPException;
    import com.mercadopago.resources.preference.Preference;
    import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.math.BigDecimal;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    public class MercadoPagoService {

        private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

        @Value("${mercadopago.access.token}")
        private String mercadoPagoAccessToken;

        @Value("${app.frontend.url}")
        private String frontendBaseUrl;

        public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
            MercadoPagoConfig.setAccessToken(accessToken);
        }

        public String createPaymentPreference(MercadoPagoCreatePreferenceRequest orderRequest) throws MPException, MPApiException {
            log.debug("frontendBaseUrl cargado como: '{}'", frontendBaseUrl);
            log.debug("Datos de la solicitud de preferencia recibidos: {}", orderRequest); // Log de la solicitud completa

            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                throw new IllegalArgumentException("El carrito no puede estar vacío.");
            }

            List<PreferenceItemRequest> itemsMp = orderRequest.getItems().stream().map(item -> {
                log.debug("Procesando item: ID={}, Title={}, Quantity={}, UnitPrice={}",
                        item.getId(), item.getTitle(), item.getQuantity(), item.getUnitPrice());
                return PreferenceItemRequest.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .pictureUrl(item.getPictureUrl())
                        .quantity(item.getQuantity())
                        .currencyId(item.getCurrencyId())
                        .unitPrice(new BigDecimal(item.getUnitPrice()))
                        .build();
            }).collect(Collectors.toList());

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
                    // Aquí podrías añadir metadata si necesitas pasar el eventId a Mercado Pago
                    // por ejemplo: .metadata(Map.of("eventId", orderRequest.getEventId()))
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            log.info("Preferencia de pago creada con ID: {} e InitPoint: {}", preference.getId(), preference.getInitPoint());
            return preference.getInitPoint();
        }
    }