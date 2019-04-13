package nasaaty.com.hm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Favorite {

	@PrimaryKey(autoGenerate = true)
	private int id;
	private String product_id;
	private String product_label;

	@Ignore
	public Favorite() {
	}

	public Favorite(int id, String product_id, String product_label) {
		this.id = id;
		this.product_id = product_id;
		this.product_label = product_label;
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

	public String getProduct_label() {
		return product_label;
	}

	public void setProduct_label(String product_label) {
		this.product_label = product_label;
	}
}
