package com.infisight.hudprojector.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * 图片翻转
 * 
 * @author hao
 * 
 */

public class ViewAnimation extends Animation {
	public static final int FRONT_ANIM = 0;
	public static final int BACK_ANIM = 1;
	int mCenterX;
	int mCenterY;
	Camera camera = new Camera();
	public int flag;
	int duration = 1000;

	public ViewAnimation(int time) {
		duration = time;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCenterX = width / 2;
		mCenterY = height / 2;
		setDuration(duration);
		setFillAfter(true);
		setInterpolator(new LinearInterpolator());
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final Matrix matrix = t.getMatrix();
		camera.save();
		if (flag == FRONT_ANIM) {
			if (interpolatedTime <= (0.5)) {
				camera.rotateY(interpolatedTime * 2 * 90);
				System.out.println("time:" + interpolatedTime * 2 * 90);
			} else {
				camera.rotateY(90);
			}
		} else if (flag == BACK_ANIM) {
			if (interpolatedTime >= 0.5) {
				camera.rotateY((float) (270 + (interpolatedTime - 0.5) * 90 * 2));
			} else {
				camera.rotateY(270);
			}

		}
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-mCenterX, -mCenterY);
		matrix.postTranslate(mCenterX, mCenterY);

	}
}
