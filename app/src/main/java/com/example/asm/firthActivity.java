package com.example.asm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.asm.OTP.getOTP;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class firthActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    private Button login, regis;

    private LinearLayout btnGoogle, btnLoginPhone;

    private FirebaseAuth mAuth;


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    ActivityResultLauncher<Intent> googleLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();

                        // Lấy thông tin GoogleSignInAccount từ dữ liệu kết quả
                        Task<GoogleSignInAccount> googleTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = googleTask.getResult(ApiException.class);
                        Toast.makeText(this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show();

                        if (googleTask.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");

                            // Lấy IdToken của tài khoản Google
                            String idToken = account.getIdToken();

                            if (idToken != null && !idToken.isEmpty()) {
                                // Tạo AuthCredential từ IdToken
                                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

                                // Đăng nhập vào Firebase với AuthCredential
                                mAuth.signInWithCredential(credential)
                                        .addOnCompleteListener(firebaseTask -> {
                                            if (firebaseTask.isSuccessful()) {
                                                // Đăng nhập thành công
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    mAuth.updateCurrentUser(user);
                                                } else {
                                                    Log.e("TAG", "User is null");
                                                }
                                                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(firthActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            } else {
                                                // Đăng nhập thất bại
                                                Log.w("TAG", "signInWithCredential:failure", firebaseTask.getException());
                                            }
                                        });
                            } else {
                                Log.e("TAG", "IdToken is null or empty");
                            }
                        } else {
                            // Nếu đăng nhập thất bại, hiển thị thông báo cho người dùng.
                            Log.w("TAG", "signInWithCredential: failure", googleTask.getException());
                        }
                    } catch (ApiException e) {
                        Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firth);

        login = findViewById(R.id.btnlogin);
        regis = findViewById(R.id.btnregis);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnLoginPhone = findViewById(R.id.btnLoginPhone);

        mAuth = FirebaseAuth.getInstance();

        // Tạo cấu hình GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Yêu cầu mã thông báo ID của ứng dụng web
                .requestEmail()  // Yêu cầu thông tin email từ người dùng
                .build();

// Tạo GoogleSignInClient từ cấu hình trên
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firthActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firthActivity.this, RegisActivity.class);
                startActivity(intent);
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                Log.i("TAG", "signInIntent: " + signInIntent);
                googleLogin.launch(signInIntent);
            }
        });

        btnLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình nhập số điện thoại để nhận mã OTP
                Intent intent = new Intent(firthActivity.this, getOTP.class);
                startActivity(intent);
            }
        });


    }
}