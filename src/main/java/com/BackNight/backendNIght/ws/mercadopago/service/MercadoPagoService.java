package com.BackNight.backendNIght.ws.mercadopago.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoCreatePreferenceRequest;
import com.BackNight.backendNIght.ws.mercadopago.dto.MercadoPagoConfirmationRequest;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ReservaRepository;
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode; // New import
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;
    private final ClientesRepository clienteRepository;

    private static final Map<String, String> ZONA_ID_MAPPING = new HashMap<>();

    static {
        ZONA_ID_MAPPING.put("general", "1");
        // Add more mappings here if you have other zones (e.g., ZONA_ID_MAPPING.put("vip", "2");)
    }

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken,
                              ReservaRepository reservaRepository,
                              EventoRepository eventoRepository,
                              ClientesRepository clienteRepository) {
        MercadoPagoConfig.setAccessToken(accessToken);
        this.reservaRepository = reservaRepository;
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public String createPaymentPreference(MercadoPagoCreatePreferenceRequest orderRequest) throws MPException, MPApiException {
        log.debug("frontendBaseUrl loaded as: '{}'", frontendBaseUrl);
        log.debug("Preference request data received: {}", orderRequest);

        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("The cart cannot be empty.");
        }
        if (orderRequest.getReservationDetails() == null) {
            throw new IllegalArgumentException("Reservation details cannot be null.");
        }

        Reserva preReserva = new Reserva();

        Integer eventId = orderRequest.getReservationDetails().getEventId();
        Optional<Evento> optionalEvento = eventoRepository.findById(eventId);
        if (optionalEvento.isEmpty()) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }
        preReserva.setEvento(optionalEvento.get());

        Integer userId = orderRequest.getReservationDetails().getUserId();
        if (userId != null) {
            Optional<Clientes> optionalCliente = clienteRepository.findById(userId);
            if (optionalCliente.isEmpty()) {
                log.warn("Client not found with ID: {}. Reservation will be created without associated client.", userId);
                preReserva.setCliente(null);
            } else {
                preReserva.setCliente(optionalCliente.get());
            }
        } else {
            log.warn("Null user ID. Reservation will be created without associated client.");
            preReserva.setCliente(null);
        }

        int totalTickets = orderRequest.getReservationDetails().getTickets().stream()
                .mapToInt(t -> t.getQuantity())
                .sum();
        preReserva.setCantidadTickets(totalTickets);

        preReserva.setMontoTotal(orderRequest.getReservationDetails().getTotalAmount());

        preReserva.setFechaReserva(LocalDate.now());

        preReserva.setEstado("Pendiente");
        preReserva.setEstadoPago("Pendiente");
        preReserva.setIdTransaccion(null);

        preReserva = reservaRepository.save(preReserva);
        log.info("Pre-reservation created with ID: {}", preReserva.getIdReserva());

        List<PreferenceItemRequest> itemsMp = orderRequest.getItems().stream().map(item -> {
            String itemIdToUse = item.getId();
            if (ZONA_ID_MAPPING.containsKey(item.getId().toLowerCase())) {
                itemIdToUse = ZONA_ID_MAPPING.get(item.getId().toLowerCase());
                log.debug("Mapping item ID '{}' to numeric ID '{}' for Mercado Pago.", item.getId(), itemIdToUse);
            } else {
                log.warn("Item ID '{}' is not numeric and not in zone mapping.", item.getId());
            }

            // *** START OF KEY CHANGE ***
            // Ensure unitPrice is not null and has a defined scale and rounding mode
            BigDecimal finalUnitPrice = item.getUnitPrice();
            if (finalUnitPrice == null) {
                log.error("Item unitPrice is null for item ID: {}. Setting to BigDecimal.ZERO.", itemIdToUse);
                finalUnitPrice = BigDecimal.ZERO; // Default to zero if null to avoid NPE
            }
            // Set scale to 2 (for cents/decimal places) and use HALF_EVEN rounding
            finalUnitPrice = finalUnitPrice.setScale(2, RoundingMode.HALF_EVEN);
            // *** END OF KEY CHANGE ***

            log.debug("Processing item for MP: ID={}, Title={}, Quantity={}, UnitPrice={}",
                    itemIdToUse, item.getTitle(), item.getQuantity(), finalUnitPrice); // Log the finalUnitPrice

            return PreferenceItemRequest.builder()
                    .id(itemIdToUse)
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .pictureUrl(item.getPictureUrl())
                    .quantity(item.getQuantity())
                    .currencyId(item.getCurrencyId())
                    .unitPrice(finalUnitPrice) // Use the potentially adjusted unitPrice
                    .build();
        }).collect(Collectors.toList());

        String successUrl = frontendBaseUrl.trim() + "/pago-exitoso";
        String pendingUrl = frontendBaseUrl.trim() + "/pago-pendiente";
        String failureUrl = frontendBaseUrl.trim() + "/pago-fallido";

        log.debug("Success URL for Mercado Pago: '{}'", successUrl);
        log.debug("Pending URL for Mercado Pago: '{}'", pendingUrl);
        log.debug("Failure URL for Mercado Pago: '{}'", failureUrl);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .pending(pendingUrl)
                .failure(failureUrl)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(itemsMp)
                .backUrls(backUrls)
                .externalReference(String.valueOf(preReserva.getIdReserva()))
                .notificationUrl("https://backnight-production.up.railway.app/servicio/mercadopago/webhook")
                .statementDescriptor("NightPlus")
                .binaryMode(false)
                .expires(false)
                .paymentMethods(PreferencePaymentMethodsRequest.builder()
                        .installments(1)
                        .build())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        log.info("Payment preference created with ID: {} and InitPoint: {}. External Reference: {}",
                preference.getId(), preference.getInitPoint(), preference.getExternalReference());

        preReserva.setPreferenceId(preference.getId());
        reservaRepository.save(preReserva);

        return preference.getInitPoint();
    }

    @Transactional
    public Reserva confirmPaymentAndReservation(MercadoPagoConfirmationRequest confirmationRequest) {
        log.info("Payment confirmation received: {}", confirmationRequest);

        Optional<Reserva> optionalReserva = reservaRepository.findByPreferenceId(confirmationRequest.getPreferenceId());

        if (optionalReserva.isEmpty()) {
            log.warn("Pending reservation not found for preferenceId: {}. Cannot confirm.", confirmationRequest.getPreferenceId());
            throw new IllegalArgumentException("Pending reservation not found for payment preference.");
        }

        Reserva reserva = optionalReserva.get();

        if ("approved".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pagado");
            reserva.setEstado("Confirmada");
            reserva.setIdTransaccion(confirmationRequest.getCollectionId());
            log.info("Reservation {} (MP ID: {}) updated to 'Paid' and 'Confirmed' status.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else if ("pending".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pendiente");
            reserva.setEstado("Pendiente");
            reserva.setIdTransaccion(null);
            log.info("Reservation {} (MP ID: {}) updated to 'Pending' status.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else {
            reserva.setEstadoPago("Rechazado");
            reserva.setEstado("Cancelada");
            reserva.setIdTransaccion(null);
            log.warn("Reservation {} (MP ID: {}) updated to 'Rejected' and 'Cancelled' status.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        }

        return reservaRepository.save(reserva);
    }
}