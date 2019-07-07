# zooming-out-gifs
Makes a gif that zooms out into another gif and so on.

![finalGIF](https://user-images.githubusercontent.com/37278446/60606500-19297480-9d81-11e9-807d-027aa0612a49.gif)

The GIF encoder and decoder was used from rtley's repo: https://github.com/rtyley

Put all the gifs in the gifs folder and number them starting from 1 to however many gifs you want to add. That will be the order the gifs will play in. To have duplicate gifs, you will have to re-add a new copy of the gif and rename it to fit in the order for now.

The gifProperties.txt file inside the gifs folder is used to determine how long each gif will last for. Only put integer numbers in there and the row which the number is on corresponds with the gif it's modifying.

Eg.

1

2

3

The first layer will last for one whole completion of the gif on the layer on top of it that you specified. The second layer will last for two whole completions of the gif on the layer on top of it that you specified. The third layer will last for three whole completion of the gif on the first layer that you started out with and completes the loop.

If you want multiple gifs to play on the same layer as each other, then chain them together with the layer number and then a dash with a consecutive number. Eg. gif1-1.gif, gif1-2.gif, gif1-3.gif, etc...

To select what position each layer starts at, put the x position separated by a y position on separate lines in gifStartingPositions.txt.

Eg.

0 0

5 2

This will start the gif off in the top left corner and then start the second layer at the 5th gif to the right from the leftmost side and the 2nd gif down from the topmost side.

To select what gif will be where, put the layer first (starting at 1 for the first layer), put the coordinates in parenthesis with a comma, and choose what gif type after it all on the same line. Put these choices on separate lines in gifTypes.txt

Eg.

1 (3,0) 3

3 (5,10) 2

2 (0,0) 4

This will put the 3rd gif (gif1-3.gif) on the first layer in the 3rd gif from the left and uppermost layer. Then it will place the 2nd gif (gif3-2.gif) on the third layer in the 5th gif from the left and 10th gif from the top. Finally, it will place the 4th gif (gif2-4.gif) on the second layer in the topleft corner.

An example has been put in the gifs folder to look at.
