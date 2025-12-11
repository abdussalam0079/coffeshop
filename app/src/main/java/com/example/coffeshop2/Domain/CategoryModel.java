package com.example.coffeshop2.Domain;
public class CategoryModel {
    private int id;
    private String title;

    public CategoryModel() { }
    public CategoryModel(int id, String title) {
        this.id = id;
        this.title = title;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
}
