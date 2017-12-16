package com.example.item;

public class ItemPhotos {

	private String id;
	private String cid;
	private String image;
	private String image_thumb;
	private String cname;
	private String views;


	public ItemPhotos(String id, String cid, String image, String image_thumb, String cname, String views) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.cid = cid;
		this.image = image;
		this.image_thumb = image_thumb;
		this.cname = cname;
		this.views = views;
	}

	public String getId() {
		return id;
	}

	public String getCatId() {
		return cid;
	}

	public String getImage() {
		return image;
	}

	public String getImageThumb() {
		return image_thumb;
	}

	public String getCName() {
		return cname;
	}

	public String getTotalViews() {
		return views;
	}

	public void setTotalViews(String views) {
		this.views = views;
	}
}
