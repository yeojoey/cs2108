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
	
	private double colorSimilarity;
	private double textSimilarity;
	private double siftSimilarity;
	private double visualConceptSimilarity;
	
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
	
	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}
	
}
