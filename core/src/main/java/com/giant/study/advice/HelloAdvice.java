package com.giant.study.advice;


import com.giant.annotation.*;

@Advice
@Pointcut("execution(com.giant.study.test.*.*(...))")
public class HelloAdvice {
	@BeforeAdvice
	public void before(Object[] args) {
		System.out.println("Before:"+args);
	}
	
	@AfterAdvice
	public void After(Object[] args,Object returnValue) {
		System.out.println("after");
	}

	@ExceptionAdvice
	public void exce(Object[] args,Throwable e) {
		System.out.println("exception");
		System.out.println(e.getLocalizedMessage());
	}

	@AfterReturningAdvice
	public void afterRetruning(Object[] args,Object returnValue){
		System.out.println("afterRetuning");
	}

	public void afterReturning(Object[] args,Object returnValue){
		System.out.println(this.getClass()+"afterReturning");
	}

	@AroundAdvice
	public void around(Object[] args,Object returnValue) {
		System.out.println(this.getClass()+"around");
	}
}
