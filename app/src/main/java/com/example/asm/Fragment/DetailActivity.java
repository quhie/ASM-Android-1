package com.example.asm.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.asm.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {



    private final int DELAY_TIME = 1000; // 1000 milliseconds = 1 second

    TextView detailDesc, detailTitle, detailGia;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String dataId; // Declare dataId variable to store the ID of the data to be deleted
    String imageUrl;
    String oldTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);

        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailGia = findViewById(R.id.detailGia);

        Intent intent = getIntent();
        if (intent != null) {
            //hiện thị dữ liệu
            dataId = intent.getStringExtra("dataId");
            imageUrl = intent.getStringExtra("Image");

            Log.d("TAG", "onClick title: " + intent.getStringExtra("Title"));
            oldTitle = intent.getStringExtra("Title");
            detailTitle.setText(intent.getStringExtra("Title"));

            intent.putExtra("Title", oldTitle);
            detailDesc.setText(intent.getStringExtra("Description"));
            detailGia.setText(intent.getStringExtra("Gia"));
            Glide.with(this).load(imageUrl).into(detailImage);
        }


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dataId.isEmpty()) { // Kiểm tra dataId có giá trị hay không
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setMessage("Bạn có chắc chắn muốn xoá sản phẩm này?");

                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteProductData(dataId);
                        }
                    });
                    builder.setNegativeButton("Từ từ đã!!!", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateProductActivity.class);
                intent.putExtra("dataId", dataId);
                intent.putExtra("Title", oldTitle);
                intent.putExtra("Description", detailDesc.getText().toString());
                intent.putExtra("Gia", detailGia.getText().toString());
                intent.putExtra("Image", imageUrl);
                startActivity(intent);
            }
        });

    }


    public void deleteProductData(String dataId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // The content of this method will be executed after the delay
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    String userID = user.getUid();

                    DatabaseReference dataReference = mDatabase.child(userID).child(dataId);
                    Log.i("dataReference", dataReference.toString());

                    dataReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Tiến hành xoá hình ảnh trên Firebase Storage nếu có
                            if (!imageUrl.isEmpty()) {
                                StorageReference storageReference = FirebaseStorage.getInstance("gs://asmandroid2.appspot.com").getReferenceFromUrl(imageUrl);
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DetailActivity.this, "Đã xoá sản phẩm và hình ảnh", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss(); // Giải phóng dialog
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetailActivity.this, "Không thể xoá hình ảnh", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Image URL is empty, only delete the data from the Realtime Database
                                Toast.makeText(DetailActivity.this, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); // Giải phóng dialog
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Không thể xoá dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }, DELAY_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra nếu có dữ liệu được truyền từ UpdateProductActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataId = extras.getString("dataId", "");
            imageUrl = extras.getString("Image", "");
            detailTitle.setText(extras.getString("dataTitle", ""));
            detailDesc.setText(extras.getString("Description", ""));
            detailGia.setText(extras.getString("Gia", ""));
            Glide.with(this).load(imageUrl).into(detailImage);

            // Cập nhật giao diện với dữ liệu mới
            updateUI(detailTitle.getText().toString(), detailDesc.getText().toString(), detailGia.getText().toString(), imageUrl);
        }

        // Kiểm tra xem dữ liệu đã được cập nhật từ UpdateProductActivity hay chưa
        if (dataId != null && !dataId.isEmpty()) {
            // Dữ liệu đã được cập nhật, không cần thực hiện gì thêm
        }
    }


    private void updateUI(String title, String desc, String gia, String imageUrl) {
        // Ánh xạ các view trong layout của DetailActivity
        TextView titleTextView = findViewById(R.id.detailTitle);
        TextView descTextView = findViewById(R.id.detailDesc);
        TextView giaTextView = findViewById(R.id.detailGia);
        ImageView imageView = findViewById(R.id.detailImage);

        // Cập nhật dữ liệu mới lên giao diện
        titleTextView.setText(title);
        descTextView.setText(desc);
        giaTextView.setText(gia);
        Glide.with(this).load(imageUrl).into(imageView);
    }

}


