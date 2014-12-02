/**
 * 
 */
package com.sogou.qadev.service.cynthia.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;

import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

/**
 * @description:image processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 上午9:48:29
 * @version:v1.0
 */
public class ImageManager {

	
	/**
	 * @description:cut the image from attachment file id,and update attachment
	 * @date:2014-5-6 上午9:49:08
	 * @version:v1.0
	 * @param fileId:attachment id
	 * @param x:cut from x 
	 * @param y
	 * @param width:cut width
	 * @param height
	 * @return
	 */
	public static boolean abscut(String fileId, int x, int y,int width, int height) {
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		
		Attachment attachment = das.queryAttachment(DataAccessFactory.getInstance().createUUID(fileId), true);
    	
		if (attachment == null) 
			return false;
		
        try {
            //读取源图像
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(attachment.getData()));
            byte[] outByte = abscut(bi, x, y, width, height);
            //更新文件data
            attachment.setData(outByte);
            return das.updateAttachment(attachment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
        }
    }
	
	/**
	 * @description:cut the image and return image data 
	 * @date:2014-5-6 上午9:53:58
	 * @version:v1.0
	 * @param bi
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static byte[] abscut(BufferedImage bi, int x, int y,int width, int height) {
    	byte[] outByte = null;
        try {
            Image img;
            ImageFilter cropFilter;
            int srcWidth = bi.getWidth(); // 源图宽度
            int srcHeight = bi.getHeight(); // 源图高度
            if (srcWidth >= width && srcHeight >= height) {
                BufferedImage tag;
                Image image = bi.getScaledInstance(srcWidth, srcHeight,Image.SCALE_DEFAULT);
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                
                cropFilter = new CropImageFilter(x, y, width, height);
                img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                int type = BufferedImage.TYPE_INT_ARGB;
                tag = new BufferedImage(width, height,type);
                Graphics2D g = (Graphics2D)tag.getGraphics();
                    
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(img, 0, 0, null); // 绘制剪切后的图
                g.dispose();
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(tag, "PNG", out);
                outByte = out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        }
        return outByte;
    }
	
	
	/**
	 *  @description:convert the image file to base64 string
	 * @date:2014-5-6 上午9:55:58
	 * @version:v1.0
	 * @param imgInputStream
	 * @return
	 */
    public static String getImageStr(InputStream imgInputStream) {  
        byte[] data = null;  
        // 读取图片字节数组  
        try {  
            data = new byte[imgInputStream.available()];  
            imgInputStream.read(data);  
            imgInputStream.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // 对字节数组Base64编码  
        BASE64Encoder encoder = new BASE64Encoder();  
        // 返回Base64编码过的字节数组字符串  
        return encoder.encode(data);  
    }

}
