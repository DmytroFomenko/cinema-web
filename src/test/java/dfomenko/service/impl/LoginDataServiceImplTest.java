package dfomenko.service.impl;


import dfomenko.service.LoginDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginDataServiceImplTest {

    @Autowired
    private LoginDataService loginDataService;

    @Test
    void encryptionTest() {
        assertEquals("tkd8BurhrK0fg6SzQOr3gw==", loginDataService.encrypt("aaaaa7"));
    }

    @Test
    void decryptionTest() {
        assertEquals("ddddd1", loginDataService.decrypt("2nSah5OBePk7RODCjeIhMQ=="));
    }

}