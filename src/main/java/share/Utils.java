package share;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

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
}
