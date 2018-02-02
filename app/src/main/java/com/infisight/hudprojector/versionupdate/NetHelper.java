package com.infisight.hudprojector.versionupdate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 需要连接服务器模块
 * @author Administrator
 *
 */
public class NetHelper {
	public static String httpStringGet(String url) throws Exception {
//        return httpStringGet(url, "utf-8");
        return null;
    }

    /**
     * 
     * @param url
     * @return
     */
    public static Drawable loadImage(String url) {
        try {
            return Drawable.createFromStream(
                    (InputStream) new URL(url).getContent(), "test");
        } catch (MalformedURLException e) {
            Log.e("exception", e.getMessage());
        } catch (IOException e) {
            Log.e("exception", e.getMessage());
        }
        return null;
    }

//    public static String httpStringGet(String url, String enc) throws Exception {
//        // This method for HttpConnection
//        String page = "";
//        BufferedReader bufferedReader = null;
//        try {
//            HttpClient client = new DefaultHttpClient();
//            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
//                    "android");
//
//             HttpParams httpParams = client.getParams();
//             HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
//             HttpConnectionParams.setSoTimeout(httpParams, 5000);
//
//            HttpGet request = new HttpGet();
//            request.setHeader("Content-Type", "text/plain; charset=utf-8");
//            request.setURI(new URI(url));
//            HttpResponse response = client.execute(request);
//            bufferedReader = new BufferedReader(new InputStreamReader(response
//                    .getEntity().getContent(), enc));
//
//            StringBuffer stringBuffer = new StringBuffer("");
//            String line = "";
//
//            String NL = System.getProperty("line.separator");
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line + NL);
//            }
//            bufferedReader.close();
//            page = stringBuffer.toString();
//            Log.i("page", page);
//            System.out.println(page + "page");
//            return page;
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    Log.d("BBB", e.toString());
//                }
//            }
//        }
//    }

    public static boolean checkNetWorkStatus(Context context) {
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
            Log.i("NetStatus", "The net was connected");
        } else {
            result = false;
            Log.i("NetStatus", "The net was bad!");
        }
        return result;
    }
}
