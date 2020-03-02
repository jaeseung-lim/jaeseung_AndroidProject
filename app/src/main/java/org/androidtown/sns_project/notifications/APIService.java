package org.androidtown.sns_project.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAeV3Pv3g:APA91bGoCn8Zb4nwQer4-wCWpaE2-5cDUZfu_bIiULQUh6UtLt-xX728h2n4EP1UI_1-s7tSteCNHATbqBMBWhp7-PRhX_8HSUrH5Gbl1Tbkxj2OTyAYwNIY6pRe-9jnps8JDPzKHXgs"
    })

    @POST("fcm/send")
    Call<Response> SendNotification(@Body Sender body);
}
