package org.openapitools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.sdk.KindeClientSDK;
import org.openapitools.sdk.enums.GrantType;
import org.openapitools.sdk.enums.TokenType;
import org.openapitools.sdk.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KindeClientSDKTest {
//    @Mock
//    private Storage storage; // Mock the Storage class
//
//    @InjectMocks
//    private KindeClientSDK client; // Inject mock dependencies into the client

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private HttpServletRequest request;

    @Value("${kinde.host}")
    public String domain;

    @Value("${kinde.redirect.url}")
    public String redirectUri;

    @Value("${kinde.post.logout.redirect.url}")
    public String logoutRedirectUri;

    @Value("${kinde.client.id}")
    public String clientId;

    @Value("${kinde.client.secret}")
    public String clientSecret;

    @BeforeEach
    void setUp() {

    }
    @Test
    public void testInitial() {
        KindeClientSDK kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri);
        assertNotNull(kindeClientSDK);
        assertEquals(KindeClientSDK.class, kindeClientSDK.getClass());
    }

    @Test
    public void testInitialEmptyDomain() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new KindeClientSDK("", "", "", "", "", "");
        });

        String expectedMessage = "Please provide domain";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testInitialEmptyRedirectUri() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new KindeClientSDK(this.domain, "", this.clientId, this.clientSecret, GrantType.PKCE.getValue(), this.logoutRedirectUri);
        });

        String expectedMessage = "Please provide redirect_uri";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testInitialEmptyClientId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new KindeClientSDK(this.domain, this.redirectUri, "", this.clientSecret, GrantType.PKCE.getValue(), this.logoutRedirectUri);
        });

        String expectedMessage = "Please provide client_id";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testInitialInvalidDomain() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new KindeClientSDK("test.c", this.redirectUri, this.clientId, this.clientSecret, GrantType.PKCE.getValue(), this.logoutRedirectUri);
        });

        String expectedMessage = "Please provide valid domain";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testInitialInvalidRedirectUri() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new KindeClientSDK(this.domain, "test.c", this.clientId, this.clientSecret, GrantType.PKCE.getValue(), this.logoutRedirectUri);
        });

        String expectedMessage = "Please provide valid redirect_uri";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testInitialValidAudience() {
        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri, "",
                Collections.singletonMap("audience",this.domain+"/api")
        );

        assertNotNull(client);
        assertEquals(KindeClientSDK.class, client.getClass());
    }

    @Test
    public void testInitialInvalidAudience() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new KindeClientSDK(this.domain, this.redirectUri, this.clientId, this.clientSecret, GrantType.PKCE.getValue(), this.logoutRedirectUri, "", Collections.singletonMap("audience", 1233));
        });

        String expectedMessage = "Please supply a valid audience. Expected: string";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetIsAuthenticated() {
        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret,
                GrantType.PKCE.getValue(), logoutRedirectUri, "", Collections.singletonMap("audience", domain + "/api"));

        assertTrue((Boolean) client.isAuthenticated(request,response) instanceof Boolean);
        assertFalse(client.isAuthenticated(request,response));
    }

    @Test
    public void testLoginInvalidOrgCode() {
        String expectedMessage = "Please supply a valid org_code. Expected: string";

        KindeClientSDK client = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri);

        Map<String, Object> additional = new HashMap<>();
        additional.put("org_code", 123); // Invalid org_code
        additional.put("org_name", "My Application");

        try {
            client.login(response, additional);
            fail("Expected exception not thrown");
        }catch (Exception e){
            System.out.println(e.getCause().getMessage());
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            String actualMessage = e.getCause().getMessage();
            assertEquals(expectedMessage, actualMessage);
//            assertTrue(actualMessage.contains(expectedMessage));
        }
    }

    @Test
    public void testLoginInvalidOrgName() {
        String expectedMessage = "Please supply a valid org_name. Expected: string";

        KindeClientSDK client = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri);
        Map<String, Object> additional = new HashMap<>();
        additional.put("org_code", "123");
        additional.put("org_name", 123);
        try {
            client.login(response, additional);
            fail("Expected exception not thrown");
        }catch (Exception e){
            System.out.println(e.getCause().getMessage());
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            String actualMessage = e.getCause().getMessage();
            assertEquals(expectedMessage, actualMessage);
//            assertTrue(actualMessage.contains(expectedMessage));
        }
    }

    @Test
    public void testLoginInvalidAdditionalOrgName() {
        String expectedMessage = "Please provide correct additional, org_name_test";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );
        Map<String, Object> additional = new HashMap<>();
        additional.put("org_code", "123");
        additional.put("org_name_test", "123");
        try {
            client.login(response, additional);
            fail("Expected exception not thrown");
        }catch (Exception e){
            System.out.println(e.getCause().getMessage());
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            String actualMessage = e.getCause().getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Test
    public void testLoginInvalidAdditionalOrgCode() {
        String expectedMessage = "Please provide correct additional, org_code_test";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );
        Map<String, Object> additional = new HashMap<>();
        additional.put("org_code_test", "123");
        additional.put("org_name", "123");
        try {
            client.login(response,additional);
            fail("Expected exception not thrown");
        } catch (RuntimeException ex) {
            System.out.println(ex.getCause().getMessage());
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
            String actualMessage = ex.getCause().getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Test
    public void testClaimHelperRequireAuthenticate() {
        String expectedMessage = "Request is missing required authentication credential";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        try {
            client.getClaim(request,"something");
            fail("Expected exception not thrown");
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            assertEquals(expectedMessage, ex.getMessage());
        }
    }

    @Test
    public void testClaimHelperAuthenticated() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        storage.setToken(response_,Collections.singletonMap("access_token", accessToken));
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) Collections.singletonMap("access_token", accessToken)), "UTF-8")));

        System.out.println(client.getClaim(request_,"iss").get("name")+" :: "+client.getClaim(request_,"iss").get("value"));
        assertEquals("https://trung.kinde.com", client.getClaim(request_,"iss").get("value"));
        assertEquals("iss", client.getClaim(request_,"iss").get("name"));
    }

    @Test
    public void testFlagHelperGetBooleanNotFound() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";
        String expectedMessage = "This flag 'iss' was not found, and no default value has been provided";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        storage.setToken(response_,Collections.singletonMap("access_token", accessToken));
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) Collections.singletonMap("access_token", accessToken)), "UTF-8")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                    client.getBooleanFlag(request_, "iss");
        });

        System.out.println(ex.getMessage());
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void testFlagHelperGetBooleanProvideDefaultValue() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        storage.setToken(response_, Collections.singletonMap("access_token", accessToken));
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) Collections.singletonMap("access_token", accessToken)), "UTF-8")));

        assertEquals(true, client.getBooleanFlag(request_,"iss", true).get("value"));
        assertEquals("iss", client.getBooleanFlag(request_,"iss", true).get("code"));
        assertEquals("boolean", client.getBooleanFlag(request_,"iss", true).get("type"));
        assertEquals(true, client.getBooleanFlag(request_,"iss", true).get("is_default"));
    }

    @Test
    public void testFlagHelperGetBoolean() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        storage.setToken(response_,Collections.singletonMap("access_token", accessToken));
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) Collections.singletonMap("access_token", accessToken)), "UTF-8")));

        assertEquals(true, client.getBooleanFlag(request_,"enable_dark_theme").get("value"));
        assertEquals("enable_dark_theme", client.getBooleanFlag(request_,"enable_dark_theme").get("code"));
        assertEquals("boolean", client.getBooleanFlag(request_,"enable_dark_theme").get("type"));
        assertEquals(false, client.getBooleanFlag(request_,"enable_dark_theme").get("is_default"));
    }

    @Test
    public void testCheckIsNotAuthenticated() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        storage.setToken(response_,Collections.singletonMap("access_token", accessToken));
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) Collections.singletonMap("access_token", accessToken)), "UTF-8")));

        assertEquals(false,client.isAuthenticated(request_,response_));
    }

    @Test
    public void testCheckIsAuthenticated() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";
        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("should_valid",true);
        storage.setToken(response_,map);
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString(map), "UTF-8")));

        assertEquals(false, client.isAuthenticated(request_,response_));
    }

    @Test
    public void testCheckIsAuthenticatedUseRefreshToken() throws JsonProcessingException, UnsupportedEncodingException {
        Object accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdWQiOltdLCJhenAiOiJiOTg0ZWFiMmM1YmU0ZWU1OWEyYTAxMmZmNzdiNTJjMCIsImV4cCI6MTY4MTk4MjA4MywiZmVhdHVyZV9mbGFncyI6eyJlbmFibGVfZGFya190aGVtZSI6eyJ0IjoiYiIsInYiOnRydWV9fSwiaWF0IjoxNjgxODk1NjgyLCJpc3MiOiJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImp0aSI6ImMyYWVhYzVmLWVjZDktNDFjOS05MDU0LTc0MWU2ZmJmNzljMyIsIm9yZ19jb2RlIjoib3JnX2U1ZjI4ZTE2NzZkIiwicGVybWlzc2lvbnMiOlsicmVhZDpwcm9maWxlIiwiY3JlYXRlOnVzZXJfMSJdLCJzY3AiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIiwib2ZmbGluZSJdLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiJ9.MgXOfcAu7tV-3-QgHqwbUOL6jo2nPXdU5FifC98pbJIf2hNv8ZqmF4uTKEOv-ffkimhjjyOZDwlCb8EGHSxrXaakf31xYmkLtybPILL_KPBpK1PTBloidiRQFumoXlozgqJHDSIRemGHvtV2Mn7Z-Fg1W8duEWlWJHU_kTLhOlXGAy44IFpV_zvdwxEFjscnp621g1Ue0fdyTjMTW-3tMz-HBV87vpGKkvu3UlQDmYHrVAge03YVWQrcKdSDF-Cnud1TKpKkL6QGwp4dfoq8fQbW_6QZt_xgtivTAdfaMLFceXIZVB3MT5TUTrZUpxohPxz8DjRTWb5S8xiVvx-ygQ";
//        Object refreshToken= "_UsfRnO5y_uSU9X7chbvCtxj7WfAjz-kMyyV4fBIRos.q80frGFGXqfw9c1CHDGtB8NRqWUjkHmRzmPUdXbjTb4";
        Object refreshToken= "some_value";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("refresh_token", refreshToken);
        map.put("access_token", accessToken);
        storage.setToken(response_,map);
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString(map), "UTF-8")));

//        assertEquals(true,client.isAuthenticated(request_,response_)); //when refreshToken provided, uncomment this
        assertEquals(false,client.isAuthenticated(request_,response_)); //when refreshToken provided, comment this
    }

    @Test
    public void testGetClaimByIdToken() throws JsonProcessingException, UnsupportedEncodingException {
        Object idToken="eyJhbGciOiJSUzI1NiIsImtpZCI6ImVhOjYxOjBkOmY0Ojk2OjE3OmQ5OjIwOjk4OjBiOjNiOjcxOjU4OmQ0OjBkOjMwIiwidHlwIjoiSldUIn0.eyJhdF9oYXNoIjoiSk5XMFEzTFJqc1gyaGZrRDR2OW5CdyIsImF1ZCI6WyJodHRwczovL3RydW5nLmtpbmRlLmNvbSIsImI5ODRlYWIyYzViZTRlZTU5YTJhMDEyZmY3N2I1MmMwIl0sImF1dGhfdGltZSI6MTY4MTg5NTY4MiwiYXpwIjoiYjk4NGVhYjJjNWJlNGVlNTlhMmEwMTJmZjc3YjUyYzAiLCJlbWFpbCI6InVzZXJ0ZXN0aW5nQHlvcG1haWwuY29tIiwiZXhwIjoxNjgxODk5MjgyLCJmYW1pbHlfbmFtZSI6InRlc3QiLCJnaXZlbl9uYW1lIjoidXNlciIsImlhdCI6MTY4MTg5NTY4MiwiaXNzIjoiaHR0cHM6Ly90cnVuZy5raW5kZS5jb20iLCJqdGkiOiIyNDVlMGMxNS1jMTlmLTQ3YzItYWQ1Ni04MzY4MmZmOGNiNWQiLCJuYW1lIjoidXNlciB0ZXN0Iiwib3JnX2NvZGVzIjpbIm9yZ185ZTQwN2Y0MDY5YzEiLCJvcmdfOTBkOWZkNDI1MjhmIiwib3JnXzI4OTRjYTM0ZmJkMiIsIm9yZ18xMDc5NDkzNjUyZTciLCJvcmdfZTVmMjhlMTY3NmQiLCJvcmdfYTdiY2MwMzg1ZGVmIiwib3JnXzI4YzI0OTY3MWU0NSJdLCJwaWN0dXJlIjpudWxsLCJzdWIiOiJrcDo1OGVjZTlmNjhhN2M0YzA5OGVmYzFjZjQ1Yzc3NGUxNiIsInVwZGF0ZWRfYXQiOjEuNjgxODk1NjgyZSswOX0.VfJ0Zqj1zOlNeszTIOEn95w3EHoC24prOOPtnyP60sfWA70NRKJTqMg9csi5rrOvRPR2ipV_0w0-M5ajF6vKRUXbxa_c3GDiOFLV7hsArKB8Uhs6WENwTKI7iIvUINA9rCEi9GOqurwySLBDFwCrE5q8XXRToV1qWwcpWYQuEd7dFw1CQbGaYaYne7Azg9wb0uDXz4BwYyntAuEkg5FyBXY2D_sVHsdfmTi7ESNPhSqeC5YJCO_i-4FZh-EjLaiwUEFIPQmvdNEDsT8W1cc42TTL80rsyV7xIGnFuXLqUcz9WESFRYvjYZonnDtB86RqcaLtBOwz92E-KoseJSKE7A";

        KindeClientSDK client = new KindeClientSDK(
                domain, redirectUri, clientId, clientSecret, GrantType.PKCE.getValue(), logoutRedirectUri
        );

        MockHttpServletRequest request_ = new MockHttpServletRequest();
        MockHttpServletResponse response_ = new MockHttpServletResponse();

        Storage storage = Storage.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put("should_valid", true);
        map.put("id_token", idToken);
        storage.setToken(response_,map);
        request_.setCookies(new Cookie("kinde_token", URLEncoder.encode(new ObjectMapper().writeValueAsString(map), "UTF-8")));


        assertEquals("user", client.getClaim(request_,"given_name", TokenType.ID_TOKEN.getValue()).get("value"));
        assertEquals("given_name", client.getClaim(request_,"given_name", TokenType.ID_TOKEN.getValue()).get("name"));
        assertEquals("test", client.getClaim(request_,"family_name", TokenType.ID_TOKEN.getValue()).get("value"));
        assertEquals("family_name", client.getClaim(request_,"family_name", TokenType.ID_TOKEN.getValue()).get("name"));
    }
}
