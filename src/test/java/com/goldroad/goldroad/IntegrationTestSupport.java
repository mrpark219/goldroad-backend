package com.goldroad.goldroad;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(properties = {
	"OPEN_AI_API_KEY=test"
})
@SpringBootTest
public abstract class IntegrationTestSupport {
}
