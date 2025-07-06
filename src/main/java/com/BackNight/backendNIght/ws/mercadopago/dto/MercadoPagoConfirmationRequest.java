package com.BackNight.backendNIght.ws.mercadopago.dto;

public class MercadoPagoConfirmationRequest {
    private String collectionId;
    private String status;
    private String preferenceId;

    public MercadoPagoConfirmationRequest() {}

    public MercadoPagoConfirmationRequest(String collectionId, String status, String preferenceId) {
        this.collectionId = collectionId;
        this.status = status;
        this.preferenceId = preferenceId;
    }

    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPreferenceId() { return preferenceId; }
    public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }

    @Override
    public String toString() {
        return "MercadoPagoConfirmationRequest{" +
                "collectionId='" + collectionId + '\'' +
                ", status='" + status + '\'' +
                ", preferenceId='" + preferenceId + '\'' +
                '}';
    }
}