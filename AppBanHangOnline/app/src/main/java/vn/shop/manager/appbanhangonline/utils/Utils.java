package vn.shop.manager.appbanhangonline.utils;

import java.util.ArrayList;
import java.util.List;

import vn.shop.manager.appbanhangonline.model.GioHang;
import vn.shop.manager.appbanhangonline.model.User;

public class Utils {
    public static final String BASE_URL="http://172.20.10.9/banhang/";
    public static List<GioHang> manggiohang;
    //checkbox
   public static List<GioHang> mangmuahang = new ArrayList<>();
    //tự động điền email và pass
    public static User user_current = new User();

}
