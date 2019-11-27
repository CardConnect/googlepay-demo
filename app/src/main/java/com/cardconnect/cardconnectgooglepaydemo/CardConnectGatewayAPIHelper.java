package com.cardconnect.cardconnectgooglepaydemo;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CardConnectGatewayAPIHelper {

    private static final String ACTION = "CR";
    private static final String REQUEST = "action=" + ACTION + "&data=";

    enum SiteName {sitename}
    public static String buildSiteURL(SiteName site) {
        if(site == SiteName.QA) return  "https://{sitename}.cardconnect.com:443/cardsecure/cs?";

        return null;
    }

    public static String buildGooglePayCardSecureRequest(String paymentsToken, SiteName site) {

        String CardSecureRequestURL = null;

        try {
            JSONObject paymentsJson = new JSONObject(paymentsToken);
            JSONObject paymentMethodDataJson = paymentsJson.getJSONObject("paymentMethodData");
            JSONObject tokenizationData = paymentMethodDataJson.getJSONObject("tokenizationData");
            String token = tokenizationData.getString("token");

            CardSecureRequestURL = buildSiteURL(site) + REQUEST + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException | JSONException e) {
            Log.d("buildGooglePay", e.toString());
        }

        return CardSecureRequestURL;
    }

    public static void sendMessage(String csURL, Context context, final EditText editTextCSToken) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(Request.Method.POST, csURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("csResponse", response);
                editTextCSToken.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("csError", "wtf");
            }
        });

        queue.add(request);
    }
}
