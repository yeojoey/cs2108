import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Commons {
	

	
	public static Map<String, Set<String>> getCategories(String filepath) throws IOException {
		
		HashMap<String, Set<String>> categoriesSet = new HashMap<String, Set<String>>();

		for (File folder : new File(filepath).listFiles()) {
			String catName = folder.getName();
			for (File img : folder.listFiles()) {
				String imgName = img.getName();
				if (!categoriesSet.containsKey(imgName)) {
					categoriesSet.put(imgName, new HashSet<String>());
				}
				categoriesSet.get(imgName).add(catName);
			}
		}
		
		return categoriesSet;
		
	}
	
	public static HashMap<String, Set<String>> getTags(String filepath) throws FileNotFoundException, IOException {
		
		HashMap<String, Set<String>> tagMap = new HashMap<String, Set<String>>();
		
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		
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
