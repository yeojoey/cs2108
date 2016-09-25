import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class Tester {
	
	ImageSearch is;
	QueryProcessor qp;
	Map<String, ImageData> queryImages;
	
	public Tester() {
		this.is = new ImageSearch();
		this.qp = new QueryProcessor(is);
		this.queryImages = qp.getQueryImages();
	}
	
	private double[] testSingleFeature(SearchType type) throws IOException {
		
		double[] averages = new double[3];
		Set<String> imgNames = queryImages.keySet();
		List<double[]> f1Values = new ArrayList<double[]>();
		List<SearchType> searchTypes = new ArrayList<SearchType>();
		searchTypes.add(type);
		
		for (String imgName : imgNames) {			
			qp.processQuery(searchTypes, new File(imgName));
			double[] f1 = qp.getF1Values();
			averages[0] += f1[0];
			averages[1] += f1[1];
			averages[2] += f1[2];			
		}
		
		averages[0] = averages[0]/imgNames.size();	// avg precision
		averages[1] = averages[1]/imgNames.size();	// avg recall
		averages[2] = averages[2]/imgNames.size();	// avg F1
		
		return averages;
		
	}
	
	
	
	public static void main(String[] args) throws IOException {

		Tester t = new Tester();
		System.out.println(Arrays.toString("Avg Text: " + t.testSingleFeature(SearchType.TEXT)));
		System.out.println(Arrays.toString("Avg ColorHist: " + t.testSingleFeature(SearchType.COLORHIST)));
		//System.out.println(Arrays.toString("Avg Semantic: " + t.testSingleFeature(SearchType.SEMANTIC));
	}

}
