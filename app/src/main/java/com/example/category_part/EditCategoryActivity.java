package com.example.category_part;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EditCategoryActivity extends AppCompatActivity {

        EditText etCategoryName;
        Button btnUpdateCategory;
        String categoryId;
        String categoryName;
        String url = "http://192.168.88.10/UpdateCategory.php"; // Correct URL for update

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit);

            etCategoryName = findViewById(R.id.etCategoryName);
            btnUpdateCategory = findViewById(R.id.btnUpdateCategory);

            // الحصول على اسم الفئة و ID الفئة من الـ Intent
            categoryName = getIntent().getStringExtra("CATEGORY_NAME");
            categoryId = getIntent().getStringExtra("CATEGORY_ID");

            // عرض اسم الفئة في الـ EditText
            etCategoryName.setText(categoryName);

            btnUpdateCategory.setOnClickListener(v -> updateCategory());
        }

        // Method to update category data
        private void updateCategory() {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating category...");
            progressDialog.show();

            final String updatedCategoryName = etCategoryName.getText().toString().trim();

            if (TextUtils.isEmpty(updatedCategoryName)) {
                Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            // إرسال طلب تحديث الفئة باستخدام الـ category_id
            String updateUrl = "http://192.168.88.10/UpdateCategory.php?category_id=" + categoryId + "&category_name=" + updatedCategoryName;

            StringRequest updateRequest = new StringRequest(Request.Method.GET, updateUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("Category updated successfully!")) {
                        Toast.makeText(EditCategoryActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
                        finish();  // العودة للنشاط السابق بعد التحديث
                    } else {
                        Toast.makeText(EditCategoryActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditCategoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(EditCategoryActivity.this);
            requestQueue.add(updateRequest);
        }
    }
