package com.itheima;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

interface Animal{

}
class dog implements Animal{
	String name;

	public dog(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "dog{" +
				"name='" + name + '\'' +
				'}';
	}
}

@SpringBootTest
class ReggieTakeOutApplicationTests {

	@Test
	void contextLoads() {
		Animal d = new dog("旺财");
		System.out.println(d);
	}


}

