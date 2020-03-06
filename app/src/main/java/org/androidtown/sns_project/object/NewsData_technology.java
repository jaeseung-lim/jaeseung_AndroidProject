package org.androidtown.sns_project.object;

import java.io.Serializable;

public class NewsData_technology implements Serializable {

     private String business_title;
     private String business_description;
     private String business_urlToImage;
     private String business_url;

    public String getTitle() {
        return business_title;
    }

    public void setTitle(String title) {
        this.business_title = title;
    }

    public String getDescription() {
        return business_description;
    }

    public void setDescription(String description) {
        this.business_description = description;
    }

    public String getUrlToImage() {
        return business_urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.business_urlToImage = urlToImage;
    }

    public String getUrl() {
        return business_url;
    }

    public void setUrl(String url) {
        this.business_url = url;
    }

}
