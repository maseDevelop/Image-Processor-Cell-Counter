//Name: Connor Jones, Mason Elliott
//ID: 1351782, 1347257

import java.awt.image.*;
import java.util.ArrayList;
import java.awt.Point;
import java.util.*;

public class ImageProcessor {

    //Filters-----------------------------------------------------------------

     // Produces an image that has convolved with Guassian Blur
     public static BufferedImage guassianBlur(BufferedImage input) {

        // Convolving the Image in a 1D filter Horizontally and Vertically
        float[] filter = { 0.25f, 0.5f, .25f };

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 1, filter), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage img1 = op.filter(input, null);
        BufferedImageOp op1 = new ConvolveOp(new Kernel(1, 3, filter), ConvolveOp.EDGE_NO_OP, null);
        return op1.filter(img1, null);//Returnig Convolved image
    }

    //Sharpens an image
    public static BufferedImage sharpen(BufferedImage input) {

        float[] filter = new float[] { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f };

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, filter), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(input, null);
    }

    //Preforms a mean tranform
    public static BufferedImage meanFilter(BufferedImage input){
        // Only works on pixels were R = G = B i.e. it is a gray scale image
        int height = input.getHeight();
        int width = input.getWidth();
        int b, p;
        int filterSize = 1;
        int meanValue = 0;

        BufferedImage copy = deepCopy(input);

        for (int v = filterSize; v <= height - 2 - filterSize; v++) {
            for (int u = filterSize; u <= width - 2 - filterSize; u++) {

                // compute filter result for position (u,v):
                for (int i = -filterSize; i <= filterSize; i++) {
                    for (int j = -filterSize; j <= filterSize; j++) {
                        p = input.getRGB(u + i, v + j);
                        b = p & 0xff;
                        meanValue += b;
                    }
                }

                meanValue = meanValue / 9; //Scaling the pixel value as to not brighten the image

                p = (meanValue << 24) | (meanValue << 16) | (meanValue << 8) | meanValue;

                copy.setRGB(u, v, p);
                meanValue = 0;
            }
        }
        return copy;
    }

    //Covolves a weighted Medium filter to remove noise - Only works on grayscale images
    public static BufferedImage BWweightedMedianFilter(BufferedImage input) {
      
        int height = input.getHeight();
        int width = input.getWidth();
        int weightValue;
        int b, p;
        int filterBoarderLength = 3;
        int medianValue;
        int newHotspotValue;
        int counter = 0;

        int[] weightsArray = { 0, 0, 1, 2, 1, 0, 0, 0, 3, 13, 22, 13, 3, 0, 1, 13, 59, 97, 59, 13, 1, 2, 22, 97, 159,
                97, 22, 2, 1, 13, 59, 97, 59, 13, 1, 0, 3, 13, 22, 13, 3, 0, 0, 0, 1, 2, 1, 0, 0 };

        ArrayList<Integer> filterNums = new ArrayList<>();

        BufferedImage copy = deepCopy(input);//Preforming a deep copy of the image as the traversal is not done in place

        for (int v = filterBoarderLength; v <= height - 2 - filterBoarderLength; v++) {
            for (int u = filterBoarderLength; u <= width - 2 - filterBoarderLength; u++) {

                // compute filter result for position (u,v):
                for (int i = -filterBoarderLength; i <= filterBoarderLength; i++) {
                    for (int j = -filterBoarderLength; j <= filterBoarderLength; j++) {
                        p = input.getRGB(u + i, v + j);
                        b = p & 0xff;

                        weightValue = weightsArray[counter];//Getting weight at certain position
                        counter++;

                        //Adding to the filternums list as per specificed by the weights array
                        for (int j2 = 1; j2 <= weightValue; j2++) {
                            filterNums.add(b);
                        }
                    }
                }
                counter = 0;//Reseting the counter 

                // Sorting the list
                Collections.sort(filterNums);
                medianValue = filterNums.size() / 2;//Getting Median value

                // Replacing hotspot with new pixel
                newHotspotValue = filterNums.get(medianValue);

                p = (newHotspotValue << 24) | (newHotspotValue << 16) | (newHotspotValue << 8) | newHotspotValue;

                copy.setRGB(u, v, p);
                filterNums.clear();//Clearing list ready for next iteration
            }
        }
        return copy;
    }

    //Sharpens image by subtracting Second Derivative from orginal image
    public static BufferedImage laplaceSharpen(BufferedImage input) {
        int height = input.getHeight();
        int width = input.getWidth();
        int filterPosition;
        int b, p;
        int filterBoarderLength = 1;
        int value = 0;
        int counter = 0;

        int[] filter = { 1, 1, 1, 1, -8, 1, 1, 1, 1 };//45 degree rotation

        BufferedImage inputDerivative = deepCopy(input);

        for (int v = filterBoarderLength; v <= height - 2 - filterBoarderLength; v++) {
            for (int u = filterBoarderLength; u <= width - 2 - filterBoarderLength; u++) {

                // compute filter result for position (u,v):
                for (int i = -filterBoarderLength; i <= filterBoarderLength; i++) {
                    for (int j = -filterBoarderLength; j <= filterBoarderLength; j++) {
                        p = input.getRGB(u + i, v + j);
                        b = p & 0xff;

                        filterPosition = filter[counter];//Gets weight at position in fitler
                        counter++;

                        value += (b * filterPosition);//Computing value for Derived Pixel
                    }
                }
                counter = 0;
                //Clipping Pixel value
                if (value < 0) {
                    value = 0;
                }

                p = (value << 24) | (value << 16) | (value << 8) | value;

                inputDerivative.setRGB(u, v, p);

                value = 0;
            }
        }

        // Subtracting Derived image from the orginal image to get the sharpening affect
        input = subtractImages(input, inputDerivative);

        return input;
    }

    //End of Filters----------------------------------------------------------

    


    //Point Operations--------------------------------------------------------

        //Increase Contrast of the image
        public static BufferedImage autoContrast(BufferedImage input, double cappingValue) {

            // Working out alow and ahigh
            int[] cumulativeHistogram = new int[256];
            int[] histogram = new int[256];
            int currVal = 0;
    
            int width = input.getWidth();
            int height = input.getHeight();
    
            //Getting the lowest and Hightest Pixel values in the image
            double thresholdSLow = width * height * cappingValue;
            double thresholdSHigh = width * height * (1 - cappingValue);
    
            int p, b;
            int low = 0;
            int high = 0;
            int mappingValue;
    
            for (int v = 0; v < height; v++) {
                for (int u = 0; u < width; u++) {
                    p = input.getRGB(u, v);
                    b = p & 0xff;
                    // getting pixel value -> only need one as it is a grayscale image
                    histogram[b]++;
                }
            }
    
            // Creating the cumulative histogram for the image
            for (int i = 0; i < histogram.length; i++) {
                currVal += histogram[i];
                cumulativeHistogram[i] = currVal;
            }
    
            // From the cumulative histgram getting the diereved alow
            for (int i = 0; i < cumulativeHistogram.length; i++) {
                if (cumulativeHistogram[i] >= thresholdSLow) {
                    low = i;
                    break; // Exiting the loop
                }
            }
    
            // From the cumulative histgram getting the diereved ahigh
            for (int i = cumulativeHistogram.length - 1; i >= 0; i--) {
                if (cumulativeHistogram[i] > 0 && cumulativeHistogram[i] <= thresholdSHigh) {
                    high = i;
                    break;// Exiting the loop
                }
            }
    
            //Mapping values to increase pixel value ranges the image
            for (int v = 0; v < height; v++) {
                for (int u = 0; u < width; u++) {
                    p = input.getRGB(u, v);
                    b = p & 0xff;
    
                    if (b <= low) {
                        mappingValue = 0;
                    } else if (b > low && b < high) {
                        mappingValue = (b - low) * (255 / (high - low));
                    } else {
                        mappingValue = 255;
                    }
                    // Replacing pixel value
                    b = mappingValue;
    
                    // replace RGB value with avg
                    p = (b << 24) | (b << 16) | (b << 8) | b;
    
                    input.setRGB(u, v, p);
                }
            }
            return input;
        }

    //Inverts the image
    public static BufferedImage invertTransform(BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        int p,b;

        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff; //only need one colour channel as grayscale image
                //Inverting Pixel 
                b = 255 - b;
                
                // replace RGB value with the new value
                p = (b<< 24) | (b << 16) | (b << 8) | b;

                input.setRGB(u, v, p);
            }
        }
        return input;
    }

    //Preforms Gamma Correcting the Image
    public static BufferedImage gammaTransform(BufferedImage input, double gammaValue) {

        int width = input.getWidth();
        int height = input.getHeight();
        int p;
        double b;

        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff;

                //Preforming Gamma Correction for pixel
                b = Math.pow((b / 255), gammaValue) * 255;

                // replace RGB value with new pixel value
                p = ((int) b << 24) | ((int) b << 16) | ((int) b << 8) | (int) b;

                input.setRGB(u, v, p);
            }
        }
        return input;
    }

       //Preforms a Grayscale transform on an image
       public static BufferedImage grayScaleTransform(BufferedImage input) {

        int width = input.getWidth();
        int height = input.getHeight();
        int p,a,r,g,b;

        // convert to greyscale
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                p = input.getRGB(u, v);

                a = (p >> 24) & 0xff;
                r = (p >> 16) & 0xff;
                g = (p >> 8) & 0xff;
                b = p & 0xff;

                //Calculate average
                int avg = ((r) + (g) + (b)) / 3;

                //Replace RGB value with avg
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                input.setRGB(u, v, p);
            }
        }
        return input;
    }

    //Brights or Darkens the image depending on the scaler value
    public static BufferedImage exposureTransform(BufferedImage input, double scaler) {

        int width = input.getWidth();
        int height = input.getHeight();
        int p,b;

        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff;//Gray scale image so we only need one colour channel

                b *= scaler;//Multipling by scaler value

                // Capping the values if they have been scaled to high or to low
                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }

                // replace RGB value with new grayscale value
                p = (b << 24) | (b << 16) | (b << 8) | b;

                input.setRGB(u, v, p);
            }
        }
        return input;
    }

     //Creates a threshold mask of the image
     public static BufferedImage thresholdTransform(BufferedImage input, int thresholdValue) {
        // Performs a threshold transformation on the image

        int width = input.getWidth();
        int height = input.getHeight();
        int p,b;

        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff;

                //If pixel is above of below certain pixel threhold set 255 otherwise set to zero
                if (b >= thresholdValue) {
                    b = 255;
                } else {
                    b = 0;
                }

                // replace RGB value with new value
                p = (b << 24) | (b << 16) | (b << 8) | b;

                input.setRGB(u, v, p);
            }
        }
        return input;
    }

    //End of Point Operations-------------------------------------------------



    //Morphological Operations------------------------------------------------

    // BWerode takes an image where Background Pixels are White and foreground
    // pixels are white - has to be a black and white image
    public static BufferedImage BWerode(BufferedImage input) {
        int height = input.getHeight();
        int width = input.getWidth();
        int filterPosition;
        int b, p, copyPixel;
        int filterboarderLength = 1;
        int counter = 0;

        BufferedImage copy = deepCopy(input);//Getting a deep copy as operation cant be prefromed in place

        int[] structuingElement = {0,1,0,1,1,1,0,1,0};

        for (int v = filterboarderLength; v <= height - 2 - filterboarderLength; v++) {
            for (int u = filterboarderLength; u <= width - 2 - filterboarderLength; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff;

                // if pixel is white
                if (b == 0) {

                    for (int i = -filterboarderLength; i <= filterboarderLength; i++) {
                        for (int j = -filterboarderLength; j <= filterboarderLength; j++) {
                            p = input.getRGB(u + i, v + j);
                            b = p & 0xff;

                            filterPosition = structuingElement[counter];
                            counter++;

                            // If on copy image the value is black, and on sturcturing element value is
                            // one then invert pixel on copy image
                            if (b == 255 && filterPosition == 1) {
                                copyPixel = (0 << 24) | (0 << 16) | (0 << 8);//inverting Pixel
                                copy.setRGB(u + i, v + j, copyPixel);
                            }
                        }
                    }
                    counter = 0;
                }
            }
        }
        return copy;
    }

    //Dilates black regions in an image - Works on grayscale images
    public static BufferedImage BWdilate(BufferedImage input) {
        //Images is inversed and Then erosion is preformed to add pixels to the outside of black regions, than inversed back to orginal image
        return invertTransform(BWerode(invertTransform(input)));
    }

    //Performs Opening on a thresholded mask - Iternation specifies how many times opening dilatation and erosion is preformed sequentially
    public static BufferedImage openTransform(BufferedImage input, int iteration) {

        // Preforming Erosion
        for (int i = 0; i < iteration; i++) {
            input = BWerode(input);
        }

        // preforming Dilation
        for (int i = 0; i < iteration; i++) {
            input = BWdilate(input);
        }

        return input;
    }

    //Performs Closing on a thresholded mask - Iternation specifies how many times opening erosion and dilation is preformed sequentially
    public static BufferedImage closeTransform(BufferedImage input, int iteration) {

        // Preforming Erosion
        for (int i = 0; i < iteration; i++) {
            input = BWdilate(input);
        }

        // preforming Dilation
        for (int i = 0; i < iteration; i++) {
            input = BWerode(input);
        }

        return input;
    }

    //End Morphological Operations---------------------------------------------------

    //Counts regions and labels them - must use a binary image - Used to count cells in the image
    public static int cellLabel(BufferedImage input) {

        int labelCount = 0;

        int width = input.getWidth();
        int height = input.getHeight();

        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                int p = input.getRGB(u, v);
                int b = p & 0xff;

                //If pixel is black
                if (b == 255) {
                    floodFill(input, u, v);
                    labelCount++;
                }
            }
        }
        return labelCount;
    }

    //Preforms foodfill DFS version - on a given starting pixel u,v
    private static BufferedImage floodFill(BufferedImage input, int u, int v) {

        Deque<Point> stack = new LinkedList<>();
        int width = input.getWidth();
        int height = input.getHeight();
        int p,a,r,g,b,finalValue;

        a = 255;
        r = 255;
        g = 0;
        b = 0;

        //Setting replacment pixel value
        finalValue = (a << 24) | (r << 16) | (g << 8) | b;


        stack.push(new Point(u, v));

        while (!stack.isEmpty()) {
            Point point = stack.pop();
            int x = (int)point.getX();
            int y = (int)point.getY();

            if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {

                p = input.getRGB(x,y);
                b = p & 0xff;

                if(b == 255){
                    input.setRGB(x, y, finalValue);
                    //Pushing new points to search onto the stack
                    stack.push(new Point(x + 1, y));
                    stack.push(new Point(x, y + 1));
                    stack.push(new Point(x, y - 1));
                    stack.push(new Point(x - 1, y));
                } 
            }
        }
        return input;
    }

    //Preforms a Binary Transformation
    public static BufferedImage outlineTransform(BufferedImage input){
        BufferedImage copy = deepCopy(input);

        copy = meanFilter(copy);

        copy = subtractImages(copy, input);

        copy = thresholdTransform(copy, 1);

        copy = invertTransform(copy);

        return copy;
    }

    public static BufferedImage subtractImages(BufferedImage original, BufferedImage subtract){
        int p, b, subP, subB;
        int width = original.getWidth();
        int height = original.getHeight();


        // Subtract second diverative from the orginal image
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = original.getRGB(u, v);
                subP = subtract.getRGB(u, v);

                b = p & 0xff;
                subB = subP & 0xff;

                b = b - subB;// Subtracing values new original - subtract

                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }

                // replace RGB value with new value
                p = (b << 24) | (b << 16) | (b << 8) | b;

                original.setRGB(u, v, p);
            }
        }
        return original;
    }


    // Produces a full copy of a Buffered Image
    public static BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
