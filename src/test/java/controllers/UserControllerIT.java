package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.domains.user.User;
import com.spring.domains.user.UserRepository;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.response.JwtResponse;
import com.spring.payload.response.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MainApplication.class)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult loggerdUser;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("root")
                .password("root")
                .build();

        loggerdUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testRegisterUser() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser")
                .password("testpass")
                .build();

        String responseString = loggerdUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();

        MvcResult result = mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
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

        String responseString = loggerdUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();

        MvcResult result = mockMvc.perform(post("/api/user/newuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
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
    public void testRegisterUserWithExistingUsername() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("existinguser")
                .password("testpass")
                .role(Set.of("user"))
                .build();

        String responseString = loggerdUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        MessageResponse messageResponse = objectMapper.readValue(result.getResponse().getContentAsString(), MessageResponse.class);
        assertEquals("Error: Username is already taken!", messageResponse.getMessage());

    }

    @Test
    public void testDeleteUser() throws Exception{
        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        String responseString = loggerdUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();

        MvcResult result = mockMvc.perform(delete(String.format("/api/user/delete/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        User deletedUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: "));

        assertNotNull(deletedUser.getAudit().getDeletedOn());
    }
}