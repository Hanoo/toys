package cc.tuinvshen;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import share.Utils;

public class ImgSpider {
	
	private static final String site_id = "http://www.tuinvshen.cc";
	private static final String SEP = "/";
	private static final String TITLE = "MeiNv";
	private static final String ALBUM = "yanpanpan_ac661c5f";
	private static final String SUFFIX_STRING = ".html";

	public static void main(String[] args) {
//        imgExtract(TITLE, ALBUM);
		albumExtract();
	}

	private static void albumExtract(){
		String titlePage = site_id + SEP + TITLE;
		String html = Utils.getPageHtml(titlePage);
		try {
			List<String> albums = getAlbumUrl(html);
			for(String album : albums) {
				imgExtract(TITLE, album);
				Thread.sleep(10000);
			}
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发起Http请求获取详情页页面
	 */
	private static void imgExtract(String title, String album){
		System.out.println("当前专辑名称：" + album);
		int i = 0;
		while(true) {
			String page;
			if(i==0) {
				page = site_id + SEP + title + SEP + album + SUFFIX_STRING;
			} else {
				page = site_id + SEP + title + SEP + album + "_" + i + SUFFIX_STRING;
			}

			String html = Utils.getPageHtml(page);
			try {

				if(null != html && html.length()>0) {
					List<String> imgUrls = getImgUrl(html);

					for (String imgUrl : imgUrls) {
						new Thread(new ImageSaver(imgUrl, SEP+TITLE+SEP+album)).start();
					}
					i++;
					Thread.sleep(10000);
				} else {
					System.out.println("请求失败，按越界处理，捕获终止！");
					System.out.println("页面总计："+i);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	/**
	 * 从HTML中抽取图片的地址
	 * @param source
	 * @return
	 * @throws ParserException
	 */
	private static List<String> getAlbumUrl(String source) throws ParserException{
		List<String> list = new LinkedList<String>();
		Parser parser = Parser.createParser(source, "UTF-8");
		NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {

			private static final long serialVersionUID = 8280758857492663073L;

			// 实现该方法,用以过滤标签
			public boolean accept(Node node) {
				if (node instanceof LinkTag)
					return true;
				return false;
			}
		});

		for (int i = 0; i < nodeList.size(); i++) {
			LinkTag lt = (LinkTag) nodeList.elementAt(i);
			String href = lt.getAttribute("href");
			if(null!=href && href.startsWith(SEP+TITLE) && href.endsWith(SUFFIX_STRING)) {
				list.add(href.substring(TITLE.length()+1, href.indexOf(SUFFIX_STRING)));
			}
		}
		return list;
	}
}