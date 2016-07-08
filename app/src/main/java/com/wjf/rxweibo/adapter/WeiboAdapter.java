package com.wjf.rxweibo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wjf.rxweibo.R;
import com.wjf.rxweibo.model.Status;
import com.youzan.titan.TitanAdapter;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/8
 */
public class WeiboAdapter extends TitanAdapter<Status>{

    @Override
    protected RecyclerView.ViewHolder createVHolder(ViewGroup parent, int viewType) {
        return new WeiboViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false), this);
    }

    @Override
    protected void showItemView(RecyclerView.ViewHolder holder, int position) {
        ((WeiboViewHolder) holder).mTextView.setText(mData.get(position).text);

    }

    @Override
    public long getAdapterItemId(int position) {
        return 0;
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
