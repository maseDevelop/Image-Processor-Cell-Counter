# Image_Processor

				//Pre proccessing cleanup
				img = ImageProcessor.autoContrast(img, 0.08);
				addGuiData(img,"AutoContrast");

				img = ImageProcessor.guassianBlur(ImageProcessor.grayScaleTransform(img));
				addGuiData(img,"GuassianBlur");

				img = ImageProcessor.BWweightedMedianFilter(img);
				addGuiData(img,"WeightMedFilter");

				//Getting ready for thresholding.
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
				
				*Need to have a check for region counting for 255, 

				//Post proccessing cleanup.
				img = ImageProcessor.openTransform(img, 4);
				addGuiData(img, "Open 3x");

				img = ImageProcessor.closeTransform(img, 3);
				addGuiData(img, "Close 3x");

				img = ImageProcessor.binaryTransform(img);

				//Labelling
				cellCount = ImageProcessor.regionLabel(img);
				addGuiData(img,"Region Count");
				
				*Add in a check to region counting to increase by one once reaching 255.
