package nasaaty.com.hm.model;

public class Step {

	private Boolean isActive;
	private String anchor;
	private String title;
	private String subTitle;

	public Step(Boolean isActive, String anchor, String title, String subTitle) {
		this.isActive = isActive;
		this.anchor = anchor;
		this.title = title;
		this.subTitle = subTitle;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
}
