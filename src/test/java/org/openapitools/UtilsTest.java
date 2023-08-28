package org.openapitools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.sdk.utils.Utils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class UtilsTest {

    @MockBean
    private Utils utils;

//    private MockMvc mockMvc;

    @Before
    public void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(utils).build();
    }

    @Test
    public void testRandomString() {
        String result = utils.randomString(28);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGenerateChallenge() {
        Map<String, String> result = utils.generateChallenge();
        assertAll(
                () -> assertNotNull(result.get("state")),
                () -> assertNotNull(result.get("codeVerifier")),
                () -> assertNotNull(result.get("codeChallenge"))
        );
    }

    @Test
    public void testValidationURL() {
        String url = "https://test.com";
        Boolean result = utils.validateURL(url);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testValidationUrlInvalid() {
        String urlInvalid = "test.c";
        boolean result = utils.validateURL(urlInvalid);
        assertNotEquals(result, true);
    }
}
