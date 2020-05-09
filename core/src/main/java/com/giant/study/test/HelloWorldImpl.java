package com.giant.study.test;

import com.giant.annotation.Autowired;
import com.giant.annotation.Service;
import com.giant.study.Hello;

@Service
public class HelloWorldImpl implements HelloWorld{
		@Autowired
		public Hello hello;
		public void sayHello() throws Exception{
			System.out.println("HelloWorld");
			//int i=5/0;
		}

	@Override
	public Hello getHello() {
		return hello;
	}

}
