package org.androidtown.sns_project.object;

public class Topitem_Data {

    String item_title;
    String item_date;
    String item_content;
    String item_imageURL;

    public Topitem_Data(String item_title, String item_date, String item_content, String item_imageURL) {
        this.item_title = item_title;
        this.item_date = item_date;
        this.item_content = item_content;
        this.item_imageURL = item_imageURL;
    }

    public String getItem_content() {
        return item_content;
    }

    public void setItem_content(String item_content) {
        this.item_content = item_content;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getItem_date() {
        return item_date;
    }

    public void setItem_date(String item_date) {
        this.item_date = item_date;
    }

    public String getItem_imageURL() {
        return item_imageURL;
    }

    public void setItem_imageURL(String item_imageURL) {
        this.item_imageURL = item_imageURL;
    }
}
