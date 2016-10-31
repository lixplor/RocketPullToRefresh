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

package com.lixplor.rocketpulltorefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lixplor.rocketpulltorefresh.footer.AbsFooter;
import com.lixplor.rocketpulltorefresh.header.AbsHeader;


/**
 * Created :  2016-10-28
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class RefreshLayout extends LinearLayout {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_PULL_DOWN_TO_REFRESH = 1;
    public static final int STATE_RELEASE_TO_REFRESH = 2;
    public static final int STATE_REFRESHING = 3;
    public static final int STATE_REFRESH_FINISH = 4;
    public static final int STATE_PULL_UP_TO_LOAD = -1;
    public static final int STATE_RELEASE_TO_LOAD = -2;
    public static final int STATE_LOADING = -3;
    public static final int STATE_LOAD_FINISH = -4;

    private int mTouchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();

    // configs
    private float mPullResistor = 0.7f;
    private long mBackAnimDuration = 200;
    private boolean mEnableRefresh = true;
    private boolean mEnableLoadMore = false;


    private OnStateListener mOnStateListener;
    private OnPullListener mOnPullListener;


    private Context mContext;
    private AttributeSet mAttributeSet;

    private FrameLayout mHeaderContainer;
    private FrameLayout mFooterContainer;
    private View mContentView;
    private View mHeaderView;
    private View mFooterView;


    private AbsHeader mAbsHeader;
    private AbsFooter mAbsFooter;


    private int mCurrentState = STATE_NORMAL;

    private float mLastTouchRawY;
    private float mDownRawY;

    private float mInterceptDownY;
    private float mInterceptMoveY;

    private int mHeaderHeight;
    private int mFooterHeight;

    private ValueAnimator mHeaderBackAnimator;
    private ValueAnimator mFooterBackAnimator;


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new IllegalStateException("Only 1 child view allowed!");
        }
        // get content view
        mContentView = getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return shouldInterceptTouchEvent(ev, mContentView);
    }

    private boolean shouldInterceptTouchEvent(MotionEvent ev, View contentView) {
        boolean shouldIntercept;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mInterceptDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mInterceptMoveY = ev.getRawY();
                mLastTouchRawY = mInterceptMoveY;
                if (Math.abs(mInterceptMoveY - mInterceptDownY) > mTouchSlop) {
                    if (mInterceptMoveY - mInterceptDownY > 0) {
                        // pull down, evaluate if content view is at top
                        shouldIntercept = !ViewCompat.canScrollVertically(contentView, -1);
                        return shouldIntercept;
                    } else {
                        // pull up, evaluate if content view is at bottom
                        shouldIntercept = !ViewCompat.canScrollVertically(contentView, 1);
                        return shouldIntercept;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownRawY = event.getRawY();
                mLastTouchRawY = mDownRawY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveRawX = event.getRawX();
                float moveRawY = event.getRawY();
                float dy = moveRawY - mLastTouchRawY;

                // move entire layout to display header or footer
                scrollBy(0, (int) (-dy * mPullResistor));

                if (getScrollY() < 0) {
                    // evaluate if head appears fully, then change to release mode
                    float outHeight = getY() - getScrollY();
                    float percent = outHeight / mHeaderHeight;
                    if (mOnPullListener != null) {
                        mOnPullListener.onPullDown(moveRawX, moveRawY, percent);
                    }

                    if (outHeight >= mHeaderHeight && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        mAbsHeader.changeToRelease();
                    } else if (outHeight < mHeaderHeight && mCurrentState != STATE_PULL_DOWN_TO_REFRESH) {
                        mCurrentState = STATE_PULL_DOWN_TO_REFRESH;
                        mAbsHeader.changeToPullDown();
                    }
                } else {
                    // evaluate if foot appears fully, then change to release mode
                    float outHeight = getScrollY() - getY();
                    float percent = outHeight / mFooterHeight;
                    if (mOnPullListener != null) {
                        mOnPullListener.onPullUp(moveRawX, moveRawY, percent);
                    }

                    if (outHeight >= mFooterHeight && mCurrentState != STATE_RELEASE_TO_LOAD) {
                        mCurrentState = STATE_RELEASE_TO_LOAD;
                        mAbsFooter.changeToRelease();
                    } else if (outHeight < mFooterHeight && mCurrentState != STATE_PULL_UP_TO_LOAD) {
                        mCurrentState = STATE_PULL_UP_TO_LOAD;
                        mAbsFooter.changeToPullUp();
                    }
                }
                mLastTouchRawY = moveRawY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentState == STATE_PULL_DOWN_TO_REFRESH) {
                    finishRefresh();
                } else if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    mAbsHeader.changeToRefreshing();
                    if (mOnStateListener != null) {
                        mOnStateListener.onRefresh();
                        animHeader(getScrollY(), -mHeaderHeight);
                    }
                } else if (mCurrentState == STATE_PULL_UP_TO_LOAD) {
                    finishLoad();
                } else if (mCurrentState == STATE_RELEASE_TO_LOAD) {
                    mCurrentState = STATE_LOADING;
                    mAbsFooter.changeToLoading();
                    if (mOnStateListener != null) {
                        mOnStateListener.onLoad();
                        animFooter(getScrollY(), mFooterHeight);
                    }
                }
                break;
        }
        return true;
    }

    public void finishRefresh() {
        mCurrentState = STATE_REFRESH_FINISH;
        mAbsHeader.changeToFinish();
        animHeader(getScrollY(), 0);
    }

    public void finishLoad() {
        mCurrentState = STATE_LOAD_FINISH;
        mAbsFooter.changeToFinish();
        animFooter(getScrollY(), 0);
    }

    private void animHeader(int from, final int to) {
        if (mHeaderBackAnimator == null) {
            mHeaderBackAnimator = new ValueAnimator();
            mHeaderBackAnimator.setDuration(mBackAnimDuration);
            mHeaderBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int destY = (int) animation.getAnimatedValue();
                    scrollTo(0, destY);
                    if (destY == 0) {
                        // when back finished, reset header
                        mCurrentState = STATE_PULL_DOWN_TO_REFRESH;
                        mAbsHeader.reset();
                    }
                }
            });
            mHeaderBackAnimator.setTarget(this);
        }
        mHeaderBackAnimator.setIntValues(from, to);
        mHeaderBackAnimator.start();
    }

    private void animFooter(int from, int to) {
        if (mFooterBackAnimator == null) {
            mFooterBackAnimator = new ValueAnimator();
            mFooterBackAnimator.setDuration(mBackAnimDuration);
            mFooterBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int destY = (int) animation.getAnimatedValue();
                    scrollTo(0, destY);
                    if (destY == 0) {
                        // when back finished, reset footer
                        mCurrentState = STATE_PULL_UP_TO_LOAD;
                        mAbsFooter.reset();
                    }
                }
            });
            mFooterBackAnimator.setTarget(this);
        }
        mFooterBackAnimator.setIntValues(from, to);
        mFooterBackAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mAttributeSet = attrs;
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initHeaderContainer() {
        mHeaderContainer = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        mHeaderView.measure(0, 0);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        params.topMargin = -mHeaderHeight;
        mHeaderContainer.addView(mHeaderView);
        addView(mHeaderContainer, 0, params);
    }

    private void initFooterContainer() {
        mFooterContainer = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        mFooterView.measure(0, 0);
        mFooterHeight = mFooterView.getMeasuredHeight();
        params.bottomMargin = -mFooterHeight;
        mFooterContainer.addView(mFooterView);
        addView(mFooterContainer, params);
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        mOnStateListener = onStateListener;
    }

    public interface OnStateListener {
        void onRefresh();

        void onLoad();
    }

    public void setOnPullListener(OnPullListener onPullListener) {
        mOnPullListener = onPullListener;
    }

    public interface OnPullListener {
        void onPullDown(float touchX, float touchY, float percent);

        void onPullUp(float touchX, float touchY, float percent);
    }

    public void setHeaderView(AbsHeader header) {
        mAbsHeader = header;
        if (mAbsHeader instanceof OnPullListener) {
            mOnPullListener = (OnPullListener) mAbsHeader;
        }
        mHeaderView = mAbsHeader.getHeader();
        initHeaderContainer();
    }

    public void setFooterView(AbsFooter footer) {
        mAbsFooter = footer;
        mFooterView = mAbsFooter.getFooter();
        initFooterContainer();
    }
}
