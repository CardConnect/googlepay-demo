package com.cardconnect.cardconnectgooglepaydemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private PaymentsClient mPaymentsCient;
    private View mGooglePaymentsButton;
    private EditText editTextCSToken;
    private final int REQUEST_CODE = 12345;
    private CardConnectGatewayAPIHelper.SiteName mSite = CardConnectGatewayAPIHelper.SiteName.QA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCSToken = findViewById(R.id.edittext_cs_token);

        mPaymentsCient = Wallet.getPaymentsClient(this,
                new Wallet.WalletOptions.Builder()
        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
        .build());

        isUserGooglePayReady();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_button_qa:
                if (checked)
                    mSite = CardConnectGatewayAPIHelper.SiteName.{sitename};
                    break;

             default:
                 break;
        }
    }

    public void requestPayment(View view) {
        JSONObject paymentDataRequestJson = null;

        try {
            paymentDataRequestJson = CardConnectGooglePayHelper.getPaymentsDataRequest();
        } catch (JSONException e) {
            Log.d("JSONRequestPayment", "Json exception");
        }

        if (paymentDataRequestJson == null) {
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());

        if (request != null) {
            AutoResolveHelper.resolveTask(mPaymentsCient.loadPaymentData(request), this, REQUEST_CODE);
        }
    }

    private void isUserGooglePayReady() {

        IsReadyToPayRequest request = null;
        try {
            request = IsReadyToPayRequest.fromJson(
                    CardConnectGooglePayHelper.getIsReadyToPayRequest().toString());
        } catch(JSONException e) {
            Log.d("readyToPay", "JSON exception when checking isReadytoPay");
            return;
        }

        Task<Boolean> task = mPaymentsCient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if(result) {
                                // Display Payments button
                                mGooglePaymentsButton = findViewById(R.id.button_pay);
                                mGooglePaymentsButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                requestPayment(v);
                                            }
                                        }
                                );

                            } else {
                                // Display some type of error
                            }
                        } catch (ApiException e) {

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE :
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String json = paymentData.toJson();
                        String csMessageURL = CardConnectGatewayAPIHelper.buildGooglePayCardSecureRequest(json, mSite);
                        CardConnectGatewayAPIHelper.sendMessage(csMessageURL, this, editTextCSToken);
                        Log.i("cardConnectPaymentsdata", json);
                        Log.i("cardCSurl", csMessageURL);
                        Toast.makeText(this, "Tokenization Success",Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED :
                        break;
                    case AutoResolveHelper.RESULT_ERROR :
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Log.d("StatusStaus", status.getStatusMessage());
                        break;
                    default :
                        break;
                }
                break;
                default:
        }
    }
}
