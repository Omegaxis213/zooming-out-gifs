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
		BufferedReader f=new BufferedReader(new FileReader("gifs/gifLength.txt"));
		for (int i = 0; i < numOfGifs; i++) {
			int num=Integer.parseInt(f.readLine());
			if(num<=0) continue;
			frameMult[i]=num;
		}
		//manual override for starting position of the gif on each layer
		int[][] startPos=new int[numOfGifs][2];
		for (int i = 0; i < numOfGifs; i++) {
			for (int j = 0; j < 2; j++) {
				startPos[i][j]=-1;
			}
		}
		f=new BufferedReader(new FileReader("gifs/gifStartingPositions.txt"));
		int counter=0;
		while(f.ready())
		{
			StringTokenizer st=new StringTokenizer(f.readLine(),"(,) ");
			int layer=Integer.parseInt(st.nextToken());
			int xPos=Integer.parseInt(st.nextToken());
			int yPos=Integer.parseInt(st.nextToken());
			layer--;
			counter++;
			if(layer<0||layer>=numOfGifs)
			{
				System.out.println("gifStartingPostions.txt - Please put in a valid layer for line "+counter);
				continue;
			}
			if(xPos<0||yPos<0||xPos>=arr[layer][0][0].getWidth()||yPos>=arr[layer][0][0].getHeight())
			{
				System.out.println("gifStartingPostions.txt - Please put a coordinate within 0 to "+(arr[layer][0][0].getWidth()-1)+" for the X position and 0 to "+(arr[layer][0][0].getHeight()-1)+" for the Y position on line "+counter);
				continue;
			}
			startPos[layer][0]=xPos;
			startPos[layer][1]=yPos;
		}
		//zoom in option
		f=new BufferedReader(new FileReader("gifs/gifZoomType.txt"));
		counter=0;
		boolean[] layerIsZoomIn=new boolean[numOfGifs];
		while(f.ready())
		{
			StringTokenizer st=new StringTokenizer(f.readLine());
			int layer=Integer.parseInt(st.nextToken());
			String type=st.nextToken().toLowerCase();
			layer--;
			counter++;
			if(layer<0||layer>=numOfGifs)
			{
				System.out.println("gifZoomType.txt - Please put in a valid layer for line "+counter);
				continue;
			}
			if(type.equals("in"))
				layerIsZoomIn[layer]=true;
			else if(!type.equals("out"))
				System.out.println("gifZoomType.txt - Please put in a valid zoom for line "+counter);
		}
		int[][][] gifTypeOverride=new int[numOfGifs][][];
		for (int i = 0; i < numOfGifs; i++) {
			int nextGifLayer=layerIsZoomIn[i]?i:(i+1)%numOfGifs;
			gifTypeOverride[nextGifLayer]=new int[arr[nextGifLayer][0][0].getHeight()][arr[nextGifLayer][0][0].getWidth()];
		}
		f=new BufferedReader(new FileReader("gifs/gifTypes.txt"));
		counter=0;
		//manual override for which gif is used where
		while(f.ready())
		{
			StringTokenizer st=new StringTokenizer(f.readLine(),"(,) ");
			int layer=Integer.parseInt(st.nextToken());
			int xPos=Integer.parseInt(st.nextToken());
			int yPos=Integer.parseInt(st.nextToken());
			int gifType=Integer.parseInt(st.nextToken());
			layer--;
			counter++;
			if(layer<0||layer>=gifTypeOverride.length)
			{
				System.out.println("gifTypes.txt - Please put in a valid layer for line "+counter);
				continue;
			}
			if(yPos<0||yPos>=gifTypeOverride[layer].length)
			{
				System.out.println("gifTypes.txt - Please put in a valid y position within 0 to "+(gifTypeOverride[layer].length-1)+" for line "+counter);
				continue;
			}
			if(xPos<0||xPos>=gifTypeOverride[layer][yPos].length)
			{
				System.out.println("gifTypes.txt - Please put in a valid x position within 0 to "+(gifTypeOverride[layer][yPos].length-1)+" for line "+counter);
				continue;
			}
			if(gifType<=0||gifType>=arr[layer].length+1)
			{
				System.out.println("gifTypes.txt - Please put in a valid gif type within 1 to "+(arr[layer].length)+" for line "+counter);
				continue;
			}
			gifTypeOverride[layer][yPos][xPos]=gifType;
		}
		for (int a = 0; a < numOfGifs; a++) {
			//added for zoom in feature
			int curGifLayer=layerIsZoomIn[a]?(a+1)%numOfGifs:a;
			int nextGifLayer=layerIsZoomIn[a]?a:(a+1)%numOfGifs;
			System.out.println(nextGifLayer);

			//starting gif number/position of the current gif relative to the next one
			double startGifNumX=(int)(Math.random()*arr[nextGifLayer][0][0].getWidth());
			double startGifNumY=(int)(Math.random()*arr[nextGifLayer][0][0].getHeight());
			if(startPos[curGifLayer][0]!=-1)
			{
				startGifNumX=startPos[curGifLayer][0];
				startGifNumY=startPos[curGifLayer][1];
			}

			//percentage zoom
			double minXBoundLen=arr[curGifLayer][0][0].getWidth()/2.0;
			double maxXBoundLen=arr[curGifLayer][0][0].getWidth()/2.0;
			double minYBoundLen=arr[curGifLayer][0][0].getHeight()/2.0;
			double maxYBoundLen=arr[curGifLayer][0][0].getHeight()/2.0;
			double nextMinXBound=arr[curGifLayer][0][0].getWidth()/2.0+startGifNumX*arr[curGifLayer][0][0].getWidth();
			double nextMaxXBound=arr[nextGifLayer][0][0].getWidth()*arr[curGifLayer][0][0].getWidth()-(arr[curGifLayer][0][0].getWidth()/2.0*3+startGifNumX*arr[curGifLayer][0][0].getWidth());
			double nextMinYBound=arr[curGifLayer][0][0].getHeight()/2.0+startGifNumY*arr[curGifLayer][0][0].getHeight();
			double nextMaxYBound=arr[nextGifLayer][0][0].getHeight()*arr[curGifLayer][0][0].getHeight()-(arr[curGifLayer][0][0].getHeight()/2.0*3+startGifNumY*arr[curGifLayer][0][0].getHeight());

			int numOfFrames=frameMult[curGifLayer]*frameLen[nextGifLayer][0];
			double minXBoundMult=Math.pow(10,Math.log10(nextMinXBound/minXBoundLen)/numOfFrames);
			double minYBoundMult=Math.pow(10,Math.log10(nextMinYBound/minYBoundLen)/numOfFrames);
			double maxXBoundMult=Math.pow(10,Math.log10(nextMaxXBound/maxXBoundLen)/numOfFrames);
			double maxYBoundMult=Math.pow(10,Math.log10(nextMaxYBound/maxYBoundLen)/numOfFrames);
			System.out.println(nextMaxXBound+" "+maxXBoundLen);

			//Linear moving center
			double gifCenterXPos=startGifNumX*arr[curGifLayer][0][0].getWidth()+arr[curGifLayer][0][0].getWidth()/2.0;
			double gifCenterYPos=startGifNumY*arr[curGifLayer][0][0].getHeight()+arr[curGifLayer][0][0].getHeight()/2.0;
			double gifEndXPos=arr[nextGifLayer][0][0].getWidth()*arr[curGifLayer][0][0].getWidth()-nextMaxXBound-arr[curGifLayer][0][0].getWidth()/2.0;
			double gifEndYPos=arr[nextGifLayer][0][0].getHeight()*arr[curGifLayer][0][0].getHeight()-nextMaxYBound-arr[curGifLayer][0][0].getHeight()/2.0;
			double gifCenterXInc=(gifEndXPos-gifCenterXPos)/numOfFrames;
			double gifCenterYInc=(gifEndYPos-gifCenterYPos)/numOfFrames;

			int[][] gifArr=new int[arr[nextGifLayer][0][0].getHeight()][arr[nextGifLayer][0][0].getWidth()];
			for (int b = 0; b < gifArr.length; b++) {
				for (int c = 0; c < gifArr[0].length; c++) {
					if(gifTypeOverride[nextGifLayer][b][c]!=0)
						gifArr[b][c]=gifTypeOverride[nextGifLayer][b][c]-1;
					else
						gifArr[b][c]=(int)(Math.random()*arr[curGifLayer].length);
				}
			}
			gifArr[(int)startGifNumY][(int)startGifNumX]=0;

			BufferedImage[] res=new BufferedImage[numOfFrames];

			//how much to increase rotation each frame. Make sure to keep the constant number a multiple of 2 to ensure consistency and smoothness
			double thetaInc=2*Math.PI/numOfFrames;

			//fix position after rotation (Don't know why I need 3 multiplications instead of just 2)
			long widthNextMod=(long)arr[nextGifLayer][0][0].getWidth()*arr[nextGifLayer][0][0].getWidth()*arr[nextGifLayer][0][0].getWidth();
			long heightNextMod=(long)arr[nextGifLayer][0][0].getHeight()*arr[nextGifLayer][0][0].getHeight()*arr[nextGifLayer][0][0].getHeight();
			long widthCurMod=(long)arr[curGifLayer][0][0].getWidth()*arr[curGifLayer][0][0].getWidth()*arr[curGifLayer][0][0].getWidth();
			long heightCurMod=(long)arr[curGifLayer][0][0].getHeight()*arr[curGifLayer][0][0].getHeight()*arr[curGifLayer][0][0].getHeight();

			for (int i = 0; i < numOfFrames; i++) {
				res[layerIsZoomIn[a]?res.length-i-1:i]=new BufferedImage(arr[0][0][0].getWidth(),arr[0][0][0].getHeight(),BufferedImage.TYPE_INT_ARGB);
				double adjustWidth=(maxXBoundLen+minXBoundLen)/arr[0][0][0].getWidth();
				double adjustHeight=(maxYBoundLen+minYBoundLen)/arr[0][0][0].getHeight();
				for (int j = 0; j < arr[0][0][0].getHeight(); j++) {
					for (int k = 0; k < arr[0][0][0].getWidth(); k++) {
						double xPos=gifCenterXPos-minXBoundLen+k*adjustWidth;
						double yPos=gifCenterYPos-minYBoundLen+j*adjustHeight;

						//rotate codes
						double tempXPos=xPos-gifCenterXPos;
						double tempYPos=yPos-gifCenterYPos;

						double newXPos=tempXPos*Math.cos(thetaInc*i)-tempYPos*Math.sin(thetaInc*i);
						double newYPos=tempXPos*Math.sin(thetaInc*i)+tempYPos*Math.cos(thetaInc*i);

						newXPos+=gifCenterXPos;
						newYPos+=gifCenterYPos;

						int gifXPos=(int)(((int)(newXPos/arr[curGifLayer][0][0].getWidth())+widthNextMod)%arr[nextGifLayer][0][0].getWidth());
						int gifYPos=(int)(((int)(newYPos/arr[curGifLayer][0][0].getHeight())+heightNextMod)%arr[nextGifLayer][0][0].getHeight());
						xPos=(newXPos+widthCurMod)%arr[curGifLayer][0][0].getWidth();
						yPos=(newYPos+heightCurMod)%arr[curGifLayer][0][0].getHeight();
						// int gifXPos=(int)(xPos/arr[curGifLayer][0][0].getWidth());
						// int gifYPos=(int)(yPos/arr[curGifLayer][0][0].getHeight());
						// xPos%=arr[curGifLayer][0][0].getWidth();
						// yPos%=arr[curGifLayer][0][0].getHeight();

						int randGifNum=gifArr[gifYPos][gifXPos];

						int colorXPos=(int)(xPos*arr[curGifLayer][randGifNum][0].getWidth()/arr[curGifLayer][0][0].getWidth());
						int colorYPos=(int)(yPos*arr[curGifLayer][randGifNum][0].getHeight()/arr[curGifLayer][0][0].getHeight());

						int color=arr[curGifLayer][randGifNum][i%frameLen[curGifLayer][randGifNum]].getRGB(colorXPos,colorYPos);

						int alpha=color>>24&255;
						int red=color>>16&255;
						int green=color>>8&255;
						int blue=color&255;

						int nextColor=arr[nextGifLayer][0][i%frameLen[nextGifLayer][0]].getRGB(gifXPos,gifYPos);
						int nextAlpha=nextColor>>24&255;
						int nextRed=nextColor>>16&255;
						int nextGreen=nextColor>>8&255;
						int nextBlue=nextColor&255;

						//invert color feature (Could be interesting to use. Maybe try random gif invert?)
						// red=255-red;
						// green=255-green;
						// blue=255-blue;
						// nextRed=255-nextRed;
						// nextGreen=255-nextGreen;
						// nextBlue=255-nextBlue;

						alpha=(int)(alpha*(1-(double)i/numOfFrames)+nextAlpha*((double)i/numOfFrames));
						red=(int)(red*(1-(double)i/numOfFrames)+nextRed*((double)i/numOfFrames));
						green=(int)(green*(1-(double)i/numOfFrames)+nextGreen*((double)i/numOfFrames));
						blue=(int)(blue*(1-(double)i/numOfFrames)+nextBlue*((double)i/numOfFrames));
						color=(alpha<<24)+(red<<16)+(green<<8)+blue;
						res[layerIsZoomIn[a]?res.length-i-1:i].setRGB(k,j,color);
					}
				}
				// if(i==0)
				// {
				// 	test.setDelay(delay);
				// 	test.addFrame(res[i]);
				// }
				minXBoundLen*=minXBoundMult;
				minYBoundLen*=minYBoundMult;
				maxXBoundLen*=maxXBoundMult;
				maxYBoundLen*=maxYBoundMult;
				gifCenterXPos+=gifCenterXInc;
				gifCenterYPos+=gifCenterYInc;
			}
			for (int i = 0; i < res.length-(layerIsZoomIn[a]^layerIsZoomIn[(a+1)%numOfGifs]?1:0); i++) {
				e.setDelay((int)(delay[curGifLayer]*(1-(double)i/numOfFrames)+delay[nextGifLayer]*(double)i/numOfFrames));
				e.addFrame(res[i]);
			}
		}
		// test.finish();
		e.finish();
	}
}