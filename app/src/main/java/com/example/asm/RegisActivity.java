package com.example.asm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisActivity extends AppCompatActivity {

    //khai báo biến
    final int DELAY_TIME = 2000;

    LottieAnimationView btnAnimation;
    ImageButton btnCloseReg2;
   EditText edtRegEmail, edtPwReg, edtPwReg2;
    RelativeLayout btnReg;

    TextView tvLogin, btnText;

    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        //ánh xạ
        btnCloseReg2 = findViewById(R.id.btnCloseReg2);
        edtRegEmail = findViewById(R.id.edtRegEmail);
        edtPwReg = findViewById(R.id.edtPwReg);
        edtPwReg2 = findViewById(R.id.edtPwReg2);
        btnReg = findViewById(R.id.btnReg);
        tvLogin = findViewById(R.id.tvLogin);
        btnText = findViewById(R.id.btnText);

        btnAnimation = findViewById(R.id.btnAnimation);
        mAuth = FirebaseAuth.getInstance();


      //chuyển đến màn hình firthActivity
        btnCloseReg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RegisActivity.this, firthActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        //đăng ký
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlayAnimation();
                String email = edtRegEmail.getText().toString();
                String password = edtPwReg.getText().toString();
                String rePw = edtPwReg2.getText().toString();
                boolean kiemTraEmail = isValidEmail(email);
                if (email.isEmpty() || password.isEmpty() || rePw.isEmpty()) {
                    Toast.makeText(RegisActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(RegisActivity.this::resetButton, DELAY_TIME);

                } else if (!kiemTraEmail) {
                    Toast.makeText(RegisActivity.this, "Vui lòng nhập đúng định email", Toast.LENGTH_SHORT).show();

                } else if (!password.equals(rePw)) {
                    Toast.makeText(RegisActivity.this, "Nhập lại mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                }
                    else if (!isStrongPassword(password)) {
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(RegisActivity.this, loginActivity.class));
                                new Handler().postDelayed(RegisActivity.this::resetButton, DELAY_TIME);

                                finish();
                                Toast.makeText(RegisActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisActivity.this, "Đăng ký không thành công.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(RegisActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

    }
    public boolean isStrongPassword(String password) {
        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu quá ngắn. Yêu cầu ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return false; // Mật khẩu quá ngắn
        }

        // Kiểm tra sự hiện diện của ít nhất một chữ cái viết hoa
        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(this, "Mật khẩu phải chứa ít nhất một chữ cái viết hoa.", Toast.LENGTH_SHORT).show();
            return false; // Mật khẩu không có chữ cái viết hoa
        }

        // Kiểm tra sự hiện diện của ít nhất một chữ cái viết thường
        if (!password.matches(".*[a-z].*")) {
            Toast.makeText(this, "Mật khẩu phải chứa ít nhất một chữ cái viết thường.", Toast.LENGTH_SHORT).show();
            return false; // Mật khẩu không có chữ cái viết thường
        }

        // Kiểm tra sự hiện diện của ít nhất một chữ số
        if (!password.matches(".*\\d.*")) {
            Toast.makeText(this, "Mật khẩu phải chứa ít nhất một chữ số.", Toast.LENGTH_SHORT).show();
            return false; // Mật khẩu không có chữ số
        }

        return true; // Mật khẩu đủ mạnh
    }
    public boolean isValidEmail(String email) {
        // Mẫu regex cho chuỗi email
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // Kiểm tra sự khớp của chuỗi email với mẫu regex
        return email.matches(regex);
    }
    private void resetButton() {
        btnAnimation.pauseAnimation();
        btnAnimation.setVisibility(View.GONE);
        btnText.setVisibility(View.VISIBLE);
    }

    private void btnPlayAnimation() {
        btnAnimation.setVisibility(View.VISIBLE);
        btnAnimation.playAnimation();
        btnText.setVisibility(View.GONE);
    }

}