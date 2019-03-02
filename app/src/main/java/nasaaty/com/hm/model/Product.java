package nasaaty.com.hm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Product {
	@PrimaryKey(autoGenerate = true)
	private int id;
	private String pid;

	private String label;
	private String description;
	private int price;
	private String owner;
	public int numRatings;
	public double avgRatings;
	private String category;

	public Product() {
	}

	public Product(String pid, String label, String description, int price, String owner, String category, int numRatings, double avgRatings) {
		this.pid = pid;
		this.label = label;
		this.description = description;
		this.price = price;
		this.owner = owner;
		this.category = category;
		this.numRatings = numRatings;
		this.avgRatings = avgRatings;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getNumRatings() {
		return numRatings;
	}

	public void setNumRatings(int numRatings) {
		this.numRatings = numRatings;
	}

	public double getAvgRatings() {
		return avgRatings;
	}

	public void setAvgRatings(double avgRatings) {
		this.avgRatings = avgRatings;
	}
}
