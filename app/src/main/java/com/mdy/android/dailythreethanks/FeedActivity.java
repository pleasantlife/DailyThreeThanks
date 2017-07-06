package com.mdy.android.dailythreethanks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mdy.android.dailythreethanks.domain.Data;
import com.mdy.android.dailythreethanks.domain.Memo;

import java.util.List;

public class FeedActivity extends AppCompatActivity {

    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference memoRef;

    RecyclerView recyclerFeed;
    ImageView imageView;
    Button btnGoList;
    FeedAdatper feedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setViews();

        database = FirebaseDatabase.getInstance();
        memoRef = database.getReference("memo");

        loadFeedData();
    }

    public void loadFeedData(){
        memoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                Data.list.clear();
                for( DataSnapshot item : data.getChildren() ){
                    // json 데이터를 Bbs 인스턴스로 변환오류 발생 가능성 있어서 예외처리 필요
                    try {
                        Memo memo = item.getValue(Memo.class);
                        Data.list.add(memo);
                    } catch (Exception e){
                        Log.e("FireBase", e.getMessage());
                    }
                }
                refreshFeed(Data.list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshFeed(List<Memo> data){
        feedAdapter.setFeedData(data);
        feedAdapter.notifyDataSetChanged();
    }

    private void setViews() {
        recyclerFeed = (RecyclerView) findViewById(R.id.recyclerFeed);
        btnGoList = (Button) findViewById(R.id.btnGoList);
        btnGoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, ListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView = (ImageView) findViewById(R.id.imageFeed);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });
        feedAdapter = new FeedAdatper(this);
        recyclerFeed.setAdapter(feedAdapter);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(this));
    }
}
