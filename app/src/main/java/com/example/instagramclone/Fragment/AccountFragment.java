package com.example.instagramclone.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.instagramclone.Data.APIClient;
import com.example.instagramclone.Data.ApiInterface;
import com.example.instagramclone.Data.AccountData;
import com.example.instagramclone.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    String email, name, nickname, profile;
    int id;

    @BindView(R.id.text_name)
    TextView text_name;
    @BindView(R.id.text_email)
    TextView text_email;
    @BindView(R.id.text_nickname)
    TextView text_nickname;
    @BindView(R.id.photo)
    CircleImageView photo;
    SharedPreferences sharedPreferences;
    String token;

    ApiInterface apiInterface;
    Bitmap bmImg;

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        ButterKnife.bind(this, view);

        sharedPreferences = getActivity().getSharedPreferences("Token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        System.out.println("Account token : " + token);

        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getAccount(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonElement element = new JsonParser().parse(response.body().toString())
                        .getAsJsonObject().get("result");

                AccountData result = new Gson().fromJson(element, AccountData.class);
                email = result.getEmail();
                name = result.getRealName();
                nickname = result.getNickName();
                profile = result.getProfile();
                id = result.getId();

                ImageAsyncTask task = new ImageAsyncTask();
                task.execute(profile);

                text_email.setText(email);
                text_name.setText(name);
                text_nickname.setText(nickname);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });



        return view;
    }

    private class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img) {
            photo.setImageBitmap(bmImg);
        }

    }
}