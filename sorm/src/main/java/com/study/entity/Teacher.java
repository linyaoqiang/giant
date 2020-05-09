package com.study.entity;
public class Teacher{
	private String name;
	private String lesson;
	private Integer id;
	private String depart;
	private String user;
	private Integer age;
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getLesson(){
		return this.lesson;
	}
	public void setLesson(String lesson){
		this.lesson=lesson;
	}
	public Integer getId(){
		return this.id;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public String getDepart(){
		return this.depart;
	}
	public void setDepart(String depart){
		this.depart=depart;
	}
	public String getUser(){
		return this.user;
	}
	public void setUser(String user){
		this.user=user;
	}
	public Integer getAge(){
		return this.age;
	}
	public void setAge(Integer age){
		this.age=age;
	}
}