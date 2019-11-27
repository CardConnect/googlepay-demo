package com.cardconnect.cardconnectgooglepaydemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class defines various helper methods that are used to build
 * the JSON messages needed to communicate with the Google Pay API.
 *
 * A complete overview of the methods below can be found in the Google
 * Pay Android documentation.
 */
class CardConnectGooglePayHelper {

    // Defines the name of the gateway that has registered with the
    // Google Pay API. This value must be the name of a registered
    // gateway or an error will be generated. This value is also important
    // because it determines which public key Google will use to
    // encrypt the token payload.
    private static final String gateway = "cardconnect";

    // This value represents the MID used by the cardconnect gateway
    // to identify a merchant. The cardconnect gateway will use this value
    // to determine which merchant processed the payment request
    private static final String getGatewayMerchantID = "cardConnectTestMerchant";

    /**
     * Set the base version of the Google Pay API your app will use
     *
     * @return
     * @throws JSONException
     */
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }

    /**
     * Define a 'tokenziation specification' which is used to define how
     * Google will encrypt the payment information for a users transaction.
     *
     * @return
     * @throws JSONException
     */
    private static JSONObject getTokenizationSpecification() throws JSONException {
        JSONObject tokenSpec = new JSONObject();
        tokenSpec.put("type", "PAYMENT_GATEWAY");
        tokenSpec.put("parameters",
                new JSONObject()
                        .put("gateway", gateway)
                        .put("gatewayMerchantId", getGatewayMerchantID));

        return tokenSpec;
    }

    /**
     * Defines card networks that your app will accept
     *
     * @return
     */
    private static JSONArray getAllowableCardNetworks() {
        return new JSONArray()
                .put("AMEX")
                .put("VISA")
                .put("MASTERCARD")
                .put("DISCOVER");
    }

    /**
     * Defines the auth methods that your app will use, please see Google
     * Pay documentation for more information.
     *
     * @return
     */
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    /**
     * Combines the JSON used to define the supported authentication methods
     * and card networks.
     *
     * @return
     * @throws JSONException
     */
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        return new JSONObject()
                .put("type", "CARD")
                .put("parameters", new JSONObject()
                .put("allowedAuthMethods", getAllowedCardAuthMethods())
                .put("allowedCardNetworks", getAllowableCardNetworks()));
    }

    /**
     * Build the tokenization Specification JSON
     *
     * @return
     * @throws JSONException
     */
    private static JSONObject getCardPaymentMethod() throws JSONException {
        return getBaseCardPaymentMethod().put("tokenizationSpecification", getTokenizationSpecification());
    }

    /**
     * This JSON is used to determine if the user can make payments using the Google Pay API
     *
     * @return
     * @throws JSONException
     */
    static JSONObject getIsReadyToPayRequest() throws JSONException {
        JSONObject isReadyToPayRequest = getBaseRequest();
        isReadyToPayRequest.put(
                "allowedPaymentMethods",
                new JSONArray()
                .put(getBaseCardPaymentMethod()));

        return isReadyToPayRequest;
    }

    /**
     * This JSON represents test transaction information.
     * @return
     * @throws JSONException
     */
    public static JSONObject getDummyTransactionInfo() throws  JSONException {
        return new JSONObject().put("totalPrice", "98.76")
                .put("totalPriceStatus", "FINAL")
                .put("currencyCode", "USD");
    }

    /**
     * This JSON defines a user-visable merchant name
     *
     * @return
     * @throws JSONException
     */
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "CardConnect Store");
    }

    /**
     * This JSON defines the information used for the payment request in
     * its totality
     *
     * @return
     * @throws JSONException
     */
    static JSONObject getPaymentsDataRequest() throws  JSONException {
        return getBaseRequest().put("allowedPaymentMethods",
                new JSONArray()
        .put(getCardPaymentMethod()))
                .put("transactionInfo", getDummyTransactionInfo())
                .put("merchantInfo", getMerchantInfo());
    }
}
