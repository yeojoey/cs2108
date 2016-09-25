import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;


public class ImageData {
	
	private String filename;
	private String filepath;
	private Set<String> categories;
	private Set<String> tags;
	private double[] semanticFeatureScores;
	
	private double colorSimilarity;
	private double textSimilarity;
	private double siftSimilarity;
	private double semanticFeatureSimilarity;
	
	private double[] colorHistogram;
	private double[] siftHistogram;
	private double[] visualConceptScore;

	// Constructor
	public ImageData(String filename, String filepath, Set<String> tags) {
		this.filename = filename;
		this.filepath = filepath;
		this.tags = tags != null ? tags : new HashSet<String>();
	}
	
	
	public String getFilename() {
		return filename;
	}
	
	public String getFilepath() {
		return filepath;
	}

	public Set<String> getTags() {
		return tags;
	}

	public Set<String> getCategories() {
		return categories;
	}
	
	public BufferedImage getImage() throws IOException {
		File file = new File(filepath);
		BufferedImage img = ImageIO.read(file);
		return img;
	}
	
	public double[] getSemanticFeatureScores(){
		return this.semanticFeatureScores;
	}
	
	public double getSemanticFeatureSimilarity(){
		return this.semanticFeatureSimilarity;
	}
	
	public void setSemanticFeatureScores(double[] sFS){
		this.semanticFeatureScores = sFS;
	}
	
	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}
	
	public void setSemanticFeatureSimilarity(double similarity){
		this.semanticFeatureSimilarity = similarity;
	}
	
	public void setTextSimilarity(double similarity) {
		this.textSimilarity = similarity;
	}
	
	public double getTextSimilarity() {
		return textSimilarity;
	}
	
	public double[] getColorHistogram() {
		return colorHistogram;
	}
	
	public void setColorHistogram(double[] histogram) {
		this.colorHistogram = histogram;
	}
	
	public double getColorSimilarity() {
		return colorSimilarity;
	}
	
	public void setColorSimilarity(double similarity) {
		this.colorSimilarity = similarity;
	}
	
	public String toString() {
		return "----\nfilename: " + filename + "\ntags: " + tags + "\ncategories: "+ categories + "\ntext: " + textSimilarity + "\ncolorhist: " + colorSimilarity + "\nsemantic: " + semanticFeatureSimilarity;
	}
	
}
