package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.adapter.EntertainmentAdapter;
import org.androidtown.sns_project.adapter.HealthAdapter;
import org.androidtown.sns_project.adapter.SearchAdapter;
import org.androidtown.sns_project.adapter.SportsAdapter;
import org.androidtown.sns_project.adapter.TechnologyAdapter;
import org.androidtown.sns_project.object.NewsData_business;
import org.androidtown.sns_project.object.NewsData_entertainment;
import org.androidtown.sns_project.object.NewsData_health;
import org.androidtown.sns_project.object.NewsData_sports;
import org.androidtown.sns_project.object.NewsData_technology;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";// 로그찍을때 태그

    private RecyclerView recyclerView_business;
    private RecyclerView recyclerView_entertainment;
    private RecyclerView recyclerView_health;
    private RecyclerView recyclerView_sports;
    private RecyclerView recyclerView_technology;

    private RecyclerView.LayoutManager layoutManager_business;
    private RecyclerView.LayoutManager layoutManager_entertainment;
    private RecyclerView.LayoutManager layoutManager_health;
    private RecyclerView.LayoutManager layoutManager_sports;
    private RecyclerView.LayoutManager layoutManager_technology;

    private RecyclerView.Adapter mAdapter_business,mAdapter_entertainment,mAdapter_health,mAdapter_sports,mAdapter_technology;


    // Instantiate the RequestQueue.
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);                                     //탭바================================
        tabHost1.setup();

        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.미정1);
        ts1.setIndicator("비즈");
        tabHost1.addTab(ts1);

        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.미정2);
        ts2.setIndicator("엔티");
        tabHost1.addTab(ts2);

        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.미정3);
        ts3.setIndicator("헬스");
        tabHost1.addTab(ts3);

        TabHost.TabSpec ts4 = tabHost1.newTabSpec("Tab Spec 4");
        ts4.setContent(R.id.미정4);
        ts4.setIndicator("스포츠");
        tabHost1.addTab(ts4);

        TabHost.TabSpec ts5 = tabHost1.newTabSpec("Tab Spec 5");
        ts5.setContent(R.id.미정5);
        ts5.setIndicator("테크");
        tabHost1.addTab(ts5);

        Log.i("MY", "--homeActivity:onCreate--");                                            //탭바================================

        recyclerView_business = (RecyclerView) findViewById(R.id.search_recyclerview_business);
        recyclerView_entertainment = (RecyclerView) findViewById(R.id.search_recyclerview_entertainment);
        recyclerView_health = (RecyclerView) findViewById(R.id.search_recyclerview_health);
        recyclerView_sports = (RecyclerView) findViewById(R.id.search_recyclerview_sports);
        recyclerView_technology = (RecyclerView) findViewById(R.id.search_recyclerview_technology);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView_business.setHasFixedSize(true);
        recyclerView_entertainment.setHasFixedSize(true);
        recyclerView_health.setHasFixedSize(true);
        recyclerView_sports.setHasFixedSize(true);
        recyclerView_technology.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager_business = new LinearLayoutManager(this);
        layoutManager_entertainment = new LinearLayoutManager(this);
        layoutManager_health = new LinearLayoutManager(this);
        layoutManager_sports = new LinearLayoutManager(this);
        layoutManager_technology = new LinearLayoutManager(this);

        recyclerView_business.setLayoutManager(layoutManager_business);
        recyclerView_entertainment.setLayoutManager(layoutManager_entertainment);
        recyclerView_health.setLayoutManager(layoutManager_health);
        recyclerView_sports.setLayoutManager(layoutManager_sports);
        recyclerView_technology.setLayoutManager(layoutManager_technology);


        //1. 화면이 로딩 --> 뉴스 정보를 받아온다.
        queue = Volley.newRequestQueue(this);
        //2. 정보 --> 어댑터 넘겨준다.
        //3. 어댑터 --> 셋팅
        getNews_business();
        getNews_entertainment();
        getNews_health();
        getNews_sports();
        getNews_technology();


        ////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.search_navi);// res - menu - item이름
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.search_navi :



                        return true;

                    case R.id.home_navi :

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.profile_navi :

                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.memberlist_navi:

                        startActivity(new Intent(getApplicationContext(), MemberlistActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.chatlist_navi:

                        startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                        overridePendingTransition(0, 0);

                        return true;


                }
                return false;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume/ 다른 보임");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    public void getNews_business(){
        Log.v(TAG, "getNews함수 시작");

        String url ="https://newsapi.org/v2/top-headlines?country=kr&category=business&apiKey=e56f453d48cd436aa4d0ddbc48e17b54";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //2. 정보 --> 어댑터 넘겨준다.
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.v(TAG, "jsonObject "+ jsonObject);

                            JSONArray arrayArticles = jsonObject.getJSONArray("articles");

                            Log.v(TAG, "arrayArticles 받아옴 "+ arrayArticles);

                            // url을 통해 받아온 데이터를 리사이클러부 어뎁터에게 넘겨준다.
                            List<NewsData_business> newsData_business=new ArrayList<>();

                            for(int i=0; i<arrayArticles.length(); i++){
                                JSONObject object=arrayArticles.getJSONObject(i);

                                NewsData_business newsData_business1=new NewsData_business();
                                newsData_business1.setTitle(object.getString("title"));
                                newsData_business1.setDescription(object.getString("description"));
                                newsData_business1.setUrlToImage(object.getString("urlToImage"));
                                newsData_business1.setUrl(object.getString("url"));

                                Log.v(TAG, "object.getString(\"title\") :  "+ object.getString("title"));
                                Log.v(TAG, "object.getString(\"description\"):  "+ object.getString("description"));
                                Log.v(TAG, "object.getString(\"urlToImage\") :  "+ object.getString("urlToImage"));
                                Log.v(TAG, "object.getString(\"url\") :  "+ object.getString("url"));

                                newsData_business.add(newsData_business1);

                            }

                            //3. 어댑터 --> 셋팅
                            // specify an adapter (see also next example)
                            mAdapter_business = new SearchAdapter(newsData_business,SearchActivity.this,new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    if(view.getTag() != null){
                                        int position = (int) view.getTag();
                                        String url1=((SearchAdapter)mAdapter_business).getNews(position).getUrl();
                                        Log.v(TAG, "((SearchAdapter)mAdapter).getNews(position).getUrl() : "+ url1);

                                        Intent intent = new Intent(SearchActivity.this,NewsLinkActivity.class);
                                        intent.putExtra("url",url1);

                                        startActivity(intent);

                                    }
                                }
                            });
                            recyclerView_business.setAdapter(mAdapter_business);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getNews_entertainment(){
        Log.v(TAG, "getNews함수 시작");

        String url ="https://newsapi.org/v2/top-headlines?country=kr&category=entertainment&apiKey=e56f453d48cd436aa4d0ddbc48e17b54";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //2. 정보 --> 어댑터 넘겨준다.
                        try {
                            JSONObject jsonObject_entertainment = new JSONObject(response);

                            Log.v(TAG, "jsonObject "+ jsonObject_entertainment);

                            JSONArray arrayArticles_entertainment = jsonObject_entertainment.getJSONArray("articles");

                            Log.v(TAG, "arrayArticles 받아옴 "+ arrayArticles_entertainment);

                            // url을 통해 받아온 데이터를 리사이클러부 어뎁터에게 넘겨준다.
                            List<NewsData_entertainment> newsData_entertainment=new ArrayList<>();

                            for(int i=0; i<arrayArticles_entertainment.length(); i++){
                                JSONObject object=arrayArticles_entertainment.getJSONObject(i);

                                NewsData_entertainment newsData_entertainment1=new NewsData_entertainment();
                                newsData_entertainment1.setTitle(object.getString("title"));
                                newsData_entertainment1.setDescription(object.getString("description"));
                                newsData_entertainment1.setUrlToImage(object.getString("urlToImage"));
                                newsData_entertainment1.setUrl(object.getString("url"));

                                Log.v(TAG, "object.getString(\"title\") :  "+ object.getString("title"));
                                Log.v(TAG, "object.getString(\"description\"):  "+ object.getString("description"));
                                Log.v(TAG, "object.getString(\"urlToImage\") :  "+ object.getString("urlToImage"));
                                Log.v(TAG, "object.getString(\"url\") :  "+ object.getString("url"));

                                newsData_entertainment.add(newsData_entertainment1);
                            }

                            //3. 어댑터 --> 셋팅
                            // specify an adapter (see also next example)
                            mAdapter_entertainment = new EntertainmentAdapter(newsData_entertainment,SearchActivity.this,new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    if(view.getTag() != null){
                                        int position = (int) view.getTag();
                                        String url1= ((EntertainmentAdapter)mAdapter_entertainment).getNews(position).getUrl();
                                        Log.v(TAG, "((SearchAdapter)mAdapter).getNews(position).getUrl() : "+ ((EntertainmentAdapter)mAdapter_entertainment).getNews(position).getUrl());

                                        Intent intent = new Intent(SearchActivity.this,NewsLinkActivity.class);
                                        intent.putExtra("url",url1);

                                        startActivity(intent);

                                    }
                                }
                            });

                            recyclerView_entertainment.setAdapter(mAdapter_entertainment);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void getNews_health(){
        Log.v(TAG, "getNews함수 시작");

        String url ="https://newsapi.org/v2/top-headlines?country=kr&category=health&apiKey=e56f453d48cd436aa4d0ddbc48e17b54";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //2. 정보 --> 어댑터 넘겨준다.
                        try {
                            JSONObject jsonObject_health = new JSONObject(response);

                            Log.v(TAG, "jsonObject "+ jsonObject_health);

                            JSONArray arrayArticles_health = jsonObject_health.getJSONArray("articles");

                            Log.v(TAG, "arrayArticles 받아옴 "+ arrayArticles_health);

                            // url을 통해 받아온 데이터를 리사이클러부 어뎁터에게 넘겨준다.
                            List<NewsData_health> newsData_healths=new ArrayList<>();

                            for(int i=0; i<arrayArticles_health.length(); i++){
                                JSONObject object_health=arrayArticles_health.getJSONObject(i);

                                NewsData_health newsData_health=new NewsData_health();
                                newsData_health.setTitle(object_health.getString("title"));
                                newsData_health.setDescription(object_health.getString("description"));
                                newsData_health.setUrlToImage(object_health.getString("urlToImage"));
                                newsData_health.setUrl(object_health.getString("url"));

                                Log.v(TAG, "object.getString(\"title\") :  "+ object_health.getString("title"));
                                Log.v(TAG, "object.getString(\"description\"):  "+ object_health.getString("description"));
                                Log.v(TAG, "object.getString(\"urlToImage\") :  "+ object_health.getString("urlToImage"));
                                Log.v(TAG, "object.getString(\"url\") :  "+ object_health.getString("url"));

                                newsData_healths.add(newsData_health);
                            }

                            //3. 어댑터 --> 셋팅
                            // specify an adapter (see also next example)
                            mAdapter_health = new HealthAdapter(newsData_healths,SearchActivity.this,new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    if(view.getTag() != null){
                                        int position = (int) view.getTag();
                                        String url1= ((HealthAdapter)mAdapter_health).getNews(position).getUrl();
                                        Log.v(TAG, "((HealthAdapter)mAdapter).getNews(position).getUrl() : "+ ((HealthAdapter)mAdapter_health).getNews(position).getUrl());

                                        Intent intent = new Intent(SearchActivity.this,NewsLinkActivity.class);
                                        intent.putExtra("url",url1);

                                        startActivity(intent);

                                    }
                                }
                            });
                            recyclerView_health.setAdapter(mAdapter_health);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void getNews_sports(){
        Log.v(TAG, "getNews함수 시작");

        String url ="https://newsapi.org/v2/top-headlines?country=kr&category=sports&apiKey=e56f453d48cd436aa4d0ddbc48e17b54";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //2. 정보 --> 어댑터 넘겨준다.
                        try {
                            JSONObject jsonObject_sports = new JSONObject(response);

                            Log.v(TAG, "jsonObject "+ jsonObject_sports);

                            JSONArray arrayArticles_sports = jsonObject_sports.getJSONArray("articles");

                            Log.v(TAG, "arrayArticles 받아옴 "+ arrayArticles_sports);

                            // url을 통해 받아온 데이터를 리사이클러부 어뎁터에게 넘겨준다.
                            List<NewsData_sports> newsData_sports=new ArrayList<>();

                            for(int i=0; i<arrayArticles_sports.length(); i++){
                                JSONObject object_sports=arrayArticles_sports.getJSONObject(i);

                                NewsData_sports newsData_sports1=new NewsData_sports();
                                newsData_sports1.setTitle(object_sports.getString("title"));
                                newsData_sports1.setDescription(object_sports.getString("description"));
                                newsData_sports1.setUrlToImage(object_sports.getString("urlToImage"));
                                newsData_sports1.setUrl(object_sports.getString("url"));

                                Log.v(TAG, "object.getString(\"title\") :  "+ object_sports.getString("title"));
                                Log.v(TAG, "object.getString(\"description\"):  "+ object_sports.getString("description"));
                                Log.v(TAG, "object.getString(\"urlToImage\") :  "+ object_sports.getString("urlToImage"));
                                Log.v(TAG, "object.getString(\"url\") :  "+ object_sports.getString("url"));

                                newsData_sports.add(newsData_sports1);
                            }

                            //3. 어댑터 --> 셋팅
                            // specify an adapter (see also next example)
                            mAdapter_sports = new SportsAdapter(newsData_sports,SearchActivity.this,new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    int position = (int) view.getTag();
                                    String url1= ((SportsAdapter)mAdapter_sports).getNews(position).getUrl();
                                    Log.v(TAG, "((SportsAdapter)mAdapter).getNews(position).getUrl() : "+ ((SportsAdapter)mAdapter_sports).getNews(position).getUrl());

                                    Intent intent = new Intent(SearchActivity.this,NewsLinkActivity.class);
                                    intent.putExtra("url",url1);

                                    startActivity(intent);
                                }
                            });
                            recyclerView_sports.setAdapter(mAdapter_sports);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void getNews_technology(){
        Log.v(TAG, "getNews함수 시작");

        String url = "https://newsapi.org/v2/top-headlines?country=kr&category=technology&apiKey=e56f453d48cd436aa4d0ddbc48e17b54";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //2. 정보 --> 어댑터 넘겨준다.
                        try {
                            JSONObject jsonObject_technology = new JSONObject(response);

                            Log.v(TAG, "jsonObject "+ jsonObject_technology);

                            JSONArray arrayArticles_technology = jsonObject_technology.getJSONArray("articles");

                            Log.v(TAG, "arrayArticles 받아옴 "+ arrayArticles_technology);

                            // url을 통해 받아온 데이터를 리사이클러부 어뎁터에게 넘겨준다.
                            List<NewsData_technology> newsData_technology=new ArrayList<>();

                            for(int i=0; i<arrayArticles_technology.length(); i++){
                                JSONObject object_technology=arrayArticles_technology.getJSONObject(i);

                                NewsData_technology newsData_technology1=new NewsData_technology();
                                newsData_technology1.setTitle(object_technology.getString("title"));
                                newsData_technology1.setDescription(object_technology.getString("description"));
                                newsData_technology1.setUrlToImage(object_technology.getString("urlToImage"));
                                newsData_technology1.setUrl(object_technology.getString("url"));

                                Log.v(TAG, "object.getString(\"title\") :  "+ object_technology.getString("title"));
                                Log.v(TAG, "object.getString(\"description\"):  "+ object_technology.getString("description"));
                                Log.v(TAG, "object.getString(\"urlToImage\") :  "+ object_technology.getString("urlToImage"));
                                Log.v(TAG, "object.getString(\"url\") :  "+ object_technology.getString("url"));

                                newsData_technology.add(newsData_technology1);
                            }

                            //3. 어댑터 --> 셋팅
                            // specify an adapter (see also next example)
                            mAdapter_technology = new TechnologyAdapter(newsData_technology,SearchActivity.this,new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    int position = (int) view.getTag();
                                    String url1= ((TechnologyAdapter)mAdapter_technology).getNews(position).getUrl();
                                    Log.v(TAG, "((TechnologyAdapter)mAdapter).getNews(position).getUrl() : "+ ((TechnologyAdapter)mAdapter_technology).getNews(position).getUrl());

                                    Intent intent = new Intent(SearchActivity.this,NewsLinkActivity.class);
                                    intent.putExtra("url",url1);

                                    startActivity(intent);
                                }
                            });
                            recyclerView_technology.setAdapter(mAdapter_technology);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){

                case R.id.sendPasswordButton:
                    Log.v(TAG, "비번 찾기 버튼 누름");

                    break;

                case R.id.loginbutton2:

                    break;

            }
        }
    };

    private void myStartActivity(Class c){

        Intent intent=new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}


