package com.example.page2x_200514_mj;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

public class Page2_X_Adapter  extends RecyclerView.Adapter<Page2_X_Adapter.ViewHolder> {
    private String[] stay = new String[100];  // 하트의 클릭 여부


    private Context context;
    private List<Page2_X_Main.Recycler_item> items;  //리사이클러뷰 안에 들어갈 값 저장
    private Page2_X_Interface mCallback;


    //메인에서 불러올 때, 이 함수를 씀
    public Page2_X_Adapter(Context context, List<Page2_X_Main.Recycler_item> items, Page2_X_Interface mCallback) {
        this.context=context;
        this.items=items;   //리스트
        this.mCallback=mCallback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.page2_x_cardview,null);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Page2_X_Main.Recycler_item item=items.get(position);

        //api 주제를 분류해서 타입에 넣어줌
        if((items.get(position).getContenttypeid().equals("12"))  && (items.get(position).getCat1().equals("A01"))){
            holder.type.setText("자연");
        }
        else if((items.get(position).getContenttypeid().equals("12"))  && items.get(position).getCat1().equals("A02") && items.get(position).getCat2().equals("A0201") ){
            holder.type.setText("역사");
        }
        else if((items.get(position).getContenttypeid().equals("12"))  &&  items.get(position).getCat1().equals("A02")  && items.get(position).getCat2().equals("A0202")){
            holder.type.setText("휴양");
        }
        else if((items.get(position).getContenttypeid().equals("12"))  && items.get(position).getCat1().equals("A02")  && items.get(position).getCat2().equals("A0203")){
            holder.type.setText("체험");
        }
        else if((items.get(position).getContenttypeid().equals("12")) &&  items.get(position).getCat1().equals("A02") && items.get(position).getCat2().equals("A0204")){
            holder.type.setText("산업");
        }
        else if((items.get(position).getContenttypeid().equals("12"))  && items.get(position).getCat1().equals("A02")  && items.get(position).getCat2().equals("A0205")){
            holder.type.setText("건축/조형");
        }
        else if((items.get(position).getContenttypeid().equals("14"))  &&  items.get(position).getCat1().equals("A02")  && items.get(position).getCat2().equals("A0206")){
            holder.type.setText("문화");
        }
        else if((items.get(position).getContenttypeid().equals("28")) &&  items.get(position).getCat1().equals("A03") ){
            holder.type.setText("레포츠");
        }
        else
            holder.type.setText("");


        //이미지뷰에 url 이미지 넣기.
        Glide.with(context).load(item.getImage()).centerCrop().into(holder.image);
        holder.title.setText(item.getTitle());


        //하트누르면 내부 데이터에 저장
        holder.heart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(stay[position]==null){
                    holder.heart.setBackgroundResource(R.drawable.ic_heart_on);
                    mCallback.make_db(item.getContentviewID(), item.getTitle());   //countId랑 title을 db에 넣으려고 함( make_db라는 인터페이스 이용)
                    mCallback.make_dialog();                                       //db에 잘 넣으면 띄우는 다이얼로그(위와 마찬가지로 인터페이스 이용
                    stay[position] = "ON";

                } else{
                    holder.heart.setBackgroundResource(R.drawable.ic_icon_addmy);
                    stay[position] = null;
                    Toast.makeText(context,"관심관광지를 취소했습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //여기에 리스트를 클릭하면, 관광지 상세페이지로 넘어가는거 구현
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,item.getContentviewID(),Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, Page3_1_X_X.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
            }
        });


        //맵 띄우는 버튼 -> x, y좌표 전달 + 맵을 위에서 끌어내림
        holder.pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,item.getContentviewID()+"------"+item.getMapx()+"------"+item.getMapy(),Toast.LENGTH_SHORT).show();

                //맵을 위에서 끌어내리는 부분
                AppBarLayout appBarLayout=(AppBarLayout)((Page2_X_Main)v.getContext()).findViewById(R.id.app_bar);
                appBarLayout.setExpanded(true);

                //터치된 해당 관광지 좌표 전달
                double x = Double.parseDouble(item.getMapx());
                double y = Double.parseDouble(item.getMapy());

                if(mCallback!=null){
                    mCallback.onClick(x,y, item.getTitle());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CardView cardview;
        Button heart;
        Button pin;
        TextView type;


        public ViewHolder(View itemView) {
            super(itemView);
            heart = (Button)itemView.findViewById(R.id.cardview_heart);
            image=(ImageView)itemView.findViewById(R.id.image);
            title=(TextView)itemView.findViewById(R.id.title);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
            pin=(Button)itemView.findViewById(R.id.cardview_pin);
            type = itemView.findViewById(R.id.cardview_type);

        }
    }

}