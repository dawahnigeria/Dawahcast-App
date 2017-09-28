package com.apps.item;

public class ItemVideo {

	private String id;
	private String name;
	private String image;
	private String url;
	private String num;

	public ItemVideo(String id, String name, String image, String url,String num) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.url = url;
		this.num=num;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String geturl() {
		return url;
	}
	public String getnum() {
		return num;
	}

}
