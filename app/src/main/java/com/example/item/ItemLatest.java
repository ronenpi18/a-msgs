package com.example.item;

public class ItemLatest {
	
 	private String CategoryName;
	private String ImageUrl; 
	
	public ItemLatest(String lcatename, String limage) {
		// TODO Auto-generated constructor stub
		this.CategoryName=lcatename;
		this.ImageUrl=limage;
	}

	public ItemLatest() {
		// TODO Auto-generated constructor stub
	}

	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}
	 
	public String getImageurl()
	{
		return ImageUrl;
		
	}
	
	public void setImageurl(String imageurl)
	{
		this.ImageUrl=imageurl;
	}

}
