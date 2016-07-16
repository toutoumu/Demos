package com.example.liubin.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

	private String name;
	private int sex;
	
	//必须提供一个名为CREATOR的static final属性 该属性需要实现android.os.Parcelable.Creator<T>接口
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {

		@Override
		public Person createFromParcel(Parcel source) {
			return new Person(source);
		}

		@Override
		public Person[] newArray(int size) {
			return new Person[size];
		}
	};
	
	public Person() {
		
	}
	
	private Person(Parcel source) {
		readFromParcel(source);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	//注意写入变量和读取变量的顺序应该一致 不然得不到正确的结果
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(sex);
	}
	
	//注意读取变量和写入变量的顺序应该一致 不然得不到正确的结果
	public void readFromParcel(Parcel source) {
		name = source.readString();
		sex = source.readInt();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}
	
}
