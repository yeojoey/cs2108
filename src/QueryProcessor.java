import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryProcessor {
	
	private static final String DATASET_PATH = "../ImageData/test/data";
	private static final String TAGS_PATH = "../ImageData/test/test_text_tags.txt";
	
	Map<String, ImageData> queryImages;
	ImageSearch is;
	
	// Constructor
	public QueryProcessor(ImageSearch is) {
		queryImages = new HashMap<String, ImageData>();
		this.is = is;
		loadQueryData();
	}
	
	public List<ImageData> processQuery(List<SearchType> searchTypes, File queryFile) {
		ImageData data = getQueryImage(queryFile);
		List<ImageData> results = is.search(searchTypes, data);
		return results;
	}
	
	
	private ImageData getQueryImage(File queryFile) {
		String filename = queryFile.getName();
		if (queryImages.containsKey(filename)) {
			return queryImages.get(filename);
		} else {
			return new ImageData(filename, queryFile.getPath(), null);
		}
	}

	
	
	private void loadQueryData() {
		try {
			Map<String, Set<String>> tags = Commons.getTags(TAGS_PATH);
			Map<String, Set<String>> categories = Commons.getCategories(DATASET_PATH);
			loadQueryImageData(tags, categories);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadQueryImageData(Map<String, Set<String>> tags, Map<String, Set<String>> categories) {
		for (File folder : new File(DATASET_PATH).listFiles()) {
			for (File file : folder.listFiles()) {
				String fileName = file.getName();
				ImageData id = new ImageData(fileName, file.getPath(), tags.get(fileName));
				id.setCategories(categories.get(fileName));
				queryImages.put(fileName, id);
			}
		}
	}
	
	

	
	public static void main(String[] args) {
		
		// testing
		ImageSearch is = new ImageSearch();
		QueryProcessor qp = new QueryProcessor(is);
		List<SearchType> types = new ArrayList<SearchType>();
		types.add(SearchType.TEXT);
		//types.add(SearchType.COLORHIST);
		types.add(SearchType.SEMANTIC);
		File img = new File("0288_364812236.jpg");
		List<ImageData> results = qp.processQuery(types, img);
		System.out.println(results.get(0));
		
	}
	

}
