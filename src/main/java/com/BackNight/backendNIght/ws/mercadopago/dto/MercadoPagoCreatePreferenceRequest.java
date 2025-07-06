package com.BackNight.backendNIght.ws.mercadopago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoCreatePreferenceRequest {
    private List<ItemRequest> items;
    private ReservationDetails reservationDetails;
}