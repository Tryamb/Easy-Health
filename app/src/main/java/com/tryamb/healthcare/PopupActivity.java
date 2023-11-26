package com.tryamb.healthcare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class PopupActivity extends Activity {

    TextView txtText;
    ImageView imageView;
    TextView noticeText;

    Bitmap bmp;
    String data;
    String numb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);


        txtText = (TextView)findViewById(R.id.txtText);
        imageView = (ImageView)findViewById(R.id.popup_imageView);
        noticeText = (TextView)findViewById(R.id.notice);

        Intent intent = getIntent();
        data = intent.getStringExtra("data");
        numb = intent.getStringExtra("numb");
        bmp = intent.getParcelableExtra("image");

        noticeText.setText(numb);
        txtText.setText(data);
        imageView.setImageBitmap(bmp);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", 1);
        setResult(RESULT_OK, intent);

        finish();
    }

    public void mOnMoreInfo(View v){
        Intent intent = new Intent();
        intent.putExtra("result", 2);
        intent.putExtra("image", bmp);
        intent.putExtra("numb", numb);
        intent.putExtra("data", data);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Block Android back button
        return;
    }
}
