package com.goldroad.goldroad.domain.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping("/api/test")
	public String test() {
		return "hello 황금向(향) goldroad";
	}
}
