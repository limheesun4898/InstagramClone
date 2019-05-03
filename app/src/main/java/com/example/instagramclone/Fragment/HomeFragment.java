package com.example.instagramclone.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagramclone.Activity.LoginActivity;
import com.example.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);



        return view;
    }

    @OnClick(R.id.btn_logout)
    void Logout(){
        SharedPreferences pref = getActivity().getSharedPreferences("Token", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.remove("token");

        editor.commit();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();

    }


}
