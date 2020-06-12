import java.awt.image.*;
import java.util.ArrayList;
import java.awt.Point;
import java.util.*;

public class ImageProcessor {

    public static BufferedImage guassianBlur(BufferedImage input) {
        // Produces an image that has convolved with Guassian Blur

        // Convolving the Image in a 1D filter
        float[] matrix = { 0.25f, 0.5f, .25f };

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 1, matrix), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage img1 = op.filter(input, null);

        BufferedImageOp op1 = new ConvolveOp(new Kernel(1, 3, matrix), ConvolveOp.EDGE_NO_OP, null);
        return op1.filter(img1, null);
    }

    public static BufferedImage sharpen(BufferedImage input) {
        // Produces an image that has convolved with Guassian Blur

        // Convolving the Image in a 1D filter
        float[] matrix = new float[] { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f };

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(input, null);

    }

    public static BufferedImage autoContrast(BufferedImage input, double cappingValue) {

        // Working out alow and ahigh
        int[] cumulativeHistogram = new int[256];
        int[] histogram = new int[256];
        int currVal = 0;

        int width = input.getWidth();
        int height = input.getHeight();

        double thresholdSLow = width * height * cappingValue;
        double thresholdSHigh = width * height * (1 - cappingValue);

        int p, a, r, g, b;
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

        // Mapping values to increase pixel value ranges the image
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                p = input.getRGB(u, v);

                a = (p >> 24) & 0xff;
                r = (p >> 16) & 0xff;
                g = (p >> 8) & 0xff;
                b = p & 0xff;

                if (b <= low) {
                    mappingValue = 0;
                } else if (b > low && b < high) {
                    mappingValue = (b - low) * (255 / (high - low));
                } else {
                    mappingValue = 255;
                }
                // Replacing pixel value
                a = mappingValue;
                r = mappingValue;
                g = mappingValue;
                b = mappingValue;

                // replace RGB value with avg
                p = (a << 24) | (r << 16) | (g << 8) | b;

                input.setRGB(u, v, p);
            }
        }

        return input;
    }

    public static BufferedImage invertTransform(BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();

        // convert to greyscale
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                int p = input.getRGB(u, v);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int[] arr = { a, r, g, b };

                for (int i = 0; i < arr.length; i++) {
                    arr[i] = 255 - arr[i];
                }

                // replace RGB value with avg
                p = (arr[0] << 24) | (arr[1] << 16) | (arr[2] << 8) | arr[3];

                input.setRGB(u, v, p);
            }
        }

        return input;
    }

    public static BufferedImage gammaTransform(BufferedImage input, double gammaValue) {

        int width = input.getWidth();
        int height = input.getHeight();

        // convert to greyscale
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                int p = input.getRGB(u, v);

                double a = (p >> 24) & 0xff;
                double r = (p >> 16) & 0xff;
                double g = (p >> 8) & 0xff;
                double b = p & 0xff;

                a = Math.pow((a / 255), gammaValue) * 255;
                r = Math.pow((r / 255), gammaValue) * 255;
                g = Math.pow((g / 255), gammaValue) * 255;
                b = Math.pow((b / 255), gammaValue) * 255;

                double[] arr = { a, r, g, b };

                // replace RGB value with avg
                p = ((int) arr[0] << 24) | ((int) arr[1] << 16) | ((int) arr[2] << 8) | (int) arr[3];

                input.setRGB(u, v, p);
            }
        }

        return input;
    }

    public static BufferedImage grayScaleTransform(BufferedImage input) {

        int width = input.getWidth();
        int height = input.getHeight();

        // convert to greyscale
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                int p = input.getRGB(u, v);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                // calculate average
                int avg = ((r) + (g) + (b)) / 3;

                // replace RGB value with avg
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                input.setRGB(u, v, p);
            }
        }
        return input;
    }

    public static BufferedImage exposureTransform(BufferedImage input, double scaler) {

        int width = input.getWidth();
        int height = input.getHeight();

        // convert to greyscale
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                int p = input.getRGB(u, v);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                a *= scaler;
                r *= scaler;
                g *= scaler;
                b *= scaler;

                int[] arr = { a, r, g, b };

                // Capping the values if they have been scaled to high or to low
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] > 255) {
                        arr[i] = 255;
                    } else if (arr[i] < 0) {
                        arr[i] = 0;
                    }
                }

                // replace RGB value with avg
                p = (arr[0] << 24) | (arr[1] << 16) | (arr[2] << 8) | arr[3];

                input.setRGB(u, v, p);
            }
        }

        return input;
    }

    public static BufferedImage thresholdTransform(BufferedImage input, int thresholdValue) {
        // Performs a threshold transformation on the image

        int width = input.getWidth();
        int height = input.getHeight();

        // convert to greyscale
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                int p = input.getRGB(u, v);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                if (b >= thresholdValue) {
                    a = r = g = b = 255;
                } else {
                    a = r = g = b = 0;
                }

                // replace RGB value with avg
                p = (a << 24) | (r << 16) | (g << 8) | b;

                input.setRGB(u, v, p);
            }
        }

        return input;
    }

    public static BufferedImage BWweightedMedianFilter(BufferedImage input) {
        // Only works on pixels were R = G = B i.e. it is a gray scale image
        int height = input.getHeight();
        int width = input.getWidth();
        int weightValue;
        int b, p;
        int filterSize = 3;
        int medianValue;
        int newHotspotValue;
        int counter = 0;

        int[] weightsArray = { 0, 0, 1, 2, 1, 0, 0, 0, 3, 13, 22, 13, 3, 0, 1, 13, 59, 97, 59, 13, 1, 2, 22, 97, 159,
                97, 22, 2, 1, 13, 59, 97, 59, 13, 1, 0, 3, 13, 22, 13, 3, 0, 0, 0, 1, 2, 1, 0, 0 };

        ArrayList<Integer> filterNums = new ArrayList<>();

        BufferedImage copy = deepCopy(input);

        for (int v = filterSize; v <= height - 2 - filterSize; v++) {
            for (int u = filterSize; u <= width - 2 - filterSize; u++) {

                // compute filter result for position (u,v):
                for (int i = -filterSize; i <= filterSize; i++) {
                    for (int j = -filterSize; j <= filterSize; j++) {
                        p = input.getRGB(u + i, v + j);
                        b = p & 0xff;

                        weightValue = weightsArray[counter];

                        counter++;
                        // Adding to the filternums list as per specificed by the weights array
                        for (int j2 = 1; j2 <= weightValue; j2++) {
                            filterNums.add(b);
                        }
                    }
                }
                counter = 0;
                // Sorting the list
                Collections.sort(filterNums);
                medianValue = filterNums.size() / 2;

                // Replacing hotspot with new pixel
                newHotspotValue = filterNums.get(medianValue);

                p = (newHotspotValue << 24) | (newHotspotValue << 16) | (newHotspotValue << 8) | newHotspotValue;

                copy.setRGB(u, v, p);
                filterNums.clear();
            }
        }
        return copy;
    }

    public static BufferedImage LaplaceSharpen(BufferedImage input) {
        int height = input.getHeight();
        int width = input.getWidth();
        int filterPosition;
        int b, p, inputDirValue, inputderValueBlue;
        int filterSize = 1;
        int value = 0;
        int counter = 0;

        int[] filter = { 1, 1, 1, 1, -8, 1, 1, 1, 1 };

        BufferedImage inputDerivative = deepCopy(input);

        for (int v = filterSize; v <= height - 2 - filterSize; v++) {
            for (int u = filterSize; u <= width - 2 - filterSize; u++) {

                // compute filter result for position (u,v):
                for (int i = -filterSize; i <= filterSize; i++) {
                    for (int j = -filterSize; j <= filterSize; j++) {
                        p = input.getRGB(u + i, v + j);
                        b = p & 0xff;

                        filterPosition = filter[counter];
                        counter++;

                        value += (b * filterPosition);
                    }
                }
                counter = 0;

                if (value < 0) {
                    value = 0;
                }

                p = (value << 24) | (value << 16) | (value << 8) | value;

                inputDerivative.setRGB(u, v, p);

                value = 0;
            }
        }

        // Subtract second diverative from the orginal image
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = input.getRGB(u, v);
                inputDirValue = inputDerivative.getRGB(u, v);

                b = p & 0xff;
                inputderValueBlue = inputDirValue & 0xff;

                b = b - inputderValueBlue;// Subtracing values

                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }

                // replace RGB value with avg
                p = (b << 24) | (b << 16) | (b << 8) | b;

                input.setRGB(u, v, p);
            }
        }

        return input;
    }

    // BWerode takes an image where Background Pixels are White and foreground
    // pixels are white.
    public static BufferedImage BWerode(BufferedImage input) {
        int height = input.getHeight();
        int width = input.getWidth();
        int filterPosition;
        int b, p, copyPixel, copyValueBlue;
        int filterboarderLength = 1;
        int value = 0;
        int newHotspotValue;
        int counter = 0;

        BufferedImage copy = deepCopy(input);

        //int[] structuingElement = { 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0 }; // Hotspot at 12
        //int[] structuingElement = { 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0 };
        int[] structuingElement = {0,1,0,1,1,1,0,1,0};

        for (int v = filterboarderLength; v <= height - 2 - filterboarderLength; v++) {
            for (int u = filterboarderLength; u <= width - 2 - filterboarderLength; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff;

                // if pixel is White
                if (b == 0) {

                    for (int i = -filterboarderLength; i <= filterboarderLength; i++) {
                        for (int j = -filterboarderLength; j <= filterboarderLength; j++) {
                            p = input.getRGB(u + i, v + j);
                            b = p & 0xff;

                            filterPosition = structuingElement[counter];
                            counter++;

                            // If on copy image the value is 0 (black), and on sturcturing element value is
                            // one then invert pixel on copy image
                            if (b == 255 && filterPosition == 1) {
                                copyPixel = (0 << 24) | (0 << 16) | (0 << 8) | 0;
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

    public static BufferedImage BWdilate(BufferedImage input) {
        return invertTransform(BWerode(invertTransform(input)));
    }

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

    public static int regionLabel(BufferedImage input) {

        int labelCount = 2;

        int width = input.getWidth();
        int height = input.getHeight();

        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                int p = input.getRGB(u, v);
                int b = p & 0xff;

                if (b == 1) {
                    floodFill(input, u, v, labelCount);
                    labelCount++;
                }
            }
        }
       
        return labelCount-2;
    }

    private static BufferedImage floodFill(BufferedImage input, int u, int v, int label) {

        Deque<Point> stack = new LinkedList<>();
        int width = input.getWidth();
        int height = input.getHeight();
        int p,b,finalValue;

        stack.push(new Point(u, v));

        while (!stack.isEmpty()) {
            Point point = stack.pop();
            int x = (int)point.getX();
            int y = (int)point.getY();

            if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {

                p = input.getRGB(x,y);
                b = p & 0xff;

                if(b == 1){
                    finalValue = (255 << 24) | (28 << 16) | (104 << 8) | label;
                    input.setRGB(x, y, finalValue);
                    stack.push(new Point(x + 1, y));
                    stack.push(new Point(x, y + 1));
                    stack.push(new Point(x, y - 1));
                    stack.push(new Point(x - 1, y));
                } 
            }
        }
        return input;
    }

    public static BufferedImage binaryTransform(BufferedImage input) {
        // Performs a threshold transformation on the image

        int width = input.getWidth();
        int height = input.getHeight();

        int p,b;

        // convert to binary
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                p = input.getRGB(u, v);
                b = p & 0xff;

                if (b >= 127) {
                    b = 1;
                } else {
                    b = 0;
                }

                // replace RGB value with avg
                p = (b << 24) | (b << 16) | (b << 8) | b;

                input.setRGB(u, v, p);
            }
        }

        return input;
    }


    // Produces a full copy of a Buffered Image
    public static BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
