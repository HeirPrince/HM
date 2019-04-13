package nasaaty.com.hm.model;

public class Order {

	private String customer_id;
	private Product product;
	private String timeStamp;

	public Order() {
	}

	public Order(String customer_id, Product product, String timeStamp) {
		this.customer_id = customer_id;
		this.product = product;
		this.timeStamp = timeStamp;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
