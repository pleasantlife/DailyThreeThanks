package com.mdy.android.dailythreethanks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mdy.android.dailythreethanks.domain.Data;
import com.mdy.android.dailythreethanks.domain.Memo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextTitle1, editTextTitle2, editTextTitle3;
    EditText editTextContent1, editTextContent2, editTextContent3;
    Button btnWrite, btnGallery;
    TextView txtDate, txtImage;
    ImageView imageView;

    int position = 0;

//    String filePath = "";

    // 데이터베이스
    FirebaseDatabase database;
    DatabaseReference memoRef;

    // 스토리지
    private StorageReference mStorageRef;

    // 프로그래서 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write);
        setViews();

        // 다이얼로그
        dialog = new ProgressDialog(this);
        dialog.setTitle("Upload Data");
        dialog.setMessage("Uploading...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // 데이터베이스 레퍼런스
        database = FirebaseDatabase.getInstance();
        memoRef = database.getReference("memo");

        // 스토리지 레퍼런스
        mStorageRef = FirebaseStorage.getInstance().getReference("images");


        setData();
    }

    public void uploadFile(String filePath){
        // 스마트폰에 있는 파일의 경로
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        // 파이어베이스에 있는 파일 경로
        String fileName = file.getName(); // + 시간값 or UUID 추가해서 만들어준다.
        // 데이터베이스의 키가 값과 동일한 구조 ( 키 = 값 )
        StorageReference fileRef = mStorageRef.child(fileName);

        fileRef.putFile(uri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 파이어베이스 스토리지에 방금 업로드한 파일의 경로
                    @SuppressWarnings("VisibleForTests")
                    Uri uploadedUri = taskSnapshot.getDownloadUrl();
                    afterUploadFile(uploadedUri);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.i("FBStorage", "Upload Fail : " + exception.getMessage());
                }
            });
    }

    public void afterUploadFile(Uri imageUri){
        String title1 = editTextTitle1.getText().toString();
        String title2 = editTextTitle2.getText().toString();
        String title3 = editTextTitle3.getText().toString();

        String content1 = editTextContent1.getText().toString();
        String content2 = editTextContent2.getText().toString();
        String content3 = editTextContent3.getText().toString();

        // 파이어베이스 데이터베이스에 데이터 넣기
        // 1. 데이터 객체 생성
        Memo memo = new Memo(title1, title2, title3, content1, content2, content3);
        memo.date = inputCurrentDate();

        if(imageUri != null){
            memo.fileUriString =imageUri.toString();
        }

        // 2. 입력할 데이터의 키 생성
        String memoKey = memoRef.push().getKey(); // 자동생성된 키를 가져온다.

        // 3. 생성된 키를 레퍼런스로 데이터를 입력
        //   insert와 update, delete 는 동일하게 동작
        memoRef.child(memoKey).setValue(memo);
        // 데이터 입력 후 창 닫기
        dialog.dismiss();
        finish();
    }

    public String inputCurrentDate(){
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        return sdf.format(currentTime);
    }



    private void setViews() {
        editTextTitle1 = (EditText) findViewById(R.id.editTextTitle1);
        editTextTitle2 = (EditText) findViewById(R.id.editTextTitle2);
        editTextTitle3 = (EditText) findViewById(R.id.editTextTitle3);
        editTextContent1 = (EditText) findViewById(R.id.editTextContent1);
        editTextContent2 = (EditText) findViewById(R.id.editTextContent2);
        editTextContent3 = (EditText) findViewById(R.id.editTextContent3);
        btnWrite = (Button) findViewById(R.id.btnModify);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtImage = (TextView) findViewById(R.id.txtImage);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnWrite.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnModify:
                dialog.show();
                String imagePath = txtImage.getText().toString();
                // 이미지가 있으면 이미지 경로를 받아서 저장해야 되기 때문에
                // 이미지를 먼저 업로드 한다.
                if( imagePath != null && !"".equals(imagePath)){
                    uploadFile(imagePath);
                } else {
                    afterUploadFile(null);
                }
                break;
            case R.id.btnGallery :
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // EXTERNAL_CONTENT_URI 에 여러가지가 있는데 그 중에서 이미지들을 가져올 수 있게 해준다.
                // 가. 이미지 선택창 호출
                startActivityForResult( Intent.createChooser(intent, "앱을 선택하세요."), 100);    // 사진앱이 여러개일 경우 선택하게끔 해준다.
        }
    }

    // startActivityForResult()가 끝나면 호출되는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                // 나. 이미지 선택창에서 선택된 이미지의 경로 추출
                case 100:
                    Uri imageUri = data.getData();
                    String filePath = getPathFromUri(this, imageUri);
                    Log.i("Gallery","imageUri========================="+imageUri);
                    txtImage.setText(filePath);
                    Toast.makeText(this, "사진이 등록되었습니다.", Toast.LENGTH_SHORT).show();
//                    imageView.setImageURI(imageUri);
                    break;
            }
        }
    }

    // Uri에서 실제 경로 꺼내는 함수
    public static String getPathFromUri(Context context, Uri uri){
        String realPath = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if( cursor.moveToNext() ){
            realPath = cursor.getString(cursor.getColumnIndex("_data"));
        }
        cursor.close();

        return realPath;
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
            editTextTitle1.setText(memo.title1);
            editTextTitle2.setText(memo.title2);
            editTextTitle3.setText(memo.title3);

            editTextContent1.setText(memo.content1);
            editTextContent2.setText(memo.content2);
            editTextContent3.setText(memo.content3);

//            txtDate.setText(memo.date);

        }
    }
}
