package com.example.hikenow;

public class Trip {
    private String title;
    private String description;
    private String difficulty;
    private int imageResId;

    public Trip(String title, String description, String difficulty, int imageResId){
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getImageResId() {
        return imageResId;
    }
}
