//does support multiple gifs but it doesn't mesh very well if they have different frame rates.
import java.util.*;
import java.io.*;
import java.awt.image.*;

public class gifMaker {
	public static void main(String[] args) throws Exception {
		//gif to be used
		int numOfGifs=0;
		while(new File("gifs/gif"+(numOfGifs+1)+"-1.gif").isFile()||new File("gifs/gif"+(numOfGifs+1)+".gif").isFile())
			numOfGifs++;

		int[][] frameLen=new int[numOfGifs][];
		int[] delay=new int[numOfGifs];
		BufferedImage[][][] arr = new BufferedImage[numOfGifs][][];
		for (int i = 0; i < numOfGifs; i++) {
			System.out.println(i);
			int numOfGifsOnOneLayer=0;
			boolean hasChainOfGifs=false;
			if(new File("gifs/gif"+(i+1)+".gif").isFile())
				numOfGifsOnOneLayer=1;
			else
			{
				while(new File("gifs/gif"+(i+1)+"-"+(numOfGifsOnOneLayer+1)+".gif").isFile())
					numOfGifsOnOneLayer++;
				hasChainOfGifs=true;
			}
			arr[i]=new BufferedImage[numOfGifsOnOneLayer][];
			frameLen[i] = new int[numOfGifsOnOneLayer];
			for (int a = 0; a < numOfGifsOnOneLayer; a++) {
				GifDecoder d = new GifDecoder();
				d.read(hasChainOfGifs?"gifs/gif"+(i+1)+"-"+(a+1)+".gif":"gifs/gif"+(i+1)+".gif");
				int n = d.getFrameCount();
				arr[i][a] = new BufferedImage[n];

				frameLen[i][a] = n;
				//only want the delay of the first gif of each layer
				if(a==0)
					delay[i] = d.getDelay(0);

				for (int j = 0; j < n; j++) {
					BufferedImage frame = d.getFrame(j);
					arr[i][a][j] = frame;
				}
			}
		}
		//contains the average colors of the current frame for the previous frame to transition into
		int[][][][][] avgColors=new int[numOfGifs][][][][];
		for (int a = 0; a < numOfGifs; a++) {
			avgColors[a]=new int[arr[a][0].length][arr[a][0][0].getHeight()+1][arr[a][0][0].getWidth()+1][4];
			for (int i = 0; i < arr[a][0].length; i++) {
				for (int j = 0; j < arr[a][0][i].getHeight(); j++) {
					for (int k = 0; k < arr[a][0][i].getWidth(); k++) {
						int xPos=k;
						int yPos=j;
						int color=arr[a][0][i].getRGB(k,j);
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
		System.out.println(arr[0][0][0].getHeight()+" "+arr[0][0][0].getWidth());

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
		//manual override for starting position of the gif on each layer
		int[][] startPos=new int[numOfGifs][2];
		f=new BufferedReader(new FileReader("gifs/gifStartingPositions.txt"));
		for (int i = 0; i < numOfGifs; i++) {
			startPos[i][0]=startPos[i][1]=-1;
			if(!f.ready()) continue;
			StringTokenizer st=new StringTokenizer(f.readLine());
			int xPos=Integer.parseInt(st.nextToken());
			int yPos=Integer.parseInt(st.nextToken());
			if(xPos<0||yPos<0||xPos>=arr[i][0][0].getWidth()||yPos>=arr[i][0][0].getHeight())
			{
				System.out.println("Please put a coordinate within 0 to "+(arr[i][0][0].getWidth()-1)+" for the X position and 0 to "+(arr[i][0][0].getHeight()-1)+" for the Y position on layer "+(i+1));
				continue;
			}
			startPos[i][0]=xPos;
			startPos[i][1]=yPos;
		}
		int[][][] gifTypeOverride=new int[numOfGifs][][];
		for (int i = 0; i < numOfGifs; i++) {
			gifTypeOverride[i]=new int[arr[(i+1)%numOfGifs][0][0].getHeight()][arr[(i+1)%numOfGifs][0][0].getWidth()];
		}
		f=new BufferedReader(new FileReader("gifs/gifTypes.txt"));
		int counter=1;
		//manual override for which gif is used where
		while(f.ready())
		{
			StringTokenizer st=new StringTokenizer(f.readLine(),"(,) ");
			int layer=Integer.parseInt(st.nextToken());
			int xPos=Integer.parseInt(st.nextToken());
			int yPos=Integer.parseInt(st.nextToken());
			int gifType=Integer.parseInt(st.nextToken());
			layer--;
			if(layer<=0||layer>gifTypeOverride.length)
			{
				System.out.println("Please put in a valid layer for line "+counter);
				continue;
			}
			if(yPos<0||yPos>=gifTypeOverride[layer].length)
			{
				System.out.println("Please put in a valid y position within 0 to "+(gifTypeOverride[layer].length-1)+" for line "+counter);
				continue;
			}
			if(xPos<0||xPos>=gifTypeOverride[layer][yPos].length)
			{
				System.out.println("Please put in a valid x position within 0 to "+(gifTypeOverride[layer][yPos].length-1)+" for line "+counter);
				continue;
			}
			if(gifType<=0||gifType>=arr[layer].length+1)
			{
				System.out.println("please put in a valid gif type within 1 to "+(arr[layer].length)+" for line "+counter);
				continue;
			}
			gifTypeOverride[layer][yPos][xPos]=gifType;
			counter++;
		}
		for (int a = 0; a < numOfGifs; a++) {
			//starting gif number/position of the current gif relative to the next one
			double startGifNumX=(int)(Math.random()*arr[(a+1)%numOfGifs][0][0].getWidth());
			double startGifNumY=(int)(Math.random()*arr[(a+1)%numOfGifs][0][0].getHeight());
			if(startPos[a][0]!=-1)
			{
				startGifNumX=startPos[a][0];
				startGifNumY=startPos[a][1];
			}

			//percentage zoom
			double minXBoundLen=arr[a][0][0].getWidth()/2.0;
			double maxXBoundLen=arr[a][0][0].getWidth()/2.0;
			double minYBoundLen=arr[a][0][0].getHeight()/2.0;
			double maxYBoundLen=arr[a][0][0].getHeight()/2.0;
			double nextMinXBound=arr[a][0][0].getWidth()/2.0+startGifNumX*arr[a][0][0].getWidth();
			double nextMaxXBound=arr[(a+1)%numOfGifs][0][0].getWidth()*arr[a][0][0].getWidth()-(arr[a][0][0].getWidth()/2.0*3+startGifNumX*arr[a][0][0].getWidth());
			double nextMinYBound=arr[a][0][0].getHeight()/2.0+startGifNumY*arr[a][0][0].getHeight();
			double nextMaxYBound=arr[(a+1)%numOfGifs][0][0].getHeight()*arr[a][0][0].getHeight()-(arr[a][0][0].getHeight()/2.0*3+startGifNumY*arr[a][0][0].getHeight());

			int numOfFrames=frameMult[a]*frameLen[(a+1)%numOfGifs][0];
			double minXBoundMult=Math.pow(10,Math.log10(nextMinXBound/minXBoundLen)/numOfFrames);
			double minYBoundMult=Math.pow(10,Math.log10(nextMinYBound/minYBoundLen)/numOfFrames);
			double maxXBoundMult=Math.pow(10,Math.log10(nextMaxXBound/maxXBoundLen)/numOfFrames);
			double maxYBoundMult=Math.pow(10,Math.log10(nextMaxYBound/maxYBoundLen)/numOfFrames);
			System.out.println(nextMaxXBound+" "+maxXBoundLen);

			//Linear moving center
			double gifCenterXPos=startGifNumX*arr[a][0][0].getWidth()+arr[a][0][0].getWidth()/2.0;
			double gifCenterYPos=startGifNumY*arr[a][0][0].getHeight()+arr[a][0][0].getHeight()/2.0;
			double gifEndXPos=arr[(a+1)%numOfGifs][0][0].getWidth()*arr[a][0][0].getWidth()-nextMaxXBound-arr[a][0][0].getWidth()/2.0;
			double gifEndYPos=arr[(a+1)%numOfGifs][0][0].getHeight()*arr[a][0][0].getHeight()-nextMaxYBound-arr[a][0][0].getHeight()/2.0;
			double gifCenterXInc=(gifEndXPos-gifCenterXPos)/numOfFrames;
			double gifCenterYInc=(gifEndYPos-gifCenterYPos)/numOfFrames;

			int[][] gifArr=new int[arr[(a+1)%numOfGifs][0][0].getHeight()][arr[(a+1)%numOfGifs][0][0].getWidth()];
			for (int b = 0; b < gifArr.length; b++) {
				for (int c = 0; c < gifArr[0].length; c++) {
					if(gifTypeOverride[a][b][c]!=0)
						gifArr[b][c]=gifTypeOverride[a][b][c]-1;
					else
						gifArr[b][c]=(int)(Math.random()*arr[a].length);
				}
			}
			gifArr[(int)startGifNumY][(int)startGifNumX]=0;

			BufferedImage[] res=new BufferedImage[numOfFrames];

			for (int i = 0; i < numOfFrames; i++) {
				res[i]=new BufferedImage(arr[0][0][0].getWidth(),arr[0][0][0].getHeight(),BufferedImage.TYPE_INT_ARGB);
				double adjustWidth=(maxXBoundLen+minXBoundLen)/arr[0][0][0].getWidth();
				double adjustHeight=(maxYBoundLen+minYBoundLen)/arr[0][0][0].getHeight();
				for (int j = 0; j < arr[0][0][0].getHeight(); j++) {
					for (int k = 0; k < arr[0][0][0].getWidth(); k++) {
						double xPos=gifCenterXPos-minXBoundLen+k*adjustWidth;
						double yPos=gifCenterYPos-minYBoundLen+j*adjustHeight;
						int gifXPos=(int)(xPos/arr[a][0][0].getWidth());
						int gifYPos=(int)(yPos/arr[a][0][0].getHeight());
						xPos%=arr[a][0][0].getWidth();
						yPos%=arr[a][0][0].getHeight();

						int randGifNum=gifArr[gifYPos][gifXPos];
						int color=arr[a][randGifNum][i%frameLen[a][randGifNum]].getRGB((int)(xPos*arr[a][randGifNum][0].getWidth()/arr[a][0][0].getWidth()),(int)(yPos*arr[a][randGifNum][0].getHeight()/arr[a][0][0].getHeight()));

						int alpha=color>>24&255;
						int red=color>>16&255;
						int green=color>>8&255;
						int blue=color&255;
						alpha=(int)(alpha*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs][0]][gifYPos][gifXPos][0]*(.9*i/numOfFrames));
						red=(int)(red*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs][0]][gifYPos][gifXPos][1]*(.9*i/numOfFrames));
						green=(int)(green*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs][0]][gifYPos][gifXPos][2]*(.9*i/numOfFrames));
						blue=(int)(blue*(1-.9*i/numOfFrames)+avgColors[(a+1)%numOfGifs][i%frameLen[(a+1)%numOfGifs][0]][gifYPos][gifXPos][3]*(.9*i/numOfFrames));
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
