package com.iecho.vo;

public class ContactVO {
	
	
	private String firstName;
	private String lastName;
	private String number;
	private String email;
	private String contactType;
	private String groupname;
	private String isWebContact;
	private String isFriend;
	private String userId;
	private byte[] image;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactType() {
		return contactType;
	}
	public void setContactType(String contactType) {
		this.contactType = contactType;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getIsWebContact() {
		return isWebContact;
	}
	public void setIsWebContact(String isWebContact) {
		this.isWebContact = isWebContact;
	}
	public String getIsFriend() {
		return isFriend;
	}
	public void setIsFriend(String isFriend) {
		this.isFriend = isFriend;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	
	public ContactVO(String firstName, String lastName, String number,
			String email, String contactType, String groupname,
			String isWebContact, String isFriend) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.number = number;
		this.email = email;
		this.contactType = contactType;
		this.groupname = groupname;
		this.isWebContact = isWebContact;
		this.isFriend = isFriend;
	}
	
	@Override
	public boolean equals(Object o) {
		ContactVO contact=(ContactVO)o;
		String s=contact.number+contact.groupname;
		return s.equals(this.number+this.groupname);
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("FirstName:-"+firstName);
		stringBuilder.append("LastName:-"+lastName);
		stringBuilder.append("number:-"+number);
		stringBuilder.append("email:-"+email);
		stringBuilder.append("contactType:-"+contactType);
		stringBuilder.append("groupname:-"+groupname);
		stringBuilder.append("isWebContact:-"+isWebContact);
		stringBuilder.append("isFriend:-"+isFriend);
		
		// TODO Auto-generated method stub
		return stringBuilder.toString();
	}
	
	public ContactVO(String firstName, String lastName, String number,
			String email, String contactType, String groupname,
			String isWebContact, String isFriend, String userId, byte[] image) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.number = number;
		this.email = email;
		this.contactType = contactType;
		this.groupname = groupname;
		this.isWebContact = isWebContact;
		this.isFriend = isFriend;
		this.userId = userId;
		this.image = image;
	}
	public ContactVO(String number, String groupname) {
		super();
		this.number = number;
		this.groupname = groupname;
	}

	
	

}
