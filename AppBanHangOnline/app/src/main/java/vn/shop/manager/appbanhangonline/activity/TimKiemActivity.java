package vn.shop.manager.appbanhangonline.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.shop.manager.appbanhangonline.R;
import vn.shop.manager.appbanhangonline.adapter.DienThoaiAdapter;
import vn.shop.manager.appbanhangonline.adapter.LapTopAdapter;
import vn.shop.manager.appbanhangonline.model.SanPhamMoi;
import vn.shop.manager.appbanhangonline.retrofit.ApiBanHang;
import vn.shop.manager.appbanhangonline.retrofit.RetrofitClient;
import vn.shop.manager.appbanhangonline.utils.Utils;

public class TimKiemActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText edtsearch;
    DienThoaiAdapter adapterDt;
    LapTopAdapter lapTopAdapter;
    List<SanPhamMoi> sanPhamMoiList;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem);
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
        sanPhamMoiList = new ArrayList<>();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        edtsearch = findViewById(R.id.edtsearch);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    sanPhamMoiList.clear();
                    adapterDt = new DienThoaiAdapter(getApplicationContext(),sanPhamMoiList);
                    recyclerView.setAdapter(adapterDt);
                    lapTopAdapter = new LapTopAdapter(getApplicationContext(),sanPhamMoiList);
                    recyclerView.setAdapter(lapTopAdapter);
                }
                else {
                    getDataSearch(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    private void getDataSearch(String s) {
        sanPhamMoiList.clear();
       // String str_timkiem = edtsearch.getText().toString().trim();
        compositeDisposable.add(apiBanHang.timKiem(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   sanPhamMoiModel -> {
                       if (sanPhamMoiModel.isSuccess()){
                           sanPhamMoiList = sanPhamMoiModel.getResult();
                           adapterDt = new DienThoaiAdapter(getApplicationContext(),sanPhamMoiList);
                           recyclerView.setAdapter(adapterDt);
                           lapTopAdapter = new LapTopAdapter(getApplicationContext(),sanPhamMoiList);
                           recyclerView.setAdapter(lapTopAdapter);
                       }
                   },
                   throwable -> {
                       Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                   }
                ));



    }




    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}