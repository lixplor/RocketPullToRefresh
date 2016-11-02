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

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lixplor.rocketpulltorefresh.RefreshLayout;
import com.lixplor.rocketpulltorefresh.footer.plain.PlainFooter;
import com.lixplor.rocketpulltorefresh.header.rocket.RocketHeader;
import com.lixplor.rocketpulltorefreshdemo.R;
import com.lixplor.rocketpulltorefreshdemo.adapter.ListAdapter;
import com.lixplor.rocketpulltorefreshdemo.util.MockUtil;
import com.lixplor.rocketpulltorefreshdemo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created :  2016-10-29
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class ListViewFragment extends BaseFragment {

    private int mLimit = 5;

    private RefreshLayout mRefreshLayout;
    private ListView mListview;
    private ListAdapter mAdapter;
    private List<Integer> mIntegers;

    @Override
    protected int setViewLayoutId() {
        return R.layout.frag_listview;
    }

    @Override
    protected void init() {
        mRefreshLayout = (RefreshLayout) mBaseView.findViewById(R.id.refl);
        mListview = (ListView) mBaseView.findViewById(R.id.lv);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.shortT(getContext(), "click " + position);
            }
        });
        initRefreshLayout();

        mIntegers = new ArrayList<>();
        mIntegers.addAll(MockUtil.genInt(0, mLimit));
        mAdapter = new ListAdapter(mIntegers);
        mListview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setHeaderView(new RocketHeader(getContext()));
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
