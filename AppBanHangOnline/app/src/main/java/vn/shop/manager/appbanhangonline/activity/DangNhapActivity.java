package vn.shop.manager.appbanhangonline.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.shop.manager.appbanhangonline.R;
import vn.shop.manager.appbanhangonline.retrofit.ApiBanHang;
import vn.shop.manager.appbanhangonline.retrofit.RetrofitClient;
import vn.shop.manager.appbanhangonline.utils.Utils;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki, txtresetpass;
    EditText email, pass;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    // tạo trạng thái
    // bước 1 của tự login
    boolean isLogin = false;
    //Fiire base
    //FirebaseAuth firebaseAuth;
    //FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();


    }

    private void initControl() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chuyển màn hình
                Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });

        txtresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
                startActivity(intent);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(str_pass)){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Mật khẩu", Toast.LENGTH_LONG).show();
                }else{
                    //lưu save dữ liệu viết lên
                    //bước 2
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);

                    if(user != null){
                        // user da co dang nhap firebase
                        //dangnhap
                        dangNhap(str_email,str_pass);
                    }else{
                        // user da signout
                        firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                                .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            dangNhap(str_email,str_pass);
                                            Toast.makeText(DangNhapActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                                            //startActivity(new Intent(DangNhapActivity.this, MainActivity.class));
                                        }
                                    }
                                });

                    }


                    }
            }
        });
    }

    private void initView() {
        // khởi tạo thư viện đã import
        // bước 1
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        // bước 2 firebase
       // firebaseAuth = FirebaseAuth.getInstance();
       // user = firebaseAuth.getCurrentUser();
        // đọc dữ lieu
        // bước 3
        if(Paper.book().read("email") != null && Paper.book().read("pass") != null ){
            // tự điền
            email.setText(Paper.book().read("email") );
            pass.setText(Paper.book().read("pass") );


         // bước 3 của tự động login
            if(Paper.book().read("islogin") !=null){
                boolean flag = Paper.book().read("islogin");
                if(flag){
                    // tự động đăng nhập
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // dangNhap(Paper.book().read("email"),Paper.book().read("pass"));
                        }
                    },1000);
                }
            }


        }

    }

    private void dangNhap(String email, String pass) {

        compositeDisposable.add(apiBanHang.dangNhap(email,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                // bước 2 của tự login
                                isLogin = true;
                                Paper.book().write("islogin", isLogin);

                                //dang nhap khi có ghi nhớ săn
                                // bước 1
                               Paper.book().write("user", userModel.getResult().get(0));
                                Utils.user_current = userModel.getResult().get(0);

                               // lưu thông tin người dùng
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null  ){
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}