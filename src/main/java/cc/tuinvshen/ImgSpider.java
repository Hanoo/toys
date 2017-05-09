package cc.tuinvshen;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import share.Utils;

public class ImgSpider {
	
	private static final String site_id = "http://www.tuinvshen.cc";
	private static final String album = "/MaskedQueen/chaobaoheisimeinv";
	private static final String SUFFIX_STRING = ".html";
	private static final int pageCount = 14;

	public static void main(String[] args) {
		
		//HttpClient 超时配置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD)
                                                           .setConnectionRequestTimeout(6000)
                                                           .setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();

        String[] pages = wavePages();
        for(String page : pages) {
            HttpGet httpGet = new HttpGet(page);
            httpGet.addHeader("User-Agent",
            		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
            httpGet.addHeader("Cookie",
            		"_gat=1; nsfw-click-load=off; gif-click-load=on; _ga=GA1.2.1861846600.1423061484");

            // 抓取页面HTML
            String html = "";
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                InputStream in = response.getEntity().getContent();
                html = Utils.convertStreamToString(in);

    			List<String> imgUrls = getImgUrl(html);
    			for(String imgUrl : imgUrls) {
    				new Thread(new ImageSaver(imgUrl, album)).start();;
    			}
    			Thread.sleep(2000);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
	}
	
	/**
	 * 根据页数生成所有页面的访问地址
	 * @return
	 */
	private static String[] wavePages(){
		String[] pages = new String[pageCount];
		for(int i=0;i<pageCount;i++) {
			if(i==0) {
				pages[i] = site_id+album+SUFFIX_STRING;
			} else {
				pages[i] = site_id+album+"_"+i+SUFFIX_STRING;
			}
		}
		return pages;
	}
	
	/**
	 * 从HTML中抽取图片的地址
	 * @param source
	 * @return
	 * @throws ParserException
	 */
	private static List<String> getImgUrl(String source) throws ParserException{
		List<String> list = new LinkedList<String>();
		Parser parser = Parser.createParser(source, "UTF-8");
		NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {

			private static final long serialVersionUID = 8280758857492663073L;

			// 实现该方法,用以过滤标签
			public boolean accept(Node node) {
				if (node instanceof ImageTag)
					return true;
				return false;
			}
		});

		for (int i = 0; i < nodeList.size(); i++) {
			ImageTag it = (ImageTag) nodeList.elementAt(i);
			String imgSrc = it.getAttribute("src");
			if(imgSrc.endsWith("jpg")) {
				String picIdString = imgSrc.substring(imgSrc.lastIndexOf("/")+1,imgSrc.lastIndexOf("."));
				if(picIdString.length()>3) {
					list.add(site_id+imgSrc);
				}
			}
		}
		return list;
	}
}