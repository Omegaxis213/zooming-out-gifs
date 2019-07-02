import java.util.*;
import java.io.*;
import java.awt.image.*;

public class gifMaker {
	public static void main(String[] args) throws Exception {
		GifDecoder d = new GifDecoder();
		d.read("gif1.gif");
		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start(new FileOutputStream("gif2.gif"));
		e.setRepeat(0);
		int n = d.getFrameCount();
		BufferedImage[] arr = new BufferedImage[n];
		for (int i = 0; i < n; i++) {
			BufferedImage frame = d.getFrame(i);
			arr[i] = frame;
			int t = d.getDelay(i);
			e.setDelay(t);
			e.addFrame(frame);
		}
		e.finish();
		System.out.println(arr[0].getHeight()+" "+arr[0].getWidth());
		double minXBound=0;
		double maxXBound=arr[0].getWidth();
		double minYBound=0;
		double maxYBound=arr[0].getHeight();
		int nextMinXBound=0;
		int nextMaxXBound=arr[0].getWidth()*20;
		int nextMinYBound=0;
		int nextMaxYBound=arr[0].getHeight()*20;
		int delay=d.getDelay(0);
		System.out.println(d.getFrameCount());
		int numOfFrames=(int)Math.ceil(5000.0/delay);
		System.out.println(numOfFrames);
		double minXBoundInc=(double)(minXBound-nextMinXBound)/numOfFrames;
		double maxXBoundInc=(double)(nextMaxXBound-maxXBound)/numOfFrames;
		double minYBoundInc=(double)(minYBound-nextMinYBound)/numOfFrames;
		double maxYBoundInc=(double)(nextMaxYBound-maxYBound)/numOfFrames;
		System.out.println(maxXBoundInc+" "+maxYBoundInc);
		BufferedImage[] res=new BufferedImage[numOfFrames];
		e=new AnimatedGifEncoder();
		e.start(new FileOutputStream("finalGIF.gif"));
		e.setRepeat(0);
		for (int i = 0; i < numOfFrames; i++) {
			res[i]=new BufferedImage(arr[0].getWidth(),arr[0].getHeight(),BufferedImage.TYPE_INT_ARGB);
			double adjustWidth=(maxXBound-minXBound)/arr[0].getWidth();
			double adjustHeight=(maxYBound-minYBound)/arr[0].getHeight();
			for (int j = 0; j < arr[0].getHeight(); j++) {
				for (int k = 0; k < arr[0].getWidth(); k++) {
					// if(j==0)
					// {
					// 	System.out.print(k*adjustWidth);
					// 	System.out.print(" ");
					// 	System.out.print(maxXBound);
					// 	System.out.println();
					// }
					double xPos=k*adjustWidth;
					double yPos=j*adjustHeight;
					xPos%=arr[0].getWidth();
					yPos%=arr[0].getHeight();
					int color=arr[i%n].getRGB((int)xPos,(int)yPos);
					// if(j==0)
						// System.out.println(xPos+" "+color);
					res[i].setRGB(k,j,color);
				}
			}
			e.setDelay(delay);
			e.addFrame(res[i]);
			maxXBound+=maxXBoundInc;
			maxYBound+=maxYBoundInc;
		}

		e.finish();
	}
}