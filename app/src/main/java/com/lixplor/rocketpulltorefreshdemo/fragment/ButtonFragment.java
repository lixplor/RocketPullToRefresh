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
import android.view.View;
import android.widget.Button;

import com.lixplor.rocketpulltorefresh.RefreshLayout;
import com.lixplor.rocketpulltorefresh.footer.plain.PlainFooter;
import com.lixplor.rocketpulltorefresh.header.rocket.RocketHeader;
import com.lixplor.rocketpulltorefreshdemo.R;
import com.lixplor.rocketpulltorefreshdemo.util.ToastUtil;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class ButtonFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    @Override
    protected int setViewLayoutId() {
        return R.layout.frag_nonscrollview;
    }

    @Override
    protected void init() {
        mRefreshLayout = (RefreshLayout) mBaseView.findViewById(R.id.refl);
        Button button = (Button) mBaseView.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.shortT(getContext(), "clicked");
            }
        });
        initRefreshLayout();
    }

    private void initRefreshLayout() {
//        mRefreshLayout.setHeaderView(new PlainHeader(getContext()));
        mRefreshLayout.setHeaderView(new RocketHeader(getContext()));
        mRefreshLayout.setFooterView(new PlainFooter(getContext()));
        mRefreshLayout.setOnStateChangedListener(new RefreshLayout.OnStateChangedListener() {
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
