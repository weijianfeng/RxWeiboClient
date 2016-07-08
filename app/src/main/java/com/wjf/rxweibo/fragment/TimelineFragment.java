package com.wjf.rxweibo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjf.rxweibo.R;
import com.wjf.rxweibo.adapter.WeiboAdapter;
import com.wjf.rxweibo.model.Status;
import com.youzan.titan.TitanRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    private TitanRecyclerView mTitanRecyclerView;
    private WeiboAdapter mAdapter;
    private List<Status> mData;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_timeline, container, false);
        mTitanRecyclerView = (TitanRecyclerView) rootView.findViewById(R.id.titan_recycler_view);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Status s = new Status();
            s.text = i + "";
            mData.add(s);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitanRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new WeiboAdapter();
        mAdapter.setData(mData);
        mTitanRecyclerView.setAdapter(mAdapter);
    }
}
