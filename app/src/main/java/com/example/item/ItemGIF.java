package com.example.item;

public class ItemGIF {

	private String id;
	private String image;
	private String views;


	public ItemGIF(String id, String image, String views) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.image = image;
		this.views = views;
	}

	public String getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public String getTotalViews() {
		return views;
	}

	public void setTotalViews(String views) {
		this.views = views;
	}
}
