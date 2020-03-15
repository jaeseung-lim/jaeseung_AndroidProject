package org.androidtown.sns_project.object;

import android.widget.EditText;

public class Memberinfo { // 멤버들의 정보를 객체화 시켜서 파이어베이스에 넘겨주기

    private String name;
    private String introduce;
    private String photoUrl;
    private String MemberUid;
    String typingTo;

    public Memberinfo(){

    }

    public Memberinfo(String name, String introduce) {
        this.name = name;
        this.introduce = introduce;
    }

    public Memberinfo(String name, String introduce, String photoUrl, String memberUid) {
        this.name = name;
        this.introduce = introduce;
        this.photoUrl = photoUrl;
        MemberUid = memberUid;
    }

    public Memberinfo(String name, String introduce, String photoUrl, String memberUid, String typingTo) {
        this.name = name;
        this.introduce = introduce;
        this.photoUrl = photoUrl;
        MemberUid = memberUid;
        this.typingTo = typingTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMemberUid() {
        return MemberUid;
    }

    public void setMemberUid(String memberUid) {
        MemberUid = memberUid;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }
}
