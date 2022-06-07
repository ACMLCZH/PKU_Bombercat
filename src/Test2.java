
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
 
import javax.imageio.ImageIO;
 
/** 绘制透明背景的文字图片
 * @author cxy @ www.cxyapi.com
 */
public class Test2
{
	public static BufferedImage drawTranslucentStringPic(int width, int height, Integer fontHeight,String drawStr)
	{
		try
		{
			BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D gd = buffImg.createGraphics();
			//设置透明  start
			buffImg = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			gd=buffImg.createGraphics();
			//设置透明  end
			gd.setFont(new Font("微软雅黑", Font.PLAIN, fontHeight)); //设置字体
			gd.setColor(Color.ORANGE); //设置颜色
			gd.drawRect(0, 0, width - 1, height - 1); //画边框
			gd.drawString(drawStr, width/2-fontHeight*drawStr.length()/2,fontHeight); //输出文字（中文横向居中）
			return buffImg;
		} catch (Exception e) {
			System.out.println("sb");
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		BufferedImage imgMap = drawTranslucentStringPic(400, 80, 36,"欢迎访问我的博客");
		File imgFile=new File("./t.png");
		try
		{
			ImageIO.write(imgMap, "PNG", imgFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("生成完成");
	}
 
}