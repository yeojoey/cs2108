import java.io.File;
import java.io.IOException;
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
	double[] f1Values;
	
	// Constructor
	public QueryProcessor(ImageSearch is) {
		queryImages = new HashMap<String, ImageData>();
		this.is = is;
		loadQueryData();
	}
	
	public List<ImageData> processQuery(List<SearchType> searchTypes, File queryFile) throws IOException {
		ImageData data = getQueryImage(queryFile);
		List<ImageData> results = is.search(searchTypes, data);
		f1Values = calculateF1(results, data);
		return results;
	}
	
	public Map<String, ImageData> getQueryImages() {
		return queryImages;
	}
	
	public double[] getF1Values() {
		return f1Values;
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
	
	private double[] calculateF1(List<ImageData> results, ImageData id) {
		double metrics[] = new double[3];
		double truePositives = 0;
		Map<String, Integer> count = new HashMap<String, Integer>();
		for (int i = 0; i < is.getResultSize(); i++) {
			Set<String> categories = results.get(i).getCategories();
			Set<String> intersection = new HashSet<String>(categories);
			if (id.getCategories() != null) {
				intersection.retainAll(id.getCategories());
			}
			if (intersection.size() > 0) {
				truePositives ++;
			}
			for (String category : categories) {
				if (!count.containsKey(category)) {
					count.put(category, 0);
				}
				count.put(category, count.get(category) + 1);
			}
		}
		
//        System.out.println("Input image has categories: " + id.getCategories());
//        System.out.println("Input image has tags: " + id.getTags());
//        System.out.println("Query results come from the following categories:");
//        for(String category: count.keySet()){
//            System.out.printf("	Category: %s, Count: %s\n", category, count.get(category));
//        }
        
        double totalRelevant = 20;
        double totalSelected = is.getResultSize();
        double precision = truePositives / totalSelected;
        double recall = truePositives / totalRelevant;
        double f1;
        if (!((precision + recall) == 0.0)) {
           f1 = 2 * ((precision * recall) / (precision + recall));
        } else {
           f1 = 0.0;
        }
        //System.out.printf("Precision: %s / Recall: %s / F1: %s\n", precision, recall, f1);
        metrics[0] = precision;
        metrics[1] = recall;
        metrics[2] = f1;
        return metrics;
		
	}
	
	
	public static void main(String[] args) throws IOException {
		
		// testing
		ImageSearch is = new ImageSearch();
		QueryProcessor qp = new QueryProcessor(is);
		List<SearchType> types = new ArrayList<SearchType>();
		types.add(SearchType.TEXT);
		types.add(SearchType.COLORHIST);
		//types.add(SearchType.SEMANTIC);
		File img = new File("0092_2019123562.jpg");
		List<ImageData> results = qp.processQuery(types, img);
		System.out.println(results.get(0));
		System.out.println(results.get(1));
		System.out.println(results.get(2));

		
	}
	

}
