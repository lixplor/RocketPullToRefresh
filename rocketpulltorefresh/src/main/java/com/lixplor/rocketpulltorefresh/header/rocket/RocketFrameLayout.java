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

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.lixplor.rocketpulltorefresh.R;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class RocketFrameLayout extends FrameLayout {

    private Context mContext;

    private int mPaintColor = Color.parseColor("#6F7494");
    /**
     * percent of whole header where elastic bounce part will be
     */
    private float mBouncePartPercent = 0.4f;
    /**
     * percent of whole header that keep divider still straight
     */
    private float mBouncePartTolerance = 0.0f; // temporarily set to 0 as the rocket drawing has bug
    private int mIndicatorBottomMargin = dp2px(2);
    private int mShakeDistance = dp2px(4);
    private Bitmap mIndicatorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vc_rocket);
    private float mIndicatorTop;
    private float mIndicatorLeft;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mViewWidth;
    private int mViewHeight;
    private float mRectY;
    private float mBouncePartHeight;
    private float mControlX;
    private float mControlY;
    private boolean shouldStopBounceAnim = false;

    private ValueAnimator mControlYAnim;
    private ValueAnimator mRocketShakeAnim;
    private ValueAnimator mIndicatorYAnim;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mBezierPath = new Path();

    public RocketFrameLayout(Context context) {
        this(context, null);
    }

    public RocketFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RocketFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        mRectY = mViewHeight * (1f - mBouncePartPercent);
        mBouncePartHeight = mViewHeight - mRectY;
        mControlX = 0;
        mControlY = mRectY;
        mIndicatorLeft = (mViewWidth - mIndicatorBitmap.getWidth()) / 2f;
        mIndicatorTop = mRectY - mIndicatorBottomMargin - mIndicatorBitmap.getHeight();
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(createPath(mControlX, mControlY), mPaint);
        canvas.drawBitmap(mIndicatorBitmap, mIndicatorLeft, mIndicatorTop, mPaint);
    }

    private void initPaint() {
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private Path createPath(float controlX, float controlY) {
        mBezierPath.reset();
        mBezierPath.moveTo(0, 0);
        mBezierPath.lineTo(mViewWidth, 0);
        mBezierPath.lineTo(mViewWidth, mRectY);
        mBezierPath.quadTo(controlX, controlY, 0, mRectY);
        mBezierPath.close();
        return mBezierPath;
    }

    public void drawBounce(float touchX, float percent) {
        float bouncePartWithTolerance = mBouncePartPercent + mBouncePartTolerance;
        if (percent >= bouncePartWithTolerance) {
            if(percent > 1f){
                percent = 1f;
            }
            float bouncePercent = (percent - bouncePartWithTolerance) / (1f - bouncePartWithTolerance);
            mControlX = touchX;
            mControlY = (mRectY + (mBouncePartHeight * bouncePercent));

            mIndicatorLeft = touchX - mIndicatorBitmap.getWidth() / 2f;
            mIndicatorTop = mRectY - mIndicatorBitmap.getHeight() - mIndicatorBottomMargin + (mBouncePartHeight * bouncePercent / 2f);

            invalidate();
        }
    }

    public void playRocketShake(){
        if(mRocketShakeAnim == null){
            mRocketShakeAnim = new ValueAnimator();
            mRocketShakeAnim.setDuration(30);
            mRocketShakeAnim.setRepeatCount(ValueAnimator.INFINITE);
            mRocketShakeAnim.setRepeatMode(ValueAnimator.REVERSE);
            mRocketShakeAnim.setInterpolator(new LinearInterpolator());
            mRocketShakeAnim.setTarget(this);
            mRocketShakeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rocketCenter = mControlX - mIndicatorBitmap.getWidth() / 2f;
                    mIndicatorLeft = rocketCenter + (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
        }
        mRocketShakeAnim.setFloatValues(-mShakeDistance, mShakeDistance);
        mRocketShakeAnim.start();
    }

    public void stopRocketShake(){
        if(mRocketShakeAnim != null){
            mRocketShakeAnim.cancel();
        }
    }

    public void playBounceBack() {
        if (mControlYAnim == null) {
            mControlYAnim = new ValueAnimator();
            mControlYAnim.setDuration(1500);
            mControlYAnim.setInterpolator(new EaseOutElasticInterpolator());
            mControlYAnim.setTarget(this);
            mControlYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mControlY = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
        }
        if (mIndicatorYAnim == null) {
            mIndicatorYAnim = new ValueAnimator();
            mIndicatorYAnim.setDuration(200);
            mIndicatorYAnim.setInterpolator(new AccelerateInterpolator());
            mIndicatorYAnim.setTarget(this);
            mIndicatorYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mIndicatorTop = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
        }
        mControlYAnim.setIntValues((int) mControlY, (int) mRectY);
        mIndicatorYAnim.setIntValues((int) mIndicatorTop, (int) -mIndicatorBitmap.getHeight() * 2);
        mControlYAnim.start();
        mIndicatorYAnim.start();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private class EaseOutElasticInterpolator implements TimeInterpolator {

        @Override
        public float getInterpolation(float input) {
            if (input == 0f) {
                return 0f;
            }
            if (input == 1.0f) {
                return 1.0f;
            }
            float p = 0.2f;  // bounce strength, affect times
            float s = p / 3f;  // bouce back speed
            return (float) (Math.pow(2, -10 * input) * Math.sin((input - s) * (2 * Math.PI / p)) + 1);
        }
    }
}
