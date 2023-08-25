package com.cx.open_applist_plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class OpenAppListModule extends UniModule {

    public String TYPE      = "type";           // 获取的APP类型
    public String QUALITY   = "quality";        // 压缩质量
    public String WIDTH     = "width";          // 获取图标指定宽度
    public String HEIGHT    = "height";         // 获取图标指定高度

    @UniJSMethod(uiThread = true)
    public void getOpenAppList(JSONObject options, UniJSCallback callback, UniJSCallback errCallback) {
        try {
            Log.i("appList", "开始获取");
            //  如果参数不存在，则设置默认值
            if (options.getString(TYPE) == null) {
                options.put("type", "image/*");
            }
            if (options.getInteger(QUALITY) == null) {
                options.put("quality", 100);
            }
            if (options.getInteger(WIDTH) == null) {
                options.put("width", 60);
            }
            if (options.getString(HEIGHT) == null) {
                options.put("height", 60);
            }
            Context context = mUniSDKInstance.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType(options.getString(TYPE));

            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            JSONArray appList = new JSONArray();

            for (ResolveInfo resolveInfo : resolveInfoList) {
                JSONObject appInfo = new JSONObject();
                appInfo.put("name", resolveInfo.activityInfo.applicationInfo.loadLabel(packageManager).toString());
                appInfo.put("packageName", resolveInfo.activityInfo.packageName);

                Drawable icon = resolveInfo.activityInfo.applicationInfo.loadIcon(packageManager);

                Bitmap bitmap = drawableToBitmap(icon, options.getInteger(WIDTH), options.getInteger(HEIGHT));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, options.getInteger(QUALITY), baos);
                byte[] bytes = baos.toByteArray();
                String iconBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

                appInfo.put("icon", iconBase64);

                appList.add(appInfo);
            }
            Log.d("appList", appList.toString());
            Log.i("appList", "获取结束");
            if (callback != null) {
                callback.invokeAndKeepAlive(appList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (errCallback != null) {
                JSONObject data = new JSONObject();
                data.put("err", e);
                errCallback.invoke(data);
            }
        }
    }
    // drawable转bitmap
    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmap = bitmapDrawable.getBitmap();
        } else {
//            int width = drawable.getIntrinsicWidth();
//            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }

        return bitmap;
    }
}