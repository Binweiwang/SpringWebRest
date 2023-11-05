package com.example.springwebrest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringWebRestApplicationTests {

    @Autowired
    private SpringWebRestApplication springWebRestApplication;
    @Test
    void contextLoads() {
        assert(springWebRestApplication != null);
    }

}
