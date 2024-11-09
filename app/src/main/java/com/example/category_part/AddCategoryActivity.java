package com.example.category_part;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddCategoryActivity extends AppCompatActivity {

    EditText etCategoryName;
    Button btnInsert;
    String url = "http://192.168.88.10/category.php"; // URL to send category data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        etCategoryName = findViewById(R.id.etCategoryName); // EditText where user enters category name
        btnInsert = findViewById(R.id.btnInsertCategory); // Button to send category

        String categoryType = getIntent().getStringExtra("CATEGORY_TYPE");

        // استخدام البيانات (مثال: عرضها أو تخصيص شيء بناءً عليها)
        Toast.makeText(this, "Category Type: " + categoryType, Toast.LENGTH_SHORT).show();
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCategory();
            }
        });
    }

    private void insertCategory() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving category...");
        progressDialog.show();

        final String categoryName = etCategoryName.getText().toString().trim();

        if (TextUtils.isEmpty(categoryName)) {
            Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        try {
            // Encoding the category name to handle special characters
            String encodedCategoryName = URLEncoder.encode(categoryName, "UTF-8");

            // Construct the URL with category_name as a query parameter
            String getUrl = url + "?category_name=" + encodedCategoryName;

            // Sending GET request
            StringRequest request = new StringRequest(Request.Method.GET, getUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", response); // Log the response for debugging
                    if (response.contains("Category inserted successfully!")) {
                        Toast.makeText(AddCategoryActivity.this, "Category added", Toast.LENGTH_SHORT).show();

                        // After successful insertion, navigate back to CategoryListActivity and refresh
                        Intent intent = new Intent(AddCategoryActivity.this, CategoryListActivity.class);
                        startActivity(intent);  // Open CategoryListActivity
                        finish();  // Close AddCategoryActivity
                    } else {
                        Toast.makeText(AddCategoryActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VolleyError", error.toString()); // Log the error for debugging
                    Toast.makeText(AddCategoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

            // Add the request to the request queue
            RequestQueue requestQueue = Volley.newRequestQueue(AddCategoryActivity.this);
            requestQueue.add(request);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error encoding the category name", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Close this activity and return to CategoryListActivity
    }
}
