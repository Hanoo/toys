package cc.tuinvshen;

import share.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * 如果使用无参构造器，就默认保存到E盘
 */
public class ImageSaver implements Runnable {

	private String base_path = "D:/tuinvshen";
	private String imageUrl;
	private String album;
	
	public ImageSaver(String imageUrl, String album){
		this.imageUrl = imageUrl;
		this.album = album;
	}
	
	public ImageSaver(String base_path, String imageUrl, String album){
		this(imageUrl, album);
		this.base_path = base_path;
	}
	
	public void run(){
		File dir = new File(base_path+album);
		if(!dir.exists()){
			dir.mkdirs();
			System.out.println("图片存放于"+dir.getAbsolutePath()+"目录下");
		}
		String imageName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
		try {
			//创建一个url对象
			URL url = new URL(imageUrl);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(new File(dir.getAbsolutePath()+"/"+imageName));

			Utils.writeToFile(is, os);

			is.close();
            os.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("================文件写入失败==================");
		}
	}

}
