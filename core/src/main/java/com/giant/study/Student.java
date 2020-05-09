package com.giant.study;

import com.giant.annotation.Autowired;
import com.giant.annotation.Value;
import com.giant.study.test.HelloWorld;

public class Student {
    private int id;
    private String name;
    private Address address;
    @Value("Hello")
    private String aaa;
    @Autowired
    private HelloWorld helloWorld;
    @Value("54")
    public double ssss;

    public Student(int id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public HelloWorld getHelloWorld() {
        return helloWorld;
    }

    public void setHelloWorld(HelloWorld helloWorld) {
        this.helloWorld = helloWorld;
    }
}
