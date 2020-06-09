import java.awt.image.*;
import java.util.ArrayList;
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

        for (int i = 0; i < cumulativeHistogram.length; i++) {
            System.out.println("index:" + i + " " + cumulativeHistogram[i]);
        }

        System.out.println(thresholdSLow);

        // From the cumulative histgram getting the diereved alow
        for (int i = 0; i < cumulativeHistogram.length; i++) {
            if (cumulativeHistogram[i] >= thresholdSLow) {
                low = i;
                break; // Exiting the loop
            }
        }

        System.out.println(low);

        // From the cumulative histgram getting the diereved ahigh
        for (int i = cumulativeHistogram.length - 1; i >= 0; i--) {
            if (cumulativeHistogram[i] > 0 && cumulativeHistogram[i] <= thresholdSHigh) {
                high = i;
                break;// Exiting the loop
            }
        }

        System.out.println(high);

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

                // System.out.println(b);

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
        int pixelValue;
        int a, r, g, b, p;
        int filterSize = 3;
        int medianValue;
        int newHotspotValue;
        int counter = 0;

        int[] weightsArray = { 1, 2, 1 ,  2, 3, 2 ,  1, 2, 1  };
        ArrayList<Integer> filterNums = new ArrayList<>();

        BufferedImage copy = deepCopy(input);


        for (int v = filterSize; v <= height - 2 - filterSize; v++) {
            for (int u = filterSize; u <= width - 2 - filterSize; u++) {

                
                // compute filter result for position (u,v):
                for (int i = - filterSize; i <= filterSize; i++) {           
                    for (int j =  - filterSize; j <= filterSize; j++) {
                        p = input.getRGB(u + i, v + j);
                        b = p & 0xff;

                        //weightValue = weightsArray[counter];
                        weightValue = 3;
                        System.out.println(counter);
                        counter++;

                        // Adding to the filternums list as per specificed by the weights array
                        for (int j2 = 1; j2 <= weightValue; j2++) {
                            filterNums.add(b);
                        }
                        
                    }
                }
                System.out.println("yoooooo");
                counter = 0;
                // Sorting the list
                Collections.sort(filterNums);
                medianValue = filterNums.size() / 2;
                medianValue++;

                //Replacing hotspot with new pixel 
                newHotspotValue = filterNums.get(medianValue);   

                p = (newHotspotValue << 24) | (newHotspotValue << 16) | (newHotspotValue << 8) | newHotspotValue;

                copy.setRGB(u, v, p);

                filterNums.clear();
            }
        }
        return copy;
    }


    private static BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
       }
}
