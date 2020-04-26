package com.academy.data;

import com.academy.data.services.TestParent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmericanacademyApplicationTests {

	//@Test
	public void contextLoads() {
	}


	@Test
	public void check()
	{
		TestParent testParent=new TestParent();
		testParent.fun();
		testParent.sound();
	}

}
