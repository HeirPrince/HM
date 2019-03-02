package nasaaty.com.hm.utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {


	static List<String> categories = new ArrayList<>();
	public static String[] getCats(){

		categories.add("Clothes");
		categories.add("Smartphones");
		categories.add("Accessories");
		categories.add("Electronics");
		categories.add("Smartphones");

		String[] array = new String[categories.size()];

		return array;
	}

	public static String getSelectedCat(int pos) {
		return categories.get(pos);
	}
}
