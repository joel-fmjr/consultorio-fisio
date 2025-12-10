package com.joel.consultorio_fisio.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
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
class AssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AssessmentRepository repository;

    @Autowired
    private PatientRepository patientRepository;

    private AssessmentRequestDTO assessmentRequestDTO;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .name("Maria Silva")
                .phone("11987654321")
                .email("maria@example.com")
                .build();
        testPatient = patientRepository.save(testPatient);

        assessmentRequestDTO = AssessmentRequestDTO.builder()
                .patientId(testPatient.getId())
                .assessmentDate(LocalDate.now())
                .mainComplaint("Dor lombar crônica")
                .painScale(7)
                .hasDiabetes(false)
                .hasHypertension(false)
                .eatingHabits(EatingHabits.HEALTHY)
                .build();
    }

    @Test
    void shouldCreateAssessment() throws Exception {
        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(testPatient.getId()))
                .andExpect(jsonPath("$.mainComplaint").value("Dor lombar crônica"))
                .andExpect(jsonPath("$.painScale").value(7))
                .andExpect(jsonPath("$.patientName").value("Maria Silva"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithInvalidData() throws Exception {
        assessmentRequestDTO.setPatientId(null); // Missing required field

        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPatientNotFound() throws Exception {
        assessmentRequestDTO.setPatientId(999L);

        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 999"));
    }

    @Test
    void shouldFindAssessmentById() throws Exception {
        String response = mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AssessmentResponseDTO created = objectMapper.readValue(response, AssessmentResponseDTO.class);

        mockMvc.perform(get("/api/assessments/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(testPatient.getId()))
                .andExpect(jsonPath("$.mainComplaint").value("Dor lombar crônica"));
    }

    @Test
    void shouldReturnNotFoundWhenAssessmentDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/assessments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAllAssessments() throws Exception {
        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/assessments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].mainComplaint").value("Dor lombar crônica"));
    }

    @Test
    void shouldFindAssessmentsByPatientId() throws Exception {
        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/assessments/patient/" + testPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].patientId").value(testPatient.getId()));
    }

    @Test
    void shouldUpdateAssessment() throws Exception {
        String response = mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AssessmentResponseDTO created = objectMapper.readValue(response, AssessmentResponseDTO.class);
        AssessmentRequestDTO updateDTO = AssessmentRequestDTO.builder()
                .patientId(created.getPatientId())
                .assessmentDate(created.getAssessmentDate())
                .mainComplaint("Dor lombar crônica - melhorou após tratamento")
                .painScale(4)
                .hasDiabetes(created.getHasDiabetes())
                .hasHypertension(created.getHasHypertension())
                .eatingHabits(created.getEatingHabits())
                .build();

        mockMvc.perform(put("/api/assessments/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mainComplaint").value("Dor lombar crônica - melhorou após tratamento"))
                .andExpect(jsonPath("$.painScale").value(4));
    }

    @Test
    void shouldDeleteAssessment() throws Exception {
        String response = mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AssessmentResponseDTO created = objectMapper.readValue(response, AssessmentResponseDTO.class);

        mockMvc.perform(delete("/api/assessments/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/assessments/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidatePainScaleRange() throws Exception {
        assessmentRequestDTO.setPainScale(15); // Invalid: greater than 10

        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isBadRequest());

        assessmentRequestDTO.setPainScale(-1); // Invalid: less than 0

        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isBadRequest());

        assessmentRequestDTO.setPainScale(5); // Valid

        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldValidateAssessmentDateNotInFuture() throws Exception {
        assessmentRequestDTO.setAssessmentDate(LocalDate.now().plusDays(1)); // Future date

        mockMvc.perform(post("/api/assessments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}
