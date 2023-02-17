package com.itheima.reggie;

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

@SpringBootTest//当测试类与引导类在同一包下时，能拿到Spring容器
class ReggieTakeOutApplicationTests {


	@Test
	void contextLoads() {
		Animal d = new dog("旺财");
		System.out.println(d);
	}


}

