package com.example.coffeshop2.Domain;

public class BannerModel {

    private String url;     // 🔥 Firebase field: url
    private String id;      // optional – for key
    private String title;   // optional – if you store title

    // ✔ Required empty constructor for Firebase
    public BannerModel() {
    }

    // ✔ Constructor if needed
    public BannerModel(String url) {
        this.url = url("https://res.cloudinary.com/dkikc5ywq/image/upload/v1748598162/banner_pnixuo.png");
    }

    private String url(String url) {
        return url;
    }

    public BannerModel(String url, String id, String title) {
        this.url = url;
        this.id = id;
        this.title = title;
    }

    // ✔ Getters (Firebase NEEDS getters)
    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // ✔ Setters (optional but recommended for Firebase)
    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
