package vn.shop.manager.appbanhangonline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import vn.shop.manager.appbanhangonline.R;

public class ThongTinActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView Thongtin,Diachi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin);
        initView();
        ActionToolBar();
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        Thongtin = findViewById(R.id.Thongtin);
        Diachi = findViewById(R.id.Diachi);
    }
}