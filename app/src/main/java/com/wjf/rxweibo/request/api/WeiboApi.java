package com.wjf.rxweibo.request.api;

import com.wjf.rxweibo.model.Status;
import com.wjf.rxweibo.model.StatusList;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/6
 */
public interface WeiboApi {
    @GET("statuses/friends_timeline.json")
    Observable<StatusList> getTimeLine(@Query("since_id") long since_id, @Query("max_id") long max_id,
                                       @Query("count") int count);
}
