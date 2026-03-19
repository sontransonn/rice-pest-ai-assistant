package com.example.frontend;

public class Pest {
    private String nameVN;
    private String nameEN;
    private int imageResId; // ID của ảnh trong thư mục drawable

    public Pest(String nameVN, String nameEN, int imageResId) {
        this.nameVN = nameVN;
        this.nameEN = nameEN;
        this.imageResId = imageResId;
    }

    // Getters
    public String getNameVN() { return nameVN; }
    public String getNameEN() { return nameEN; }
    public int getImageResId() { return imageResId; }
}