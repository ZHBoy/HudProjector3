package com.infisight.hudprojector.widget;

/**
 * Created by Administrator on 2015/9/17.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class SurfacePicture extends SurfaceView {

    public SurfacePicture(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public SurfacePicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SurfacePicture(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public boolean saveScreenshot(String path) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        doDraw();
        File file = new File(path);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("Panel", "FileNotFoundException", e);
            return false;
        } catch (IOException e) {
            Log.e("Panel", "IOEception", e);
            return false;
        }

    }

    private void doDraw() {
        // TODO Auto-generated method stub

    }
}

