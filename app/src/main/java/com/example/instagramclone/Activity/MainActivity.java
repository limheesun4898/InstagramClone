package com.example.instagramclone.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramclone.Fragment.AccountFragment;
import com.example.instagramclone.Fragment.HomeFragment;
import com.example.instagramclone.Fragment.PlusFragment;
import com.example.instagramclone.Fragment.SearchFragment;
import com.example.instagramclone.R;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    AccountFragment accountFragment = new AccountFragment();
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    PlusFragment plusFragment = new PlusFragment();

    SharedPreferences sharedPreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext().getSharedPreferences("Token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        System.out.println("main token : " + token);

        if (token.equals("")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottomnavigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.homeaction:
                        transaction1.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();
                        break;

                    case R.id.searchaction:
                        transaction1.replace(R.id.frame_layout, searchFragment).commitAllowingStateLoss();
                        break;

                    case R.id.plusaction:
                        transaction1.replace(R.id.frame_layout, plusFragment).commitAllowingStateLoss();
                        break;

                    case R.id.accountaction:
                        transaction1.replace(R.id.frame_layout, accountFragment).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });

    }

}
