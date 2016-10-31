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

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lixplor.rocketpulltorefreshdemo.R;

import java.util.List;

/**
 * Created :  2016-10-31
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class ListAdapter extends BaseAdapter {

    private List<Integer> mIntegers;

    public ListAdapter(List<Integer> integers){
        mIntegers = integers;
    }

    @Override
    public int getCount() {
        return mIntegers.size();
    }

    @Override
    public Object getItem(int position) {
        return mIntegers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.item_block, null);
            holder = new VH(convertView);
            convertView.setTag(holder);
        }else{
            holder = (VH) convertView.getTag();
        }
        holder.mTvBlock.setText("" + position);
        return convertView;
    }

    private class VH{

        private TextView mTvBlock;

        VH(View itemView){
            mTvBlock = (TextView) itemView.findViewById(R.id.tv_block);
        }
    }
}
