package org.androidtown.sns_project.object;

import java.io.Serializable;

public class NewsData_health implements Serializable {

     private String health_title;
     private String health_description;
     private String health_urlToImage;
     private String health_url;

    public String getTitle() {
        return health_title;
    }

    public void setTitle(String title) {
        this.health_title = title;
    }

    public String getDescription() {
        return health_description;
    }

    public void setDescription(String description) {
        this.health_description = description;
    }

    public String getUrlToImage() {
        return health_urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.health_urlToImage = urlToImage;
    }

    public String getUrl() {
        return health_url;
    }

    public void setUrl(String url) {
        this.health_url = url;
    }

}
