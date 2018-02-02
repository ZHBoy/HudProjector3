package com.infisight.hudprojector.widget;

/**
 * 柱状图
 */
import java.math.BigDecimal;
import java.util.ArrayList;

import com.infisight.hudprojector.data.TrafficInfo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RainAnimotion extends SurfaceView implements
		SurfaceHolder.Callback {
	private int currentX;
	private int score;

	private int oldX;

	private SurfaceHolder sfh;

	private boolean isRunning = true;

	private int tick = 20; // 时间间隔(ms)
	private int bottom = 125; // 坐标系地段距离框架顶端的距离
	private int top = 30; // 坐标系顶端距离框架顶端框的距�?
	private int lift = 60; // 坐标系左边距离框架左边框的距�?
	static int right = 30; // 坐标系右边距离框架左边的距离(!)
	static int gapX = 100; // 两根竖线间的间隙(!)
	private int gapY = 20; // 两根横线间的间隙

	private float unit = 1f;
	private String unitString = "b";

	private ArrayList<TrafficInfo> mData = new ArrayList<TrafficInfo>();

	public RainAnimotion(Context context) {
		super(context);
		init(context);
	}

	// 在这里初始化才是�?��始化的�?
	public RainAnimotion(Context context, AttributeSet atr) {
		super(context, atr);
		init(context);
	}

	private void init(Context context) {
		setZOrderOnTop(true);// 设置置顶（不然实现不了�?明）
		sfh = this.getHolder();
		sfh.addCallback(this);
		sfh.setFormat(PixelFormat.TRANSLUCENT);// 设置背景透明

		bottom = dip2px(context, 150);
		gapY = (bottom - top) / 7;
	}

	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public void setData(ArrayList<TrafficInfo> data) {
		// mData = data;
		if (data.size() <= 31) {
			mData = data;
		} else {
			for (int i = 0; i < 31; i++) {
				if (!data.get(i).equals(null)) {
					mData.add(data.get(i));
				}
			}
		}
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("系统消息", "surfaceCreated");

		// 加入下面这三句是当抽屉隐藏后，打�?��防止已经绘过图的区域闪烁，所以干脆就从新�?��绘制�?
		isRunning = true;
		currentX = 0;
		clearCanvas();
		gridDraw();
		drawChartLine();
		// Thread thread = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// gridDraw();
		// drawChartLine();
		// }
		// });
		//
		// thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.i("系统信息", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.i("系统信息", "surfaceDestroyed");

		// 加入这个变量是为了控制抽屉隐藏时不会出现异常�?
		isRunning = false;
	}
	/**
	 * 绘制坐标轴
	 */
	protected void gridDraw() {
		long max = mData.get(0).traffic;
		long temMax = max;
		long min = max;
		long temMin = max;
		float space = 0f;// 平均�?
		for (int i = 1; i < mData.size(); i++) {
			if (max < mData.get(i).traffic) {
				max = mData.get(i).traffic;
			}
			if (min > mData.get(i).traffic) {
				min = mData.get(i).traffic;
			}
			temMax = max;
			temMin = min;
		}
		switchUnit(temMax);
		// temMax = (long) (temMax/unit);
		// temMin = (long) (temMin/unit);
		space = (temMax - temMin) / (unit * 7.0f);

		Canvas canvas = sfh.lockCanvas();

		Paint mbackLinePaint = new Paint();// 用来画坐标系�?
		mbackLinePaint.setColor(Color.WHITE);
		mbackLinePaint.setAntiAlias(true);
		mbackLinePaint.setStrokeWidth(1);
		mbackLinePaint.setStyle(Style.FILL);

		TextPaint mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(22F);// 设置温度值的字体大小

		Paint mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(Color.WHITE);

		// 绘制坐标�?
		for (int i = 0; i < 8; i++) {
			if (i == 7)
				canvas.drawLine(lift, bottom, lift + gapX * mData.size(),
						bottom, mbackLinePaint);
			else
				canvas.drawCircle(lift, top + gapY * i + 4, 2, mPointPaint);
			// canvas.drawLine(lift, top + gapY * i, lift + gapX * mData.size(),
			// top + gapY * i, mbackLinePaint);
			mTextPaint.setTextAlign(Align.RIGHT);
			if (temMax < 0.8) {
				float result = 0.1f * i;
				canvas.drawText("" + result, lift - 2, bottom + 3 - gapY * i,
						mTextPaint);
			} else {
				float result = temMin + space * i;// 精确的各个节点的�?
				BigDecimal b = new BigDecimal(result);// 新建�?��BigDecimal
				float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
						.floatValue();// 进行小数点一位保留处理现实在坐标系上的数�?
				canvas.drawText("" + displayVar, lift - 2, bottom + 3 - gapY
						* i, mTextPaint);
			}
		}

		for (int i = 0; i < mData.size(); i++) {

			if (i == 0) {
				canvas.drawLine(lift + gapX * i, top, lift + gapX * i, bottom,
						mbackLinePaint);
				mTextPaint.setTextAlign(Align.CENTER);
//				String text = mData.get(i).date;
//				StaticLayout layout = new StaticLayout(text, mTextPaint, 60,
//						Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
				canvas.translate(0, bottom + 22);
//				layout.draw(canvas);
			} else {
				mTextPaint.setTextAlign(Align.CENTER);
				String text = mData.get(i).date;
				StaticLayout layout = new StaticLayout(text, mTextPaint, 60,
						Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
				canvas.translate(gapX, 0);
				layout.draw(canvas);
			}
			// canvas.drawText(mData.get(i).date, lift + gapX * i + gapX / 2,
			// bottom + 22,mTextPaint);
		}
		sfh.unlockCanvasAndPost(canvas);
	}

	protected void GridDraw(Canvas canvas) {
		if (canvas == null) {
			return;
		}
		long max = mData.get(0).traffic;
		long temMax = max;
		long min = max;
		long temMin = max;
		float space = 0f;// 平均�?
		for (int i = 1; i < mData.size(); i++) {
			if (max < mData.get(i).traffic) {
				max = mData.get(i).traffic;
			}
			if (min > mData.get(i).traffic) {
				min = mData.get(i).traffic;
			}
			temMax = max;
			temMin = min;
		}
		switchUnit(temMax);
		// temMax = (long) (temMax/unit);
		// temMin = (long) (temMin/unit);
		space = (temMax - temMin) / (unit * 7.0f);

		Paint mbackLinePaint = new Paint();// 用来画坐标系�?
		mbackLinePaint.setColor(Color.DKGRAY);
		mbackLinePaint.setAntiAlias(true);
		mbackLinePaint.setStrokeWidth(1);
		mbackLinePaint.setStyle(Style.FILL);

		Paint mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		// mTextPaint.setTextAlign(Align.RIGHT);
		mTextPaint.setColor(Color.DKGRAY);
		mTextPaint.setTextSize(12F);// 设置温度值的字体大小

		Paint mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(Color.DKGRAY);
		// 绘制坐标�?
		for (int i = 0; i < 8; i++) {
			if (i == 7)
				canvas.drawLine(lift, bottom, lift + gapX * mData.size(),
						bottom, mbackLinePaint);
			// canvas.drawLine(lift, top + gapY * i, lift + gapX * mData.size(),
			// top + gapY * i, mbackLinePaint);
			else
				canvas.drawCircle(lift, top + gapY * i + 4, 2, mPointPaint);
			mTextPaint.setTextAlign(Align.RIGHT);
			if (temMax < 0.8) {
				float result = 0.1f * i;
				canvas.drawText("" + result, lift - 2, bottom + 3 - gapY * i,
						mTextPaint);
			} else {
				float result = temMin + space * i;// 精确的各个节点的�?
				BigDecimal b = new BigDecimal(result);// 新建�?��BigDecimal
				float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
						.floatValue();// 进行小数点一位保留处理现实在坐标系上的数�?
				canvas.drawText("" + displayVar, lift - 2, bottom + 3 - gapY
						* i, mTextPaint);
			}
		}
		for (int i = 0; i < mData.size(); i++) {
			if (i == 0)
				canvas.drawLine(lift + gapX * i, top, lift + gapX * i, bottom,
						mbackLinePaint);
			mTextPaint.setTextAlign(Align.CENTER);
			canvas.drawText(mData.get(i).date, lift + gapX * i + gapX / 2,
					bottom + 14, mTextPaint);

		}
	}

	private void drawChartLine() {
		while (isRunning) {
			if (currentX == 0)
				oldX = bottom + top;
			Canvas canvas = sfh.lockCanvas(new Rect(lift, oldX - currentX, lift
					+ gapX * mData.size(), oldX));// 范围选取正确
			Log.i("系统消息", "oldX = " + oldX + "  currentX = " + currentX);
			try {
				// score = bottom - currentX;
				// drawChart(score);// 绘制
				drawChart(canvas);// 绘制
				currentX = currentX + 10;// �?���?

				if (currentX >= bottom + top) {
					// 如果到了终点，则清屏重来
					break;
				}

				try {
					Thread.sleep(tick);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sfh.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
			}
		}
	}

	/**
	 * 画柱形
	 * 
	 * @param canvas
	 */
	void drawChart(Canvas canvas) {

		Paint mLinePaint = new Paint();// 用来画折�?
		mLinePaint.setColor(Color.CYAN);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(2);
		mLinePaint.setStyle(Style.FILL);

		long max = mData.get(0).traffic;
		long temMax = max;
		long min = max;
		long temMin = max;
		float spacePX = 0f;// 平均像素�?
		for (int i = 1; i < mData.size(); i++) {
			if (max < mData.get(i).traffic) {
				max = mData.get(i).traffic;
			}
			if (min > mData.get(i).traffic) {
				min = mData.get(i).traffic;
			}
			temMax = max;
			temMin = min;
		}
		if ((temMax - temMin) <= 0)
			spacePX = 1;
		else
			spacePX = (float) ((bottom - top) / (float) (temMax - temMin));// 平均每个温度值说占用的像素�?

		Paint mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		// mTextPaint.setTextAlign(Align.RIGHT);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(18F);// 设置温度值的字体大小

		float cx = 0f;
		float cy = 0f;
		float dx = 0f;
		float dy = 0f;
		for (int j = 1; j < mData.size(); j++) { //此处把j改成1，第1个方块将消失
			cx = lift + gapX * (j-1);
			cy = bottom - (mData.get(j).traffic - temMin) * spacePX;
			dx = lift + gapX * (j );
			dy = bottom - (mData.get(j).traffic - temMin) * gapY * 10;

			if (mData.get(j).traffic == 0) {
				mTextPaint.setTextAlign(Align.CENTER);
				String textString = mData.get(j).traffic / 1024 + "KB";
				canvas.drawText(textString, cx + gapX / 2, cy - 10, mTextPaint);
				canvas.drawRect(new RectF(cx + 5, bottom - 5, dx - 5, bottom),
						mLinePaint);// 当雨量�?�?时，绘制2px的矩形，表示这里有�?
			} else {
				mTextPaint.setTextAlign(Align.CENTER);
				float result = (mData.get(j).traffic / unit);
				BigDecimal b = new BigDecimal(result);// 新建�?��BigDecimal
				float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
						.floatValue();// 进行小数点一位保留处理现实在坐标系上的数�?
				String textString = displayVar + unitString;
				canvas.drawText(textString, cx + gapX / 2, cy - 4, mTextPaint);
				canvas.drawRect(new RectF(cx + 5, cy, dx - 5, bottom),
						mLinePaint);
			}

		}

	}

	/**
	 * 把画布擦干净，准备绘图使用�?
	 */
	private void clearCanvas() {
		Canvas canvas = sfh.lockCanvas();

		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);// 清除画布

//		GridDraw(canvas);

		sfh.unlockCanvasAndPost(canvas);
	}

	private void switchUnit(long max) {
		if (max / 1024 > 0) {
			unit = 1024;
			unitString = "K";
			if (max / (1024 * 1024) > 0) {
				unit = 1024 * 1024;
				unitString = "M";
				if (max / (1024 * 1024 * 1024) > 0) {
					unit = 1024 * 1024 * 1024;
					unitString = "G";
				}
			}
		} else {
			unit = 1;
			unitString = "b";
		}
	}
}
