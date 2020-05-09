package com.giant.study.test;


import com.giant.annotation.Service;

@Service
public class HelloProxyImpl implements HelloProxy{
	
	@Override
	public String helloProxy(String hello) {
		// TODO Auto-generated method stub
		return "Hello";
	}

	@Override
	public void dosfdsafd() {
		// TODO Auto-generated method stub
		System.out.println("tesr");
	}
	
	
}
