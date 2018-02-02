package com.infisight.hudprojector.versionupdate;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
/**
 * 提示对话框
 * @author Administrator
 *
 */

public class DialogHelper {
	public static void Alert(Context ctx, CharSequence title,
			CharSequence message, CharSequence okText,
			OnClickListener oklistener) {
		AlertDialog.Builder builder = createDialog(ctx, title, message);
		builder.setPositiveButton(okText, oklistener);
		builder.create().show();
	}

	public static void Alert(Context ctx, int titleId, int messageId,
			int okTextId, OnClickListener oklistener) {
		Alert(ctx, ctx.getText(titleId), ctx.getText(messageId),
				ctx.getText(okTextId), oklistener);
	}

	/**
	 * 
	 * @param ctx context
	 * @param title 标题
	 * @param message 信息
	 * @param okText OK button
	 * @param oklistener OK BUTTON LISTENER
	 * @param cancelText CANCEL BUTTON
	 * @param cancellistener CANCEL BUTTON LISTENER
	 */
	public static void Confirm(Context ctx, CharSequence title,
			CharSequence message, CharSequence okText,
			OnClickListener oklistener, CharSequence cancelText,
			OnClickListener cancellistener) {
		AlertDialog.Builder builder = createDialog(ctx, title, message);
		builder.setPositiveButton(okText, oklistener);
		builder.setNegativeButton(cancelText, cancellistener);
		builder.create().show();
	}
	/**
	 * 
	 * @param ctx
	 * @param titleId
	 * @param messageId
	 * @param okTextId
	 * @param oklistener
	 * @param cancelTextId
	 * @param cancellistener
	 */
	public static void Confirm(Context ctx, int titleId, int messageId,
			int okTextId, OnClickListener oklistener, int cancelTextId,
			OnClickListener cancellistener) {
		Confirm(ctx, ctx.getText(titleId), ctx.getText(messageId),
				ctx.getText(okTextId), oklistener, ctx.getText(cancelTextId),
				cancellistener);
	}

	private static AlertDialog.Builder createDialog(Context ctx,
			CharSequence title, CharSequence message) {
		AlertDialog.Builder builder = new Builder(ctx);
		builder.setMessage(message);
		if (title != null) {
			builder.setTitle(title);
		}
		return builder;
	}

	@SuppressWarnings("unused")
	private static AlertDialog.Builder createDialog(Context ctx, int titleId,
			int messageId) {
		AlertDialog.Builder builder = new Builder(ctx);
		builder.setMessage(messageId);
		builder.setTitle(titleId);
		return builder;
	}

	public static void ViewDialog(Context ctx, CharSequence title, View view,
			CharSequence okText, OnClickListener oklistener,
			CharSequence cancelText, OnClickListener cancellistener) {

	}

	public static void ViewDialog(Context ctx, int titleId, View view,
			int okTextId, OnClickListener oklistener, int cancelTextId,
			OnClickListener cancellistener) {

		ViewDialog(ctx, ctx.getText(titleId), view, ctx.getText(okTextId),
				oklistener, ctx.getText(cancelTextId), cancellistener);

	}

	//
	public static void SetDialogShowing(DialogInterface dialog, boolean showing) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, showing);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
