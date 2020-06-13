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

				CellCounterLoadingScreen loadingScreen = new CellCounterLoadingScreen(); //Loading Screen 
				
				addGuiData(img,"Base Image");//Orgininal Image


				
				//Making image easier to process.
				img = ImageProcessor.autoContrast(img, 0.05);
				addGuiData(img,"AutoContrast");

				img = ImageProcessor.exposureTransform(img, 2);
				addGuiData(img,"Brighten");

				img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));
				addGuiData(img,"GuassianBlur");

				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"WeightMedFilter");


				//Getting ready for thresholding, with cleanup and intensity change.
				img = ImageProcessor.laplaceSharpen(img);
				addGuiData(img,"LaplaceSharpen");

				img = ImageProcessor.gammaTransform(img, 3);
				addGuiData(img,"Gamma");

				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"WeightMedFilter");

				img = ImageProcessor.guassianBlur(img);
				addGuiData(img,"Guassian Blur");

				img = ImageProcessor.laplaceSharpen(img);
				addGuiData(img,"LaplaceSharpen");

				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"WeightMedFilter");

		

				//Thresholding
				img = ImageProcessor.thresholdTransform(img, 110);
				addGuiData(img,"Threshold Transform");

				//Post proccessing cleanup.
				img = ImageProcessor.openTransform(img, 3);
				addGuiData(img, "Open 3x");

				img = ImageProcessor.closeTransform(img, 4);
				addGuiData(img, "Close 4x");

				
				//Labelling and cellcount.
				cellCount = ImageProcessor.cellLabel(img);
				addGuiData(img,"Output");
				


				loadingScreen.closeLoading();//Closing Loading Screen

				//Intialising GUI 
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
