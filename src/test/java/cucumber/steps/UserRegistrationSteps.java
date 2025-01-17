package cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.entities.User;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.repositories.UserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserRegistrationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    SignupRequest signupRequest;
    MvcResult loggerdUser;
    MvcResult createdUser;

    @Before
    public void setup() {
        signupRequest = new SignupRequest();
    }

    @Given("Eu estou nao estou autenticado")
    public void euEstouNaoEstouAutenticado() {
        //
    }

    @Given("Eu estou autenticado como administrador")
    public void euEstouAutenticadoComoAdministrador() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("root")
                .password("root")
                .build();

        loggerdUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(loggerdUser);
    }

    @When("Eu submeto o formulário de cadastro")
    public void euSubmetoOFormularioDeCadastro() throws Exception {
        MockHttpServletRequestBuilder ra = post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest));

        if (loggerdUser != null) {
            String responseString = loggerdUser.getResponse().getContentAsString();
            String token = objectMapper.readTree(responseString).get("token").asText();
            ra.header("Authorization", "Bearer " + token);
        }

        createdUser = mockMvc.perform(ra).andReturn();

        assertNotNull(createdUser);
    }

    @When("Eu submeto o formulario de cadastro de usuario com perfil")
    public void euSubmetoOFormularioDeCadastroDeUsuarioComPerfil() throws Exception {
        String responseString = loggerdUser.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseString).get("token").asText();

        createdUser = mockMvc.perform(post("/api/auth/newuser")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andReturn();

        assertNotNull(createdUser);
    }

    @Then("Eu devo ver status OK")
    public void euDevoVerStatusOK() {
        assertEquals(HttpStatus.OK.value(), createdUser.getResponse().getStatus());
    }

    @And("O usuário {string} deve estar registrado no sistema")
    public void oUsuarioDeveEstarRegistradoNoSistema(String username) {
        assertTrue(userRepository.findByUsername(username).isPresent());
    }

    @And("O usuário {string} deve ter a role {string}")
    public void oUsuarioDeveTerARole(String username, String role) {
        User user = userRepository.findByUsername(username).get();
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName().name().equalsIgnoreCase(role)));
    }

    @And("campo {string} com {string}")
    public void campoCom(String arg0, String arg1) {
        try {
            String fieldName = arg0.toLowerCase();
            Field field = SignupRequest.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType().equals(String.class)) {
                field.set(signupRequest, arg1);
            } else if (field.getType().isInterface()) {
                Object enumValue = Set.of(arg1.toUpperCase());
                field.set(signupRequest, enumValue);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao definir o valor do campo: " + arg0, e);
        }
    }


    @Given("Eu estou autenticado como usuario")
    public void euEstouAutenticadoComoUsuario() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("gambit")
                .password("gambit")
                .build();

        loggerdUser = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(loggerdUser);
    }

    @Then("Eu devo ver status FORBIDDEN")
    public void euDevoVerStatusFORBIDDEN() {
        assertEquals(HttpStatus.FORBIDDEN.value(), createdUser.getResponse().getStatus());
    }


    @Then("Eu devo ver status UNAUTHORIZED")
    public void euDevoVerStatusUNAUTHORIZED() {
        assertEquals(HttpStatus.UNAUTHORIZED.value(), createdUser.getResponse().getStatus());
    }

    @Then("Eu devo ver status BAD_REQUEST")
    public void euDevoVerStatusBAD_REQUEST() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), createdUser.getResponse().getStatus());
    }
}