package com.wjf.rxweibo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.wjf.rxweibo.R;
import com.wjf.rxweibo.model.Status;
import com.youzan.titan.TitanAdapter;

import java.util.List;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/8
 */
public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.WeiboViewHolder> {

    private List<Status> mStatusList;

    public WeiboAdapter(List<Status> statusList) {
        mStatusList = statusList;
    }

    @Override
    public WeiboViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeiboViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false), this);
    }

    @Override
    public void onBindViewHolder(WeiboViewHolder holder, int position) {
        holder.mTextView.setText(mStatusList.get(position).text);
    }

    @Override
    public int getItemCount() {
        return mStatusList.size();
    }
    
    public static class WeiboViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        WeiboAdapter mAdapter;

        WeiboViewHolder(View view, WeiboAdapter adapter) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text_view);
            mAdapter = adapter;
        }
    }
}
