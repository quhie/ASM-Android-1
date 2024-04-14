package com.example.asm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SlashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // Người dùng đã đăng nhập, chuyển đến MainActivity
                    Intent intent = new Intent(SlashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    // Người dùng chưa đăng nhập, chuyển đến LoginActivity
                    Intent intent = new Intent(SlashScreen.this, firthActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        }, 2500);

    }
}