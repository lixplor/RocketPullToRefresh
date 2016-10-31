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

package com.lixplor.rocketpulltorefresh.header.plain;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lixplor.rocketpulltorefresh.R;
import com.lixplor.rocketpulltorefresh.util.ResouceUtil;
import com.lixplor.rocketpulltorefresh.header.AbsHeader;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class PlainHeader extends AbsHeader {

    private static final int DURATION_INDICATOR_ROTATE = 200;

    private String mTipPullDown;
    private String mTipRelease;
    private String mTipRefreshing;
    private String mTipFinish;

    private TextView mTvTip;
    private ImageView mIvIndicator;
    private ProgressBar mPbSpinner;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;

    public PlainHeader(Context context) {
        super(context);
        mTipPullDown = ResouceUtil.getString(context, R.string.tip_header_plain_pull_down);
        mTipRelease = ResouceUtil.getString(context, R.string.tip_header_plain_release);
        mTipRefreshing = ResouceUtil.getString(context, R.string.tip_header_plain_refreshing);
        mTipFinish = ResouceUtil.getString(context, R.string.tip_header_plain_finish);
    }

    @Override
    public View getHeader() {
        mHeader = View.inflate(mContext, R.layout.view_header_plain, null);
        mTvTip = (TextView) mHeader.findViewById(R.id.tv_tip);
        mIvIndicator = (ImageView) mHeader.findViewById(R.id.iv_indicator);
        mPbSpinner = (ProgressBar) mHeader.findViewById(R.id.pb_spinner);
        createRotateUpAnim();
        createRotateDownAnim();
        return mHeader;
    }

    @Override
    public void changeToPullDown() {
        mTvTip.setText(mTipPullDown);
        mPbSpinner.setVisibility(View.GONE);
        mIvIndicator.setVisibility(View.VISIBLE);
        mIvIndicator.clearAnimation();
        mIvIndicator.startAnimation(mRotateDownAnim);
    }

    @Override
    public void changeToRelease() {
        mTvTip.setText(mTipRelease);
        mPbSpinner.setVisibility(View.GONE);
        mIvIndicator.setVisibility(View.VISIBLE);
        mIvIndicator.clearAnimation();
        mIvIndicator.startAnimation(mRotateUpAnim);
    }

    @Override
    public void changeToRefreshing() {
        mTvTip.setText(mTipRefreshing);
        mPbSpinner.setVisibility(View.VISIBLE);
        mIvIndicator.clearAnimation();
        mIvIndicator.setVisibility(View.GONE);
    }

    @Override
    public void changeToFinish() {
        mTvTip.setText(mTipFinish);
        mPbSpinner.setVisibility(View.GONE);
        mIvIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void reset() {
        mTvTip.setText(mTipPullDown);
        mPbSpinner.setVisibility(View.GONE);
        mIvIndicator.clearAnimation();
        mIvIndicator.setVisibility(View.VISIBLE);
    }

    private void createRotateUpAnim() {
        mRotateUpAnim = new RotateAnimation(
                0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        mRotateUpAnim.setDuration(DURATION_INDICATOR_ROTATE);
        mRotateUpAnim.setInterpolator(new LinearInterpolator());
        mRotateUpAnim.setFillAfter(true);
    }

    private void createRotateDownAnim() {
        mRotateDownAnim = new RotateAnimation(
                180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        mRotateDownAnim.setDuration(DURATION_INDICATOR_ROTATE);
        mRotateDownAnim.setInterpolator(new LinearInterpolator());
        mRotateDownAnim.setFillAfter(true);
    }
}
