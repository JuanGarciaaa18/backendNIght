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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap; // Nuevo import
import java.util.Map;   // Nuevo import

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;
    private final ClientesRepository clienteRepository;

    // Mapa para convertir IDs de zona de string a numérico (si es necesario)
    private static final Map<String, String> ZONA_ID_MAPPING = new HashMap<>();

    static {
        // Aquí defines el mapeo de tus IDs de zona.
        // Por ejemplo, "general" -> "1", "vip" -> "2", etc.
        // Ajusta estos valores según los IDs numéricos que tengas para tus zonas.
        ZONA_ID_MAPPING.put("general", "1");
        // Agrega más mapeos si tienes otras zonas (ej. ZONA_ID_MAPPING.put("vip", "2");)
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
        log.debug("frontendBaseUrl cargado como: '{}'", frontendBaseUrl);
        log.debug("Datos de la solicitud de preferencia recibidos: {}", orderRequest);

        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío.");
        }
        if (orderRequest.getReservationDetails() == null) {
            throw new IllegalArgumentException("Los detalles de la reserva no pueden ser nulos.");
        }

        Reserva preReserva = new Reserva();

        Integer eventId = orderRequest.getReservationDetails().getEventId();
        Optional<Evento> optionalEvento = eventoRepository.findById(eventId);
        if (optionalEvento.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventId);
        }
        preReserva.setEvento(optionalEvento.get());

        Integer userId = orderRequest.getReservationDetails().getUserId();
        if (userId != null) {
            Optional<Clientes> optionalCliente = clienteRepository.findById(userId);
            if (optionalCliente.isEmpty()) {
                log.warn("Cliente no encontrado con ID: {}. La reserva se creará sin cliente asociado.", userId);
                preReserva.setCliente(null);
            } else {
                preReserva.setCliente(optionalCliente.get());
            }
        } else {
            log.warn("ID de usuario nulo. La reserva se creará sin cliente asociado.");
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
        log.info("Pre-reserva creada con ID: {}", preReserva.getIdReserva());

        List<PreferenceItemRequest> itemsMp = orderRequest.getItems().stream().map(item -> {
            String itemIdToUse = item.getId();
            // *** INICIO DEL CAMBIO CLAVE ***
            // Si el ID del ítem recibido del frontend es una cadena no numérica como "general",
            // intentamos mapearla a un ID numérico string
            if (ZONA_ID_MAPPING.containsKey(item.getId().toLowerCase())) {
                itemIdToUse = ZONA_ID_MAPPING.get(item.getId().toLowerCase());
                log.debug("Mapeando ID de ítem '{}' a ID numérico '{}' para Mercado Pago.", item.getId(), itemIdToUse);
            } else {
                // Si el ID no está en el mapeo, y no es un número, podríamos loggear una advertencia
                // o manejarlo de otra manera si se esperan solo IDs numéricos.
                // Aquí, simplemente lo dejamos como está si no hay un mapeo directo.
                log.warn("El ID de ítem '{}' no es un número ni está en el mapeo de zonas.", item.getId());
            }
            // *** FIN DEL CAMBIO CLAVE ***

            log.debug("Procesando item: ID={}, Title={}, Quantity={}, UnitPrice={}",
                    itemIdToUse, item.getTitle(), item.getQuantity(), item.getUnitPrice());
            return PreferenceItemRequest.builder()
                    .id(itemIdToUse) // Usamos el ID mapeado o el original
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .pictureUrl(item.getPictureUrl())
                    .quantity(item.getQuantity())
                    .currencyId(item.getCurrencyId())
                    .unitPrice(item.getUnitPrice())
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

        log.info("Preferencia de pago creada con ID: {} e InitPoint: {}. External Reference: {}",
                preference.getId(), preference.getInitPoint(), preference.getExternalReference());

        preReserva.setPreferenceId(preference.getId());
        reservaRepository.save(preReserva);

        return preference.getInitPoint();
    }

    @Transactional
    public Reserva confirmPaymentAndReservation(MercadoPagoConfirmationRequest confirmationRequest) {
        log.info("Confirmación de pago recibida: {}", confirmationRequest);

        Optional<Reserva> optionalReserva = reservaRepository.findByPreferenceId(confirmationRequest.getPreferenceId());

        if (optionalReserva.isEmpty()) {
            log.warn("Reserva pendiente no encontrada para preferenceId: {}. No se puede confirmar.", confirmationRequest.getPreferenceId());
            throw new IllegalArgumentException("Reserva pendiente no encontrada para la preferencia de pago.");
        }

        Reserva reserva = optionalReserva.get();

        if ("approved".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pagado");
            reserva.setEstado("Confirmada");
            reserva.setIdTransaccion(confirmationRequest.getCollectionId());
            log.info("Reserva {} (ID MP: {}) actualizada a estado 'Pagado' y 'Confirmada'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else if ("pending".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pendiente");
            reserva.setEstado("Pendiente");
            reserva.setIdTransaccion(null);
            log.info("Reserva {} (ID MP: {}) actualizada a estado 'Pendiente'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else {
            reserva.setEstadoPago("Rechazado");
            reserva.setEstado("Cancelada");
            reserva.setIdTransaccion(null);
            log.warn("Reserva {} (ID MP: {}) actualizada a estado 'Rechazado' y 'Cancelada'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        }

        return reservaRepository.save(reserva);
    }
}