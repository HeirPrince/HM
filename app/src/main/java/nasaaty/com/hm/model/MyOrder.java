package nasaaty.com.hm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class MyOrder {
	@PrimaryKey(autoGenerate = true)
	private int id;

	private String order_id;
	private String product_id;
	private String owner;
	private int count;

	public MyOrder() {
	}

	public MyOrder(int id, String order_id, String product_id, String owner, int count) {
		this.id = id;
		this.order_id = order_id;
		this.product_id = product_id;
		this.owner = owner;
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
