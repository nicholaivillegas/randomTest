package com.resume.nico.randomtest;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    TextInputLayout tilNumber, tilAmount;
    TextInputEditText editNumber, editAmount;
    Button btnSubmit;
    RadioButton radioGlobe, radioSmart, radioSun;
    RadioGroup radioGroupProvider;
    String amount, phoneNo, provider = "globe";
    RequestQueue queue;
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!editNumber.getText().toString().matches("(0)\\d{10}")) {
                tilNumber.setError("Invalid Number");
                btnSubmit.setEnabled(false);
            } else {
                tilNumber.setError(null);
                btnSubmit.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSubmit = (Button) findViewById(R.id.button_submit);
        editNumber = (TextInputEditText) findViewById(R.id.edit_number);
        editAmount = (TextInputEditText) findViewById(R.id.edit_amount);
        tilNumber = (TextInputLayout) findViewById(R.id.til_number);
        tilAmount = (TextInputLayout) findViewById(R.id.til_amount);
        radioGlobe = (RadioButton) findViewById(R.id.radio_globe);
        radioSmart = (RadioButton) findViewById(R.id.radio_smart);
        radioSun = (RadioButton) findViewById(R.id.radio_sun);
        radioGroupProvider = (RadioGroup) findViewById(R.id.radio_group_provider);
        btnSubmit.setOnClickListener(this);
        radioGroupProvider.setOnCheckedChangeListener(this);
        editNumber.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_submit:
                tilNumber.setError(null);
                tilAmount.setError(null);
                phoneNo = editNumber.getText().toString();
                amount = editAmount.getText().toString();
                if (TextUtils.isEmpty(phoneNo)) {
                    tilNumber.setError("Enter Number");
                    return;
                } else {
                    if (TextUtils.isEmpty(amount)) {
                        tilAmount.setError("Enter Amount");
                    } else {
                        if (Integer.parseInt(amount) <= 300) {
                            getCheckedProvider();
                            submitInfo();
                            Toast.makeText(this, "Provider: " + provider + "\n" + "Number: " + phoneNo + "\n" + "Amount: " + amount, Toast.LENGTH_SHORT).show();
                        } else {
                            tilAmount.setError("Enter lower amount");
                        }
                    }
                }

                break;
        }
    }

    public void submitInfo() {
        Toast.makeText(this, "G", Toast.LENGTH_SHORT).show();
        queue = Volley.newRequestQueue(this);
        String url = "http://www.gameaccesscheats.com/indianrockstar/loading-backend/public/index.php/load/" + provider + "/" + amount + "/" + phoneNo;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);

    }

    public void getCheckedProvider() {
        if (radioGroupProvider.getCheckedRadioButtonId() == R.id.radio_globe) {
            provider = "globe";
        } else if (radioGroupProvider.getCheckedRadioButtonId() == R.id.radio_smart) {
            provider = "smart";
        } else if (radioGroupProvider.getCheckedRadioButtonId() == R.id.radio_sun) {
            provider = "sun";
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        clearFields();
    }

    public void clearFields() {
        editNumber.setText("");
        editAmount.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(this);
        }
    }
}
