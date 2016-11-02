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

    private Context mContext;

    /**
     * Touch tolerance of system
     */
    private int mTouchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
    /**
     * Current pulling state
     */
    private int mCurrentState = STATE_NORMAL;
    /**
     * Callback when pulling state changed
     */
    private OnStateChangedListener mOnStateChangedListener;
    /**
     * Callback when pulling distance changed
     */
    private OnPullListener mOnPullListener;
    /**
     * View that shows content
     */
    private View mContentView;
    /**
     * Header view which can be pull down
     */
    private View mHeaderView;
    /**
     * Footer view which can be pull up
     */
    private View mFooterView;
    /**
     * Header abstract class
     */
    private AbsHeader mAbsHeader;
    /**
     * Footer abstract class
     */
    private AbsFooter mAbsFooter;
    /**
     * Height of header view
     */
    private int mHeaderHeight;
    /**
     * Height of footer view
     */
    private int mFooterHeight;
    /**
     * Raw Y px in the last touch event
     */
    private float mLastTouchRawY;
    /**
     * Raw Y px of touch down event
     */
    private float mDownRawY;
    /**
     * Y px in touch down event of onInterceptTouchEvent
     */
    private float mInterceptDownY;
    /**
     * Y px in touch move event of onInterceptTouchEvent
     */
    private float mInterceptMoveY;
    /**
     * Shrink back animation of header view
     */
    private ValueAnimator mHeaderBackAnimator;
    /**
     * Shrink back animation of footer view
     */
    private ValueAnimator mFooterBackAnimator;

    // configs
    /**
     * Pulling resistor. 1f means same speed as touch movement; less than 1f means slower; more than 1f means faster
     */
    private float mPullResistor = 0.7f;
    /**
     * Shrink back animation duration
     */
    private long mBackAnimDuration = 200;
    /**
     * Whether to enable pull down to refresh
     */
    private boolean mEnableRefresh = false;
    /**
     * Whether to enable pull up to load more
     */
    private boolean mEnableLoadMore = false;


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
                    if (mOnStateChangedListener != null) {
                        mOnStateChangedListener.onRefresh();
                        animHeader(getScrollY(), -mHeaderHeight);
                    }
                } else if (mCurrentState == STATE_PULL_UP_TO_LOAD) {
                    finishLoad();
                } else if (mCurrentState == STATE_RELEASE_TO_LOAD) {
                    mCurrentState = STATE_LOADING;
                    mAbsFooter.changeToLoading();
                    if (mOnStateChangedListener != null) {
                        mOnStateChangedListener.onLoad();
                        animFooter(getScrollY(), mFooterHeight);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Init of view
     * @param context Context
     * @param attrs AttributeSet
     */
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * Init header container view
     */
    private void initHeaderContainer() {
        FrameLayout headerContainer = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        mHeaderView.measure(0, 0);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        params.topMargin = -mHeaderHeight;
        headerContainer.addView(mHeaderView);
        addView(headerContainer, 0, params);
    }

    /**
     * Init footer container view
     */
    private void initFooterContainer() {
        FrameLayout footerContainer = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        mFooterView.measure(0, 0);
        mFooterHeight = mFooterView.getMeasuredHeight();
        params.bottomMargin = -mFooterHeight;
        footerContainer.addView(mFooterView);
        addView(footerContainer, params);
    }

    /**
     * Evaluate if this should intercept touch event to enable pull actions
     * @param ev MotionEvent
     * @param contentView content view to help evaluate
     * @return true if intercepts; false otherwise
     */
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
                    if (mEnableRefresh && mInterceptMoveY - mInterceptDownY > 0) {
                        // pull down, evaluate if content view is at top
                        shouldIntercept = !ViewCompat.canScrollVertically(contentView, -1);
                        return shouldIntercept;
                    } else if(mEnableLoadMore && mInterceptMoveY - mInterceptDownY < 0) {
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

    /**
     * Perform header view shrink back animation
     * @param from animation start Y px
     * @param to animation stop Y px
     */
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

    /**
     * Perform footer view shrink back animation
     * @param from animation start Y px
     * @param to animation stop Y px
     */
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

    /**
     * Set custom header view. This will also enable pull down to refresh
     * @param header class that extends from AbsHeader
     */
    public void setHeaderView(AbsHeader header) {
        setEnableRefresh(true);
        mAbsHeader = header;
        if (mAbsHeader instanceof OnPullListener) {
            mOnPullListener = (OnPullListener) mAbsHeader;
        }
        mHeaderView = mAbsHeader.getHeader();
        initHeaderContainer();
    }

    /**
     * Set custom footer view. This will also enable pull up to load more
     * @param footer class that extends from AbsFooter
     */
    public void setFooterView(AbsFooter footer) {
        setEnableLoadMore(true);
        mAbsFooter = footer;
        mFooterView = mAbsFooter.getFooter();
        initFooterContainer();
    }

    /**
     * Set pulling resistor
     * @param resistor 1f means same speed as touch movement; less than 1f means slower; more than 1f means faster
     */
    public void setPullResistor(float resistor){
        if(resistor > 0f && resistor <= 2f){
            mPullResistor = resistor;
        }
    }

    /**
     * Set header or footer shrink back animation duration
     * @param duration duration in milli seconds
     */
    public void setShrinkBackAnimDuration(long duration){
        if(duration < 0){
            duration = 0;
        }
        mBackAnimDuration = duration;
    }

    /**
     * Whether to enable pull to refresh. <br/>
     * This is normally used to enable/disable fresh at runtime. Be sure that you called setFooterView() first!
     * @param enable true to enable; false otherwise
     */
    public void setEnableRefresh(boolean enable){
        mEnableRefresh = enable;
    }

    /**
     * Whether to enable pull to load. <br/>
     * This is normally used to enable/disable load at runtime. Be sure that you called setHeaderView() first!
     * @param enable true to enable; false otherwise
     */
    public void setEnableLoadMore(boolean enable){
        mEnableLoadMore = enable;
    }

    /**
     * Call this when you have finished refreshing. This will shrink back header with animation
     */
    public void finishRefresh() {
        mCurrentState = STATE_REFRESH_FINISH;
        mAbsHeader.changeToFinish();
        animHeader(getScrollY(), 0);
    }

    /**
     * Call this when you have finished loading. This will shrink back footer view with animation
     */
    public void finishLoad() {
        mCurrentState = STATE_LOAD_FINISH;
        mAbsFooter.changeToFinish();
        animFooter(getScrollY(), 0);
    }

    /**
     * Set a listener to listen pulling distance changes
     * @param onPullListener OnPullListener
     */
    public void setOnPullListener(OnPullListener onPullListener) {
        mOnPullListener = onPullListener;
    }

    /**
     * Set a listener to listen to pull state change.
     * @param onStateChangedListener OnStateChangedListener
     */
    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    /**
     * Listener that callbacks pull state
     */
    public interface OnStateChangedListener {
        /**
         * Called when header state changes to refreshing
         */
        void onRefresh();

        /**
         * Called when footer state changes to loading
         */
        void onLoad();
    }

    /**
     * Listener to listen pulling distance changes
     */
    public interface OnPullListener {
        /**
         * Called when header pull down distance changes
         * @param touchX touch X position
         * @param touchY touch Y position
         * @param percent percent of appearence of header
         */
        void onPullDown(float touchX, float touchY, float percent);
        /**
         * Called when footer pull up distance changes
         * @param touchX touch X position
         * @param touchY touch Y position
         * @param percent percent of appearence of footer
         */
        void onPullUp(float touchX, float touchY, float percent);
    }
}
