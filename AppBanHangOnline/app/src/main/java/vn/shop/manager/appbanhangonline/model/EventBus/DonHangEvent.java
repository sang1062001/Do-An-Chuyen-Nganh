package vn.shop.manager.appbanhangonline.model.EventBus;

import vn.shop.manager.appbanhangonline.model.DonHang;

public class DonHangEvent {

    DonHang donHang;

    public DonHangEvent(DonHang donHang) {
        this.donHang = donHang;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }
}
