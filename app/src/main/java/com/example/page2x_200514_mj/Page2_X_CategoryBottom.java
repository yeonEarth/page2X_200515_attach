package com.example.page2x_200514_mj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class Page2_X_CategoryBottom  extends BottomSheetDialogFragment {

    private Page2_X_Main page2_x_main;
    ListView listView;
    ArrayList<Category_item> list;
    Page2_X_CategoryBottom_Adapter adapter;
    Page2_X_Interface xInterface;


    public static Page2_X_CategoryBottom  getInstance(){
        return new Page2_X_CategoryBottom();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        page2_x_main = (Page2_X_Main) getActivity();

        if(context instanceof Page2_X_Interface){
            xInterface = (Page2_X_Interface) context;
        } else {
            throw new RuntimeException(context.toString() + "오류");
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.page2_x_category_bottomsheet, container, false);

        listView = rootview.findViewById(R.id.listview);
        list = new ArrayList<>();
        list.add(new Category_item("자연", "12", "A01", ""));
        list.add(new Category_item("역사", "12", "A02", "A0201"));
        list.add(new Category_item("휴양", "12", "A02", "A0201"));
        list.add(new Category_item("체험", "14", "A02", "A0206"));
        list.add(new Category_item("산업", "12", "A02", "A0204"));
        list.add(new Category_item("건축/조형", "12", "A02", "A0205"));
        list.add(new Category_item("문화", "14", "A02", "A0206"));
        list.add(new Category_item("레포츠", "28", "A03", ""));

        final boolean[] isSelected = {false, false, false, false, false, false, false, false};

        //리스트뷰 아이템을 누르면
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                if(!isSelected[(Integer) v.getTag()]){
                    v.setSelected(true);
                    isSelected[(Integer) v.getTag()] = true;
                }
                else {
                    v.setSelected(false);
                    isSelected[(Integer) v.getTag()] = false;
                }
            }
        };

        //액티비티로 보낼 값
        final ArrayList<Category_item> sendData = new ArrayList<>();

        //완료버튼 누르면
        Button done_btn = (Button)rootview.findViewById(R.id.category_done_btn);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0; i<isSelected.length; i++){
                    if(isSelected[i]){
                        sendData.add(list.get(i));
                    }
                }

                xInterface.onData(sendData);
                dismiss();
            }
        });


        adapter = new Page2_X_CategoryBottom_Adapter(list, page2_x_main.getApplicationContext() , mOnClickListener);
        listView.setAdapter(adapter);

        return rootview;
    }


    //arraylist값을 구성하는 클래스
    public class Category_item {
        String name;
        String contentId;
        String cat1;
        String cat2;

        public Category_item(String name, String contentId, String cat1, String cat2) {
            this.name = name;
            this.contentId = contentId;
            this.cat1 = cat1;
            this.cat2 = cat2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }



        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

        public String getCat1() {
            return cat1;
        }

        public void setCat1(String cat1) {
            this.cat1 = cat1;
        }

        public String getCat2() {
            return cat2;
        }

        public void setCat2(String cat2) {
            this.cat2 = cat2;
        }


    }
}