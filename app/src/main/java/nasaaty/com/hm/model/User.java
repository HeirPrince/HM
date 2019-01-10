package nasaaty.com.hm.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;

@Entity
public class User {
	@PrimaryKey(autoGenerate = true)
	private int id;

	private String uid;
	private String name;
	private String email;
	@TypeConverters(UriConverter.class)
	private Uri photoUrl;

	public User(String uid, String name, String email, Uri photoUrl) {
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.photoUrl = photoUrl;
	}

	public User() {
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Uri getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(Uri photoUrl) {
		this.photoUrl = photoUrl;
	}
}
