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

package com.lixplor.rocketpulltorefreshdemo.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lixplor.rocketpulltorefresh.RefreshLayout;
import com.lixplor.rocketpulltorefresh.footer.plain.PlainFooter;
import com.lixplor.rocketpulltorefresh.header.plain.PlainHeader;
import com.lixplor.rocketpulltorefreshdemo.R;
import com.lixplor.rocketpulltorefreshdemo.adapter.RecyclerAdapter;
import com.lixplor.rocketpulltorefreshdemo.util.MockUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class HoriRecyclerViewFragment extends BaseFragment {

    private int mLimit = 5;

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private List<Integer> mIntegers;

    @Override
    protected int setViewLayoutId() {
        return R.layout.frag_recyclerview;
    }

    @Override
    protected void init() {
        mRefreshLayout = (RefreshLayout) mBaseView.findViewById(R.id.refl);
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.rv);
        initRefreshLayout();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mIntegers = new ArrayList<>();
        mIntegers.addAll(MockUtil.genInt(0, mLimit));
        mAdapter = new RecyclerAdapter(mIntegers);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setHeaderView(new PlainHeader(getContext()));
        mRefreshLayout.setFooterView(new PlainFooter(getContext()));
        mRefreshLayout.setOnStateChangedListener(new RefreshLayout.OnStateChangedListener() {
            @Override
            public void onRefresh() {
                MockUtil.getData(0, mLimit, new MockUtil.OnGetDataFinishCallback() {
                    @Override
                    public void onFinish(List<Integer> integers) {
                        mIntegers.clear();
                        mIntegers.addAll(integers);
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.finishRefresh();
                    }
                });
            }

            @Override
            public void onLoad() {
                int start = mIntegers.size();
                MockUtil.getData(start, start + mLimit, new MockUtil.OnGetDataFinishCallback() {
                    @Override
                    public void onFinish(List<Integer> integers) {
                        mIntegers.addAll(integers);
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.finishLoad();
                    }
                });
            }
        });
    }
}
