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

package com.lixplor.rocketpulltorefresh.header.rocket;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lixplor.rocketpulltorefresh.R;
import com.lixplor.rocketpulltorefresh.RefreshLayout;
import com.lixplor.rocketpulltorefresh.util.ResouceUtil;
import com.lixplor.rocketpulltorefresh.header.AbsHeader;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class RocketHeader extends AbsHeader implements RefreshLayout.OnPullListener {

    private String mTipPullDown;
    private String mTipRelease;
    private String mTipRefreshing;
    private String mTipFinish;

    private RocketFrameLayout mRocketFrameLayout;
    private TextView mTvTip;

    public RocketHeader(Context context) {
        super(context);
        mTipPullDown = ResouceUtil.getString(context, R.string.tip_header_rocket_pull_down);
        mTipRelease = ResouceUtil.getString(context, R.string.tip_header_rocket_release);
        mTipRefreshing = ResouceUtil.getString(context, R.string.tip_header_rocket_refreshing);
        mTipFinish = ResouceUtil.getString(context, R.string.tip_header_rocket_finish);
    }

    @Override
    public View getHeader() {
        mHeader = View.inflate(mContext, R.layout.view_header_rocket, null);
        mRocketFrameLayout = (RocketFrameLayout) mHeader.findViewById(R.id.rfl_header);
        mTvTip = (TextView) mHeader.findViewById(R.id.tv_tip);
        return mHeader;
    }

    @Override
    public void changeToPullDown() {
        mTvTip.setText(mTipPullDown);
        mRocketFrameLayout.stopRocketShake();
    }

    @Override
    public void changeToRelease() {
        mTvTip.setText(mTipRelease);
        mRocketFrameLayout.playRocketShake();
    }

    @Override
    public void changeToRefreshing() {
        mTvTip.setText(mTipRefreshing);
        mRocketFrameLayout.stopRocketShake();
        mRocketFrameLayout.playBounceBack();
    }

    @Override
    public void changeToFinish() {
        mTvTip.setText(mTipFinish);
    }

    @Override
    public void reset() {
        mTvTip.setText(mTipPullDown);
    }

    @Override
    public void onPullDown(float touchX, float touchY, float percent) {
        mRocketFrameLayout.drawBounce(touchX, percent);
    }

    @Override
    public void onPullUp(float touchX, float touchY, float percent) {

    }
}
