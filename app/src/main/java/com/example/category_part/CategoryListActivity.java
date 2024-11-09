package com.example.category_part;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryListActivity extends AppCompatActivity {

    private LinearLayout categoriesContainer;
    private String url = "http://192.168.88.10/viewcategory.php"; // URL to fetch categories

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        categoriesContainer = findViewById(R.id.categoriesContainer); // The LinearLayout to hold the cards

        // Fetch categories from the server
        fetchCategories();
    }

    // Method to open AddCategoryActivity when the "Add Category" button is clicked
    public void openAddCategory(View view) {
        Intent intent = new Intent(CategoryListActivity.this, AddCategoryActivity.class);
        startActivity(intent);  // Start AddCategoryActivity
    }

    // Method to handle opening the EditCategoryActivity when the edit icon is clicked
    public void openEditCategory(View view) {
        // Access the parent CardView containing the category information
        CardView parentCard = (CardView) view.getParent().getParent(); // Get the parent CardView

        // Retrieve the categoryId stored in the tag of the CardView
        String categoryId = (String) parentCard.getTag();

        // Access the category name from the TextView inside the CardView
        TextView tvCategoryName = parentCard.findViewById(R.id.tvCategoryName);
        String categoryName = tvCategoryName.getText().toString();

        // Open EditCategoryActivity and pass the categoryId and categoryName
        Intent intent = new Intent(CategoryListActivity.this, EditCategoryActivity.class);
        intent.putExtra("CATEGORY_NAME", categoryName);
        intent.putExtra("CATEGORY_ID", categoryId);  // Pass category_id to the EditActivity
        startActivity(intent);
    }

    public void openDeleteCategory(View view) {
        // Access the parent CardView containing the category information
        CardView parentCard = (CardView) view.getParent().getParent(); // Get the parent CardView

        // Retrieve the categoryId stored in the tag of the CardView
        final String categoryId = (String) parentCard.getTag();

        // Access the category name from the TextView inside the CardView
        TextView tvCategoryName = parentCard.findViewById(R.id.tvCategoryName);
        final String categoryName = tvCategoryName.getText().toString();

        // Show a confirmation dialog before deleting the category
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete the category: " + categoryName + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Proceed with deleting the category
                        deleteCategory(categoryId, parentCard); // Pass the parentCard to remove it from the UI
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // Method to handle deletion (send a GET request to your PHP backend)
    private void deleteCategory(String categoryId, View parentCard) {
        // URL to your PHP delete script
        String url = "http://192.168.88.10/delete.php?category_id=" + categoryId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            if ("Data Deleted Successfully".equals(message)) {
                                // Show success message
                                Toast.makeText(getApplicationContext(), "Category deleted", Toast.LENGTH_SHORT).show();

                                // Remove the category from the UI
                                categoriesContainer.removeView(parentCard);  // Remove the parent card from the container
                            } else {
                                // Show failure message
                                Toast.makeText(getApplicationContext(), "Failed to delete category", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Method to fetch categories from the server and display them
    private void fetchCategories() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading categories...");
        progressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Log the full response for debugging purposes
                    Log.d("Response", response.toString());

                    // Clear previous categories
                    categoriesContainer.removeAllViews();

                    // Check if the response is successful
                    if (response.getInt("success") == 1) {
                        JSONArray categoriesArray = response.getJSONArray("categories");

                        // Log categories for debugging purposes
                        Log.d("Categories", categoriesArray.toString());

                        // Loop through the categories and create views for each one
                        for (int i = 0; i < categoriesArray.length(); i++) {
                            JSONObject categoryObject = categoriesArray.getJSONObject(i);
                            String categoryId = categoryObject.getString("CategoryID");
                            String categoryName = categoryObject.getString("name");

                            // Inflate category card layout
                            View categoryCard = LayoutInflater.from(CategoryListActivity.this).inflate(R.layout.category_card, null);

                            // Set category name to TextView
                            TextView categoryText = categoryCard.findViewById(R.id.tvCategoryName);
                            categoryText.setText(categoryName);

                            // Store category ID in the tag of the CardView
                            categoryCard.setTag(categoryId);

                            // Set onClick for the edit icon
                            View editIcon = categoryCard.findViewById(R.id.ivEditCategory);
                            editIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openEditCategory(v);  // Call method to open EditCategoryActivity
                                }
                            });

                            // Add the category card to the container
                            categoriesContainer.addView(categoryCard);
                        }
                    } else {
                        Toast.makeText(CategoryListActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CategoryListActivity.this, "An error occurred while loading categories", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
                Toast.makeText(CategoryListActivity.this, "Error connecting to the server", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        // Add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(CategoryListActivity.this);
        requestQueue.add(request);
    }

    // Override onResume to refresh categories when returning to this activity
    @Override
    protected void onResume() {
        super.onResume();
        fetchCategories(); // Refresh the list when returning from AddCategoryActivity
    }
}
