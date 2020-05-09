package com.study.entity;
public class Login{
	private Integer id;
	private String pwd;
	private Integer type;
	private String user;
	public Integer getId(){
		return this.id;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public String getPwd(){
		return this.pwd;
	}
	public void setPwd(String pwd){
		this.pwd=pwd;
	}
	public Integer getType(){
		return this.type;
	}
	public void setType(Integer type){
		this.type=type;
	}
	public String getUser(){
		return this.user;
	}
	public void setUser(String user){
		this.user=user;
	}
}