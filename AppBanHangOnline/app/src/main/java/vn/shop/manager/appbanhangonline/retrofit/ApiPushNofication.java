package vn.shop.manager.appbanhangonline.retrofit;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import vn.shop.manager.appbanhangonline.model.NotiResponse;
import vn.shop.manager.appbanhangonline.model.NotiSendData;

public interface ApiPushNofication {
    @Headers(
            {
                    "Content-Type:application/json",
                        "Authorization:key=AAAAxw37goM:APA91bHdjiasd5HWrIzZUlQya0nhqr5MEXG95-gRfwrXgHJ3v1AcW0I0eSH2lCrD9qMkquTrbu0Li_1r17w1O5jH9X-wsexmKSodnllqHck5iPIQ1uz36379agiL-soW45SB7X1tfKF8"
            }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNofitication(@Body NotiSendData data);
}
