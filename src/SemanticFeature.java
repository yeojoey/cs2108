import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SemanticFeature {
	private static final String unprocessedImagesFilePath = ".." + File.separator + "unprocessedImagePaths.txt"; 
	private static final String semanticFeatureEXEdir = ".." + File.separator + "FeatureExtractor" + File.separator + "semanticFeature";
	private static final String semanticFeatureEXE = semanticFeatureEXEdir + File.separator + "image_classification.exe";
	
	private static final int categorySize = 1000;
	
	//this is for pre processing
	public static void process(HashMap<String, ImageData> imagesMap) throws IOException {
		File imagesFile = new File(unprocessedImagesFilePath);
		if ( imagesFile.exists()){
			imagesFile.delete();
		}
		
		ArrayList<String> unprocessedImages = getUnprocessedImages(imagesMap);
		if( unprocessedImages.size() > 0 ){
			//generate textFile for exe file to read
			createInputFile(unprocessedImages);
			//run exe file
			runSemanticFeatureTool();
		}
	}
	//get all unprocessed images for preprocessing
	private static ArrayList<String> getUnprocessedImages(HashMap<String, ImageData>imagesMap) throws FileNotFoundException{
		ArrayList<String> unprocessedImages = new ArrayList<String>();
		for ( ImageData imageData : imagesMap.values()){
			String filePath = imageData.getFilepath();
			String semanticFeatureOutputFilePath = createTextFilePath(filePath);
			File semanticFeatureOutputFile = new File(semanticFeatureOutputFilePath);
			if (semanticFeatureOutputFile.exists()){
				double[] semanticFeatureScores = getScoreFromFile(imageData);
				imageData.setSemanticFeatureScores(semanticFeatureScores);
			}
			else{
				unprocessedImages.add(filePath);
			}
		}
		
		return unprocessedImages;
	}
	
	//create textFile for EXE to read from
    private static void createInputFile(ArrayList<String> unprocessedImages) {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(unprocessedImagesFilePath))) {
    		for(String filePath: unprocessedImages) {
        		bw.write(".." + File.separator + ".." + File.separator + filePath);
        		bw.newLine();
        	}
    		bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    //generate a text file path for an image file
	private static String createTextFilePath(String filePath){
		String textFilePath = "";
		int i = filePath.lastIndexOf(".");
		
		if ( i < filePath.length()-1 && i > 0){
			textFilePath = filePath.substring(0,i+1)+"txt";
		}
		
		return textFilePath;
		
	}
    
	//run exe tool file
    private static void runSemanticFeatureTool()throws IOException {
    	ProcessBuilder pB = new ProcessBuilder(semanticFeatureEXE, ".." + File.separator + unprocessedImagesFilePath);
    	pB.directory(new File(semanticFeatureEXEdir).getAbsoluteFile());
    	pB.redirectErrorStream(true);
    	Process process = pB.start();
    	
    	Scanner sc = new Scanner(process.getInputStream());
    	StringBuilder sB = new StringBuilder();
    	while(sc.hasNext()){
    		System.out.println(sc.nextLine());
    	}
    	sc.close();
    	
    	try {
	        int result;
			result = process.waitFor();
	        System.out.printf( "Process exited with result %d and output %s%n", result, sB );
    	} catch (InterruptedException e){
    		e.printStackTrace();
    	}
    }
	
    // read the score from the output txt file and store it in imageData
	private static double[] getScoreFromFile(ImageData imageData) throws FileNotFoundException{
		double[] scores = new double[categorySize];
		String scoreFilePath = createTextFilePath(imageData.getFilepath());
		File scoreFile = new File(scoreFilePath);
		Scanner sc = new Scanner(scoreFile);
		
		int scoreIndex = 0;
		while (sc.hasNext()){
			double score = sc.nextDouble();
			scores[scoreIndex] = score;
			scoreIndex ++;
		}
		sc.close();
		return scores;
	}
	
	//calculate similarity between the query image and all images in the database
	public static void calSimilarity(ArrayList<ImageData> images,ImageData queryImage) throws IOException{
		String qISemanticFeatureFilePath = createTextFilePath(queryImage.getFilepath());
		File qISemanticFeatureFile = new File(qISemanticFeatureFilePath);
		if(!qISemanticFeatureFile.exists()){ //has not been processed
			ArrayList<String> unprocessed = new ArrayList<String>();
			unprocessed.add(queryImage.getFilepath());
			createInputFile(unprocessed);
			runSemanticFeatureTool();
		}
		
		double[] qiScores = getScoreFromFile(queryImage);
		for (ImageData id : images){
			double[] idScores = getScoreFromFile(id);
			double similarity = 0;
			
			if(qiScores == null || idScores == null || qiScores.length < categorySize || idScores.length < categorySize ){
				similarity = 0;
			}
			else{
				for (int i=0; i<categorySize;i++){
					if(qiScores[i] > 0 && idScores[i] > 0){
						similarity = Math.min(qiScores[i], idScores[i]);
					}
				}
			}
			id.setSemanticFeatureSimilarity(similarity);
		}
	}

}

