package sn.travel.payment_service.data.enums;

/**
 * Supported payment methods.
 * SIMULATED is used for testing and development.
 */
public enum PaymentMethod {
    STRIPE,
    PAYPAL,
    WAVE,
    SIMULATED
}
