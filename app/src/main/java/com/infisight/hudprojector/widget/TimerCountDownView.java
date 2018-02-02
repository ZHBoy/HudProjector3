package com.infisight.hudprojector.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * 倒计时计时器
 * 
 * @author AdministratorQYL
 * 
 */
public class TimerCountDownView extends View {

	int mMaxSeconds = 0;
	/** 储存最大时间的临时变量 **/
	int tempSeconds = 0;
	float mRateAngle = 0;
	private float mMaxAngle = 0;

	/** 外圈相关 **/
	private float mOutCircleWidth = 30;
//	private int mOutCircleColor = 0xf6967E0;
	private int mOutCircleColor = 0xaa00ff00;
	// 起始角度
	private float mOutStartAngle = 145;
	// 扫描角度
	private float mOutSweepAngle = 250;

	/** 内圈相关 **/
	private float mInCircleWidth = 25;
	private int mInCircleColor = 0xaa00ff00;
	// 起始角度
	private float mInStartAngle = 145;
	// 扫描角度
	private float mInSweepAngle = 250;

	/** 外圈与内圈的距离 **/
	private float mOutAndInPadding = 12; // 外援环和小圆环虹之间的间隔

	// 发起重回命令
	private int mActionHeartbeat = 1;

	// 间隔时间
	private int mDelayTime = 1 * 1000;

	private CountdownTimerListener mListener;

	public TimerCountDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == mActionHeartbeat) {

				mMaxAngle = mMaxAngle - mRateAngle;
				mMaxSeconds = mMaxSeconds - 1;
				if (mMaxSeconds >= 0) {
					invalidate();
					mHandler.sendEmptyMessageDelayed(mActionHeartbeat,
							mDelayTime);
					if (mListener != null) {
						mListener.onCountDown(showTheTimer(mMaxSeconds));
						mListener.onTimeArrive(false);
					}
					// Log.d("", "剩余"+mMaxSeconds+"秒" +"  剩余角度："+mMaxAngle);
				} else {
					mListener.onTimeArrive(true);
				}
			}
		};
	};

	public void updateView() {
		mMaxAngle = mOutSweepAngle;
		mMaxSeconds = tempSeconds;
		mHandler.removeMessages(mActionHeartbeat);
		mHandler.sendEmptyMessage(mActionHeartbeat);
	}

	public void destroy() {
		mHandler.removeMessages(100);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		drawInCircle(canvas);
		drawOutCircle(canvas);
	}

	public void drawInCircle(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(mInCircleColor);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(mInCircleWidth);
		float left = mOutAndInPadding + mOutCircleWidth;
		float top = left;
		float right = getWidth() - left;
		float bottom = getHeight() - top;
		RectF oval = new RectF(left, top, right, bottom);
		canvas.drawArc(oval, mInStartAngle, mInSweepAngle, false, paint);

	}

	public void drawOutCircle(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(mOutCircleColor);
		paint.setAlpha(100);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(mOutCircleWidth);

		float left = 1;
		float top = left;
		float right = getWidth() - left;
		float bottom = getHeight() - top;
		RectF oval = new RectF(left + mOutCircleWidth, top + mOutCircleWidth,
				right - mOutCircleWidth, bottom - mOutCircleWidth);

		canvas.drawArc(oval, mOutStartAngle, mMaxAngle, false, paint);

	}

	/**
	 * 设置初始最大时间
	 * 
	 * @param minute
	 *            单位分
	 */
	public void setMaxTime(int seconds) {
		mMaxAngle = mOutSweepAngle;
		tempSeconds = seconds;
		mMaxSeconds = seconds;
		mRateAngle = mMaxAngle / mMaxSeconds;
	}

	public void setOutCicleColor(int color) {
		mOutCircleColor = color;
	}

	public void setOutCicleWidth(int width) {
		mOutCircleWidth = width;
	}

	public void setInCicleColor(int color) {
		mInCircleColor = color;
	}

	public void setInCicleWidth(int width) {
		mInCircleWidth = width;
	}

	public void setOuterAndInerPadding(int padding) {
		mOutAndInPadding = padding;
	}

	public String showTheTimer(int seconds) {
		String timer = "";
		String sminute = "";
		String ssecond = "";
		if (seconds >= 0) {
			int minute = seconds / 60;
			int second = seconds % 60;

			if (minute < 10) {
				sminute = "0" + minute + ":";
			} else {
				sminute = minute + ":";
			}
			if (second < 10) {
				ssecond = "0" + second;
			} else {
				ssecond = second + "";
			}
			timer = ssecond;
		} else {
			timer = "00";
		}
		return timer;
	}

	public interface CountdownTimerListener {

		// 当前倒计时计算的文本 格式 mm-ss
		public void onCountDown(String time);

		// 倒计时是否到达
		public void onTimeArrive(boolean isArrive);
	}

	public void addCountdownTimerListener(CountdownTimerListener listener) {
		mListener = listener;
	}
}
