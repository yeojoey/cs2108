import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageSearch {
	
	private static final String DATASET_FILEPATH = "ImageData/train/data";
	private static final String CATEGORIES_FILEPATH = "ImageData/category_names.txt";
	private static final String TAGS_FILEPATH  = "ImageData/train/train_text_tags.txt";
	
	private HashMap<String, ImageData> images;
	
	// Constructor
	public ImageSearch() {
		images = new HashMap<String, ImageData>();
		preprocessAll();
	}
	
	private void preprocessAll() {
		
		try {
			
			Map<String, Set<String>> tags = Commons.getTags(TAGS_FILEPATH);
			Map<String, Set<String>> categories = Commons.getCategories(CATEGORIES_FILEPATH);
			
			loadImageData(tags, categories);
			
			// preprocess for each indiviudal method
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	// Create ImageData
	private void loadImageData(Map<String, Set<String>> tags, Map<String, Set<String>> categories) {
		
		for (File folder : new File(DATASET_FILEPATH).listFiles()) {
			File dir = new File(folder.getPath());
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getName();
				ImageData data = new ImageData(filename, files[i].getPath(), tags.get(filename));
				data.setCategories(categories.get(filename));
				images.put(filename, data);
			}
		}
	}
	
	public static void main(String[] args) {
		
		ImageSearch srch = new ImageSearch();
		
		System.out.println(srch.images.get("0339_2053280825.jpg").getTags().toString());
		
	}
	
}
