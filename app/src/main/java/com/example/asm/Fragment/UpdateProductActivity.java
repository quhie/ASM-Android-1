package com.example.asm.Fragment;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.asm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProductActivity extends AppCompatActivity {
    private static final int DELAY_TIME = 2000;
    ImageView updateImage;
    EditText updateDesc, updateTitle, updateGia, updateId;
    String title, desc, gia, id;
    String imageUrl = "";
    RelativeLayout saveButtonUpdate;

    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;

    AlertDialog dialog;

    private TextView btnTextUpdate;

    private LottieAnimationView btnAnimation;

    private boolean isOldImageDeleted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Khởi tạo các tham chiếu đến Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        updateId = findViewById(R.id.updateId);

        // Ánh xạ các view trong layout
        saveButtonUpdate = findViewById(R.id.saveButtonUpdate);
        btnTextUpdate = findViewById(R.id.btnTextUpdate);
        btnAnimation = findViewById(R.id.btnAnimation);
        updateDesc = findViewById(R.id.updateDesc);
        updateImage = findViewById(R.id.updateImage);
        updateGia = findViewById(R.id.updateGia);
        updateTitle = findViewById(R.id.updateTitle);

        // Lấy dữ liệu từ Intent và hiển thị lên giao diện
        Intent intent = getIntent();
        if (intent != null) {
            updateTitle.setText(intent.getStringExtra("Title"));
            updateDesc.setText(intent.getStringExtra("Description"));
            updateGia.setText(intent.getStringExtra("Gia"));
            updateId.setText(intent.getStringExtra("dataId"));
            Glide.with(UpdateProductActivity.this).load(intent.getStringExtra("Image")).into(updateImage);
            oldImageURL = intent.getStringExtra("Title");
        }

        // Xử lý sự kiện khi người dùng chọn hình ảnh mới
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút "Lưu"
        saveButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                // Hiệu ứng nút "Lưu" sau khi người dùng nhấn
                btnTextUpdate.setVisibility(View.GONE);
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
                        btnTextUpdate.setVisibility(View.VISIBLE);
                        btnAnimation.setVisibility(View.GONE);
                    }
                }, DELAY_TIME);
            }
        });
    }

    // Xử lý kết quả khi người dùng chọn hình ảnh từ Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            imageUrl = uri.toString();
            updateImage.setImageURI(uri);

        } else {
            Toast.makeText(UpdateProductActivity.this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức lưu dữ liệu lên Firebase
    public void saveData() {
        // Lấy thông tin dữ liệu từ giao diện
        id = updateId.getText().toString().trim();
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        gia = updateGia.getText().toString();

        if (uri != null) {
            // Xử lý lưu ảnh vào Firebase Storage
            uploadImageToFirebaseStorage(uri);
        } else {
            // Không có ảnh mới, cập nhật thông tin sản phẩm và kết thúc cập nhật dữ liệu
            updateProductData(id, firebaseAuth.getCurrentUser().getUid(), imageUrl);

        }
    }

    // Phương thức tải ảnh lên Firebase Storage và đặt tên hình ảnh mới dựa vào dataTitle
    private void uploadImageToFirebaseStorage(Uri uri) {
        storageReference = FirebaseStorage.getInstance("gs://asmandroid2.appspot.com").getReference().child("Android Images").child(title);
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Task<Uri> uriTask = task.getResult().getStorage().getDownloadUrl();
                    uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri urlImage = task.getResult();
                                imageUrl = urlImage.toString();
                                // Lưu URL hình ảnh mới vào Firebase Database và xóa ảnh cũ (nếu có)
                                updateProductData(id, firebaseAuth.getCurrentUser().getUid(), imageUrl);
                            } else {
                                // Lỗi khi lấy URL tải lên
                                Exception exception = task.getException();
                                if (exception != null) {
                                    // Xử lý lỗi
                                    Toast.makeText(UpdateProductActivity.this, "Lỗi khi lấy URL tải lên: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    // Lỗi khi tải lên ảnh
                    Exception exception = task.getException();
                    if (exception != null) {
                        Toast.makeText(UpdateProductActivity.this, "Lỗi khi tải lên ảnh: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    // Phương thức cập nhật dữ liệu lên Firebase
    private void updateProductData(String id, String userID, String newImageUrl) {
        // Tham chiếu đến đối tượng dữ liệu trên Firebase
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference(userID).child(id);



        // Cập nhật thông tin sản phẩm
        productReference.child("dataId").setValue(id);
        productReference.child("dataTitle").setValue(title);
        productReference.child("dataDesc").setValue(desc);
        productReference.child("dataGia").setValue(gia);
//        productReference.child("dataImage").setValue(imageUrl);


        if (!newImageUrl.equals(oldImageURL)) {
            Log.d("TAG", "new: " + newImageUrl);
            Log.d("TAG", "old: " + oldImageURL);
            // Cập nhật URL hình ảnh mới
            productReference.child("dataImage").setValue(newImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Nếu cập nhật URL hình ảnh mới thành công, xóa ảnh cũ (nếu có) và kết thúc cập nhật dữ liệu
                        StorageReference storageReference = FirebaseStorage.getInstance("gs://asmandroid2.appspot.com")
                                .getReference()
                                .child("Android Images")
                                .child(oldImageURL);
                        deleteOldImageAndFinish(storageReference);
                    } else {
                        // Lỗi khi cập nhật URL hình ảnh mới
                        Toast.makeText(UpdateProductActivity.this, "Lỗi khi cập nhật URL hình ảnh mới: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        // Kết thúc cập nhật dữ liệu dù thất bại trong việc cập nhật URL hình ảnh mới
                        dialog.dismiss();
                    }
                }
            });
    }}

    // Phương thức xóa ảnh cũ khỏi Firebase Storage và kết thúc cập nhật thông tin sản phẩm
    private void deleteOldImageAndFinish(StorageReference productReference) {
        if (oldImageURL != null && !isOldImageDeleted) {
            isOldImageDeleted = true;
            // Xử lý xóa ảnh cũ khỏi Firebase Storage
            productReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Ảnh cũ đã được xóa thành công
                    Toast.makeText(UpdateProductActivity.this, "Xoá hình ảnh cũ thành công!! ", Toast.LENGTH_SHORT).show();
                    // Kết thúc cập nhật thông tin sản phẩm sau khi xóa ảnh cũ
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Lỗi khi xóa ảnh cũ
                    Toast.makeText(UpdateProductActivity.this, "Lỗi khi xoá hình ảnh cũ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Kết thúc cập nhật thông tin sản phẩm dù thất bại trong việc xóa ảnh cũ
                    finish();
                }
            });
        } else {
            // Không có ảnh cũ hoặc đã xóa ảnh cũ, kết thúc cập nhật thông tin sản phẩm
            finish();
        }
    }
}
