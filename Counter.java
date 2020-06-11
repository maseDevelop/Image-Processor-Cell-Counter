import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class Counter {

	private static ArrayList<BufferedImage> imageList;
	private static ArrayList<String> processList;

	public static void main(String args[]) throws IOException {
		try {
			// Reading in file name form the commmand line
			if (args.length == 1) {
				String fileName = args[0];
				String[] fileType = fileName.split("\\.");


				// reading image
				File f = new File(fileName);
				BufferedImage img = ImageIO.read(f);

				int cellCount;

				
				imageList = new ArrayList<>();
				processList = new ArrayList<>();
				
				addGuiData(img,"Base Image");


				//Pre proccessing cleanup
				img = ImageProcessor.autoContrast(img, 0.05);
				
				img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));
				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"blur");

				//Getting ready for thresholding.
				img = ImageProcessor.LaplaceSharpen(img);
				img = ImageProcessor.gammaTransform(img, 2.5);
				img = ImageProcessor.BWweightedMedianFilter(img);
				img = ImageProcessor.guassianBlur(img);
				addGuiData(img,"Weighed Median");

				//Thresholding
				img = ImageProcessor.thresholdTransform(img, 30);
				addGuiData(img,"Threshold Transform");

				//Post proccessing cleanup.
				img = ImageProcessor.BWweightedMedianFilter(img);

				img = ImageProcessor.binaryTransform(img);

				cellCount = ImageProcessor.regionLabel(img);
				addGuiData(img,"Final");

				new CellCounterGUI(imageList, processList, cellCount);

				// write image
				f = new File(fileType[0] + "_output." + fileType[1] );
				ImageIO.write(img, fileType[1], f);

			} else {
				System.out.println("Usage: java Counter <ImageFile>");
			}

		} catch (Exception e) {
			System.out.print(e);
			System.out.println();
		}
	}

	private static void addGuiData(BufferedImage newImage, String processName){

		imageList.add(deepCopy(newImage));
		processList.add(processName);
	}

	   // Produces a full copy of a Buffered Image
	   private static BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
