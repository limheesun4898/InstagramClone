package com.example.instagramclone.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagramclone.Activity.EditFeedActivity;
import com.example.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlusFragment extends Fragment {

    public PlusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plus, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btn_edit)
    public void onClick(){
        Intent intent = new Intent(getActivity(), EditFeedActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}
