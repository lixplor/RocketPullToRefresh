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

package com.lixplor.rocketpulltorefresh.footer.plain;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lixplor.rocketpulltorefresh.R;
import com.lixplor.rocketpulltorefresh.util.ResouceUtil;
import com.lixplor.rocketpulltorefresh.footer.AbsFooter;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class PlainFooter extends AbsFooter {

    private String mTipPullUp;
    private String mTipRelease;
    private String mTipLoading;
    private String mTipFinish;

    private TextView mTvTip;
    private ProgressBar mPbSpinner;

    public PlainFooter(Context context) {
        super(context);
        mTipPullUp = ResouceUtil.getString(context, R.string.tip_footer_plain_pull_up);
        mTipRelease = ResouceUtil.getString(context, R.string.tip_footer_plain_release);
        mTipLoading = ResouceUtil.getString(context, R.string.tip_footer_plain_loading);
        mTipFinish = ResouceUtil.getString(context, R.string.tip_footer_plain_finish);
    }

    @Override
    public View getFooter() {
        mFooter = View.inflate(mContext, R.layout.view_footer_plain, null);
        mTvTip = (TextView) mFooter.findViewById(R.id.tv_tip);
        mPbSpinner = (ProgressBar) mFooter.findViewById(R.id.pb_spinner);
        return mFooter;
    }

    @Override
    public void changeToPullUp() {
        mTvTip.setText(mTipPullUp);
        mPbSpinner.setVisibility(View.GONE);
    }

    @Override
    public void changeToRelease() {
        mTvTip.setText(mTipRelease);
        mPbSpinner.setVisibility(View.GONE);
    }

    @Override
    public void changeToLoading() {
        mTvTip.setText(mTipLoading);
        mPbSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void changeToFinish() {
        mTvTip.setText(mTipFinish);
        mPbSpinner.setVisibility(View.GONE);
    }

    @Override
    public void reset() {
        mTvTip.setText(mTipPullUp);
        mPbSpinner.setVisibility(View.GONE);
    }

}
