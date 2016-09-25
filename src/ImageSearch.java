import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImageSearch {
	
	private static final String DATASET_FILEPATH = "../ImageData/train/data";
	private static final String CATEGORIES_FILEPATH = "../ImageData/category_names.txt";
	private static final String TAGS_FILEPATH  = "../ImageData/train/train_text_tags.txt";
	
	private HashMap<String, ImageData> images;
	
	// Constructor
	public ImageSearch() {
		images = new HashMap<String, ImageData>();
		preprocessAll();
	}
	
	
	public List<ImageData> search(List<SearchType> searchTypes, ImageData queryImg) {
		
		calculateSimilarities(searchTypes, queryImg);
		List<ImageData> results = rankResults(searchTypes);
		return results;
		
	}
	
	public Map<String, ImageData> getImages() {
		return images;
	}
	
	
	private void preprocessAll() {
		
		try {
			
			Map<String, Set<String>> tags = Commons.getTags(TAGS_FILEPATH);
			Map<String, Set<String>> categories = Commons.getCategories(DATASET_FILEPATH);
			
			loadImageData(tags, categories);
			
			// preprocess for each individual method
			ColorHist.preprocess(images);
			//SemanticFeature.process(images);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	// Create ImageData
	private void loadImageData(Map<String, Set<String>> tags, Map<String, Set<String>> categories) {
		
		for (File folder : new File(DATASET_FILEPATH).listFiles()) {
			// excludes .ds_store - extra hidden file in macs
			if (!folder.getPath().endsWith(".DS_Store")) {
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
	}
	
	
	private void calculateSimilarities(List<SearchType> searchTypes, ImageData queryImg) {
		
		for (SearchType searchType : searchTypes) {
			
			switch (searchType) {
			
			case COLORHIST:
				ColorHist.computeSimilarity(images, queryImg);
				break;
				
			case SIFT:
				// todo
				break;
				
			case SEMANTIC:
				// SemanticFeature.calSimilarity(images, queryImg);
				break;
				
			case TEXT:
				TextFeature.calculateSimilarity(images, queryImg.getTags());
				break;
				
			default:
				break;
			
			}
			
			
		}
		
	}
	
	private List<ImageData> rankResults(List<SearchType> searchTypes) {
		List<ImageData> results = new ArrayList<ImageData>(images.values());
		Collections.sort(results, new imageComparator(searchTypes));
		return results;
	}
	
	
	
	// Comparator for ranking
	class imageComparator implements Comparator<ImageData> {
		
		List<SearchType> searchTypes;
		
		public imageComparator(List<SearchType> searchTypes) {
			this.searchTypes = searchTypes;
		}
		
		public int compare(ImageData a, ImageData b) {
			double simA = 0.0;
			double simB = 0.0;
			
			for (SearchType searchType : searchTypes) {
				
				switch (searchType) {
				
				case COLORHIST:
					simA += a.getColorSimilarity();
					simB += b.getColorSimilarity();
					break;
				
				case SEMANTIC:
					// todo
					break;
					
				case SIFT:
					// todo
					break;
					
				case TEXT:
					simA += a.getTextSimilarity();
					simB += b.getTextSimilarity();
					break;
					
				default:
					break;
				
				}
				
			}
			
			return simA > simB ? -1 : simA == simB ? 0 : 1;
		}
		
	}
	
}
