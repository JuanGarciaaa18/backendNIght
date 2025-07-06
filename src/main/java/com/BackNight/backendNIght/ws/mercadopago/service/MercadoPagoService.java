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
    import com.BackNight.backendNIght.ws.entity.Clientes; // Asegúrate de que esta clase Cliente se llame Clientes
    import com.BackNight.backendNIght.ws.repository.ReservaRepository;
    import com.BackNight.backendNIght.ws.repository.EventoRepository;
    import com.BackNight.backendNIght.ws.repository.ClientesRepository; // Asegúrate de que este repositorio sea para Clientes

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
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
        private final ClientesRepository clienteRepository; // Renombrado de 'ClientesRepository' si tu clase se llama 'Cliente' en el paquete entity

        private static final Map<String, String> ZONA_ID_MAPPING = new HashMap<>();

        static {
            // Asegúrate de que estos IDs numéricos coincidan con los IDs de tus zonas en la tabla `zonas` de la BD
            ZONA_ID_MAPPING.put("general", "1");
            ZONA_ID_MAPPING.put("preferencial", "2");
            ZONA_ID_MAPPING.put("vip", "3");
            // Agrega más mapeos aquí si tienes otras zonas (e.g., ZONA_ID_MAPPING.put("palco", "4");)
        }

        public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken,
                                  ReservaRepository reservaRepository,
                                  EventoRepository eventoRepository,
                                  ClientesRepository clienteRepository) { // Usa ClientesRepository
            MercadoPagoConfig.setAccessToken(accessToken);
            this.reservaRepository = reservaRepository;
            this.eventoRepository = eventoRepository;
            this.clienteRepository = clienteRepository; // Asignación correcta
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
                Optional<Clientes> optionalCliente = clienteRepository.findById(userId); // Usa ClientesRepository
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
                // Asegúrate de que el mapeo aquí también incluya "preferencial" y "vip"
                if (ZONA_ID_MAPPING.containsKey(item.getId().toLowerCase())) {
                    itemIdToUse = ZONA_ID_MAPPING.get(item.getId().toLowerCase());
                    log.debug("Mapeando ID de ítem '{}' a ID numérico '{}' para Mercado Pago.", item.getId(), itemIdToUse);
                } else {
                    // Si el ID ya es numérico (e.g. "1"), se usa directamente
                    try {
                        Integer.parseInt(item.getId());
                        log.debug("Item ID '{}' ya es numérico, usándolo directamente.", item.getId());
                    } catch (NumberFormatException e) {
                        log.warn("Item ID '{}' no es numérico y no está en el mapeo de zonas.", item.getId());
                    }
                }

                BigDecimal originalUnitPrice = item.getUnitPrice();
                BigDecimal finalUnitPriceForMp;

                if (originalUnitPrice == null || originalUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    log.error("Item unitPrice es nulo o no positivo para ítem ID: {}. Se usará BigDecimal.ZERO para MP.", itemIdToUse);
                    finalUnitPriceForMp = BigDecimal.ZERO;
                } else {
                    // *** MODIFICACIÓN CLAVE (Conversión explícita a LONG y luego a BigDecimal para asegurar enteros para COP) ***
                    // 1. Redondea el BigDecimal original a un entero.
                    // 2. Conviértelo a un Long para asegurar que no hay decimales.
                    // 3. Crea un NUEVO BigDecimal a partir de ese Long, lo que garantiza un valor entero.
                    Long priceAsLong = originalUnitPrice.setScale(0, RoundingMode.HALF_UP).longValue();
                    finalUnitPriceForMp = BigDecimal.valueOf(priceAsLong); // Crea un BigDecimal a partir de un Long
                    log.debug("Convertido original unitPrice {} a Long {} y luego a BigDecimal {} para Mercado Pago.", originalUnitPrice, priceAsLong, finalUnitPriceForMp);
                }

                log.debug("Procesando item para MP: ID={}, Title={}, Quantity={}, UnitPrice (final para MP)={}",
                        itemIdToUse, item.getTitle(), item.getQuantity(), finalUnitPriceForMp);

                return PreferenceItemRequest.builder()
                        .id(itemIdToUse)
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .pictureUrl(item.getPictureUrl())
                        .quantity(item.getQuantity())
                        .currencyId(item.getCurrencyId())
                        .unitPrice(finalUnitPriceForMp)
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
                            .installments(1) // Si solo permites 1 cuota
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