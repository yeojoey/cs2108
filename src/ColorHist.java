import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.HashMap;

public class ColorHist {
	
	private static int dim = 32;
	
	public static void preprocess(HashMap<String, ImageData> images) throws IOException {
		// generates all color histograms for quicker subsequent queries
		for (ImageData image : images.values()) {
			double[] histogram = getHist(image.getImage());
			image.setColorHistogram(histogram);
		}
	}
	
	// similarity between query image and all images
	public static void computeSimilarity(HashMap<String, ImageData> images, ImageData queryImage) {
		double[] queryHistogram = queryImage.getColorHistogram();
		for (ImageData image : images.values()) {
			double similarity = computeSimilaritySingle(image.getColorHistogram(), queryHistogram);
			image.setColorSimilarity(similarity);
		}
	}
	
	// similarity between two images
	private static double computeSimilaritySingle(double[] hist1, double[] hist2) {
		return 1 - calculateDistance(hist1, hist2);
	}
	
	private static double[] getHist(BufferedImage image) {
		int imHeight = image.getHeight();
        int imWidth = image.getWidth();
        double[] bins = new double[dim*dim*dim];
        int step = 256 / dim;
        Raster raster = image.getRaster();
        for(int i = 0; i < imWidth; i++)
        {
            for(int j = 0; j < imHeight; j++)
            {
            	// rgb->ycrcb
            	int r = raster.getSample(i,j,0);
            	int g = raster.getSample(i,j,1);
            	int b = raster.getSample(i,j,2);
            	
            	//Changed Codes. 
            	int y  = (int)( 0 + 0.299   * r + 0.587   * g + 0.114   * b);
        		int cb = (int)(128 -0.16874 * r - 0.33126 * g + 0.50000 * b);
        		int cr = (int)(128 + 0.50000 * r - 0.41869 * g - 0.08131 * b);
        		
        		int ybin = y / step;
        		int cbbin = cb / step;
        		int crbin = cr / step;

        		//Changed Codes. 
                bins[ybin*dim*dim+cbbin*dim+crbin] ++;
            }
        }
        
        //Changed Codes. 
        for(int i = 0; i < dim*dim*dim; i++) {
        	bins[i] = bins[i]/(imHeight*imWidth);
        }
        
        return bins;
	}
	
	private static double calculateDistance(double[] array1, double[] array2)
    {
		// Euclidean distance
        /*double Sum = 0.0;
        for(int i = 0; i < array1.length; i++) {
           Sum = Sum + Math.pow((array1[i]-array2[i]),2.0);
        }
        return Math.sqrt(Sum);
        */
        
        // Bhattacharyya distance
		double h1 = 0.0;
		double h2 = 0.0;
		int N = array1.length;
        for(int i = 0; i < N; i++) {
        	h1 = h1 + array1[i];
        	h2 = h2 + array2[i];
        }

        double Sum = 0.0;
        for(int i = 0; i < N; i++) {
           Sum = Sum + Math.sqrt(array1[i]*array2[i]);
        }
        double dist = Math.sqrt( 1 - Sum / Math.sqrt(h1*h2));
        return dist;
    }
}
