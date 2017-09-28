package com.apps.item;

public class ItemCat {
	
	private String CategoryId;
	private String CategoryName;

	public ItemCat(String CategoryId, String CategoryName) {
		this.CategoryId = CategoryId;
		this.CategoryName = CategoryName;
	}
	
	public String getCategoryId() {
		return CategoryId;
	}

	public void setCategoryId(String categoryid) {
		this.CategoryId = categoryid;
	}
	
	
	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}

}
