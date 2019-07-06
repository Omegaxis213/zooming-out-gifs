//does support multiple gifs but it doesn't mesh very well if they have different frame rates.
import java.util.*;
import java.io.*;
import java.awt.image.*;

public class gifMaker {
	public static void main(String[] args) throws Exception {
		//gif to be used
		int numOfGifs=0;
		while(new File("gifs/gif"+(numOfGifs+1)+".gif").isFile())
			numOfGifs++;

		int[] frameLen=new int[numOfGifs];
		int[] delay=new int[numOfGifs];
		BufferedImage[][] arr = new BufferedImage[numOfGifs][];
		int maxFrameLen=0;
		for (int i = 0; i < numOfGifs; i++) {
			System.out.println(i);
			GifDecoder d = new GifDecoder();
			d.read("gifs/gif"+(i+1)+".gif");
			int n = d.getFrameCount();
			frameLen[i] = n;
			maxFrameLen=Math.max(maxFrameLen,frameLen[i]);
			arr[i] = new BufferedImage[n];
			delay[i] = d.getDelay(0);
			for (int j = 0; j < n; j++) {
				BufferedImage frame = d.getFrame(j);
				arr[i][j] = frame;
			}
		}
		//contains the average colors of the current frame for the previous frame to transition into
		int[][][][][] avgColors=new int[numOfGifs][][][][];
		for (int a = 0; a < numOfGifs; a++) {
			avgColors[a]=new int[arr[a].length][arr[a][0].getHeight()+1][arr[a][0].getWidth()+1][4];
			for (int i = 0; i < arr[a].length; i++) {
				for (int j = 0; j < arr[a][i].getHeight(); j++) {
					for (int k = 0; k < arr[a][i].getWidth(); k++) {
						int xPos=k;
						int yPos=j;
						int color=arr[a][i].getRGB(k,j);
						int alpha=color>>24&255;
						int red=color>>16&255;
						int green=color>>8&255;
						int blue=color&255;
						avgColors[a][i][yPos][xPos][0]+=alpha;
						avgColors[a][i][yPos][xPos][1]+=red;
						avgColors[a][i][yPos][xPos][2]+=green;
						avgColors[a][i][yPos][xPos][3]+=blue;
					}
				}
			}
		}
		System.out.println(Arrays.toString(delay));
		System.out.println(arr[0][0].getHeight()+" "+arr[0][0].getWidth());

		// AnimatedGifEncoder test=new AnimatedGifEncoder();
		// test.start(new FileOutputStream("test.gif"));
		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start(new FileOutputStream("finalGIF.gif"));
		e.setRepeat(0);

		// int numOfLoops=4;

		//choose how many times the gif above the current one will play (will affect zoom speed)
		//Default value will be one time. Only supports integer numbers
		int[] frameMult=new int[numOfGifs];
		Arrays.fill(frameMult,1);
		BufferedReader f=new BufferedReader(new FileReader("gifs/gifProperties.txt"));
		for (int i = 0; i < numOfGifs; i++) {
			int num=Integer.parseInt(f.readLine());
			if(num<=0) continue;
			frameMult[i]=num;
		}
		for (int a = 0; a < numOfGifs; a++) {
			//starting gif number/position of the current gif relative to the next one
			double startGifNumX=(int)(Math.random()*arr[(a+1)%numOfGifs][0].getWidth());
			double startGifNumY=(int)(Math.random()*arr[(a+1)%numOfGifs][0].getHeight());

			//percentage zoom
			double minXBoundLen=arr[a][0].getWidth()/2.0;
			double maxXBoundLen=arr[a][0].getWidth()/2.0;
			double minYBoundLen=arr[a][0].getHeight()/2.0;
			double maxYBoundLen=arr[a][0].getHeight()/2.0;
			double nextMinXBound=arr[a][0].getWidth()/2.0+startGifNumX*arr[a][0].getWidth();
			double nextMaxXBound=arr[(a+1)%numOfGifs][0].getWidth()*arr[a][0].getWidth()-(arr[a][0].getWidth()/2.0*3+startGifNumX*arr[a][0].getWidth());
			double nextMinYBound=arr[a][0].getHeight()/2.0+startGifNumY*arr[a][0].getHeight();
			double nextMaxYBound=arr[(a+1)%numOfGifs][0].getHeight()*arr[a][0].getHeight()-(arr[a][0].getHeight()/2.0*3+startGifNumY*arr[a][0].getHeight());

			int numOfFrames=frameMult[a]*frameLen[(a+1)%numOfGifs];
			double minXBoundMult=Math.pow(10,Math.log10(nextMinXBound/minXBoundLen)/numOfFrames);
			double minYBoundMult=Math.pow(10,Math.log10(nextMinYBound/minYBoundLen)/numOfFrames);
			double maxXBoundMult=Math.pow(10,Math.log10(nextMaxXBound/maxXBoundLen)/numOfFrames);
			double maxYBoundMult=Math.pow(10,Math.log10(nextMaxYBound/maxYBoundLen)/numOfFrames);
			System.out.println(nextMaxXBound+" "+maxXBoundLen);

			//Linear moving center
			double gifCenterXPos=startGifNumX*arr[a][0].getWidth()+arr[a][0].getWidth()/2.0;
			double gifCenterYPos=startGifNumY*arr[a][0].getHeight()+arr[a][0].getHeight()/2.0;
			double gifEndXPos=arr[(a+1)%numOfGifs][0].getWidth()*arr[a][0].getWidth()-nextMaxXBound-arr[a][0].getWidth()/2.0;
			double gifEndYPos=arr[(a+1)%numOfGifs][0].getHeight()*arr[a][0].getHeight()-nextMaxYBound-arr[a][0].getHeight()/2.0;
			double gifCenterXInc=(gifEndXPos-gifCenterXPos)/numOfFrames;
			double gifCenterYInc=(gifEndYPos-gifCenterYPos)/numOfFrames;

			BufferedImage[] res=new BufferedImage[numOfFrames];

			for (int i = 0; i < numOfFrames; i++) {
				res[i]=new BufferedImage(arr[0][0].getWidth(),arr[0][0].getHeight(),BufferedImage.TYPE_INT_ARGB);
				double adjustWidth=(maxXBoundLen+minXBoundLen)/arr[0][0].getWidth();
				double adjustHeight=(maxYBoundLen+minYBoundLen)/arr[0][0].getHeight();
				for (int j = 0; j < arr[0][0].getHeight(); j++) {
					for (int k = 0; k < arr[0][0].getWidth(); k++) {
						double xPos=gifCenterXPos-minXBoundLen+k*adjustWidth;
						double yPos=gifCenterYPos-minYBoundLen+j*adjustHeight;
						int gifXPos=(int)(xPos/arr[a][0].getWidth());
						int gifYPos=(int)(yPos/arr[a][0].getHeight());
						xPos%=arr[a][0].getWidth();
						yPos%=arr[a][0].getHeight();

						int color=arr[a][i%frameLen[a]].getRGB((int)xPos,(int)yPos);
						int alpha=color>>24&255;
						int red=color>>16&255;
						int green=color>>8&255;
						int blue=color&255;
						alpha=(int)(alpha*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs]][gifYPos][gifXPos][0]*(.9*i/numOfFrames));
						red=(int)(red*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs]][gifYPos][gifXPos][1]*(.9*i/numOfFrames));
						green=(int)(green*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs]][gifYPos][gifXPos][2]*(.9*i/numOfFrames));
						blue=(int)(blue*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs]][gifYPos][gifXPos][3]*(.9*i/numOfFrames));
						color=(alpha<<24)+(red<<16)+(green<<8)+blue;
						res[i].setRGB(k,j,color);
					}
				}
				// if(i==0)
				// {
				// 	test.setDelay(delay);
				// 	test.addFrame(res[i]);
				// }
				e.setDelay((int)(delay[a]*(1-(double)i/numOfFrames)+delay[(a+1)%numOfGifs]*(double)i/numOfFrames));
				e.addFrame(res[i]);
				minXBoundLen*=minXBoundMult;
				minYBoundLen*=minYBoundMult;
				maxXBoundLen*=maxXBoundMult;
				maxYBoundLen*=maxYBoundMult;
				gifCenterXPos+=gifCenterXInc;
				gifCenterYPos+=gifCenterYInc;
			}
		}
		// test.finish();
		e.finish();
	}
}