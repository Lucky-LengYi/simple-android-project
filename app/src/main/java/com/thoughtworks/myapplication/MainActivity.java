package com.thoughtworks.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtworks.myapplication.domain.PM25;
import com.thoughtworks.myapplication.service.AirServiceClient;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView pm25TextView;
    private TextView positionTextView;
    private TextView pmTextView;
    private TextView qualityTextView;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = (EditText) findViewById(R.id.edit_view_input);
        pm25TextView = (TextView) findViewById(R.id.text_view_pm25);

        positionTextView = (TextView) findViewById(R.id.position);
        pmTextView = (TextView) findViewById(R.id.pm2_5);
        qualityTextView = (TextView) findViewById(R.id.quality);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.loading_message));

        findViewById(R.id.button_query_pm25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onQueryPM25Click();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner1);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] citys = getResources().getStringArray(R.array.city);
                Toast.makeText(MainActivity.this, "你点击的是:"+citys[pos], 2000).show();

                final String city = citys[pos].toString();
                if (!TextUtils.isEmpty(city)) {
                    showLoading();
                    AirServiceClient.getInstance().requestPM25(city, new Callback<List<PM25>>() {
                        @Override
                        public void onResponse(Response<List<PM25>> response, Retrofit retrofit) {
                            showSuccessScreen(response);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showErrorScreen();
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onQueryPM25Click() {
        final String city = cityEditText.getText().toString();
        if (!TextUtils.isEmpty(city)) {
            showLoading();
            AirServiceClient.getInstance().requestPM25(city, new Callback<List<PM25>>() {
                @Override
                public void onResponse(Response<List<PM25>> response, Retrofit retrofit) {
                    showSuccessScreen(response);
                }

                @Override
                public void onFailure(Throwable t) {
                    showErrorScreen();
                }
            });
        }
    }

    private void showSuccessScreen(Response<List<PM25>> response) {
        hideLoading();
        if (response != null) {
            populate(response.body());
        }
    }

    private void showErrorScreen() {
        hideLoading();
        pm25TextView.setText(R.string.error_message_query_pm25);
    }

    private void showLoading() {
        loadingDialog.show();
    }

    private void hideLoading() {
        loadingDialog.dismiss();
    }

    private void populate(List<PM25> data) {

        positionTextView.setText("");
        pmTextView.setText("");
        qualityTextView.setText("");

        if (data != null && !data.isEmpty()) {

            positionTextView.setText("city");
            pmTextView.setText("PM2.5");
            qualityTextView.setText("质量");

            for (int i = 0; i < data.size(); i++){
                PM25 pm25 = data.get(i);

                if (pm25.getPositionName() != null){
                    positionTextView.setText(positionTextView.getText() + "\n" + pm25.getPositionName());
                    pmTextView.setText(pmTextView.getText() + "\n" + pm25.getPm25());
                    qualityTextView.setText(qualityTextView.getText() + "\n" + pm25.getQuality());
                }
            }
        }
    }
}
