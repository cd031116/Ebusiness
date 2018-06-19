package com.hoare.hand.idcard;

import android.graphics.Bitmap;

public class IDCardModel {
	private Bitmap bitmap;
	private String Name;
	private String Sex;
	private String Nation;
	private String Year;
	private String Month;
	private String Day;
	private String Address;
	private String Office;
	private String IDCardNumber;
	private String BeginTime;
	private String EndTime;
	private String OtherData;
	private byte[] fingerprint1;
	private byte[] fingerprint2;
	
	public IDCardModel()
	{
		super();
	};
	public IDCardModel(Bitmap bitmap,String Name,String Nation,String Sex,String Year,String Month,String Day,String Address
			, String Office,String IDCardNumber,String OtherData,String BeginTime,String EndTime,int IsOut)
	{
		super();
		this.bitmap = bitmap;
		this.Name = Name;
		this.Nation = Nation;
		this.Sex = Sex;
		this.Year = Year;
		this.Month = Month;
		this.Day = Day;
		this.Address = Address;
		this.Office =  Office;		
		this.IDCardNumber = IDCardNumber;	
		this.OtherData =  OtherData;	
		this.BeginTime =  BeginTime;	
		this.EndTime = EndTime;		
	}
	public void setPhotoBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	public Bitmap getPhotoBitmap()
	{
		return bitmap;
	}
	public void setName(String Name_string)
	{
		this.Name = Name_string;
		
	}
	public String getName()
	{
		return Name;
	}
	public void setNation(String Nation)
	{
		this.Nation = Nation;
		
	}
	public String getNation()
	{
		return Nation;
	}
	public void setSex(String Sex)
	{
		this.Sex = Sex;
		
	}
	public String getSex()
	{
		return Sex;
	}
	public void setYear(String Year)
	{
		this.Year = Year;
		
	}
	public String getYear()
	{
		return Year;
	}
	public void setMonth(String Month)
	{
		this.Month = Month;
		
	}
	public String getMonth()
	{
		return Month;
	}
	public void setDay(String Day)
	{
		this.Day = Day;
		
	}
	public String getDay()
	{
		return Day;
	}public void setAddress(String Address)
	{
		this.Address = Address;
		
	}
	public String getAddress()
	{
		return Address;
	}
	public void setIDCardNumber(String IDCardNumber) {
		this.IDCardNumber = IDCardNumber;
	}
	public String getIDCardNumber() {
		return IDCardNumber;
	}
	public void setOffice(String Office) {
		this.Office = Office;
	}
	public String getOffice() {
		return Office;
	}
	public void setOtherData(String OtherData) {
		this.OtherData = OtherData;
	}
	public String getOtherData() {
		return OtherData;
	}
	public void setBeginTime(String BeginTime) {
		this.BeginTime = BeginTime;
	}
	public String getBeginTime() {
		return BeginTime;
	}
	public void setEndTime(String EndTime) {
		this.EndTime = EndTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setFP1(byte[] bs) {
		this.fingerprint1 = bs;
	}
	public byte[] getFP1() {
		return fingerprint1;
	}
	public void setFP2(byte[] bs) {
		this.fingerprint2 = bs;
	}
	public byte[] getFP2() {
		return fingerprint2;
	}
}
