package com.lobxy.test_api_login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    String username, password;
    Button submit;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Working...");
        dialog.setCancelable(false);

        editPassword = findViewById(R.id.main_pwd);
        editUsername = findViewById(R.id.main_username);

        submit = findViewById(R.id.main_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editUsername.getText().toString().trim();
                password = editPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else {
                    getData();
                }
            }
        });

    }

    private void getData() {
        dialog.show();

        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        Call<Message> call = apiInterface.getData(username, password);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                int responseCode = response.code();
                Message message = response.body();

                dialog.dismiss();

                Log.i("Main", "onResponse: response code: " + responseCode);

                if (responseCode != 200) {
                    if (responseCode == 404) {
                        Log.i("main", "onResponse: res: " + response.errorBody().toString());
                        Toast.makeText(MainActivity.this, "Wrong City Name", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 400) {
                        Toast.makeText(MainActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 401) {
                        Toast.makeText(MainActivity.this, "Invalid API Key", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 500) {
                        Toast.makeText(MainActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 429) {
                        Toast.makeText(MainActivity.this, "API Key Blocked", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Internal Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (message.getSuccess()) {
                        startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
                        finish();
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body().toString());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Main", "onFailure: " + t.getLocalizedMessage());
            }
        });

    }


}
