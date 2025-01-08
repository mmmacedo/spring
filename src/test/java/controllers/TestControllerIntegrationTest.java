package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.response.JwtResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String userToken;
    private static String modToken;
    private static String adminToken;


    @BeforeAll
    public static void setup(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // Cadastra usuário USER e faz login para obter o token
        SignupRequest signupRequestUser = SignupRequest.builder()
                .username("testuser")
                .password("testpass")
                .role(Set.of("user"))
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestUser)))
                .andExpect(status().isOk());

        LoginRequest loginRequestUser = new LoginRequest();
        loginRequestUser.setUsername("testuser");
        loginRequestUser.setPassword("testpass");

        MvcResult loginResultUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestUser)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponseUser = objectMapper.readValue(loginResultUser.getResponse().getContentAsString(), JwtResponse.class);
        assertNotNull(jwtResponseUser.getAccessToken());
        userToken = "Bearer " + jwtResponseUser.getAccessToken();

        // Cadastra usuário MODERATOR e faz login para obter o token
        SignupRequest signupRequestMod = SignupRequest.builder()
                .username("testmod")
                .password("testpass")
                .role(Set.of("mod"))
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestMod)))
                .andExpect(status().isOk());

        LoginRequest loginRequestMod = new LoginRequest();
        loginRequestMod.setUsername("testmod");
        loginRequestMod.setPassword("testpass");

        MvcResult loginResultMod = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestMod)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponseMod = objectMapper.readValue(loginResultMod.getResponse().getContentAsString(), JwtResponse.class);
        assertNotNull(jwtResponseMod.getAccessToken());
        modToken = "Bearer " + jwtResponseMod.getAccessToken();


        // Cadastra usuário ADMIN e faz login para obter o token
        SignupRequest signupRequestAdmin = SignupRequest.builder()
                .username("testadmin")
                .password("testpass")
                .role(Set.of("admin"))
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestAdmin)))
                .andExpect(status().isOk());


        LoginRequest loginRequestAdmin = new LoginRequest();
        loginRequestAdmin.setUsername("testadmin");
        loginRequestAdmin.setPassword("testpass");

        MvcResult loginResultAdmin = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestAdmin)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponseAdmin = objectMapper.readValue(loginResultAdmin.getResponse().getContentAsString(), JwtResponse.class);
        assertNotNull(jwtResponseAdmin.getAccessToken());
        adminToken = "Bearer " + jwtResponseAdmin.getAccessToken();
    }

    @Test
    public void testPublicEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("Public Content.", result.getResponse().getContentAsString());
    }

    @Test
    public void testUserEndpointWithUserRole() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/user")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("User Content.", result.getResponse().getContentAsString());
    }

    @Test
    public void testUserEndpointWithModeratorRole() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/user")
                        .header("Authorization", modToken))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("User Content.", result.getResponse().getContentAsString());
    }

    @Test
    public void testUserEndpointWithAdminRole() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/user")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("User Content.", result.getResponse().getContentAsString());
    }

    @Test
    public void testUserEndpointWithoutRole() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testModeratorEndpointWithModeratorRole() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/mod")
                        .header("Authorization", modToken))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("Moderator Board.", result.getResponse().getContentAsString());
    }

    @Test
    public void testModeratorEndpointWithoutRole() throws Exception {
        mockMvc.perform(get("/api/test/mod"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAdminEndpointWithAdminRole() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/admin")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("Admin Board.", result.getResponse().getContentAsString());
    }

    @Test
    public void testAdminEndpointWithoutRole() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testTestEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/test/test"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("OK", result.getResponse().getContentAsString());
    }
}