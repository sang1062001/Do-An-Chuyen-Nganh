package vn.shop.manager.appbanhangonline.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import io.paperdb.Paper;
import vn.shop.manager.appbanhangonline.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);
        Thread thread = new Thread(){
            public  void run(){
                 try {
                    sleep(2000);
                 }catch (Exception ex){

                 }finally {
                     // bước 2 của khi có ghi nhớ sẵn
                     // nếu thông tin người dùng chưa có thì vào màn hình đăng nhập
                     if(Paper.book().read("user") == null){
                         Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                         startActivity(intent);
                         finish();
                         // nếu có thông tin rồi thì đưa vào màn hình chính
                     }else {
                         Intent home = new Intent(getApplicationContext(), MainActivity.class);
                         startActivity(home);
                         finish();

                     }





                 }
            }
        };
        thread.start();
    }
}