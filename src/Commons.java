import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Commons {
	
	private static final String CATEGORIES_FILEPATH = "ImageData/category_names.txt";
	private static final String TAGS_FILEPATH  = "ImageData/test/test_text_tags.txt";
	
	public static Set<String> getCategories() throws IOException {
		
		Set<String> categoriesSet = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(CATEGORIES_FILEPATH));
		
		String line = br.readLine();
		while (line!= null) {
			categoriesSet.add(line);
			line = br.readLine();
		}
			
		br.close();
		
		return categoriesSet;
		
	}
	
	public static HashMap<String, Set<String>> getTags() throws FileNotFoundException, IOException {
		
		HashMap<String, Set<String>> tagMap = new HashMap<String, Set<String>>();
		
		BufferedReader br = new BufferedReader(new FileReader(TAGS_FILEPATH));
		
		String line = br.readLine();
		while (line!= null) {
			String[] arr = line.split("      ");
			String filename = arr[0];
			String[] tags = arr[1].split(" ");
			tagMap.put(filename, new HashSet<String>(Arrays.asList(tags)));
			line = br.readLine();
		}
		
		br.close();
		
		return tagMap;
		
	}
	
}
