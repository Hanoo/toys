package share;
import cc.tuinvshen.ImageSaver;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.List;

public class Utils {
    public static void writeToFile(InputStream is, OutputStream os) throws IOException {
        byte[] buff = new byte[1024];
        while(true) {
            int readed = is.read(buff);
            if(readed == -1) {
                break;
            }
            byte[] temp = new byte[readed];
            System.arraycopy(buff, 0, temp, 0, readed);
            //写入文件
            os.write(temp);
        }
    }

    /*
     * 
     */
    public static String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }

    public static CloseableHttpClient getClient(){
        //HttpClient 超时配置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD)
                .setConnectionRequestTimeout(6000)
                .setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        return httpClient;
    }

    /**
     * 发起Get请求，获取页面源代码
     * @return
     */
    public static String getPageHtml(String url){
        //HttpClient 超时配置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD)
                .setConnectionRequestTimeout(6000)
                .setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
        httpGet.addHeader("Cookie",
                "_gat=1; nsfw-click-load=off; gif-click-load=on; _ga=GA1.2.1861846600.1423061484");
        String html = "";
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200) {
                InputStream in = response.getEntity().getContent();
                html = Utils.convertStreamToString(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return html;
    }
}
