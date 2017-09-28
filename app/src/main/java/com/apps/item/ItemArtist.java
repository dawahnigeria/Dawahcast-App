package com.apps.item;

public class ItemArtist {

	private String id;
	private String name;
	private String image;
	private String thumb;

	public ItemArtist(String id, String name, String image, String thumb) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.thumb = thumb;
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

	public String getThumb() {
		return thumb;
	}

}
