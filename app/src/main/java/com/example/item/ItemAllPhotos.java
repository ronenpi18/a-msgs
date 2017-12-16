package com.example.item;

public class ItemAllPhotos {

	private String CategoryId; 
	private String CategoryName;
	private String CategoryImage;


	public ItemAllPhotos(String categoryid, String categoryname, String categoryimage) {
		// TODO Auto-generated constructor stub
		this.CategoryId=categoryid;
		this.CategoryName=categoryname;
		this.CategoryImage=categoryimage;
	}

	public ItemAllPhotos() {
		// TODO Auto-generated constructor stub
	}

	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryImage(String categoryimage) {
		this.CategoryImage = categoryimage;
	}

	public String getCategoryImage() {
		return CategoryImage;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}

	public String getCategoryId() {
		return CategoryId;
	}

	public void setCategoryId(String categoryid) {
		this.CategoryId = categoryid;
	}

}
