package org.androidtown.sns_project.object;

import java.io.Serializable;

public class NewsData_entertainment implements Serializable {

     private String entertainment_title;
     private String entertainment_description;
     private String entertainment_urlToImage;
     private String entertainment_url;

    public String getTitle() {
        return entertainment_title;
    }

    public void setTitle(String title) {
        this.entertainment_title = title;
    }

    public String getDescription() {
        return entertainment_description;
    }

    public void setDescription(String description) {
        this.entertainment_description = description;
    }

    public String getUrlToImage() {
        return entertainment_urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.entertainment_urlToImage = urlToImage;
    }

    public String getUrl() {
        return entertainment_url;
    }

    public void setUrl(String url) {
        this.entertainment_url = url;
    }

}
