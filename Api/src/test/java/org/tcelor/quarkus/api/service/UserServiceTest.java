package org.tcelor.quarkus.api.service;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class UserServiceTest {

    @Test
    public void testSalutation() throws NoSuchFieldException, IllegalAccessException {
        UserService userService = new UserService();
        /* Use reflection pour mock property */
        Field salutationWordField = UserService.class.getDeclaredField("salutationWord");
        salutationWordField.setAccessible(true);
        salutationWordField.set(userService, "Hello");
        /* */
        String result = userService.salutation("John");
        assertEquals("Hello John", result);
    }
}