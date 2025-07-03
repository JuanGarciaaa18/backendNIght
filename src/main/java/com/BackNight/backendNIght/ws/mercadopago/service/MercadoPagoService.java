package com.BackNight.backendNIght.ws.mercadopago.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest; // Importar si se usa paymentMethods
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

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${mercadopago.access.token}")
    private String mercadoPagoAccessToken;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;
    private final ClientesRepository clienteRepository;

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken,
                              ReservaRepository reservaRepository,
                              EventoRepository eventoRepository,
                              ClientesRepository clienteRepository) {
        // Inicializa el SDK de Mercado Pago con el token de acceso
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

        // --- 1. Crear una "Pre-reserva" en tu base de datos ---
        Reserva preReserva = new Reserva();

        // Buscar y establecer el Evento
        Integer eventId = orderRequest.getReservationDetails().getEventId();
        Optional<Evento> optionalEvento = eventoRepository.findById(eventId);
        if (optionalEvento.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventId);
        }
        preReserva.setEvento(optionalEvento.get());

        // Buscar y establecer el Cliente
        Integer userId = orderRequest.getReservationDetails().getUserId();
        if (userId != null) {
            Optional<Clientes> optionalCliente = clienteRepository.findById(userId);
            if (optionalCliente.isEmpty()) {
                log.warn("Cliente no encontrado con ID: {}. La reserva se creará sin cliente asociado.", userId);
                preReserva.setCliente(null); // O maneja esto según tu lógica de negocio
            } else {
                preReserva.setCliente(optionalCliente.get());
            }
        } else {
            log.warn("ID de usuario nulo. La reserva se creará sin cliente asociado.");
            preReserva.setCliente(null); // O establece un cliente por defecto
        }

        // Calcular la cantidad total de tickets y el monto total
        int totalTickets = orderRequest.getReservationDetails().getTickets().stream()
                .mapToInt(t -> t.getQuantity())
                .sum();
        preReserva.setCantidadTickets(totalTickets);
        preReserva.setMontoTotal(orderRequest.getReservationDetails().getTotalAmount());

        preReserva.setFechaReserva(LocalDate.now()); // Fecha actual de la pre-reserva
        preReserva.setEstado("Pendiente"); // Estado de la reserva (ej: "Activa", "Pendiente", "Cancelada")
        preReserva.setEstadoPago("Pendiente"); // Estado inicial del pago

        preReserva.setIdTransaccion(null); // Aún no hay ID de transacción de MP

        // Guarda la pre-reserva para obtener un ID
        preReserva = reservaRepository.save(preReserva);
        log.info("Pre-reserva creada con ID: {}", preReserva.getIdReserva());

        // --- 2. Crear la preferencia de Mercado Pago ---
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
                // .autoReturn("approved") // SE COMENTA/ELIMINA ESTA LÍNEA
                .externalReference(String.valueOf(preReserva.getIdReserva())) // **CRUCIAL: Usa el ID de tu pre-reserva aquí**
                .notificationUrl("https://backnight-production.up.railway.app/servicio/mercadopago/webhook") // Asegúrate de que esta URL sea accesible públicamente para notificaciones
                .statementDescriptor("NightPlus") // Texto que aparecerá en el resumen de la tarjeta del comprador
                .binaryMode(false) // Si es true, solo se aceptan pagos aprobados, no se permiten pendientes ni rechazados
                .expires(false) // La preferencia no expira
                // Opcional: Métodos de pago. Puedes agregar esto si necesitas configurar cuotas, etc.
                .paymentMethods(PreferencePaymentMethodsRequest.builder()
                        .installments(1) // Por ejemplo, 1 cuota
                        .build())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        log.info("Preferencia de pago creada con ID: {} e InitPoint: {}. External Reference: {}",
                preference.getId(), preference.getInitPoint(), preference.getExternalReference());

        // Actualiza la pre-reserva con el ID de preferencia de Mercado Pago
        preReserva.setPreferenceId(preference.getId());
        reservaRepository.save(preReserva);

        return preference.getInitPoint();
    }

    @Transactional
    public Reserva confirmPaymentAndReservation(MercadoPagoConfirmationRequest confirmationRequest) {
        log.info("Confirmación de pago recibida: {}", confirmationRequest);

        // Busca la pre-reserva usando el preferenceId que nos devuelve Mercado Pago
        Optional<Reserva> optionalReserva = reservaRepository.findByPreferenceId(confirmationRequest.getPreferenceId());

        if (optionalReserva.isEmpty()) {
            log.warn("Reserva pendiente no encontrada para preferenceId: {}. No se puede confirmar.", confirmationRequest.getPreferenceId());
            throw new IllegalArgumentException("Reserva pendiente no encontrada para la preferencia de pago.");
        }

        Reserva reserva = optionalReserva.get();

        if ("approved".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pagado");
            reserva.setEstado("Confirmada"); // Cambia el estado de la reserva a "Confirmada"
            reserva.setIdTransaccion(confirmationRequest.getCollectionId()); // Guardar el ID de transacción real de MP
            log.info("Reserva {} (ID MP: {}) actualizada a estado 'Pagado' y 'Confirmada'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else if ("pending".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pendiente"); // Ya debería estar en pendiente, pero lo aseguramos
            reserva.setEstado("Pendiente"); // Estado de la reserva a "Pendiente"
            reserva.setIdTransaccion(null); // O limpiar el ID de transacción si ya se había asignado temporalmente
            log.info("Reserva {} (ID MP: {}) actualizada a estado 'Pendiente'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else { // "rejected" u otro estado
            reserva.setEstadoPago("Rechazado");
            reserva.setEstado("Cancelada"); // Estado de la reserva a "Cancelada"
            reserva.setIdTransaccion(null); // No hay transacción exitosa
            log.warn("Reserva {} (ID MP: {}) actualizada a estado 'Rechazado' y 'Cancelada'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        }

        return reservaRepository.save(reserva); // Guarda la reserva actualizada
    }
}