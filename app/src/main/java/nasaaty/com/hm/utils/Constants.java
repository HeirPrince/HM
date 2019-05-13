package nasaaty.com.hm.utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	String message = "";

	public Constants() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getCategories(){
		List<String> categories = new ArrayList<>();
		categories.add("Electronics");
		categories.add("Home Appliances");
		categories.add("Phone Accessories");
		categories.add("Clothes");
		categories.add("Beauty");
		categories.add("Office");
		categories.add("Kitchen Tools");

		return categories;
	}
}
