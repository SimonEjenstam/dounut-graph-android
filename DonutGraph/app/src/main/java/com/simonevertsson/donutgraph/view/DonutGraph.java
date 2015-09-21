package com.simonevertsson.donutgraph.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.simonevertsson.donutgraph.R;

import java.util.ArrayList;

/**
 * Created by simon.evertsson on 2015-09-21.
 */
public class DonutGraph extends View {

    private static final float OUTER_ADJUSTMENT_VALUE = .038f;
    private static final float INNER_ADJUSTMENT_VALUE = .276f;
    private final int backgroundColor;
    private final float donutWidthPx;
    private ValueAnimator animator;

    private float radius;

    private Paint paint;

    Path drawPath;

    RectF circleRect;

    ArrayList<ColoredValue> coloredValues;

    private int maxValue;
    private int valueSum;
    private boolean isAnimating;
    private float currentAnimatedFraction;


    public DonutGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DonutGraph,
                0, 0
        );

        try {
            radius = attrArray.getDimension(R.styleable.DonutGraph_radius, 20.0f);
            backgroundColor = attrArray.getColor(R.styleable.DonutGraph_backgroundColor, 3388901);
            float donutWidthDp = attrArray.getDimension(R.styleable.DonutGraph_donutWidth, 5.0f);
            DisplayMetrics dm = getResources().getDisplayMetrics() ;
            donutWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, donutWidthDp, dm);
        } finally {
            attrArray.recycle();
        }

        coloredValues = new ArrayList<>();
        drawPath = new Path();
        initPaint();
        initDrawingRects();
        valueSum = 0;
        initAnimator();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initAnimator() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setInterpolator(new DecelerateInterpolator(2.0f));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DonutGraph.this.currentAnimatedFraction = valueAnimator.getAnimatedFraction();
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    DonutGraph.this.isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    DonutGraph.this.isAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    DonutGraph.this.isAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    DonutGraph.this.isAnimating = true;
                }
            });
        }
    }

    private void initDrawingRects() {
        circleRect = new RectF();

        float outerAdjust = donutWidthPx/2;
        circleRect.set(outerAdjust, outerAdjust, radius * 2 - outerAdjust, radius * 2 - outerAdjust);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(donutWidthPx);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isInEditMode()) {
            drawEditMode(canvas);
        } else {
            drawColoredValues(canvas);
        }

    }

    private void drawColoredValues(Canvas canvas) {
        float currentAngle = -90;
        float animationFraction = isAnimating ? currentAnimatedFraction : 1f;
        for(ColoredValue coloredValue : coloredValues) {
            paint.setColor(coloredValue.getColor());
            float currentSweep = (animationFraction*360)*((float)coloredValue.getValue()/maxValue);
            drawDonut(canvas, paint, currentAngle, currentSweep);
            currentAngle += currentSweep;
        }

        if(currentAngle < 270) {
            paint.setColor(backgroundColor);
            drawDonut(canvas, paint, currentAngle, (270 - currentAngle));
        }
    }

    public void drawEditMode(Canvas canvas) {
        // green
        setPaintColor(0,255,0);
        drawDonut(canvas,paint, -90,60);

        //red
        setPaintColor(255,0, 0);
        drawDonut(canvas, paint, -30, 60);

        // blue
        setPaintColor(0, 0, 255);
        drawDonut(canvas, paint, 30, 60);

        // yellow
        paint.setColor(backgroundColor);
        drawDonut(canvas, paint, 90, 180);

    }

    public void drawDonut(Canvas canvas, Paint paint, float start,float sweep){
        canvas.drawArc(circleRect, start, sweep, false, paint);
    }

    public void setPaintColor(int r, int g, int b) {
        paint.setARGB(255, r, g, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) radius*2;
        int desiredHeight = (int) radius*2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //70dp exact
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if (widthMode == MeasureSpec.AT_MOST) {
            //wrap content
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    public void addColoredValue(int value, int a, int r, int g, int b) {
        addColoredValue(new ColoredValue(value, a,r,g,b));
    }

    public void addColoredValue(ColoredValue coloredValue) {
        coloredValues.add(coloredValue);
        valueSum += coloredValue.getValue();
        if(valueSum > maxValue) {
            throw new IllegalArgumentException("The sum of values added to the DonutGraph exceeds the maximum value");
        }
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setAnimationDuration(long duration) {
        if(animator != null) {
            animator.setDuration(duration);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void startAnimation() {
        if(animator != null) {
            animator.start();
        }
    }

    public void addColoredValue(int value, int color) {
        addColoredValue(new ColoredValue(value, color));
    }
}
