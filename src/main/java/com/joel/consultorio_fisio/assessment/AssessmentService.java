package com.joel.consultorio_fisio.assessment;

import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository repository;
    private final AssessmentMapper mapper;
    private final PatientRepository patientRepository;

    public List<AssessmentResponseDTO> findAll() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    public AssessmentResponseDTO findById(Long id) {
        Assessment assessment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        return mapper.toResponseDTO(assessment);
    }

    public List<AssessmentResponseDTO> findByPatientId(Long patientId) {
        return mapper.toResponseDTOList(repository.findByPatientIdOrderByAssessmentDateDesc(patientId));
    }

    @Transactional
    public AssessmentResponseDTO create(AssessmentRequestDTO dto) {
        // Validate patient exists
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + dto.getPatientId()));

        Assessment assessment = mapper.toEntity(dto);
        assessment.setPatient(patient);

        Assessment saved = repository.save(assessment);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    public AssessmentResponseDTO update(Long id, AssessmentRequestDTO dto) {
        Assessment existingAssessment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));

        // Validate patient exists if changing patient
        if (!existingAssessment.getPatient().getId().equals(dto.getPatientId())) {
            Patient newPatient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + dto.getPatientId()));
            existingAssessment.setPatient(newPatient);
        }

        // Update fields manually (only update if not null)
        if (dto.getAssessmentDate() != null) {
            existingAssessment.setAssessmentDate(dto.getAssessmentDate());
        }

        // Diagnosis
        existingAssessment.setMainComplaint(dto.getMainComplaint());
        existingAssessment.setClinicalDiagnosis(dto.getClinicalDiagnosis());
        existingAssessment.setPhysiotherapyDiagnosis(dto.getPhysiotherapyDiagnosis());

        // Medical History - Chronic Diseases
        if (dto.getHasChronicDisease() != null) {
            existingAssessment.setHasChronicDisease(dto.getHasChronicDisease());
        }
        if (dto.getHasDiabetes() != null) {
            existingAssessment.setHasDiabetes(dto.getHasDiabetes());
        }
        if (dto.getHasHypertension() != null) {
            existingAssessment.setHasHypertension(dto.getHasHypertension());
        }
        if (dto.getHasOsteoporosis() != null) {
            existingAssessment.setHasOsteoporosis(dto.getHasOsteoporosis());
        }
        if (dto.getHasEndocrine() != null) {
            existingAssessment.setHasEndocrine(dto.getHasEndocrine());
        }
        if (dto.getHasCardiac() != null) {
            existingAssessment.setHasCardiac(dto.getHasCardiac());
        }
        if (dto.getHasCirculatory() != null) {
            existingAssessment.setHasCirculatory(dto.getHasCirculatory());
        }
        if (dto.getHasRhinitisSinusitis() != null) {
            existingAssessment.setHasRhinitisSinusitis(dto.getHasRhinitisSinusitis());
        }
        if (dto.getHasChronicFatigue() != null) {
            existingAssessment.setHasChronicFatigue(dto.getHasChronicFatigue());
        }
        if (dto.getHasHeadache() != null) {
            existingAssessment.setHasHeadache(dto.getHasHeadache());
        }
        if (dto.getHasCancer() != null) {
            existingAssessment.setHasCancer(dto.getHasCancer());
        }
        if (dto.getHasHypoglycemia() != null) {
            existingAssessment.setHasHypoglycemia(dto.getHasHypoglycemia());
        }
        if (dto.getHasKidneyProblem() != null) {
            existingAssessment.setHasKidneyProblem(dto.getHasKidneyProblem());
        }
        if (dto.getHasHypothyroidism() != null) {
            existingAssessment.setHasHypothyroidism(dto.getHasHypothyroidism());
        }
        if (dto.getHasHyperthyroidism() != null) {
            existingAssessment.setHasHyperthyroidism(dto.getHasHyperthyroidism());
        }
        existingAssessment.setOtherChronicDiseases(dto.getOtherChronicDiseases());

        // Medications
        if (dto.getUsesMedications() != null) {
            existingAssessment.setUsesMedications(dto.getUsesMedications());
        }
        existingAssessment.setMedicationDetails(dto.getMedicationDetails());

        // Previous Surgeries
        if (dto.getHasPreviousSurgeries() != null) {
            existingAssessment.setHasPreviousSurgeries(dto.getHasPreviousSurgeries());
        }
        existingAssessment.setSurgeryDetails(dto.getSurgeryDetails());

        // Lifestyle
        if (dto.getIsSmoker() != null) {
            existingAssessment.setIsSmoker(dto.getIsSmoker());
        }
        if (dto.getConsumesAlcohol() != null) {
            existingAssessment.setConsumesAlcohol(dto.getConsumesAlcohol());
        }
        if (dto.getDoesPhysicalActivity() != null) {
            existingAssessment.setDoesPhysicalActivity(dto.getDoesPhysicalActivity());
        }
        existingAssessment.setPhysicalActivityDetails(dto.getPhysicalActivityDetails());

        // Eating Habits
        existingAssessment.setEatingHabits(dto.getEatingHabits());
        existingAssessment.setFamilyHistory(dto.getFamilyHistory());

        // History of Dysfunction
        existingAssessment.setProblemOnset(dto.getProblemOnset());
        existingAssessment.setFrequencyAndIntensity(dto.getFrequencyAndIntensity());
        existingAssessment.setPreviousTreatments(dto.getPreviousTreatments());
        existingAssessment.setCurrentMedications(dto.getCurrentMedications());

        // Self-assessment
        existingAssessment.setPainLocation(dto.getPainLocation());
        existingAssessment.setSensationCharacteristics(dto.getSensationCharacteristics());
        existingAssessment.setPainFrequencyIntensity(dto.getPainFrequencyIntensity());
        existingAssessment.setPainReducesOrWorsens(dto.getPainReducesOrWorsens());
        existingAssessment.setPainScale(dto.getPainScale());
        existingAssessment.setSelfAssessmentObservations(dto.getSelfAssessmentObservations());

        // Patient Objectives
        existingAssessment.setPatientObjectives(dto.getPatientObjectives());

        // Complementary Exam Results
        existingAssessment.setComplementaryExamResults(dto.getComplementaryExamResults());

        // Physical Examination
        existingAssessment.setInspectionPalpation(dto.getInspectionPalpation());
        existingAssessment.setRangeOfMotion(dto.getRangeOfMotion());
        existingAssessment.setMuscleStrength(dto.getMuscleStrength());
        existingAssessment.setPerimetry(dto.getPerimetry());
        existingAssessment.setSpecialFunctionalTests(dto.getSpecialFunctionalTests());

        // Treatment Objectives
        existingAssessment.setTreatmentObjectives(dto.getTreatmentObjectives());

        // Therapeutic Resources
        existingAssessment.setTherapeuticResources(dto.getTherapeuticResources());

        // Therapeutic Plan
        existingAssessment.setTherapeuticPlan(dto.getTherapeuticPlan());

        // General Notes
        existingAssessment.setGeneralNotes(dto.getGeneralNotes());

        // Other Observations
        existingAssessment.setOtherObservations(dto.getOtherObservations());

        Assessment updated = repository.save(existingAssessment);
        return mapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Assessment not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
