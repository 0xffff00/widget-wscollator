package hzk.widgets.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageTransformUtils {
	
	/**
	 * clip the origin image to part
	 * @param origin
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage clip(BufferedImage origin,int x,int y,int w,int h){
		int type=origin.getType();
		BufferedImage img = new BufferedImage(w,h, type);
		Graphics2D g = img.createGraphics();
		g.drawImage(origin,  0, 0, w, h, x, y, x+w, y+h,null);
		return img;
	}
	
	/**
	 * 
	 * @param imgs images to be merged
	 * @param n_paddings the top(north) padding height of every image ,which need be cut off.
	 * @param s_paddings the bottom(south) padding height of every image ,which need be cut off.
	 * @return
	 */
	public static BufferedImage mergeVertically(BufferedImage[] imgs,int[] n_paddings,int[] s_paddings){
		if (imgs==null) return null;
		int n=imgs.length;
		if (n==0) return null;
		int wR=0,hR=0;	//result image's width,height
		int imgType=imgs[0].getType();
		
		for (int i = 0; i < n; i++) {
			if (imgs[i].getWidth()>wR) wR=imgs[i].getWidth();
			hR+=imgs[i].getHeight()-n_paddings[i]-s_paddings[i];
		}
		BufferedImage result=new BufferedImage(wR,hR,imgType);
		Graphics2D g = result.createGraphics();
		int y=0,np,sp,w,h;
		for (int i = 0; i < n; i++) {
			//g.setColor(Color.gray);
			np=n_paddings[i];sp=s_paddings[i];
			w=imgs[i].getWidth();
			h=imgs[i].getHeight();
			
			g.drawImage(imgs[i], 0, y, w, y+h-sp-np, 0, np, w, h-sp, null);
			y+=h-sp-np;
		}
		return result;
	}
	
}
