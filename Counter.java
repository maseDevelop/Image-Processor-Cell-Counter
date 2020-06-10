import java.io.File;
import java.io.IOException;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Counter {
	public static void main(String args[]) throws IOException {
		try {
			// Reading in file name form the commmand line
			if (args.length == 1) {
				String fileName = args[0];
				String[] fileType = fileName.split("\\.");


				// reading image
				File f = new File(fileName);
				BufferedImage img = ImageIO.read(f);
				// reading image

				

				//img = ImageProcessor.exposureTransform(img, 1.8);
				//img = ImageProcessor.grayScaleTransform(img);
				//img = ImageProcessor.autoContrast(img, 0.05);
				//img = ImageProcessor.thresholdTransform(img, 60);
				//img = ImageProcessor.exposureTransform(img, .5);
				//img = ImageProcessor.gammaTransform(img,1.9);
				//img = ImageProcessor.grayScaleTransform(img);
				//img = ImageProcessor.invertTransform(img);
				//img = ImageProcessor.guassianBlur( ImageProcessor.grayScaleTransform(img));
				//img = ImageProcessor.sharpen(ImageProcessor.guassianBlur( ImageProcessor.grayScaleTransform(img)));
				// = ImageProcessor.exposureTransform(img, 1.1);
				//img = ImageProcessor.invertTransform(img);

				//img = ImageProcessor.sharpen(ImageProcessor.guassianBlur( ImageProcessor.grayScaleTransform(img)));
				//ImageProcessor.BWweightedMedianFilter(img2);
				
				//img = ImageProcessor.gammaTransform(img, 1.8);

				//img = ImageProcessor.thresholdTransform(img, 230);
				//img = ImageProcessor.guassianBlur( ImageProcessor.grayScaleTransform(img));
				//img = ImageProcessor.guassianBlur( ImageProcessor.grayScaleTransform(img));
				//img = ImageProcessor.sharpen(img);

				//img = ImageProcessor.BWweightedMedianFilter(img);

				//Pipline to treshold.
				//Pre proccessing cleanup
				img = ImageProcessor.autoContrast(img, 0.05);
				img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));
				img = ImageProcessor.BWweightedMedianFilter(img);

				//Getting ready for thresholding.
				img = ImageProcessor.LaplaceSharpen(img);
				img = ImageProcessor.gammaTransform(img, 2.5);
				img = ImageProcessor.BWweightedMedianFilter(img);
				//img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));

				//Thresholding
				img = ImageProcessor.thresholdTransform(img, 30);

				//Post proccessing cleanup.
				img = ImageProcessor.BWweightedMedianFilter(img);
				//img = ImageProcessor.BWweightedMedianFilter(img);
				//img = ImageProcessor.BWweightedMedianFilter(img);


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
}
