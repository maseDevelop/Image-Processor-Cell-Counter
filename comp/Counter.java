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
			if (args.length != 1) {
				System.out.println("Usage: java Counter <ImageFile>");
				return;
			}

			String fileName = args[0];
			String[] fileType = fileName.split("\\.");


			// reading image
			File f = new File(fileName);
			BufferedImage img = ImageIO.read(f);

			int cellCount;
				
			imageList = new ArrayList<>();
			processList = new ArrayList<>();

			//Loading screen to display while image is being processed.
			CellCounterLoadingScreen loadingScreen = new CellCounterLoadingScreen();
			
			//Original Image	
			addGuiData(img,"Base Image");

				
			//Pre proccessing cleanup
			img = ImageProcessor.autoContrast(img, 0.1);
			addGuiData(img,"AutoContrast");

			img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));
			addGuiData(img,"GuassianBlur");

			img = ImageProcessor.BWweightedMedianFilter(img);
			addGuiData(img,"WeightMedFilter");


			//Getting ready for thresholding.
			img = ImageProcessor.laplaceSharpen(img);
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
			addGuiData(img, "Open 4x");

			img = ImageProcessor.closeTransform(img, 3);
			addGuiData(img, "Close 3x");

				
			//Labelling and cellcount.
			cellCount = ImageProcessor.cellLabel(img);
			addGuiData(img,"Output");
			

			//Setting up main GUI
			//Closing the loading screen.
			loadingScreen.closeLoading();
			//Creating the main GUI
		    new CellCounterGUI(imageList, processList, cellCount);


			// write image
			f = new File(fileType[0] + "_output." + fileType[1] );
			ImageIO.write(img, fileType[1], f);

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
