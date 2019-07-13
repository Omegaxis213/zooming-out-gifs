# zooming-out-gifs
Makes a gif that zooms out into another gif and so on.

![finalGIF](https://user-images.githubusercontent.com/37278446/60606500-19297480-9d81-11e9-807d-027aa0612a49.gif)

The GIF encoder and decoder was used from rtley's repo: https://github.com/rtyley


Changing how long a gif lasts:


Put all the gifs in the gifs folder and number them starting from 1 to however many gifs you want to add. That will be the order the gifs will play in. To have duplicate gifs, you will have to re-add a new copy of the gif and rename it to fit in the order for now.

The gifProperties.txt file inside the gifs folder is used to determine how long each gif will last for. The first number will be the layer number and the second number will be how long a gif will last for relative to the one above it.

Eg.

1 1

2 2

3 3

The first layer will last for one whole completion of the gif on the layer on top of it that you specified. The second layer will last for two whole completions of the gif on the layer on top of it that you specified. The third layer will last for three whole completion of the gif on the first layer that you started out with and completes the loop.


Chaining multiple gifs into one layer:


If you want multiple gifs to play on the same layer as each other, then chain them together with the layer number and then a dash with a consecutive number. Eg. gif1-1.gif, gif1-2.gif, gif1-3.gif, etc...


Changing starting position of gifs:


To select what position each layer starts at, put the layer number, and then the coordinate in parentheses on separate lines in gifStartingPositions.txt. (Leaving it empty for a layer will make it start from a random gif)

Eg.

1 (0,0)

2 (5,2)

This will start the gif off in the top left corner and then start the second layer at the 5th gif to the right from the leftmost side and the 2nd gif down from the topmost side.


Changing which gif will be placed where:


To select what gif will be where, put the layer first (starting at 1 for the first layer), put the coordinates in parenthesis with a comma, and choose what gif type after it all on the same line. Put these choices on separate lines in gifTypes.txt. (Leaving it empty for gifs will make them be random)

Eg.

1 (3,0) 3

3 (5,10) 2

2 (0,0) 4

This will put the 3rd gif (gif1-3.gif) on the first layer in the 3rd gif from the left and uppermost layer. Then it will place the 2nd gif (gif3-2.gif) on the third layer in the 5th gif from the left and 10th gif from the top. Finally, it will place the 4th gif (gif2-4.gif) on the second layer in the topleft corner.


Changing zoom type:


To make a layer zoom in instead of zooming out, go into gifZoomType.txt and add which layer you want to zoom in/out. Default is zoom out for all layers.

Eg.

1 in

2 out

3 in

This will make the first layer zoom in, the second layer zoom out, and then the third layer will zoom back in.


Changing spin speed:


To make a layer spin, go into gifSpinSpeed.txt and specify how fast you want a layer to spin. Default is 0 (no spinning).

Eg.

1 1

3 10

This will make the first layer spin one complete time, the second layer not spin at all, and the third layer spin 10 times.

An example has been put in the gifs folder to look at.
