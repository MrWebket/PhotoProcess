package com.hope.photoprocess.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class HeaderLayout extends RelativeLayout {
	
	
	Paint paint = null;

	public HeaderLayout(Context paramContext) {
		super(paramContext);
		init();
	}

	public HeaderLayout(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public HeaderLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init();
	}

	private void init() {
		setWillNotDraw(false);
		requestLayout();
		
		paint = new Paint();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 把整张画布绘制成白色
		canvas.drawColor(Color.parseColor("#f6f6f6"));

		// 去锯齿
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#f6f6f6"));
		paint.setStyle(Paint.Style.FILL);

		int top = getTop();
		int right = getRight();
		int bottom = getBottom();
		float left = getLeft();
		int width = getWidth();
		int height = getHeight();
		canvas.drawRect(left, top, right, bottom, paint);
		
		paint.setColor(Color.parseColor("#cccccc"));
		paint.setStyle(Paint.Style.STROKE);
		float interval = 1.5f;
		paint.setStrokeWidth(interval);

		canvas.drawLine(0, height - paint.getStrokeWidth(), width, height - paint.getStrokeWidth(), paint);
	}
}
