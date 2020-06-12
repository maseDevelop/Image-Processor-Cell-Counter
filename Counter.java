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
				//Variable declaration.
				int cellCount;
				imageList = new ArrayList<>();
				processList = new ArrayList<>();
				String fileName = args[0];
				String[] fileType = fileName.split("\\.");


				//Get the base image.
				File f = new File(fileName);
				BufferedImage img = ImageIO.read(f);				
				addGuiData(img,"Base Image");


				//Making image easier to process.
				img = ImageProcessor.autoContrast(img, 0.08);
				addGuiData(img,"AutoContrast");

				img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));
				addGuiData(img,"GuassianBlur");

				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"WeightMedFilter");


				//Getting ready for thresholding, with cleanup and intensity change.
				img = ImageProcessor.LaplaceSharpen(img);
				addGuiData(img,"LaplaceSharpen");

				img = ImageProcessor.gammaTransform(img, 4);
				addGuiData(img,"Gamma");

				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"WeightMedFilter");

				img = ImageProcessor.guassianBlur(img);
				addGuiData(img,"Guassian Blur");


				//Thresholding
				img = ImageProcessor.thresholdTransform(img, 30);
				addGuiData(img,"Threshold Transform");

				//Post proccessing cleanup.
				img = ImageProcessor.openTransform(img, 4);
				addGuiData(img, "Open 3x");

				img = ImageProcessor.closeTransform(img, 3);
				addGuiData(img, "Close 3x");

				
				//Labelling and cellcount.
				cellCount = ImageProcessor.regionLabel(ImageProcessor.binaryTransform(img));
				addGuiData(img,"Region Count");
				


				//Displaying out GUI
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
		imageList.add(ImageProcessor.deepCopy(newImage));
		processList.add(processName);
	}
}
