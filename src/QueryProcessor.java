import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QueryProcessor {
	
	private static final String DATASET_PATH = "ImageData/data";
	private static final String TAGS_PATH = "ImageData/test_text_tags.txt";
	
	Map<String, ImageData> queryImages;
	ImageSearch is;
	
	// Constructor
	public QueryProcessor(ImageSearch is) {
		queryImages = new HashMap<String, ImageData>();
		this.is = is;
		loadQueryData();
	}
	
	private void loadQueryData() {
		try {
			Map<String, Set<String>> tags = Commons.getTags(TAGS_PATH);
			Map<String, Set<String>> categories = getCategories();
			loadQueryImageData(tags, categories);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadQueryImageData(Map<String, Set<String>> tags, Map<String, Set<String>> categories) {
		for (File folder : new File(DATASET_PATH).listFiles()) {
			String catName = folder.getName();
			for (File file : folder.listFiles()) {
				String filename = file.getName();
			}
			
		}
	}
	
	
	
	// Since there is no categories.txt for the test images
	private Map<String, Set<String>> getCategories() {
		Map<String, Set<String>> categories = new HashMap<String, Set<String>>();
		for (File folder : new File(DATASET_PATH).listFiles()) {
			String catName = folder.getName();
			categories.put(catName, new HashSet<String>());
		}
		return categories;
	}
	

}
