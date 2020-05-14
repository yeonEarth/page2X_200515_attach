package com.example.page2x_200514_mj;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import databases.DbOpenHelper;
import databases.Heart_page;

public class Page2_X_Main extends AppCompatActivity implements Page2_X_Interface{

    //역 이름을 받아서 지역코드랑 시군구코드 받기 위한 배열(현재 3개 지역만 넣어놔서 배열크기가 3임)
    int station_code = 6;
    String[] arr_line = null;
    String[] _name = new String[station_code];           //txt에서 받은 역이름
    String[] _areaCode = new String[station_code];       //txt에서 받은 지역코드
    String[] _sigunguCode = new String[station_code];    //txt에서 받은 시군구코드
    String[] _x = new String[station_code];              //txt에서 받은 x좌표
    String[] _y = new String[station_code];              //txt에서 받은 y좌표
    String[] _benefitURL = new String[station_code];     //txt에서 받은 혜택url
    String st_name, areaCode, sigunguCode, benefitURL;            //전달받은 역의 지역코드, 시군구코드, 혜택URL
    Double x, y;                                         //전달받은 역의 x,y 좌표


    //returnResult를 줄바꿈 단위로 쪼개서 넣은 배열
    String name_1[];

    //name_1를 "  " 단위로 쪼개서 넣은 배열
    String name_2[] = new String[5];

    //api 관련
    int page = 1;                                        //api 페이지 수
    String returnResult, url;
    String Url_front = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=";
    String contentTypeId = "12", cat1 = "", cat2 = "";
    boolean isLoadData = true;
    ProgressBar progressBar;
    Button category_selected_btn, gift;
    ScrollView scrollView;
    ProgressDialog asyncDialog;

    //레이아웃 관련
    AppBarLayout appBarLayout;
    MapView mapView;
    MapPOIItem marker;
    ViewGroup mapViewContainer;
    Button reset_btn;

    //리사이클러뷰 관련
    RecyclerView recyclerView;
    Page2_X_Adapter adapter;
    private DbOpenHelper mDbOpenHelper;
    List<Recycler_item> items = new ArrayList<>();



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2_x_main);

        //데이터베이스 관련
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();


        //맵 관련
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.map);


        //그 외 객체연결
        gift = (Button) findViewById(R.id.gift);
        category_selected_btn= (Button)findViewById(R.id.btn);
        scrollView = (ScrollView)findViewById(R.id.selected_category_btn);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        asyncDialog= new ProgressDialog( this);
        adapter = new Page2_X_Adapter(getApplicationContext(), items, this);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    //Log.d("접혔어!!!!", "접혔어!!");
//                    page3_1_x_region.setVisibility(View.VISIBLE);
//                    benefit.setVisibility(View.VISIBLE);
//                    benefit_url.setVisibility(View.VISIBLE);
                } else if (verticalOffset == 0) {
                    // Expanded
                    //Log.d("확장됐어!!", "확장쓰!!");
                } else {
                    // Somewhere in between
                    //Log.d("중간이야!!!", "중간이야!!!!");
//                    page3_1_x_region.setVisibility(View.INVISIBLE);
//                    benefit.setVisibility(View.INVISIBLE);
//                    benefit_url.setVisibility(View.INVISIBLE);
                }
            }
        });



        //앞 액티비티에서 값 전달받아서 서치 텍스트에 넣기
        Intent intent = getIntent();
        st_name = intent.getStringExtra("st_name");
        TextView search_name = (TextView) findViewById(R.id.search_name);
        search_name.setText(st_name);


        //txt 값 읽기
        settingList();


        //전달된 역의 지역코드, 시군구코드 찾기
        compareStation();


        //혜택 버튼을 누르면
        gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //혜택이 없을 경우
                if(benefitURL.equals("혜택없음")){
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Page2_X_Main.this);
                    alertDialogBuilder .setMessage(st_name + "역은 혜택이 없습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick( DialogInterface dialog, int id) {
                                    // 프로그램을 종료한다
                                   dialog.cancel(); } });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialogBuilder.show();
                }

                //있을 경우, url로 연결해준다.
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(benefitURL));
                    startActivity(intent);
                }
            }
        });



        //세로 드래그 문제를 해결하기 위한 부분
        //https://do-dam.tistory.com/entry/CoordinatorLayout-App-Bar-%EB%93%9C%EB%9E%98%EA%B7%B8-%EB%B9%84%ED%99%9C%EC%84%B1%ED%99%94-%EC%83%81%EB%8B%A8-%EC%8A%A4%ED%81%AC%EB%A1%A4-%EA%B5%AC%ED%98%84
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        if (appBar.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }


        //맵뷰
        mapView = new MapView(this);
        mapView.setClickable(false);
        mapViewContainer.addView(mapView,0);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(y, x), 8, true);
        marker = new MapPOIItem();


        //리사이클러뷰 구현 부분
        recyclerView = (RecyclerView) findViewById(R.id.page2_X_recyclerview);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);


        //지도가 확대되지 않도록 함
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        //맵 리셋버튼
        reset_btn = (Button)findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.566297, 126.977946), 8, true);
            }
        });



        //관광 api 연결 부분
        settingAPI_Data();

        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("데이터 로딩중입니다.");



        //more loading
        final NestedScrollView nestedScrollView = (NestedScrollView)findViewById(R.id.nestScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int  visibleItemCount,  totalItemCount, pastVisiblesItems;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(v.getChildAt(v.getChildCount() -1) != null) {
                    if( (scrollY >= (v.getChildAt(v.getChildCount() -1).getMeasuredHeight() -  v.getMeasuredHeight() )) && scrollY > oldScrollY) {

                        visibleItemCount = gridLayoutManager.getChildCount();
                        totalItemCount = gridLayoutManager.getItemCount() ;
                        pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                        //받아온 api 개수가 20개가 안되면 다음 페이지가 없다고 판단. false로 바꿔줌
                        if(name_1.length < 20){
                            isLoadData = false;
                        }

                        //isLoadData가 true이면
                        if(isLoadData) {
                            if( (visibleItemCount + pastVisiblesItems) >= totalItemCount ){
                                //Toast.makeText(getApplicationContext(), "됐다", Toast.LENGTH_SHORT).show();
                                page++;

                                //관광 api 연결 부분
                                settingAPI_Data();

                                //메시지 갱신 위치
                                adapter.notifyDataSetChanged();
                            }
                        }

                        //데이터가 더 없을 때
                        else {
                            noData_Dialog();
                        }
                    }
                }
            }
        });


        //카테고리 버튼 누르면
        final Button category_btn = (Button)findViewById(R.id.category_btn);
        category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Page2_X_CategoryBottom category_bottomsheet = new Page2_X_CategoryBottom();
                category_bottomsheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottom);
                category_bottomsheet.show(getSupportFragmentManager(), "category");
            }
        });


        //카테고리에서 선택된 타입을 버튼화
        category_selected_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_selected_btn.getLayoutParams().height = 0;
                category_btn.requestLayout();
            }
        });


    }



    //api 연결 후 값 정제
    private void settingAPI_Data(){
        SearchTask task = new SearchTask();
        try {
            String RESULT = task.execute().get();
            Log.i("전달 받은 값", RESULT);


            //사진링크, 타이틀(관광명), 분야뭔지 분리
            name_1 = RESULT.split("\n");
            int length = name_1.length;
            //xml 파싱한 값을 분류해서 쪼개 넣음
            String name[] = new String[length];        //관광지 이름
            String img_Url[] = new String[length];     //이미지 URL
            String contentid[] = new String[length];   //관광지ID
            String mapx[] = new String[length];        //X좌표
            String mapy[] = new String[length];        //Y좌표
            String cat1[] = new String[length];
            String cat2[] = new String[length];
            String contenttypeid[] = new String[length];

            for (int i = 0; i < length; i++) {
                name_2 = name_1[i].split("  ");

                //img_Url이 없는 경우도 있기 때문에, 길이=5=있음/ 길이=4=없음
                if (name_2.length == 8) {
                    cat1[i] = name_2[0];
                    cat2[i] = name_2[1];
                    contentid[i] = name_2[2];
                    contenttypeid[i] = name_2[3];
                    img_Url[i] = name_2[4];
                    mapx[i] = name_2[5];
                    mapy[i] = name_2[6];
                    name[i] = name_2[7];
                } else {
                    cat1[i] = name_2[0];
                    cat2[i] = name_2[1];
                    contentid[i] = name_2[2];
                    contenttypeid[i] = name_2[3];
                    img_Url[i] = null;
                    mapx[i] = name_2[4];
                    mapy[i] = name_2[5];
                    name[i] = name_2[6];
                }
            }

            Recycler_item[] item = new Recycler_item[length];
            for (int i = 0; i < length; i++) {
                item[i] = new Recycler_item(Url_front + img_Url[i], name[i], contentid[i], mapx[i], mapy[i] ,cat1[i], cat2[i], contenttypeid[i]);

                //마커 많이 만들기
                double X = Double.parseDouble(mapx[i]);
                double Y = Double.parseDouble(mapy[i]);
                marker.setTag(1);
                marker.setItemName(name[i]);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Y, X));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(marker);
            }
            for (int i = 0; i < length; i++) {
                items.add(item[i]);
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }


    //txt 돌려 역 비교할 배열 만들기(이름 지역코드 동네코드)<-로 구성
    private void settingList(){

        String readStr = "";
        AssetManager assetManager = getResources().getAssets();
        InputStream inputStream = null;

        try{
            inputStream = assetManager.open("station_code.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while (((str = reader.readLine()) != null)){ readStr += str + "\n";}
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] arr_all = readStr.split("\n"); //txt 내용을 줄바꿈 기준으로 나눈다.

        //한 줄의 값을 띄어쓰기 기준으로 나눠서, 역명/지역코드/시군구코드 배열에 넣는다.
        for(int i=0; i <arr_all.length; i++) {
            arr_line = arr_all[i].split(" ");

            _name[i] = arr_line[0];         //서울
            _areaCode[i] = arr_line[1];     //1
            _sigunguCode[i] = arr_line[2];  //0
            _y[i] = arr_line[3];            //y좌표
            _x[i] = arr_line[4];            //x좌표
            _benefitURL[i] = arr_line[5];
        }
    }


    //앞 액티비티에서 선택된 역과 같은 역을 찾는다.
    private void compareStation(){
        for(int i=0; i<_name.length; i++){
            if(st_name.equals(_name[i])){
                areaCode = _areaCode[i];
                sigunguCode = _sigunguCode[i];
                y = Double.parseDouble(_y[i]);
                x = Double.parseDouble(_x[i]);
                benefitURL = _benefitURL[i];
            }
        }
    }


    @Override
    public void onData(ArrayList<Page2_X_CategoryBottom.Category_item> list) {

        //선택된 타입을 보여준다.
//        category_selected_btn.getLayoutParams().height = 200;
//        category_selected_btn.requestLayout();


        //기존의 api 값을 지운다.
        items.clear();


        for(int p=0; p < list.size(); p++){
            contentTypeId = list.get(p).getContentId();
            cat1 = list.get(p).getCat1();
            cat2 = list.get(p).getCat2();


            //관광 api 연결 부분
           settingAPI_Data();
        }
        adapter.notifyDataSetChanged();


    }


    //이 클래스는 어댑터와 서로 주고받으며 쓰는 클래스임
    public class Recycler_item {
        String image;
        String title;
        String contentviewID;
        String mapx;
        String mapy;
        String cat1;
        String cat2;
        String contenttypeid;

        String getImage() {
            return this.image;
        }

        String getTitle() {
            return this.title;
        }

        String getContentviewID() {
            return this.contentviewID;
        }

        String getMapx() {
            return this.mapx;
        }

        String getMapy() {
            return this.mapy;
        }

        public String getCat1() {
            return cat1;
        }

        public String getCat2() {
            return cat2;
        }

        public String getContenttypeid() {
            return contenttypeid;
        }

        public Recycler_item(String image, String title, String contentviewID, String mapx, String mapy, String cat1, String cat2, String contenttypeid) {
            this.image = image;
            this.title = title;
            this.contentviewID = contentviewID;
            this.mapx = mapx;
            this.mapy = mapy;
            this.cat1 = cat1;
            this.cat2 = cat2;
            this.contenttypeid = contenttypeid;
        }
    }


    //관광api 연결
    class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            //초기화 단계에서 사용
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("시작", "시작");


            //시군구코드가 0 일 때와 0이 아닐때를 구분해서 url을 넣어준다.
            if(sigunguCode.equals("0")){
                url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
                        "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                        "&pageNo=" + page +
                        "&numOfRows=20&MobileApp=AppTest&MobileOS=ETC&arrange=B" +
                        "&contentTypeId=" + contentTypeId +
                        "&sigunguCode=" +
                        "&areaCode=" + areaCode +
                        "&cat1=" + cat1 +
                        "&cat2=" + cat2 +
                        "&cat3=" +
                        "&listYN=Y";
            } else {
                url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
                        "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                        "&pageNo=" + page +
                        "&numOfRows=20&MobileApp=AppTest&MobileOS=ETC&arrange=B" +
                        "&contentTypeId=" + contentTypeId +
                        "&sigunguCode=" + sigunguCode +
                        "&areaCode=" + areaCode +
                        "&cat1=" + cat1 +
                        "&cat2=" + cat2 +
                        "&cat3=" +
                        "&listYN=Y";
            }
//
//            String url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
//                    "&pageNo=1&numOfRows=10&MobileApp=AppTest&MobileOS=ETC&arrange=A&contentTypeId=12&sigunguCode=&areaCode=1&listYN=Y";

            URL xmlUrl;
            returnResult = "";
            String re = "";

            try {
                boolean title = false;
                boolean firstimage = false;
                boolean item = false;
                boolean contentid = false;
                boolean mapx = false;
                boolean mapy = false;
                boolean cat1 = false;
                boolean cat2 = false;
                boolean contenttypeid = false;

                xmlUrl = new URL(url);
                Log.d("url", url);
                xmlUrl.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlUrl.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }
                            if (parser.getName().equals("cat1")) {
                                cat1 = true;
                            }
                            if (parser.getName().equals("cat2")) {
                                cat2 = true;
                            }
                            if (parser.getName().equals("contentid")) {
                                contentid = true;
                                Log.d("태그 시작", "태그 시작2");
                            }
                            if (parser.getName().equals("contenttypeid")) {
                                contenttypeid = true;
                            }
                            if (parser.getName().equals("mapx")) {
                                mapx = true;
                            }
                            if (parser.getName().equals("mapy")) {
                                mapy = true;
                            }
                            if (parser.getName().equals("firstimage")) {
                                firstimage = true;
                                Log.d("태그 시작", "태그 시작3");
                            }
                            if (parser.getName().equals("title")) {
                                title = true;
                                Log.d("태그 시작", "태그 시작4");
                            }
                            break;
                        }

                        case XmlPullParser.TEXT: {
                            if (cat1) {
                                returnResult += parser.getText() + "  ";
                                cat1 = false;
                            }
                            if (cat2) {
                                returnResult += parser.getText() + "  ";
                                cat2 = false;
                            }
                            if (contenttypeid) {
                                returnResult += parser.getText() + "  ";
                                contenttypeid = false;
                            }
                            if (contentid) {
                                returnResult += parser.getText() + "  ";
                                contentid = false;
                            }
                            if (mapx) {
                                returnResult += parser.getText() + "  ";
                                mapx = false;
                            }
                            if (mapy) {
                                returnResult += parser.getText() + "  ";
                                mapy = false;
                            }
                            if (firstimage) {
                                returnResult += parser.getText() + "  ";
                                firstimage = false;
                            }
                            if (title) {
                                returnResult += parser.getText() + "\n";
                                Log.d("태그 받음", "태그받음4");
                                title = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("err", "erro");
            }
            return returnResult;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    //지도 아이콘을 누르면
    @Override
    public void onClick(double x, double y, String name) {
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(y, x), 3, true);
        marker.setTag(1);
        marker.setItemName(name);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(y, x));
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
    }


    //인터페이스 부분/ db에 넣는다.
    @Override
    public void make_db(String countId, String name) {
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(countId, name);
        mDbOpenHelper.close();
    }


    //인터페이스
    @Override
    public void make_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("관심관광지 추가 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //관심관광지 페이지로 감
                Intent intent = new Intent(Page2_X_Main.this, Heart_page.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    
    //로딩할 데이터가 더이상 없을 때
    public void noData_Dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("마지막 데이터 입니다.");
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }



}
