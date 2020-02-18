package org.androidtown.sns_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.Memberinfo;
import org.androidtown.sns_project.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MemberInfoAdapter extends RecyclerView.Adapter<MemberInfoAdapter.MembersHolder>{

    Context context;
    List<Memberinfo> memberinfoList;

    public MemberInfoAdapter(Context context, List<Memberinfo> memberinfoList) {
        this.context = context;
        this.memberinfoList = memberinfoList; //userList
    }

    @NonNull
    @Override
    public MembersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) { // viewHolder를 상속 받아서 리사이클러뷰에 Adapter를 표현하기 위한 Layout설정

        View view = LayoutInflater.from(context).inflate(R.layout.item_memberlist,viewGroup,false);


        return new MembersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersHolder membersHolder, int position) { // 리사이클러뷰에 표현해줄 데이터를 묶어주는 곳
                                        //MyHolder myHolder
        //get data
        String memberProfileImage=memberinfoList.get(position).getphotoUrl();
        final String memberProfileName=memberinfoList.get(position).getName();
        String memberProfileIntroduce=memberinfoList.get(position).getIntroduce();

        //set data // MembersHolder membersHolder 에서 레이아웃 값 받아서 데이터를 셋팅해줌
        membersHolder.members_name_TextView.setText(memberProfileName);
        membersHolder.members_introduce_TextView.setText(memberProfileIntroduce);
        try{
            Picasso.get().load(memberProfileImage).placeholder(R.drawable.unnamed).into(membersHolder.members_CircularImageView);
        } catch (Exception e){

        }

        //handle item click
        //Holder에서 레이아웃(item_memberList)을 연결시켜줌
        membersHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,""+memberProfileName, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return memberinfoList.size();
    }



    class MembersHolder extends RecyclerView.ViewHolder{ // item_memberlist 의 뷰를 홀더에 지정해줌 (레이아웃의 id를 통해 지정해주는 역할)
                                                        // --> 나중에 holder 는 리사이클러뷰의 Adapter에 사용되는데 Adapter에서 뷰를 표현해주는 역할을 해줌

        ImageView members_CircularImageView; // mAvatarIv
        TextView members_name_TextView,members_introduce_TextView; // mNameTv,mEmailTv


        public MembersHolder(@NonNull View itemView){

            super(itemView);

            members_CircularImageView=itemView.findViewById(R.id.members_CircularImageView);
            members_name_TextView=itemView.findViewById(R.id.members_name_TextView);
            members_introduce_TextView=itemView.findViewById(R.id.members_introduce_TextView);

        }

    }


}
