package nasaaty.com.hm.model;

public class CatModel {

	private String title;
	private String url;
	private int resource;

	public CatModel(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public CatModel(String title, int resource) {
		this.title = title;
		this.url = url;
		this.resource = resource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}
}
