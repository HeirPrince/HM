package nasaaty.com.hm.model;

import com.google.firebase.firestore.Query;

public class Category {

	private String text;
	private Query query;
	private Boolean isAllowed;

	public Boolean isAllowed() {
		return isAllowed;
	}

	public void setAllowed(Boolean allowed) {
		isAllowed = allowed;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
}
