package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.core.entities.ERole;
import com.spring.domains.user.User;
import com.spring.domains.user.UserRepository;
import com.spring.payload.request.ChangePasswordRequest;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.request.UpdateRolesRequest;
import com.spring.payload.response.JwtResponse;
import com.spring.payload.response.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private MvcResult loggedUser;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("root")
                .password("root")
                .build();

        loggedUser = mockMvc.perform(post("/api/auth/signin")
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

        String responseString = loggedUser.getResponse().getContentAsString();
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

        String responseString = loggedUser.getResponse().getContentAsString();
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

        String responseString = loggedUser.getResponse().getContentAsString();
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
    public void testDeleteUser() throws Exception {
        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        String responseString = loggedUser.getResponse().getContentAsString();
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

    @Test
    public void changeUserPasswordTest() throws Exception {
        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("gambit")
                .newPassword("cambito")
                .build();

        String responseString = loggedUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();


        MvcResult result = mockMvc.perform(put(String.format("/api/user/change-password/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void selfChangeUserPasswordTest() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("gambit")
                .password("gambit")
                .build();

        loggedUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("gambit")
                .newPassword("cambito")
                .build();

        String responseString = this.loggedUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();


        MvcResult result = mockMvc.perform(put(String.format("/api/user/change-password/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    //senha errada
    @Test
    public void failOnchangeUserPasswordTest() throws Exception {
        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("gamdsadasdbit")
                .newPassword("cambito")
                .build();

        String responseString = loggedUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();


        MvcResult result = mockMvc.perform(put(String.format("/api/user/change-password/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    //senha usuario nao admin ou diferente do q ele tenta alterar
    @Test
    public void failOnchangeUserPasswordTest2() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("gambit")
                .password("gambit")
                .build();

        loggedUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String userId = "c9f00788-9b3a-4ca6-8114-ed240566303e";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("root")
                .newPassword("roots")
                .build();

        String responseString = this.loggedUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();


        MvcResult result = mockMvc.perform(put(String.format("/api/user/change-password/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

    @Test
    public void changeUserRoleTest() throws Exception {
        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        UpdateRolesRequest request = UpdateRolesRequest.builder()
                .roles(Set.of("admin", "mod"))
                .build();

        String responseString = loggedUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();


        MvcResult result = mockMvc.perform(put(String.format("/api/user/change-roles/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        User user = userRepository.findById(UUID.fromString(userId)).get();
        assertTrue(user.getRoles().stream()
                .anyMatch((role) -> role.getName().equals(ERole.ROLE_ADMIN)));
        assertTrue(user.getRoles().stream()
                .anyMatch((role) -> role.getName().equals(ERole.ROLE_MODERATOR)));

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    // usuario nao admin tenta trocar roles
    @Test
    public void failOnChangeUserRoleTest() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("gambit")
                .password("gambit")
                .build();

        loggedUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = loggedUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();

        String userId = "22d219b5-65c6-4f1f-94ae-696c94974f9b";
        UpdateRolesRequest request = UpdateRolesRequest.builder()
                .roles(Set.of("admin", "mod"))
                .build();

        MvcResult result = mockMvc.perform(put(String.format("/api/user/change-roles/%1$s", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();


        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

}