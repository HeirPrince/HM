package nasaaty.com.hm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Order {
	@PrimaryKey(autoGenerate = true)
	private int id;
	private String product_id;
	private String owner;

	public Order() {
	}

	public Order(int id, String product_id, String owner) {
		this.id = id;
		this.product_id = product_id;
		this.owner = owner;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
}
