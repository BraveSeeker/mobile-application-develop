package com.example.weatherforcast.utils;

import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static com.example.weatherforcast.slice.MainAbilitySlice.LABEL_LOG;


public class HttpUtil {
    public static String sendGetRequest(Context context, String urlString) {
        return sendRequest(context, urlString, "GET", null, null);
    }

    private static String sendRequest(Context context, String urlString, String method, String token, String data) {
        String result = null;

        NetManager netManager = NetManager.getInstance(context);

        if (!netManager.hasDefaultNet()) {
            return null;
        }
        NetHandle netHandle = netManager.getDefaultNet();

        // 可以获取网络状态的变化
        NetStatusCallback callback = new NetStatusCallback() {
            // 重写需要获取的网络状态变化的override函数
        };
        netManager.addDefaultNetStatusCallback(callback);

        // 通过openConnection来获取URLConnection
        HttpURLConnection connection = null;
        try {
//            String urlString = "EXAMPLE_URL"; // 开发者根据实际情况自定义EXAMPLE_URL
            URL url = new URL(urlString);

            URLConnection urlConnection = netHandle.openConnection(url,
                    java.net.Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
                connection.setRequestMethod(method);
                if (token != null) {
                    connection.setRequestProperty("token", token);
                }

//                if(data!=null &&("put".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method))){
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty("Content-Type","application/json;charset=utf8");
//                }
                connection.connect();

                // 之后可进行url的其他操作
                if (data != null && ("put".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method))) {

                    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
                    OutputStream os = connection.getOutputStream();
                    os.write(bytes);
                    os.flush();
                }
                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream is = connection.getInputStream();
                    StringBuilder builder = new StringBuilder();
                    byte[]bs = new byte[1024];
                    int len = -1;
                    while((len=is.read(bs))!=-1){
                        builder.append(new String(bs,0,len));
                    }
                    result=builder.toString();
                    System.out.println("*******>"+result);
                }
            }
        } catch (IOException e) {
            HiLog.debug(LABEL_LOG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        HiLog.debug(LABEL_LOG, "获取数据完成");
        return result;
    }

}