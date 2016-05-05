package com.zesped.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestsRunner {
	
	public static void main(String[] args) throws Exception {
		Result result = JUnitCore.runClasses(SmokeTests.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}