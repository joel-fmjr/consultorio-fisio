package com.joel.consultorio_fisio.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joel.consultorio_fisio.patient.dtos.PatientRequestDTO;
import com.joel.consultorio_fisio.patient.dtos.PatientResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository repository;

    private PatientRequestDTO patientRequestDTO;

    @BeforeEach
    void setUp() {
        patientRequestDTO = PatientRequestDTO.builder()
                .name("Maria Santos")
                .cpf("98765432100")
                .email("maria@example.com")
                .phone("11987654321")
                .birthDate(LocalDate.of(1985, 3, 20))
                .address("Rua B, 456")
                .build();
    }

    @Test
    void shouldCreatePatient() throws Exception {
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria Santos"))
                .andExpect(jsonPath("$.cpf").value("98765432100"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        patientRequestDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
        patientRequestDTO.setName(null);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void shouldReturnBadRequestWhenPhoneIsMissing() throws Exception {
        patientRequestDTO.setPhone(null);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.phone").exists());
    }

    @Test
    void shouldGetAllPatients() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCreatePatientWithMinimalData() throws Exception {
        PatientRequestDTO minimalDTO = PatientRequestDTO.builder()
                .name("Quick Patient")
                .phone("11999999999")
                .build();

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Quick Patient"))
                .andExpect(jsonPath("$.phone").value("11999999999"));
    }

    @Test
    void shouldGetPatientById() throws Exception {
        String response = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        PatientResponseDTO created = objectMapper.readValue(response, PatientResponseDTO.class);

        mockMvc.perform(get("/api/patients/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Santos"));
    }

    @Test
    void shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        String response = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        PatientResponseDTO created = objectMapper.readValue(response, PatientResponseDTO.class);
        PatientRequestDTO updateDTO = PatientRequestDTO.builder()
                .name("Updated Name")
                .cpf(created.getCpf())
                .email(created.getEmail())
                .phone(created.getPhone())
                .birthDate(created.getBirthDate())
                .address(created.getAddress())
                .build();

        mockMvc.perform(put("/api/patients/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        String response = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        PatientResponseDTO created = objectMapper.readValue(response, PatientResponseDTO.class);

        mockMvc.perform(delete("/api/patients/" + created.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldSearchPatientsByName() throws Exception {
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/patients/search?name=Maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Maria Santos"));
    }

    @Test
    void shouldReturnBadRequestWhenCpfAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated());

        PatientRequestDTO duplicateCpf = PatientRequestDTO.builder()
                .name("Another Patient")
                .cpf("98765432100")
                .phone("11999999999")
                .build();

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateCpf)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF already registered"));
    }

    @Test
    void shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isCreated());

        PatientRequestDTO duplicateEmail = PatientRequestDTO.builder()
                .name("Another Patient")
                .email("maria@example.com")
                .phone("11999999999")
                .build();

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }
}
