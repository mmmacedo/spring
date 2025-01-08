package controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.response.JwtResponse;
import com.spring.payload.response.MessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MainApplication.class)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testRegisterUser() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser")
                .password("testpass")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        MessageResponse messageResponse = objectMapper.readValue(result.getResponse().getContentAsString(), MessageResponse.class);
        assertEquals("User registered successfully!", messageResponse.getMessage());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), JwtResponse.class);
        assertNotNull(jwtResponse.getAccessToken());
        assertEquals("testuser", jwtResponse.getUsername());
    }

    @Test
    public void testRegisterUserWithRoles() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser2")
                .password("testpass2")
                .role(Set.of("admin", "mod"))
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        MessageResponse messageResponse = objectMapper.readValue(result.getResponse().getContentAsString(), MessageResponse.class);
        assertEquals("User registered successfully!", messageResponse.getMessage());


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser2");
        loginRequest.setPassword("testpass2");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), JwtResponse.class);
        assertNotNull(jwtResponse.getAccessToken());
        assertEquals("testuser2", jwtResponse.getUsername());
        List<String> expectedRoles = List.of("ROLE_ADMIN", "ROLE_MODERATOR");
        List<String> actualRoles = new ArrayList<>(jwtResponse.getRoles());
        actualRoles.sort(String::compareTo);
        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser3")
                .password("testpass3")
                .role(Set.of("user"))
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser3");
        loginRequest.setPassword("testpass3");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), JwtResponse.class);
        assertNotNull(jwtResponse.getAccessToken());
        assertEquals("testuser3", jwtResponse.getUsername());
        assertEquals(List.of("ROLE_USER"), jwtResponse.getRoles());
    }


    @Test
    public void testRegisterUserWithExistingUsername() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("existinguser")
                .password("testpass")
                .role(Set.of("user"))
                .build();


        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        MessageResponse messageResponse = objectMapper.readValue(result.getResponse().getContentAsString(), MessageResponse.class);
        assertEquals("Error: Username is already taken!", messageResponse.getMessage());

    }
}