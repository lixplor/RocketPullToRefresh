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

import android.os.SystemClock;

import com.lixplor.rocketpulltorefresh.RefreshLayout;
import com.lixplor.rocketpulltorefresh.footer.plain.PlainFooter;
import com.lixplor.rocketpulltorefresh.header.plain.PlainHeader;
import com.lixplor.rocketpulltorefreshdemo.R;

/**
 * Created :  2016-10-29
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class ScrollViewFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    @Override
    protected int setViewLayoutId() {
        return R.layout.frag_scrollview;
    }

    @Override
    protected void init() {
        mRefreshLayout = (RefreshLayout) mBaseView.findViewById(R.id.refl);
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setHeaderView(new PlainHeader(getContext()));
        mRefreshLayout.setFooterView(new PlainFooter(getContext()));
        mRefreshLayout.setOnStateListener(new RefreshLayout.OnStateListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.finishRefresh();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onLoad() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.finishLoad();
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
