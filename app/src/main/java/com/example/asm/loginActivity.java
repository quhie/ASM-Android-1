package com.example.asm;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.asm.OTP.getOTP;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class loginActivity extends AppCompatActivity {

    // Khai báo biến
    private static final String TAG = "LoginActivity";
    private static final int DELAY_TIME = 2000;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private LottieAnimationView btnAnimation;
    private ImageButton btnCloseReg;
    private TextView tvRegister, btnText;
    private CheckBox chkShowPw;
    private RelativeLayout btnLoginLayout;
    private LinearLayout btnGoogle, btnLoginPhone;
    private SharedPreferences sharedPreferences;
    private EditText edtLoginEmail, edtLoginPw;

    private FirebaseAuth mAuth;
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
                                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
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
        setContentView(R.layout.activity_login);



        // Tạo cấu hình GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Yêu cầu mã thông báo ID của ứng dụng web
                .requestEmail()  // Yêu cầu thông tin email từ người dùng
                .build();

// Tạo GoogleSignInClient từ cấu hình trên
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Ánh xạ các view
        btnCloseReg = findViewById(R.id.btnCloseReg);
        btnText = findViewById(R.id.btnText);
        chkShowPw = findViewById(R.id.chkShowPw);
        btnAnimation = findViewById(R.id.btnAnimation);
        btnLoginLayout = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPw = findViewById(R.id.edtLoginPw);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();




        // Tìm đến ConstraintLayout và gắn bộ lắng nghe chạm vào màn hình để ẩn bàn phím
        View constraintLayout = findViewById(R.id.content_layout);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });


        // Xử lý sự kiện khi người dùng chọn hoặc bỏ chọn ô kiểm
        chkShowPw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Nếu ô kiểm được chọn (isChecked = true), hiển thị mật khẩu
                if (isChecked) {
                    edtLoginPw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else { // Nếu ô kiểm không được chọn (isChecked = false), ẩn mật khẩu
                    edtLoginPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // Xử lý sự kiện đóng form đăng ký
        btnCloseReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện đăng ký mới
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình đăng ký
                Intent intent = new Intent(loginActivity.this, RegisActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện đăng nhập bằng email và mật khẩu
        btnLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtLoginEmail.getText().toString();
                String password = edtLoginPw.getText().toString();
                if (email.isEmpty()) {
                    edtLoginEmail.setError("Vui lòng nhập email");
                    edtLoginEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    edtLoginPw.setError("Vui lòng nhập mật khẩu");
                    edtLoginPw.requestFocus();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng nhập thành công, chuyển đến MainActivity
                                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    // Đăng nhập thất bại, hiển thị thông báo lỗi
                                    Toast.makeText(loginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // Xử lý sự kiện hiệu ứng nút đăng nhập
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnText.setVisibility(View.GONE);
                btnAnimation.setVisibility(View.VISIBLE);
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(DELAY_TIME);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        btnAnimation.setProgress(progress);
                    }
                });
                animator.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnText.setVisibility(View.VISIBLE);
                        btnAnimation.setVisibility(View.GONE);
                    }
                }, DELAY_TIME);
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }


}
