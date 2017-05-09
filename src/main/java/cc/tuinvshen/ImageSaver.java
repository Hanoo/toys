package cc.tuinvshen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class ImageSaver implements Runnable {

	private String base_path = "E:/tuinvshen";
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
			File file = new File( dir.getAbsolutePath()+"/"+imageName);
			OutputStream os = new FileOutputStream(file);
			//创建一个url对象
			URL url = new URL(imageUrl);
			InputStream is = url.openStream();
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
			is.close(); 
            os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
