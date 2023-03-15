package vn.shop.manager.appbanhangonline.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

import vn.shop.manager.appbanhangonline.Interface.IImageClickListenner;
import vn.shop.manager.appbanhangonline.R;
import vn.shop.manager.appbanhangonline.model.EventBus.TinhTongEvent;
import vn.shop.manager.appbanhangonline.model.GioHang;
import vn.shop.manager.appbanhangonline.utils.Utils;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewholder> {
    Context context;
    List<GioHang> gioHangList;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang,parent,false);


        return new MyViewholder(view);
    }
// hàm bắt sự kiện
    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensp());
        holder.item_giohang_soluong.setText(gioHang.getSoluong() +" ");
        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_giohang_image);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        // phần giá chuyễn đổi dữ liệu chuỗi string
        holder.item_giohang_gia.setText(decimalFormat.format((gioHang.getGiasp())));
        // giá tien x sl
        long gia = gioHang.getSoluong() * gioHang.getGiasp();
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia));


        //checkbox
       holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // nếu check là true,k là false
                if(b){
                    // nếu sản phẩm nào dc chọn thì thêm vào giỏ hàng
                    Utils.mangmuahang.add(gioHang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else{
                    // nếu bỏ chọn thì remove xóa khỏi giỏ hàng thì phải duyệt4 qya
                    // nếu như người dùng click bỏ chọn 1 sp bất kì thì ta phải vào mang mua hàng và duỵet
                    // qua tất cả phần tử trong mang mua hàng và tìm xem có id nào trùng với id dc click k  để bỏ đi
                    for (int i =0; i<Utils.mangmuahang.size();i++){
                        if(Utils.mangmuahang.get(i).getIdsp() == gioHang.getIdsp()){
                            Utils.mangmuahang.remove(i);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }

                }
            }
        });



        holder.setListenner(new IImageClickListenner() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                // kiểm tra số lượng sản phẩm trong giỏ hàng nếu lớn hon 1 thì trừ
                Log.d("TAG","onImageClick"+pos+"..."+giatri);
                if(giatri == 1){
                    // 1 là trừ
                    if(gioHangList.get(pos).getSoluong() > 1){
                        int soluongmoi = gioHangList.get(pos).getSoluong()-1;
                        // khi trừ xong xét lại giá trị
                        gioHangList.get(pos).setSoluong(soluongmoi);
                        holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong() +" ");
                        long gia = gioHangList.get(pos).getSoluong() * gioHangList.get(pos).getGiasp();
                        holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                        // bấm - trong trường hơp = 1 sẽ xóa sản phẩm đó
                    }else if(gioHangList.get(pos).getSoluong() == 1){
                        // thông báo cho người dùng khi muốn xóa
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("THÔNG BÁO");
                        builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không ?");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.manggiohang.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                            }
                        });
                        builder.show();

                    }
                }else if(giatri == 2){
                    // 2 là cộng
                    if(gioHangList.get(pos).getSoluong() < 11){
                        int soluongmoi = gioHangList.get(pos).getSoluong()+1;
                        // khi cong xong xét lại giá trị
                        gioHangList.get(pos).setSoluong(soluongmoi);
                    }
                    holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong() +" ");
                    long gia = gioHangList.get(pos).getSoluong() * gioHangList.get(pos).getGiasp();
                    holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }


    public class MyViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_giohang_image, imgtru, imgcong;
        TextView item_giohang_tensp,item_giohang_gia,item_giohang_soluong,item_giohang_giasp2;
        IImageClickListenner listenner;
        CheckBox checkBox;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
            imgtru = itemView.findViewById(R.id.item_giohang_tru);
            imgcong = itemView.findViewById(R.id.item_giohang_cong);
            checkBox = itemView.findViewById(R.id.item_giohang_check);
            // để bắt sự kiên +- thì tạo interface
            imgcong.setOnClickListener(this);
            imgtru.setOnClickListener(this);

        }

        public void setListenner(IImageClickListenner listenner) {
            this.listenner = listenner;

        }

        @Override
        public void onClick(View view) {
            if(view == imgtru){
                // 1 là trừ
                listenner.onImageClick( view, getAdapterPosition(), 1);

            }else if(view == imgcong){
                // 2 là cộng
                listenner.onImageClick( view, getAdapterPosition(), 2);

            }



        }
    }
}
