package com.webcloud.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable{
	private String id;
	private String icon;
	private String title;
	
	public Category() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeString(this.id);
		parcel.writeString(this.icon);
		parcel.writeString(this.title);
	}
	
	public Category(Parcel parcel){
		this.id=parcel.readString();
		this.icon = parcel.readString();
		this.title = parcel.readString();
	}
	
	public static final Parcelable.Creator<Category> CREATOR = new Creator<Category>() {

		public Category[] newArray(int size) {
			return new Category[size];
		}

		public Category createFromParcel(Parcel source) {
			return new Category(source);
		}
	};
}
