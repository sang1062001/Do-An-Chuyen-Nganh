package vn.shop.manager.appbanhangonline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.shop.manager.appbanhangonline.R;
import vn.shop.manager.appbanhangonline.adapter.LoaiSpAdapter;
import vn.shop.manager.appbanhangonline.adapter.SanPhamMoiAdapter;
import vn.shop.manager.appbanhangonline.model.LoaiSp;
import vn.shop.manager.appbanhangonline.model.SanPhamMoi;
import vn.shop.manager.appbanhangonline.model.User;
import vn.shop.manager.appbanhangonline.retrofit.ApiBanHang;
import vn.shop.manager.appbanhangonline.retrofit.RetrofitClient;
import vn.shop.manager.appbanhangonline.utils.Utils;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        // kiểm tra khi có ghi nhớ sẵn
        // bước 2
        Paper.init(this);
      if(Paper.book().read("user") !=null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }


        getToken();


        Anhxa();
        ActionBar();

        if(isConnected(this)){

            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(),"khong co internet, vui long ket noi lai",Toast.LENGTH_LONG).show();
        }
    }


    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)){
                                compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(), s)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                messageModel -> {

                                                },
                                                throwable -> {
                                                    Log.d("Log", throwable.getMessage());
                                                }
                                        ));
                        }
                    }
                });
    }








    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiMainActivity.class);
                        // truyen loai
                        dienthoai.putExtra("loai",1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        // sử dụng lại màn hình điện thoại cho laptop
                        Intent laptop = new Intent(getApplicationContext(), LaptopMainActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;
                    case 3:
                        Intent Thongtin = new Intent(getApplicationContext(), ThongTinActivity.class);
                        startActivity(Thongtin);
                        break;
                    case 4:
                       Intent LienHe = new Intent(getApplicationContext(), LienHeActivity.class);
                       startActivity(LienHe);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent Quanli = new Intent(getApplicationContext(), QuanLiActivity.class);
                        startActivity(Quanli);
                        break;
                    case 7 :
                        //Đăng xuất
                        // xóa thong tin user
                        Paper.book().delete("user");

                        FirebaseAuth.getInstance().signOut();

                        Intent quayvedangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(quayvedangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(),mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever"+throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
         compositeDisposable.add(apiBanHang.getLoaiSp()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(
                         loaiSpModel -> {
                             if(loaiSpModel.isSuccess()){
                                 mangloaisp = loaiSpModel.getResult();
                                 mangloaisp.add(new LoaiSp("Quản lý","https://cdn-icons-png.flaticon.com/512/1283/1283187.png"));
                                 //dangxuat
                                 mangloaisp.add(new LoaiSp("Đăng xuất","https://th.bing.com/th/id/OIP.MMitMMoXqKjtDlw__VB_AgHaHa?pid=ImgDet&rs=1"));
                                 loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                 listViewManHinhChinh.setAdapter(loaiSpAdapter);

                             }
                         },
                         throwable -> {
                             Log.d("loggg", throwable.getMessage());
                         }
                 ));

    }

    // hàm chạy quảng cáo
    private void ActionViewFlipper() {
    // tạo 1 mảng chứ đường dẫn hình ảnh và cấp phát bộ nhớ
        List<String> mangquangcao = new ArrayList<>();
        //đưa hình ảnh dạng chuỗi nên cần””
        // có mảng chứa những tấm hình
        // viewflipper chĩ nhân imageview và k nhận dường dẫn nên gắn đường dẫn vào imageview truoc rồi mới gan từ imageview vào viewflipper
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/720-220-720x220-139.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/720-220-720x220-122.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/720-220-720x220-4.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/aseri-720-220-720x220-7.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/reno8-720-220-720x220.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/720-220-720x220-87.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/IP-14--Desktop-380x200.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/380x200-380x200-14.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/MacProdesktop-380x200-2.png");
        mangquangcao.add("https://cdn.tgdd.vn/2022/09/banner/380-x-200--1--380x200-1.png");

        // duyệt qua mảng và lấy ra từng phần tử, i sẽ bé hơn  độ dài của mảng
        for(int i=0; i<mangquangcao.size();i++){
            // khởi tạo imageview
            ImageView imageView = new ImageView(getApplicationContext());
            // sử dụng thư viện để đưa hình ảnh vào từ đường dẫn url , truyền vào dạng load url trong mảng và gọi lại mangquangcao, get biến i , into là đổ vào imageview
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            // muốn imageview căn vừa với viewflipper và k bị cắt mất hình là thuộc tính setscaletype
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            // sau khi có đủ dữ liệu thì add vào viewflipper
            viewFlipper.addView(imageView);
        }
        // bắt sự kiện viewflipper chạy trong bao lau
        viewFlipper.setFlipInterval(3000);
        // bắt sự kiện viewflipper chạy tự chạy
        viewFlipper.setAutoStart(true);
        // set anim cho viewflipper
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        // hàm hỗ trợ toolbar truyền vào toolbar
        setSupportActionBar(toolbar);
        // xét nút home toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // lấy hình ảnh trong thư viện của androi, xét icon lại
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        // bắt sự kiện khi click vào sẽ hiển thị ra thanh menu
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

    }

    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerViewManHinhChinh = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        navigationView = findViewById(R.id.navigationview);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        drawerLayout = findViewById(R.id.drawlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);

        // khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
         if(Utils.manggiohang == null){
             Utils.manggiohang = new ArrayList<>();
         }else{
             int totalItem = 0;
             // cứ 1 lần chạy qua là lấy số lượng + tổng
             for(int i = 0; i<Utils.manggiohang.size();i++){
                 totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
             }
             badge.setText(String.valueOf(totalItem));
         }
         // giỏi hàng
         frameLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                 startActivity(giohang);
             }
         });




        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TimKiemActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        // cứ 1 lần chạy qua là lấy số lượng + tổng
        for(int i = 0; i<Utils.manggiohang.size();i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    // tạo hàm kiểm tra kết nối internet
    private  boolean isConnected (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi !=null && wifi.isConnected())||(mobile !=null && mobile.isConnected()) ){
            return true;
        }else
            return false;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}