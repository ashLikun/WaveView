package com.ashlikun.waveview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/9/2　10:59
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：贝塞尔三次曲线--旋律视图
 */
public class RhythmWaveView extends View {
    private float mMaxHeight = 0;//最到点
    private float mPerHeight = 0;//最到点

    private float min;//最小x
    private float max;//最大x

    private float φ = 0;//初相
    private float A = mMaxHeight;//振幅
    private float ω;//角频率

    private Paint mPaint;//主画笔
    private Path mPath;//主路径
    private Path mReflexPath;//镜像路径
    private ValueAnimator mAnimator;
    private int mHeight;
    private int mWidth;
    //#1500b6   #2987ff   #6cfdc9  #09db8f
    private int[] colors = new int[]{
            Color.parseColor("#1500b6"),//红
            Color.parseColor("#2987ff"),//橙
            Color.parseColor("#6cfdc9"),//黄
            Color.parseColor("#09db8f")//绿
    };

    public RhythmWaveView(Context context) {
        this(context, null);
    }

    public RhythmWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();//初始化
    }

    private void init() {
        //初始化主画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp(2));
        //初始化主路径
        mPath = new Path();
        mReflexPath = new Path();
        //数字时间流
        mAnimator = ValueAnimator.ofFloat(0, (float) (2 * Math.PI));
        mAnimator.setDuration(1000);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                φ = (float) animation.getAnimatedValue();
                A = (float) (mMaxHeight * mPerHeight * (1 - (float) animation.getAnimatedValue() / (2 * Math.PI)));
                invalidate();
            }
        });
    }

    /**
     * 设置高的百分比
     *
     * @param perHeight
     */
    public void setPerHeight(float perHeight) {
        mPerHeight = perHeight;
        mAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mMaxHeight = mHeight / 2 * 0.9f;
        min = -mWidth / 2;
        max = mWidth / 2;
        handleColor();
        setMeasuredDimension(mWidth, mHeight);
    }


    private void handleColor() {


        mPaint.setShader(
                new LinearGradient(
                        (int) min, 0, (int) max, 0,
                        colors, getLinearGradientPos(),
                        Shader.TileMode.CLAMP
                ));
    }

    public float[] getLinearGradientPos() {
        int size = colors.length;
        float[] pos = new float[size];
        for (int i = 0; i < size; i++) {
            pos[i] = (i + 1f) / size;
        }
        return pos;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        mReflexPath.reset();
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        formPath(0);
        mPaint.setAlpha(255);
        canvas.drawPath(mPath, mPaint);
        mPaint.setAlpha(66);
        canvas.drawPath(mReflexPath, mPaint);
        canvas.restore();
    }

    /**
     * 对应法则
     *
     * @param x 原像(自变量)
     * @return 像(因变量)
     */
    private float f(double x, float A) {
        double len = max - min;
        double a = 4 / (4 + Math.pow(rad(x / Math.PI * 800 / len), 4));
        double aa = Math.pow(a, 2.5);
        float ω = (float) (2 * Math.PI / (rad(len) / 2));
        float y = (float) (aa * A * Math.sin(ω * rad(x) - φ));
        return y;
    }

    private void formPath(int size) {
        mPath.moveTo((float) min, (float) f(min, A));
        mReflexPath.moveTo((float) min, (float) f(min, A));
        for (double x = min; x <= max; x++) {
            float y = f(x, A);
            mPath.lineTo((float) x, y);
            mReflexPath.lineTo((float) x, -(float) y);
        }

    }

    private double rad(double deg) {
        return deg / 180 * Math.PI;
    }

    protected float dp(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }
}
