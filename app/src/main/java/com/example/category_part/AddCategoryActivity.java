package com.example.category_part;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddCategoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView ivCategoryImage;
    private EditText etCategoryName;
    private CategoryDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        ivCategoryImage = findViewById(R.id.ivCategoryImage);
        etCategoryName = findViewById(R.id.etCategoryName);
        db = new CategoryDatabaseHelper(this);

        // Select image
        findViewById(R.id.btnSelectImage).setOnClickListener(v -> openGallery());

        // Save category
        findViewById(R.id.btnSaveCategory).setOnClickListener(v -> saveCategory());
    }

    // Open gallery to select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Save category to the database
    private void saveCategory() {
        String name = etCategoryName.getText().toString();
        if (name.isEmpty() || ivCategoryImage.getDrawable() == null) {
            Toast.makeText(this, "Please provide both a name and an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap image = ((BitmapDrawable) ivCategoryImage.getDrawable()).getBitmap();
        db.addCategory(name, image);
        Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Close activity
    }

    // Handle selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ivCategoryImage.setImageURI(selectedImageUri);
            ivCategoryImage.setVisibility(View.VISIBLE); // Show image
        }
    }
}
