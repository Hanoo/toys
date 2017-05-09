package pic177;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Catch177PicImage {
	
	// 文件保存的路径
	private static final String FOLDER_STRING = "download";
	// 编码
	private static final String ECODING = "UTF-8";
	// 获取img标签正则
	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	// 获取src路径的正则
	private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";
	
	private static int picCount = 13;

	public static void main(String[] args) throws Exception {
		if(null==args || args.length!=2) {
			System.out.println("输入的参数数量不正确！");
		} else {
			String base_uri = args[0];
			int count = Integer.valueOf(args[1]);
			System.out.println("你要扒的图片地址为："+base_uri);
			System.out.println("目标的页码数量为："+count);
			List<String> imgUrl = new LinkedList<String>();
			int i = 0;
			String name = base_uri.substring(base_uri.lastIndexOf("/")+1);
			System.out.println("保存文件的名称是："+name);
			System.out.println("抓取开始");
			for(;i<count;i++) {
				String uri = base_uri;
				if(0!=i) {
					uri = base_uri+"/"+(i+1);
				}
				String HTML = gotSource(uri);
				imgUrl.addAll(getImageUrl(HTML));
				System.out.println("第"+i+"页处理完成。");
			}
			// 获取图片标签
			// 获取图片src地址
//			List<String> imgSrc = cm.getImageSrc(imgUrl);
			// 下载图片
//			cm.Download(imgSrc);
			System.out.println("共获得图片数量"+imgUrl.size());
			generateHtml(imgUrl, name);

		}
	}

	/***
	 * 获取HTML内容
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String getHTML(String url) throws Exception {
		URL uri = new URL(url);
		URLConnection connection = uri.openConnection();
		InputStream in = connection.getInputStream();
		byte[] buf = new byte[1024];
		int length = 0;
		StringBuffer sb = new StringBuffer();
		while ((length = in.read(buf, 0, buf.length)) > 0) {
			sb.append(new String(buf, ECODING));
		}
		in.close();
		return sb.toString();
	}

	/***
	 * 获取ImageUrl地址
	 * 
	 * @param HTML
	 * @return
	 */
	private static List<String> getImageUrl(String HTML) {
		Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
		List<String> listImgUrl = new ArrayList<String>();
		while (matcher.find()) {
			listImgUrl.add(matcher.group());
		}
		return listImgUrl;
	}

	/***
	 * 获取ImageSrc地址
	 * 
	 * @param listImageUrl
	 * @return
	 */
	private List<String> getImageSrc(List<String> listImageUrl) {
		List<String> listImgSrc = new ArrayList<String>();
		for (String image : listImageUrl) {
			Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
			while (matcher.find()) {
				listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
			}
		}
		return listImgSrc;
	}

	/***
	 * 下载图片
	 * 
	 * @param listImgSrc
	 */
	private void Download(List<String> listImgSrc) {
		File file = new File(FOLDER_STRING);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		for (String url : listImgSrc) {
			String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
			try {
				URL uri = new URL(url);
				InputStream in = uri.openStream();
				FileOutputStream fo = new FileOutputStream(new File(FOLDER_STRING +"/" + imageName));
				byte[] buf = new byte[1024];
				int length = 0;
				System.out.println("开始下载:" + imageName);
				while ((length = in.read(buf, 0, buf.length)) != -1) {
					fo.write(buf, 0, length);
				}
				in.close();
				fo.close();
				picCount ++;
				System.out.println(imageName + "下载完成");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(url + "下载失败");
			}
		}

		System.out.println("全部下载完成！下载图片"+picCount+"张。");
	}
	
    private static String gotSource(String uri){
    	long startmill = System.currentTimeMillis();
        // 创建代理对象      
        StringBuilder sb = new StringBuilder();
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 58080));
        try{
            Socket socket = new Socket(proxy);
            socket.connect(new InetSocketAddress("www.177pic.info", 80));
            OutputStream output = socket.getOutputStream();
            InputStreamReader isr = new InputStreamReader(socket.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder request = new StringBuilder();
            request.append("GET "+uri+" HTTP/1.1\r\n");
            request.append("Accept-Language: zh-cn\r\n");
            request.append("Host: www.177pic.info\r\n");
            request.append("\r\n");
            output.write(request.toString().getBytes());
//            output.flush();

            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }

            br.close();
            isr.close();
            output.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        long endMill = System.currentTimeMillis();
        System.out.println("处理当前地址耗时"+(endMill-startmill)+"毫秒");
        return sb.toString();
    }
    
    private static void generateHtml(List<String> imgList, String name){
//    	String path = FOLDER_STRING+"/"+name;
    	String jarFile = Catch177PicImage.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String path = jarFile.substring(0, jarFile.lastIndexOf("/")+1)+name;
		File file = new File(path);
		try {

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			FileWriter fileWritter = new FileWriter(path, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

	    	for(String img: imgList) {
				bufferWritter.write(img);
				System.out.println(img);
	    	}

			bufferWritter.close();

			System.out.println("页面渲染完成");

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}