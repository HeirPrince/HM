package nasaaty.com.hm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {
	@PrimaryKey(autoGenerate = true)
	private int id;

	private String uid;
	private String name;
	private String email;
	private String photoUrl;
	private String phoneNum;
	private String providerID;

	public User() {
	}

	public User(String uid, String name, String email, String photoUrl, String phoneNum, String providerID) {
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.photoUrl = photoUrl;
		this.phoneNum = phoneNum;
		this.providerID = providerID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}
}
