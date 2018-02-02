package com.infisight.hudprojector.util;

import java.util.ArrayList;
import java.util.List;

import com.infisight.hudprojector.util.LrcProcess.LrcContent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 项目名称 : FRMusic 自定义绘画歌词，产生滚动效果
 * 
 * @author 作者 : fengrui
 * @date 创建时间：2015年8月7日 上午11:20:28
 * @version 1.0
 */

public class LrcView extends TextView {

	private float width;
	private float high;
	private Paint CurrentPaint;
	private Paint NotCurrentPaint;
	private float TextHigh = 43;
	private float TextSize = 30;
	private int Index = 0;

	private List<LrcContent> mSentenceEntities = new ArrayList<LrcContent>();

	public void setSentenceEntities(List<LrcContent> mSentenceEntities) {
		this.mSentenceEntities = mSentenceEntities;
	}

	public LrcView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public LrcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		setFocusable(true);

		// 高亮部分
		CurrentPaint = new Paint();
		CurrentPaint.setAntiAlias(true);
		CurrentPaint.setTextAlign(Paint.Align.CENTER);

		// 非高亮部分
		NotCurrentPaint = new Paint();
		NotCurrentPaint.setAntiAlias(true);
		NotCurrentPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (canvas == null) {
			return;
		}

		// CurrentPaint.setColor(Color.argb(210, 251, 248, 29));
//		CurrentPaint.setColor(Color.rgb(228, 53, 110));
		CurrentPaint.setColor(Color.rgb(43, 193, 254));
		NotCurrentPaint.setColor(Color.argb(255, 255, 255, 255));

		CurrentPaint.setTextSize(TextHigh);
		CurrentPaint.setTypeface(Typeface.SERIF);

		NotCurrentPaint.setTextSize(TextSize);
		NotCurrentPaint.setTypeface(Typeface.DEFAULT);

		try {
			setText("");
			canvas.drawText(mSentenceEntities.get(Index).getLrc(), width / 2,
					high / 2, CurrentPaint);

			float tempY = high / 2;
			// 画出本句之前的句子
			for (int i = Index - 1; i >= 0; i--) {
				// 向上推移
				tempY = tempY - TextHigh;

				canvas.drawText(mSentenceEntities.get(i).getLrc(), width / 2,
						tempY, NotCurrentPaint);
			}
			tempY = high / 2;
			// 画出本句之后的句子
			for (int i = Index + 1; i < mSentenceEntities.size(); i++) {
				// 往下推移
				tempY = tempY + TextHigh;
				canvas.drawText(mSentenceEntities.get(i).getLrc(), width / 2,
						tempY, NotCurrentPaint);
			}
		} catch (Exception e) {
			setText("...木有歌词文件，赶紧去下载...");
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		this.width = w;
		this.high = h;
	}

	public void SetIndex(int index) {
		this.Index = index;
	}
}
