package com.gtxtreme.currencyconverter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner,toSpinner;
    private Button checkButton,convertButton;
    private String toSelected,fromSelected,fromCountryCode,toCountryCode,finalConversionRate;
    private EditText editText;
    private TextView detailsText;
    String URL = "https://api.exchangeratesapi.io/latest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner = findViewById(R.id.FromSpinner);
        toSpinner= findViewById(R.id.ToSpinner);
        checkButton=findViewById(R.id.buttonCheckNow);
        convertButton=findViewById(R.id.buttonConvert);
        editText=findViewById(R.id.AmounteditText);
        detailsText = findViewById(R.id.details);
        ArrayAdapter<CharSequence> adapter1= ArrayAdapter.createFromResource(this,R.array.spinner_items_array,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter1);
        ArrayAdapter<CharSequence> adapter2= ArrayAdapter.createFromResource(this,R.array.spinner_items_array,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(adapter2);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromSelected= adapterView.getItemAtPosition(i).toString();
                fromCountryCode = fromSelected.substring(0,fromSelected.indexOf("-"));
               // Log.d("HellofromCountry",fromCountryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                       }
        });

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toSelected= adapterView.getItemAtPosition(i).toString();
                toCountryCode = toSelected.substring(0,toSelected.indexOf("-"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromSelected.equals(toSelected)){
                    Toast.makeText(MainActivity.this,"Both currencies can't be same..Please change atleast one of the values",Toast.LENGTH_LONG).show();
                    fromSpinner.requestFocus();
                }
                else{
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    final StringRequest request = new StringRequest(Request.Method.GET, URL + "?symbols=" + fromCountryCode + "," + toCountryCode + "&" + "base=" + fromCountryCode, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            try {
                                JSONObject reader = new JSONObject(response);
                                String date = reader.getString("date");
                                JSONObject rate = reader.getJSONObject("rates");
                                finalConversionRate = rate.getString(toCountryCode);
                                detailsText.setText("Values were last updated at " + date + "\n" + "The conversion rate from " + fromCountryCode + " to " + toCountryCode + " is " + finalConversionRate);
                                //Log.d("Date",date);
                            } catch (JSONException e) {
                                Log.e("CurrencyConverter", e.toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Something went wrong..Please Try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    );

                    queue.add(request);

                }
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountValue = editText.getText().toString();

                if(detailsText.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"Please Check the Details first..Press the check button",Toast.LENGTH_LONG).show();
                }
               else if(fromSelected.equals(toSelected)){
                    Toast.makeText(MainActivity.this,"Both currencies can't be same..Please change atleast one of the values",Toast.LENGTH_LONG).show();
                    fromSpinner.requestFocus();
                }


               else if(amountValue.equals("")){
                    Toast.makeText(MainActivity.this, "Amount can't be blank", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("fromCountryCode", fromCountryCode);
                    intent.putExtra("toCountryCode", toCountryCode);
                   // Log.d("Hellooo",finalConversionRate+"");
                    intent.putExtra("finalConversionRate", finalConversionRate);
                    intent.putExtra("amountValue", amountValue);
                    startActivity(intent);
                }
            }
        });
    }


}
