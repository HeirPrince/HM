package nasaaty.com.hm.model;


public class Review {

	private String uid;
	private String review;
	private String timeStamp;


	public Review() {
	}

	public Review(String uid, String review) {
		this.uid = uid;
		this.review = review;
		this.timeStamp = getTimeStamp();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	private String getTimeStamp() {
		Long tsLong = System.currentTimeMillis() / 1000;
		timeStamp = tsLong.toString();
		return timeStamp;
	}
}
