package com.example.quickly1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ChangeColorIconWithText extends View {

	private int mColor = 0xFF45C01A;
	private Bitmap mIconBitmap;
	private String mText = "微信";
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
	/**
	 * 在内存中绘图,
	 * 通过mBitmap得到mCanvas,
	 * 通过mCanvas得到mPaint进行绘制
	 */
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;
	
	/**
	 * 不断修改纯色的透明度
	 */
	private float mAlpha = 1.0f;
	
	private Rect mIconRect;
	private Rect mTextBound;	
	private Paint mTextPaint;
	
	public ChangeColorIconWithText(Context context) {
		this(context,null);
		
	}

	public ChangeColorIconWithText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		
	}
	/**
	 * 获取自定义属性的值
	 */
	public ChangeColorIconWithText(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ChangeColorIconWithText);
		int n = a.getIndexCount();
		for(int i = 0; i<n; i++){
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.ChangeColorIconWithText_icon1:
				BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
				mIconBitmap = drawable.getBitmap();
				break;
			case R.styleable.ChangeColorIconWithText_color1:
				mColor = a.getColor(attr, 0xFF45C01A);
				break;
			case R.styleable.ChangeColorIconWithText_text:
				mText = a.getString(attr);
				break;
			case R.styleable.ChangeColorIconWithText_text_size:
				mTextSize = (int) a.getDimension(attr, TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
				break;

			}
		}
		a.recycle();
		
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xff555555);
		
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
				getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());
		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = (getMeasuredHeight() - mTextBound.height())/2 - iconWidth / 2;
		mIconRect = new Rect(left, top, left+iconWidth,top+iconWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//绘制原图
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		int alpha =(int) Math.ceil(255 * mAlpha) ;
		
//内存去准备mBitmap,setAlpha,纯色,xfermode,图标
		setupTargetBitmap(alpha);
		//1. 绘制原文本  2, 绘制变色的文本  
		drawSourceText(canvas,alpha);
		
		drawTargetText(canvas,alpha);
		
		//通过canvas把图标绘制出来
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}
	
	/**
	 * 绘制变色的文本
	 * @param canvas
	 * @param alpha
	 */
private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		
		int x = getMeasuredWidth()/2 - mTextBound.width()/2;
		int y = mIconRect.bottom +mTextBound.height();		
		canvas.drawText(mText, x, y, mTextPaint);
		
	}

private void drawSourceText(Canvas canvas, int alpha) {
		mTextPaint.setColor(0Xff333333);
		mTextPaint.setAlpha(255-alpha);
		
		int x = getMeasuredWidth()/2 - mTextBound.width()/2;
		int y = mIconRect.bottom +mTextBound.height();		
		canvas.drawText(mText, x, y, mTextPaint);
	}

/**
 * 在内存中回执可变色的Icon
 */
	private void setupTargetBitmap(int alpha) {
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(),
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);
		//paint结束之后,用canvas绘制一个纯色
		mCanvas.drawRect(mIconRect, mPaint);
		//设置xfermode
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect,mPaint);
	}

	private static final String INSTANCE_STATUS = "instance_status";
	private static final String STATUS_ALPHA = "status_alpha";
	@Override
	protected Parcelable onSaveInstanceState() {
	
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
		bundle.putFloat(STATUS_ALPHA, mAlpha);
		
		return bundle;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {

		if(state instanceof Bundle){
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATUS_ALPHA);
		    super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
		    return;
		}
		super.onRestoreInstanceState(state);
	}
	
	public void setIconAlpha(float alpha){
		this.mAlpha = alpha;
		invalidateView();
	}
	
	
/**
 * 重绘
 */
	private void invalidateView() {
		if(Looper.getMainLooper() == Looper.myLooper()){
			invalidate();
		}else
		{
			postInvalidate();
		}
		
	}
}
