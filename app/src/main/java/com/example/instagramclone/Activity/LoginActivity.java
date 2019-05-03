package com.example.instagramclone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.Data.APIClient;
import com.example.instagramclone.Data.ApiInterface;
import com.example.instagramclone.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    @BindView(R.id.edit_email) EditText edit_email;
    @BindView(R.id.edit_password) EditText edit_password;
    @BindView(R.id.text_email) TextView text_email;

    String email, password;
    // 이메일 정규식
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    public void Signup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_login)
    void Login() {
        email = edit_email.getText().toString();
        password = edit_password.getText().toString();
        if (validateEmail(email)) {
            text_email.setVisibility(View.INVISIBLE);
            Login_Retrofit(email, password);
        } else {
            text_email.setVisibility(View.VISIBLE);
        }

    }

    private void Login_Retrofit(String email, String password) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getlogin(email, password);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonElement token = new JsonParser().parse(response.body().toString())
                            .getAsJsonObject().get("token");

                    if (token == null){
                        Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        SharedPreferences sharedPreferences = getSharedPreferences("Token", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token.toString());
                        editor.apply();

                        System.out.println("login token : " + token);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    // 이메일 검사
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
