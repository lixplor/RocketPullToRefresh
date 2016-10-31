/*
 *  Copyright 2016 Lixplor
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.lixplor.rocketpulltorefreshdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lixplor.rocketpulltorefreshdemo.R;
import com.lixplor.rocketpulltorefreshdemo.util.ToastUtil;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created :  2016-10-29
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH> {

    private List<Integer> mIntegers;

    public RecyclerAdapter(List<Integer> integers){
        mIntegers = integers;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block, parent, false);
        return new VH(item);
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.mTvBlock.setText("" + position);
        holder.mTvBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.shortT(v.getContext(), "clicked " + holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIntegers.size();
    }

    class VH extends RecyclerView.ViewHolder{

        private TextView mTvBlock;

        VH(View itemView) {
            super(itemView);
            mTvBlock = (TextView) itemView.findViewById(R.id.tv_block);
        }
    }
}
