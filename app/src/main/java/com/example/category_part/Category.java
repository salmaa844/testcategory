package com.example.category_part;

import android.graphics.Bitmap;

public class Category {
    private int id;
    private String name;
    private Bitmap image;

    public Category(String name, Bitmap image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public Bitmap getImage() { return image; }
}
