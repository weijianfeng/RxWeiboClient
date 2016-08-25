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
                    loadData();
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
                        loadmore();
                    }
                }, 300);
            }
        });


    }

    private void loadData() {
        List<Status> statusList = mStatusDao.getStatus("0", TIMELINE_ONCE_COUNT);
        if (statusList == null || statusList.size() == 0) {
            getWeiboOnLine();
        } else {
            mData.addAll(statusList);
            mAdapter.notifyDataSetChanged();
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            mIsFirstLoad = false;
        }
    }

    private void loadmore() {
        String lastId = mData.get(mData.size() - 1).id;
        List<Status>statusList = mStatusDao.getStatus(lastId, TIMELINE_ONCE_COUNT);
        if (statusList == null || statusList.size() == 0) {
            loadmoreOnline();
        } else {
            mData.addAll(statusList);
            mAdapter.notifyDataSetChanged();
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        }
    }

    private void loadmoreOnline() {
        long maxid = Long.parseLong(mData.get(mData.size() - 1).id);
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0,maxid,TIMELINE_ONCE_COUNT + 1)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
                        //Collections.reverse(statusList.statuses);
                        statusList.statuses.remove(0);
                        mStatusDao.saveStatuses(statusList.statuses);
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
                        mPullLoadMoreRecyclerView.getRecyclerView().smoothScrollToPosition(mData.size() - TIMELINE_ONCE_COUNT);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onNext(Status status) {
                        mData.add(status);
                    }
                });
    }

    private void refresh() {
        long since_id = Long.parseLong(mData.get(0).id);
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(since_id, 0, TIMELINE_ONCE_COUNT)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
                        mStatusDao.saveStatuses(statusList.statuses);
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


    private void getWeiboOnLine() {
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0,0,TIMELINE_ONCE_COUNT)
                .flatMap(new Func1<StatusList, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(StatusList statusList) {
                        mStatusDao.saveStatuses(statusList.statuses);
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
                        mIsFirstLoad = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                        mIsFirstLoad = false;
                    }

                    @Override
                    public void onNext(Status status) {
                        mData.add(status);
                    }
                });

    }

    private void getWeiboOnLineByMap() {
        ApiFactory.createWeiboApi(WeiboApi.class).getTimeLine(0,0,TIMELINE_ONCE_COUNT)
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
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onNext(ArrayList<Status> status) {
                        for (Status s : status) {
                            mData.add(s);
                        }
                    }
                });
    }

    public Observable saveStatus(final List<Status> statusList) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                mStatusDao.saveStatuses(statusList);
                subscriber.onNext(null);
                subscriber.onCompleted();
            }

        }).subscribeOn(Schedulers.io());
    }

    public Observable getStatus(final String lastId, final int limit) {
        return Observable.create(new Observable.OnSubscribe<List<Status>>() {
            @Override
            public void call(Subscriber<? super List<Status>> subscriber) {
                subscriber.onNext(mStatusDao.getStatus(lastId, limit));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
