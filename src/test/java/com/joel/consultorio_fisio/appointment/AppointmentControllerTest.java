package com.joel.consultorio_fisio.appointment;

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
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private PatientRepository patientRepository;

    private AppointmentRequestDTO appointmentRequestDTO;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .name("Maria Santos")
                .phone("11987654321")
                .build();
        testPatient = patientRepository.save(testPatient);

        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        appointmentRequestDTO = AppointmentRequestDTO.builder()
                .patientId(testPatient.getId())
                .startTime(futureTime)
                .duration(AppointmentDuration.ONE_HOUR)
                .isPaid(false)
                .isCancelled(false)
                .notes("Initial consultation")
                .build();
    }

    @Test
    void shouldCreateAppointment() throws Exception {
        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(testPatient.getId()))
                .andExpect(jsonPath("$.duration").value("ONE_HOUR"))
                .andExpect(jsonPath("$.endTime").exists());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        appointmentRequestDTO.setStartTime(LocalDateTime.now().minusDays(1)); // Past date

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPatientNotFound() throws Exception {
        appointmentRequestDTO.setPatientId(999L);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 999"));
    }

    @Test
    void shouldGetAllAppointments() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetAppointmentById() throws Exception {
        String response = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AppointmentResponseDTO created = objectMapper.readValue(response, AppointmentResponseDTO.class);

        mockMvc.perform(get("/api/appointments/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(testPatient.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/appointments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateAppointment() throws Exception {
        String response = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AppointmentResponseDTO created = objectMapper.readValue(response, AppointmentResponseDTO.class);
        AppointmentRequestDTO updateDTO = AppointmentRequestDTO.builder()
                .patientId(created.getPatientId())
                .startTime(created.getStartTime())
                .duration(AppointmentDuration.TWO_HOURS)
                .isPaid(created.getIsPaid())
                .isCancelled(created.getIsCancelled())
                .notes("Updated notes")
                .build();

        mockMvc.perform(put("/api/appointments/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Updated notes"))
                .andExpect(jsonPath("$.duration").value("TWO_HOURS"));
    }

    @Test
    void shouldDeleteAppointment() throws Exception {
        String response = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AppointmentResponseDTO created = objectMapper.readValue(response, AppointmentResponseDTO.class);

        mockMvc.perform(delete("/api/appointments/" + created.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldMarkAppointmentAsPaid() throws Exception {
        String response = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AppointmentResponseDTO created = objectMapper.readValue(response, AppointmentResponseDTO.class);

        mockMvc.perform(patch("/api/appointments/" + created.getId() + "/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPaid").value(true));
    }

    @Test
    void shouldCancelAppointment() throws Exception {
        String response = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        AppointmentResponseDTO created = objectMapper.readValue(response, AppointmentResponseDTO.class);

        mockMvc.perform(patch("/api/appointments/" + created.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCancelled").value(true));
    }

    @Test
    void shouldGetAppointmentsByPatientId() throws Exception {
        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/appointments/patient/" + testPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(testPatient.getId()));
    }

    @Test
    void shouldReturnBadRequestWhenStartTimeIsMissing() throws Exception {
        appointmentRequestDTO.setStartTime(null);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.startTime").exists());
    }

    @Test
    void shouldReturnBadRequestWhenDurationIsMissing() throws Exception {
        appointmentRequestDTO.setDuration(null);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.duration").exists());
    }
}
