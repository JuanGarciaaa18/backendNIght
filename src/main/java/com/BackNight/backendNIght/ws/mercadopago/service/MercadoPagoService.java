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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        ZONA_ID_MAPPING.put("preferencial", "2");
        ZONA_ID_MAPPING.put("vip", "3");
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
        log.debug("DEBUG MP SERVICE: frontendBaseUrl cargado como: '{}'", frontendBaseUrl);
        log.debug("DEBUG MP SERVICE: Datos de la solicitud de preferencia recibidos: {}", orderRequest);

        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío.");
        }
        if (orderRequest.getReservationDetails() == null) {
            throw new IllegalArgumentException("Los detalles de la reserva no pueden ser nulos.");
        }

        Reserva preReserva = new Reserva();

        // 1. Asignar Evento
        Integer eventId = orderRequest.getReservationDetails().getEventId();
        Optional<Evento> optionalEvento = eventoRepository.findById(eventId);
        if (optionalEvento.isEmpty()) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + eventId);
        }
        preReserva.setEvento(optionalEvento.get());
        log.debug("DEBUG MP SERVICE: Evento asignado a preReserva. ID Evento: {}", preReserva.getEvento().getIdEvento());


        // 2. Asignar Cliente (¡Punto crítico y CORRECCIÓN aquí!)
        Integer userId = orderRequest.getReservationDetails().getUserId();
        if (userId == null) {
            // Si el userId es nulo desde el frontend, no podemos crear una reserva para un cliente.
            // Esto debería ser manejado antes por la validación del frontend si es obligatorio,
            // pero es una seguridad extra aquí.
            log.error("DEBUG MP SERVICE: ID de usuario nulo en reservationDetails. No se puede crear la reserva sin un cliente asociado.");
            throw new IllegalArgumentException("El ID de usuario es requerido para crear la reserva.");
        }

        Optional<Clientes> optionalCliente = clienteRepository.findById(userId);
        if (optionalCliente.isEmpty()) {
            // Si el cliente no se encuentra en la DB, LANZAMOS UNA EXCEPCIÓN.
            // Esto asegura que la columna id_cliente nunca sea NULL si es nullable=false en la DB,
            // y evita la creación de una reserva sin un cliente válido.
            log.error("DEBUG MP SERVICE: Cliente no encontrado con ID: {}. No se puede crear la reserva sin un cliente válido.", userId);
            throw new IllegalArgumentException("No se encontró un cliente válido con el ID proporcionado: " + userId);
        } else {
            preReserva.setCliente(optionalCliente.get());
            log.debug("DEBUG MP SERVICE: Cliente asignado a preReserva. ID Cliente: {}, Usuario: {}",
                    preReserva.getCliente().getIdCliente(), preReserva.getCliente().getUsuarioCliente());
        }

        // 3. Otros detalles de la reserva
        int totalTickets = orderRequest.getReservationDetails().getTickets().stream()
                .mapToInt(t -> t.getQuantity())
                .sum();
        preReserva.setCantidadTickets(totalTickets);
        preReserva.setMontoTotal(orderRequest.getReservationDetails().getTotalAmount());
        preReserva.setFechaReserva(LocalDate.now());
        preReserva.setEstado("Pendiente"); // Estado inicial
        preReserva.setEstadoPago("Pendiente"); // Estado de pago inicial
        preReserva.setIdTransaccion(null); // Se llenará después de la confirmación de MP

        // --- LOGS DE DEPURACIÓN CRÍTICOS (ANTES DE GUARDAR) ---
        log.debug("DEBUG MP SERVICE: Pre-reserva antes de guardar en DB:");
        log.debug("  Reserva ID (pre): {}", preReserva.getIdReserva()); // Será null inicialmente
        log.debug("  Cliente en preReserva (ID): {}", (preReserva.getCliente() != null ? preReserva.getCliente().getIdCliente() : "NULL"));
        log.debug("  Cliente en preReserva (Usuario): {}", (preReserva.getCliente() != null ? preReserva.getCliente().getUsuarioCliente() : "NULL"));
        log.debug("  Cliente en preReserva (Nombre): {}", (preReserva.getCliente() != null ? preReserva.getCliente().getNombre() : "NULL"));
        log.debug("  Evento en preReserva (ID): {}", (preReserva.getEvento() != null ? preReserva.getEvento().getIdEvento() : "NULL"));
        log.debug("  Evento en preReserva (Nombre): {}", (preReserva.getEvento() != null ? preReserva.getEvento().getNombreEvento() : "NULL"));


        // 4. Guardar la pre-reserva en la base de datos
        // Si el id_cliente sigue siendo NULL aquí, el problema es en la entidad Reserva o en la configuración JPA
        preReserva = reservaRepository.save(preReserva);
        log.info("DEBUG MP SERVICE: Pre-reserva creada con ID: {}", preReserva.getIdReserva());

        // --- LOGS DE DEPURACIÓN CRÍTICOS (DESPUÉS DE GUARDAR) ---
        log.debug("DEBUG MP SERVICE: Cliente en savedPreReserva (ID): {}", (preReserva.getCliente() != null ? preReserva.getCliente().getIdCliente() : "NULL"));
        log.debug("DEBUG MP SERVICE: Cliente en savedPreReserva (Usuario): {}", (preReserva.getCliente() != null ? preReserva.getCliente().getUsuarioCliente() : "NULL"));
        log.debug("DEBUG MP SERVICE: Cliente en savedPreReserva (Nombre): {}", (preReserva.getCliente() != null ? preReserva.getCliente().getNombre() : "NULL"));
        log.debug("DEBUG MP SERVICE: Evento en savedPreReserva (ID): {}", (preReserva.getEvento() != null ? preReserva.getEvento().getIdEvento() : "NULL"));


        // 5. Preparar ítems para Mercado Pago
        List<PreferenceItemRequest> itemsMp = orderRequest.getItems().stream().map(item -> {
            String itemIdToUse = item.getId();
            if (ZONA_ID_MAPPING.containsKey(item.getId().toLowerCase())) {
                itemIdToUse = ZONA_ID_MAPPING.get(item.getId().toLowerCase());
                log.debug("DEBUG MP SERVICE: Mapeando ID de ítem '{}' a ID numérico '{}' para Mercado Pago.", item.getId(), itemIdToUse);
            } else {
                try {
                    Integer.parseInt(item.getId());
                    log.debug("DEBUG MP SERVICE: Item ID '{}' ya es numérico, usándolo directamente.", item.getId());
                } catch (NumberFormatException e) {
                    log.warn("DEBUG MP SERVICE: Item ID '{}' no es numérico y no está en el mapeo de zonas.", item.getId());
                }
            }

            BigDecimal originalUnitPrice = item.getUnitPrice();
            BigDecimal finalUnitPriceForMp;

            if (originalUnitPrice == null || originalUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("DEBUG MP SERVICE: Item unitPrice es nulo o no positivo para ítem ID: {}. Se usará BigDecimal.ZERO para MP.", itemIdToUse);
                finalUnitPriceForMp = BigDecimal.ZERO;
            } else {
                Long priceAsLong = originalUnitPrice.setScale(0, RoundingMode.HALF_UP).longValue();
                finalUnitPriceForMp = BigDecimal.valueOf(priceAsLong);
                log.debug("DEBUG MP SERVICE: Convertido original unitPrice {} a Long {} y luego a BigDecimal {} para Mercado Pago.", originalUnitPrice, priceAsLong, finalUnitPriceForMp);
            }

            log.debug("DEBUG MP SERVICE: Procesando item para MP: ID={}, Title={}, Quantity={}, UnitPrice (final para MP)={}",
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

        log.debug("DEBUG MP SERVICE: URL de éxito para Mercado Pago: '{}'", successUrl);
        log.debug("DEBUG MP SERVICE: URL de pendiente para Mercado Pago: '{}'", pendingUrl);
        log.debug("DEBUG MP SERVICE: URL de fallo para Mercado Pago: '{}'", failureUrl);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .pending(pendingUrl)
                .failure(failureUrl)
                .build();

        // Usar el ID de la reserva recién creada como external_reference para MP
        // Esto permite que el webhook de MP encuentre la reserva correcta.
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(itemsMp)
                .backUrls(backUrls)
                .externalReference(String.valueOf(preReserva.getIdReserva())) // <--- ¡Importante: Usar el ID de la preReserva!
                .notificationUrl("https://backnight-production.up.railway.app/servicio/mercadopago/webhook") // Asegúrate de que esta URL sea accesible públicamente y reciba POST
                .statementDescriptor("NightPlus") // Descripción que aparece en el estado de cuenta del comprador
                .binaryMode(false) // Modo binario: solo permite pagos aprobados o rechazados
                .expires(false) // No expira la preferencia
                .paymentMethods(PreferencePaymentMethodsRequest.builder()
                        .installments(1) // Permite 1 cuota
                        .build())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        log.info("DEBUG MP SERVICE: Preferencia de pago creada con ID: {} e InitPoint: {}. External Reference: {}",
                preference.getId(), preference.getInitPoint(), preference.getExternalReference());

        // 6. Actualizar la pre-reserva con el preference_id de Mercado Pago
        // Esto es una segunda llamada a save, pero solo actualiza el preferenceId
        preReserva.setPreferenceId(preference.getId());
        reservaRepository.save(preReserva); // Guardar el preferenceId

        return preference.getInitPoint();
    }

    @Transactional
    public Reserva confirmPaymentAndReservation(MercadoPagoConfirmationRequest confirmationRequest) {
        log.info("DEBUG MP SERVICE: Confirmación de pago recibida: {}", confirmationRequest);

        // Buscar la reserva usando el preferenceId que nos envía Mercado Pago en el Webhook
        Optional<Reserva> optionalReserva = reservaRepository.findByPreferenceId(confirmationRequest.getPreferenceId());

        if (optionalReserva.isEmpty()) {
            log.warn("DEBUG MP SERVICE: Reserva pendiente no encontrada para preferenceId: {}. No se puede confirmar.", confirmationRequest.getPreferenceId());
            throw new IllegalArgumentException("Reserva pendiente no encontrada para la preferencia de pago.");
        }

        Reserva reserva = optionalReserva.get();

        // --- LOGS DE DEPURACIÓN (Antes de actualizar estado) ---
        log.debug("DEBUG MP SERVICE: Reserva antes de actualizar estado. ID: {}, Cliente ID: {}, Estado Pago: {}",
                reserva.getIdReserva(), (reserva.getCliente() != null ? reserva.getCliente().getIdCliente() : "NULL"), reserva.getEstadoPago());


        // Actualizar el estado de la reserva basado en el status de Mercado Pago
        if ("approved".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pagado");
            reserva.setEstado("Confirmada");
            reserva.setIdTransaccion(confirmationRequest.getCollectionId());
            log.info("DEBUG MP SERVICE: Reserva {} (ID MP: {}) actualizada a estado 'Pagado' y 'Confirmada'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else if ("pending".equalsIgnoreCase(confirmationRequest.getStatus())) {
            reserva.setEstadoPago("Pendiente");
            reserva.setEstado("Pendiente");
            reserva.setIdTransaccion(null); // No hay ID de transacción final si está pendiente
            log.info("DEBUG MP SERVICE: Reserva {} (ID MP: {}) actualizada a estado 'Pendiente'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        } else {
            reserva.setEstadoPago("Rechazado");
            reserva.setEstado("Cancelada");
            reserva.setIdTransaccion(null); // No hay ID de transacción final si es rechazado
            log.warn("DEBUG MP SERVICE: Reserva {} (ID MP: {}) actualizada a estado 'Rechazado' y 'Cancelada'.", reserva.getIdReserva(), confirmationRequest.getCollectionId());
        }

        // --- LOGS DE DEPURACIÓN (Después de actualizar estado y antes de guardar) ---
        log.debug("DEBUG MP SERVICE: Reserva después de actualizar estado y antes de guardar. ID: {}, Cliente ID: {}, Nuevo Estado Pago: {}",
                reserva.getIdReserva(), (reserva.getCliente() != null ? reserva.getCliente().getIdCliente() : "NULL"), reserva.getEstadoPago());

        Reserva savedReserva = reservaRepository.save(reserva);

        // --- LOGS DE DEPURACIÓN (Después de guardar la confirmación) ---
        log.debug("DEBUG MP SERVICE: Reserva guardada después de confirmación. ID: {}, Cliente ID: {}, Estado Pago Final: {}",
                savedReserva.getIdReserva(), (savedReserva.getCliente() != null ? savedReserva.getCliente().getIdCliente() : "NULL"), savedReserva.getEstadoPago());

        return savedReserva;
    }
}