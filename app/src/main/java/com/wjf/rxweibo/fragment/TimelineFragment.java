package com.wjf.rxweibo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.wjf.rxweibo.R;
import com.wjf.rxweibo.adapter.WeiboAdapter;
import com.wjf.rxweibo.model.Status;
import com.wjf.rxweibo.model.StatusList;
import com.wjf.rxweibo.request.ApiFactory;
import com.wjf.rxweibo.request.api.WeiboApi;
import com.youzan.titan.TitanRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    private TitanRecyclerView mTitanRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WeiboAdapter mAdapter;
    private List<Status> mData;
    private boolean mIsFirstLoad = true;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_timeline, container, false);
        mTitanRecyclerView = (TitanRecyclerView) rootView.findViewById(R.id.titan_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipelayout);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadData();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitanRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new WeiboAdapter();
        mData = new ArrayList<>();
        mAdapter.setData(mData);
        mTitanRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mIsFirstLoad) {
                    loadData();
                } else {
                    refresh();
                }

            }
        });

        mTitanRecyclerView.setOnLoadMoreListener(new TitanRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mAdapter.getData() == null) {
                    loadMore();
                } else {
                    mTitanRecyclerView.setHasMore(false);
                }
            }
        });
    }

    private void loadData() {
        getWeiboOnLine();
    }

    private void loadMore() {
        long maxid = Long.parseLong(mAdapter.getData().get(mAdapter.getItemCount()).id);
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0,maxid,5)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
                        //Collections.reverse(statusList.statuses);
                        return Observable.from(statusList.statuses);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        mTitanRecyclerView.setHasMore(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTitanRecyclerView.setHasMore(false);
                    }

                    @Override
                    public void onNext(Status status) {
                        mData.add(status);
                    }
                });
    }

    private void refresh() {
        long since_id = Long.parseLong(mAdapter.getData().get(0).id);
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(since_id,0,5)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
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
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Status status) {
                        mData.add(0,status);
                    }
                });
    }


    private void getWeiboOnLine() {
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0,0,5)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
                        return Observable.from(statusList.statuses);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mIsFirstLoad = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mIsFirstLoad = false;
                    }

                    @Override
                    public void onNext(Status status) {
                        mData.add(status);
                    }
                });

    }

    private void getWeiboOnLineByMap() {
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0,0,5)
                .map(new Func1<StatusList, ArrayList<Status>>() {
                    @Override
                    public ArrayList<Status> call(StatusList statusList) {
                        return statusList.statuses;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Status>>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(ArrayList<Status> status) {
                        for (Status s : status) {
                            mData.add(s);
                        }
                    }
                });
    }
}
