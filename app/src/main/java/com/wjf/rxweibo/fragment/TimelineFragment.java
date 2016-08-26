package com.wjf.rxweibo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjf.rxweibo.R;
import com.wjf.rxweibo.adapter.WeiboAdapter;
import com.wjf.rxweibo.database.dao.StatusDao;
import com.wjf.rxweibo.model.Status;
import com.wjf.rxweibo.model.StatusList;
import com.wjf.rxweibo.request.ApiFactory;
import com.wjf.rxweibo.request.api.WeiboApi;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.AsyncOnSubscribe;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    private static final int TIMELINE_ONCE_COUNT = 5;

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private WeiboAdapter mAdapter;
    private List<Status> mData;
    private boolean mIsFirstLoad = true;
    private StatusDao mStatusDao;


    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_timeline, container, false);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)rootView.findViewById(R.id.pullloadmore_recycler_view);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadData();
        mStatusDao = new StatusDao(getActivity().getApplication());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullLoadMoreRecyclerView.setLinearLayout();
        mData = new ArrayList<Status>();
        mAdapter = new WeiboAdapter(mData);

        mPullLoadMoreRecyclerView.setAdapter(mAdapter);

        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                if (mIsFirstLoad) {
                    //loadData();
                    initData();
                } else {
                    refresh();
                }
            }

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() == 0) {
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //loadmore();
                        loadmoreData();
                    }
                }, 300);
            }
        });


    }

    /***** 初始化数据 ******/

    private void initData() {
        Observable<List<Status>> ob = Observable
                .concat(getWeiboFromDB(),getWeiboOnline())
                .takeFirst(new Func1<List<Status>, Boolean>() {
                    @Override
                    public Boolean call(List<Status> statuses) {
                        return !(statuses == null || statuses.size() == 0);
                    }
                });
        ob.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                        mIsFirstLoad = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                        mIsFirstLoad = false;
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mData.addAll(statuses);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private Observable<List<Status>> getWeiboOnline() {
        return ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0, 0, TIMELINE_ONCE_COUNT)
                .map(new Func1<StatusList, List<Status>>() {
                    @Override
                    public List<Status> call(StatusList statusList) {
                        mStatusDao.saveStatus(statusList.statuses);
                        return statusList.statuses;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<List<Status>> getWeiboFromDB() {
        return mStatusDao.getStatuses("0", TIMELINE_ONCE_COUNT);
    }

    /***** 下拉刷新数据 *****/

    private void refresh() {
        long since_id = Long.parseLong(mData.get(0).id);
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(since_id, 0, TIMELINE_ONCE_COUNT)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
                        mStatusDao.saveStatus(statusList.statuses);
                        Collections.reverse(statusList.statuses);
                        return Observable.from(statusList.statuses);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onNext(Status status) {
                        mData.add(0, status);
                    }
                });
    }

    /***** 上拉加载更多数据  *****/

    private void loadmoreData() {
        Observable<List<Status>> ob = Observable
                .concat(loadmoreWeiboFromDB(),loadmoreWeiboOnline())
                .takeFirst(new Func1<List<Status>, Boolean>() {
                    @Override
                    public Boolean call(List<Status> statuses) {
                        return !(statuses == null || statuses.size() == 0);
                    }
                });
        ob.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        mPullLoadMoreRecyclerView.getRecyclerView().smoothScrollToPosition(mData.size() - TIMELINE_ONCE_COUNT);
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mData.addAll(statuses);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private Observable<List<Status>> loadmoreWeiboOnline() {
        long maxid = Long.parseLong(mData.get(mData.size() - 1).id);
        return ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0, maxid, TIMELINE_ONCE_COUNT + 1)
                .map(new Func1<StatusList, List<Status>>() {
                    @Override
                    public List<Status> call(StatusList statusList) {
                        statusList.statuses.remove(0);
                        mStatusDao.saveStatus(statusList.statuses);
                        return statusList.statuses;
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<List<Status>> loadmoreWeiboFromDB() {
        String lastId = mData.get(mData.size() - 1).id;
        return mStatusDao.getStatuses(lastId, TIMELINE_ONCE_COUNT);
    }

}
