package com.webcloud.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Content implements Parcelable {
	private String id;
	private String title;
	private String icon;
	private String content;
	
	public Content() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Content(Parcel parcel){
		this.id = parcel.readString();
		this.title = parcel.readString();
		this.icon = parcel.readString();
		this.content = parcel.readString();
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeString(this.id);
		parcel.writeString(this.title);
		parcel.writeString(this.icon);
		parcel.writeString(this.content);
	}
	
	public static final Parcelable.Creator<Content> CREATOR = new Creator<Content>() {

		public Content[] newArray(int size) {
			return new Content[size];
		}

		public Content createFromParcel(Parcel source) {
			return new Content(source);
		}
	};
}
