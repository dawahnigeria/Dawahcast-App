package com.apps.item;

public class ItemAbout {

 	private String app_name, app_logo, app_desc, app_version, author, contact, email, website, privacy, developedby;

	public ItemAbout(String app_name, String app_logo, String app_desc, String app_version, String author, String contact, String email, String website, String privacy, String developedby) {
		this.app_name = app_name;
		this.app_logo = app_logo;
		this.app_desc = app_desc;
		this.app_version = app_version;
		this.author = author;
		this.contact = contact;
		this.email = email;
		this.website = website;
		this.privacy = privacy;
		this.developedby = developedby;
	}

	public String getAppName() {
		return app_name;
	}
	 
	public String getAppLogo() {
		return app_logo;
	}

	public String getAppDesc() {
		return app_desc;
	}

	public String getAppVersion() {
		return app_version;
	}

	public String getAuthor() {
		return author;
	}

	public String getContact() {
		return contact;
	}

	public String getEmail() {
		return email;
	}

	public String getWebsite() {
		return website;
	}

	public String getPrivacy() {
		return privacy;
	}

	public String getDevelopedby() {
		return developedby;
	}
}
