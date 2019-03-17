package nasaaty.com.hm.model;

import android.net.Uri;

public class ImageFile {
	private String fileName;
	private String downloadUrl;
	private Uri file;
	private Boolean isDefault;

	public ImageFile() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public Uri getFile() {
		return file;
	}

	public void setFile(Uri file) {
		this.file = file;
	}

	public Boolean getDefault() {
		return isDefault;
	}

	public void setDefault(Boolean aDefault) {
		isDefault = aDefault;
	}
}
