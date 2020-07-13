package com.e.polemyiteration1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CircleProgressBar extends View {
    private final String TAG = "SuperCircleView";

    private ValueAnimator valueAnimator;
    private int mViewCenterX;
    private int mViewCenterY;

    private int mMinRadio;
    private float mRingWidth;
    private int mMinCircleColor;
    private int mRingNormalColor;
    private Paint mPaint;
    private int[] color = new int[3];

    private RectF mRectF;
    private int mSelectRing = 0;
    private int mMaxValue;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        mMinRadio = a.getInteger(R.styleable.CircleProgressBar_min_circle_radio, 90);

        mRingWidth = a.getFloat(R.styleable.CircleProgressBar_ring_width, 40);


        mMinCircleColor = a.getColor(R.styleable.CircleProgressBar_circle_color, context.getResources().getColor(R.color.black));

        mRingNormalColor = a.getColor(R.styleable.CircleProgressBar_ring_normal_color, context.getResources().getColor(R.color.gray));

        mSelectRing = a.getInt(R.styleable.CircleProgressBar_ring_color_select, 0);

        mMaxValue = a.getInt(R.styleable.CircleProgressBar_max_Value, 100);

        a.recycle();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setAntiAlias(true);

        this.setWillNotDraw(false);


        color[0] = Color.parseColor("#FBBD00");
        color[1] = Color.parseColor("#FBBD00");
        color[2] = Color.parseColor("#FB754B");
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mViewCenterX = viewWidth / 2;
        mViewCenterY = viewHeight / 2;

        mRectF = new RectF(mViewCenterX - mMinRadio - mRingWidth / 2, mViewCenterY - mMinRadio - mRingWidth / 2, mViewCenterX + mMinRadio + mRingWidth / 2, mViewCenterY + mMinRadio + mRingWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mMinCircleColor);
        canvas.drawCircle(mViewCenterX, mViewCenterY, mMinRadio, mPaint);

        drawNormalRing(canvas);

        drawColorRing(canvas);
    }


    /**
     * set the color of ring
     * @param canvas
     */
    private void drawColorRing(Canvas canvas) {
        Paint ringColorPaint = new Paint(mPaint);
        ringColorPaint.setStyle(Paint.Style.STROKE);
        ringColorPaint.setStrokeWidth(mRingWidth);
        ringColorPaint.setShader(new SweepGradient(mViewCenterX, mViewCenterX, color, null));
        canvas.rotate(-90, mViewCenterX, mViewCenterY);
        canvas.drawArc(mRectF, 360, mSelectRing, false, ringColorPaint);
        ringColorPaint.setShader(null);
    }

    /**
     * draw required ring on canvas
     * @param canvas
     */
    private void drawNormalRing(Canvas canvas) {
        Paint ringNormalPaint = new Paint(mPaint);
        ringNormalPaint.setStyle(Paint.Style.STROKE);
        ringNormalPaint.setStrokeWidth(mRingWidth);
        ringNormalPaint.setColor(mRingNormalColor);
        canvas.drawArc(mRectF, 360, 360, false, ringNormalPaint);
    }

    /**
     * Use this method to set value for a circle progress bar
     *
     * @param value value
     * @param textView  use textView to display value
     */
    public void setValue(int value, TextView textView, boolean isPollen) {
        int start = 0;
        int end ;
        if (isPollen) {
            end = value/5;
            if(value < 20){textView.setText("Low");}
            else if(value < 50) {textView.setText("Mod");}
            else {textView.setText("High");}
            startAnimator(start, end, 2000);
        } else {
            if(value > 100){value = 100;}
            end = value;
            textView.setText(String.valueOf(value));
            startAnimator(start, end, 2000);
        }
    }

    /**
     * Use this method to start animation of loading data.
     * @param start refer to the beginning of range, usually set 0.
     * @param end refer to value
     */
    private void startAnimator(int start, int end, long animTime) {
        valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(animTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = Integer.valueOf(String.valueOf(animation.getAnimatedValue()));
                mSelectRing=(int) (360 * (i / 100f));
                invalidate();
            }
        });
        valueAnimator.start();
    }

}

