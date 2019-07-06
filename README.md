# zooming-out-gifs
Makes a gif that zooms out into another gif and so on.

The GIF encoder and decoder was used from rtley's repo: https://github.com/rtyley

Put all the gifs in the gifs folder and number them starting from 1 to however many gifs you want to add. That will be the order the gifs will play in. To have duplicate gifs, you will have to re-add a new copy of the gif and rename it to fit in the order for now. The gifProperties text file inside the gifs folder is used to determine how long each gif will last for. Only put integer numbers in there and the row which the number is on corresponds with the gif it's modifying.

If you want multiple gifs to play on the same layer as each other, then chain them together with the layer number and then a dash with a consecutive number. Eg. gif1-1.gif, gif1-2.gif, gif1-3.gif, etc...

To select what position each layer starts at, put the x position separated by a y position on separate lines in gifStartingPosition.txt

To select what gif will be where, put the layer first (starting at 1 for the first layer), put the coordinates in parenthesis with a comma, and choose what gif type after it all on the same line. Put these choices on separate lines in gifTypes.txt

An example has been put in the gifs folder to look at.

![finalGIF](https://user-images.githubusercontent.com/37278446/60606500-19297480-9d81-11e9-807d-027aa0612a49.gif)
