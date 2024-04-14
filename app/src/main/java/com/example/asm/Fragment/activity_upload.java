package com.example.asm.Fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.asm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class activity_upload extends AppCompatActivity {
    private static final int DELAY_TIME = 2000;
    ImageView uploadImage;
    ImageButton backButton;
    RelativeLayout saveButton;
    EditText uploadTopic, uploadDesc, uploadGia, uploadId;
    String imageURL;

    Uri uri;
    DatabaseReference databaseReference;
    DatabaseReference productsReference;

    FirebaseAuth firebaseAuth;
    String userID;
    private LottieAnimationView btnAnimation;
    private TextView btnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadId = findViewById(R.id.uploadId);
        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDesc);
        uploadTopic = findViewById(R.id.uploadTopic);
        uploadGia = findViewById(R.id.uploadGia);
        saveButton = findViewById(R.id.saveButton);
        btnText = findViewById(R.id.btnText);
        btnAnimation = findViewById(R.id.btnAnimation);
        backButton = findViewById(R.id.backButton);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        productsReference = FirebaseDatabase.getInstance().getReference().child("Products");

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_upload.this);
                String message = "<font color='#FF0000'>Bạn có muốn thoát không?</font>";
                builder.setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY));
                builder.setCancelable(true);
                builder.setPositiveButton("Có 🫡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                builder.setNegativeButton("🫣Hông ", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            uploadImage.setImageURI(uri);
        } else {
            Toast.makeText(activity_upload.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveData() {
        final String id = uploadId.getText().toString().trim();
        final String title = uploadTopic.getText().toString().trim();
        final String desc = uploadDesc.getText().toString().trim();
        final String gia = uploadGia.getText().toString().trim();

        if (uri == null) {
            Toast.makeText(activity_upload.this, "Vui lòng chọn hình ảnh.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (id.isEmpty() || title.isEmpty() || desc.isEmpty() || gia.isEmpty()) {
            Toast.makeText(activity_upload.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra sản phẩm có tồn tại trong Firebase chưa
        productsReference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // ID đã tồn tại
                        Toast.makeText(activity_upload.this, "ID đã tồn tại, vui lòng chọn một ID khác.", Toast.LENGTH_SHORT).show();
                    } else {
                        // ID không tồn tại, tiến hành lưu dữ liệu
                        saveProductData(id, title, desc, gia);
                    }
                } else {
                    Toast.makeText(activity_upload.this, "Lỗi khi kiểm tra ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveProductData(String id, String title, String desc, String gia) {
        StorageReference storageReference = FirebaseStorage.getInstance("gs://asmandroid2.appspot.com")
                .getReference()
                .child("Android Images")
                .child(title);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_upload.this);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý sự kiện hiệu ứng nút đăng nhập
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
                // Tiến hành tải lên hình ảnh
                storageReference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isComplete()) ;
                                Uri urlImage = uriTask.getResult();
                                imageURL = urlImage.toString();

                                DataClass dataClass = new DataClass(id, title, desc, gia, imageURL);

                                databaseReference.child(userID).child(id)
                                        .setValue(dataClass)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity_upload.this, "Đã lưu", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss(); // Giải phóng dialog
                                                    finish();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity_upload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss(); // Giải phóng dialog
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity_upload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); // Giải phóng dialog
                            }
                        });

                btnText.setVisibility(View.VISIBLE);
                btnAnimation.setVisibility(View.GONE);
            }
        }, DELAY_TIME);
    }
}
