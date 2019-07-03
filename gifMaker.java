import java.util.*;
import java.io.*;
import java.awt.image.*;

public class gifMaker {
	public static void main(String[] args) throws Exception {
		GifDecoder d = new GifDecoder();
		d.read("gif1.gif");
		int n = d.getFrameCount();
		BufferedImage[] arr = new BufferedImage[n];
		for (int i = 0; i < n; i++) {
			BufferedImage frame = d.getFrame(i);
			arr[i] = frame;
		}
		int scaleFactor=1;
		int[][][][] avgColors=new int[n][arr[0].getHeight()/scaleFactor+1][arr[0].getWidth()/scaleFactor+1][4];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < arr[0].getHeight(); j++) {
				for (int k = 0; k < arr[0].getWidth(); k++) {
					int xPos=k/scaleFactor;
					int yPos=j/scaleFactor;
					int color=arr[i].getRGB(k,j);
					int alpha=color>>24&255;
					int red=color>>16&255;
					int green=color>>8&255;
					int blue=color&255;
					avgColors[i][yPos][xPos][0]+=alpha;
					avgColors[i][yPos][xPos][1]+=red;
					avgColors[i][yPos][xPos][2]+=green;
					avgColors[i][yPos][xPos][3]+=blue;
				}
			}
		}
		for (int i = 0; i < avgColors.length; i++) {
			for (int j = 0; j < avgColors[0].length; j++) {
				for (int k = 0; k < avgColors[0][0].length; k++) {
					for (int l = 0; l < avgColors[0][0][0].length; l++) {
						avgColors[i][j][k][l]/=scaleFactor*scaleFactor;
					}
				}
			}
		}
		System.out.println(arr[0].getHeight()+" "+arr[0].getWidth());
		//starting gif number/position
		double startGifNumX=arr[0].getWidth()/(scaleFactor*3);
		double startGifNumY=arr[0].getHeight()/(scaleFactor*2);

		//linear zoom
		// double minXBound=startGifNumX*arr[0].getWidth();
		// double maxXBound=minXBound+arr[0].getWidth();
		// double minYBound=startGifNumY*arr[0].getHeight();
		// double maxYBound=minYBound+arr[0].getHeight();
		// int nextMinXBound=0;
		// int nextMaxXBound=arr[0].getWidth()*arr[0].getWidth()/scaleFactor;
		// int nextMinYBound=0;
		// int nextMaxYBound=arr[0].getHeight()*arr[0].getHeight()/scaleFactor;


		// int numOfFrames=(int)Math.ceil(5000.0/delay);

		// double minXBoundInc=(double)(minXBound-nextMinXBound)/numOfFrames;
		// double maxXBoundInc=(double)(nextMaxXBound-maxXBound)/numOfFrames;
		// double minYBoundInc=(double)(minYBound-nextMinYBound)/numOfFrames;
		// double maxYBoundInc=(double)(nextMaxYBound-maxYBound)/numOfFrames;

		//percentage zoom

		double minXBoundLen=arr[0].getWidth()/2.0;
		double maxXBoundLen=arr[0].getWidth()/2.0;
		double minYBoundLen=arr[0].getHeight()/2.0;
		double maxYBoundLen=arr[0].getHeight()/2.0;
		double nextMinXBound=arr[0].getWidth()/2.0+startGifNumX*arr[0].getWidth();
		double nextMaxXBound=arr[0].getWidth()*arr[0].getWidth()/scaleFactor-(arr[0].getWidth()/2.0*3+startGifNumX*arr[0].getWidth());
		double nextMinYBound=arr[0].getHeight()/2.0+startGifNumY*arr[0].getHeight();
		double nextMaxYBound=arr[0].getHeight()*arr[0].getHeight()/scaleFactor-(arr[0].getHeight()/2.0*3+startGifNumY*arr[0].getHeight());

		int numOfFrames=3*n;
		double minXBoundMult=Math.pow(10,Math.log10(nextMinXBound/minXBoundLen)/numOfFrames);
		double minYBoundMult=Math.pow(10,Math.log10(nextMinYBound/minYBoundLen)/numOfFrames);
		double maxXBoundMult=Math.pow(10,Math.log10(nextMaxXBound/maxXBoundLen)/numOfFrames);
		double maxYBoundMult=Math.pow(10,Math.log10(nextMaxYBound/maxYBoundLen)/numOfFrames);
		System.out.println(nextMaxXBound+" "+maxXBoundLen);

		//Linear moving center for now
		double gifCenterXPos=startGifNumX*arr[0].getWidth()+arr[0].getWidth()/2.0;
		double gifCenterYPos=startGifNumY*arr[0].getHeight()+arr[0].getHeight()/2.0;
		double gifEndXPos=arr[0].getWidth()*arr[0].getWidth()/scaleFactor-nextMaxXBound-arr[0].getWidth()/2.0;
		double gifEndYPos=arr[0].getHeight()*arr[0].getHeight()/scaleFactor-nextMaxYBound-arr[0].getHeight()/2.0;
		double gifCenterXInc=(gifEndXPos-gifCenterXPos)/numOfFrames;
		double gifCenterYInc=(gifEndYPos-gifCenterYPos)/numOfFrames;

		int delay=d.getDelay(0);

		BufferedImage[] res=new BufferedImage[numOfFrames];

		AnimatedGifEncoder test=new AnimatedGifEncoder();
		test.start(new FileOutputStream("test.gif"));

		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start(new FileOutputStream("finalGIF.gif"));
		e.setRepeat(0);
		for (int i = 0; i < numOfFrames; i++) {
			res[i]=new BufferedImage(arr[0].getWidth(),arr[0].getHeight(),BufferedImage.TYPE_INT_ARGB);
			// double adjustWidth=(maxXBound-minXBound)/arr[0].getWidth();
			// double adjustHeight=(maxYBound-minYBound)/arr[0].getHeight();
			double adjustWidth=(maxXBoundLen+minXBoundLen)/arr[0].getWidth();
			double adjustHeight=(maxYBoundLen+minYBoundLen)/arr[0].getHeight();
			for (int j = 0; j < arr[0].getHeight(); j++) {
				for (int k = 0; k < arr[0].getWidth(); k++) {
					// double xPos=k*adjustWidth+minXBound;
					// double yPos=j*adjustHeight+minYBound;
					double xPos=gifCenterXPos-minXBoundLen+k*adjustWidth;
					double yPos=gifCenterYPos-minYBoundLen+j*adjustHeight;
					int gifXPos=(int)(xPos/arr[0].getWidth());
					int gifYPos=(int)(yPos/arr[0].getHeight());
					xPos%=arr[0].getWidth();
					yPos%=arr[0].getHeight();
					if(j==0)
					{
						// StringBuilder out=new StringBuilder("frame: ");
						// out.append(i);
						// out.append(" finPos: ");
						// out.append(xPos);
						// out.append(" initial pos: ");
						// out.append(k*adjustWidth+minXBound);
						// out.append(" adjustWidth: ");
						// out.append(adjustWidth);
						// System.out.println(out);
						// System.out.println("frame: "+i+" finPos: "+xPos+" initial pos: "+(k*adjustWidth+minXBound)+);
					}
					//nearest neighbor
					double disOne=((int)xPos-xPos)*((int)xPos-xPos)+((int)yPos-yPos)*((int)yPos-yPos);
					double disTwo=((int)xPos-xPos+1)*((int)xPos-xPos+1)+((int)yPos-yPos)*((int)yPos-yPos);
					double disThree=((int)xPos-xPos)*((int)xPos-xPos)+((int)yPos-yPos+1)*((int)yPos-yPos+1);
					double disFour=((int)xPos-xPos+1)*((int)xPos-xPos+1)+((int)yPos-yPos+1)*((int)yPos-yPos+1);

					if((int)xPos==arr[0].getWidth()-1)
					{
						disTwo=Double.POSITIVE_INFINITY;
						disFour=Double.POSITIVE_INFINITY;
					}
					if((int)yPos==arr[0].getHeight()-1)
					{
						disThree=Double.POSITIVE_INFINITY;
						disFour=Double.POSITIVE_INFINITY;
					}

					double minDis=Math.min(disOne,Math.min(disTwo,Math.min(disThree,disFour)));
					int color=minDis==disOne?arr[i%n].getRGB((int)xPos,(int)yPos):minDis==disTwo?arr[i%n].getRGB((int)xPos+1,(int)yPos):minDis==disThree?arr[i%n].getRGB((int)xPos,(int)yPos+1):arr[i%n].getRGB((int)xPos+1,(int)yPos+1);
					// int color=arr[i%n].getRGB((int)xPos,(int)yPos);
					int alpha=color>>24&255;
					int red=color>>16&255;
					int green=color>>8&255;
					int blue=color&255;
					alpha=(int)(alpha*(1-.9*i/numOfFrames)+avgColors[i%n][gifYPos][gifXPos][0]*(.9*i/numOfFrames));
					red=(int)(red*(1-.9*i/numOfFrames)+avgColors[i%n][gifYPos][gifXPos][1]*(.9*i/numOfFrames));
					green=(int)(green*(1-.9*i/numOfFrames)+avgColors[i%n][gifYPos][gifXPos][2]*(.9*i/numOfFrames));
					blue=(int)(blue*(1-.9*i/numOfFrames)+avgColors[i%n][gifYPos][gifXPos][3]*(.9*i/numOfFrames));
					color=(alpha<<24)+(red<<16)+(green<<8)+blue;
					res[i].setRGB(k,j,color);
				}
			}
			if(i==0)
			{
				test.setDelay(delay);
				test.addFrame(res[i]);
			}
			e.setDelay(delay);
			e.addFrame(res[i]);
			// minXBound-=minXBoundInc;
			// minYBound-=minYBoundInc;
			// maxXBound+=maxXBoundInc;
			// maxYBound+=maxYBoundInc;
			// System.out.println("before: "+minXBoundLen+" "+minYBoundLen+" "+maxXBoundLen+" "+maxYBoundLen+" "+gifCenterXPos+" "+gifCenterYPos);
			minXBoundLen*=minXBoundMult;
			minYBoundLen*=minYBoundMult;
			maxXBoundLen*=maxXBoundMult;
			maxYBoundLen*=maxYBoundMult;
			gifCenterXPos+=gifCenterXInc;
			gifCenterYPos+=gifCenterYInc;
			// System.out.println("after: "+minXBoundLen+" "+minYBoundLen+" "+maxXBoundLen+" "+maxYBoundLen+" "+gifCenterXPos+" "+gifCenterYPos);

		}
		test.finish();
		e.finish();
	}
}