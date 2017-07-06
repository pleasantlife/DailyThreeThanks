package com.mdy.android.dailythreethanks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mdy.android.dailythreethanks.domain.Data;
import com.mdy.android.dailythreethanks.domain.Memo;

public class ReadActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txtTitle1, txtTitle2, txtTitle3;
    TextView txtContent1, txtContent2, txtContent3;
    Button btnModify;
    TextView txtDate;
    ImageView imageView;

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_read);
        setViews();

        setData();

    }

    public void setData() {
        // 목록에서 넘어온 position 값을 이용해 상세보기 데이터를 결정
        Intent intent = getIntent();
        // null값 확인
        position = intent.getIntExtra("LIST_POSITION", -1);

        // 값이 있으면
        if (position > -1) {
            Memo memo = Data.list.get(position);

            // 이미지 세팅
            if (memo.fileUriString != null && !"".equals(memo.fileUriString)) {
                Glide.with(this).load(memo.fileUriString).into(imageView);
            }


            // 값 세팅
            txtTitle1.setText(memo.title1);
            txtTitle2.setText(memo.title2);
            txtTitle3.setText(memo.title3);

            txtContent1.setText(memo.content1);
            txtContent2.setText(memo.content2);
            txtContent3.setText(memo.content3);

//            txtDate.setText(memo.date);

        }
    }

    private void setViews() {
        txtTitle1 = (TextView) findViewById(R.id.txtTitle1);
        txtTitle2 = (TextView) findViewById(R.id.txtTitle2);
        txtTitle3 = (TextView) findViewById(R.id.txtTitle3);
        txtContent1 = (TextView) findViewById(R.id.txtContent1);
        txtContent2 = (TextView) findViewById(R.id.txtContent2);
        txtContent3 = (TextView) findViewById(R.id.txtContent3);
        btnModify = (Button) findViewById(R.id.btnModify);
        txtDate = (TextView) findViewById(R.id.txtDate);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnModify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), WriteActivity.class);
        intent.putExtra("LIST_POSITION", position);
        v.getContext().startActivity(intent);
    }
}