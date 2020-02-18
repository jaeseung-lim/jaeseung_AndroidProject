package org.androidtown.sns_project;

import android.widget.EditText;

public class Memberinfo { // 멤버들의 정보를 객체화 시켜서 파이어베이스에 넘겨주기

    private String name;
    private String introduce;
    private String photoUrl;
    private String MemberUid;

    public Memberinfo(String name, String introduce, String photoUrl, String MemberUid){
        this.name=name;
        this.introduce=introduce;
        this.photoUrl=photoUrl;
        this.MemberUid=MemberUid;
    }

    public Memberinfo(String name,String introduce){
        this.name=name;
        this.introduce=introduce;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getIntroduce(){
        return this.introduce;
    }

    public void setIntroduce(String introduce){
        this.introduce=introduce;
    }

    public String getphotoUrl(){
        return this.photoUrl;
    }

    public void setphotoUrl(String photoUrl){
        this.photoUrl=photoUrl;
    }

    public String getMemberUid() {
        return this.MemberUid;
    }

    public void setMemberUid(String memberUid) {
        this.MemberUid = memberUid;
    }

}
