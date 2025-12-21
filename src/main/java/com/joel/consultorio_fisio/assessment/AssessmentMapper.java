package com.joel.consultorio_fisio.assessment;

import com.joel.consultorio_fisio.assessment.dtos.AssessmentRequestDTO;
import com.joel.consultorio_fisio.assessment.dtos.AssessmentResponseDTO;
import com.joel.consultorio_fisio.patient.Patient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssessmentMapper {

    public AssessmentResponseDTO toResponseDTO(Assessment entity) {
        if (entity == null) {
            return null;
        }
        return AssessmentResponseDTO.builder()
                .id(entity.getId())
                .patientId(entity.getPatient().getId())
                .patientName(entity.getPatient().getName())
                .assessmentDate(entity.getAssessmentDate())
                // Diagnosis
                .mainComplaint(entity.getMainComplaint())
                .clinicalDiagnosis(entity.getClinicalDiagnosis())
                .physiotherapyDiagnosis(entity.getPhysiotherapyDiagnosis())
                // Medical History - Chronic Diseases
                .hasChronicDisease(entity.getHasChronicDisease())
                .hasDiabetes(entity.getHasDiabetes())
                .hasHypertension(entity.getHasHypertension())
                .hasOsteoporosis(entity.getHasOsteoporosis())
                .hasEndocrine(entity.getHasEndocrine())
                .hasCardiac(entity.getHasCardiac())
                .hasCirculatory(entity.getHasCirculatory())
                .hasRhinitisSinusitis(entity.getHasRhinitisSinusitis())
                .hasChronicFatigue(entity.getHasChronicFatigue())
                .hasHeadache(entity.getHasHeadache())
                .hasCancer(entity.getHasCancer())
                .hasHypoglycemia(entity.getHasHypoglycemia())
                .hasKidneyProblem(entity.getHasKidneyProblem())
                .hasHypothyroidism(entity.getHasHypothyroidism())
                .hasHyperthyroidism(entity.getHasHyperthyroidism())
                .otherChronicDiseases(entity.getOtherChronicDiseases())
                // Medications
                .usesMedications(entity.getUsesMedications())
                .medicationDetails(entity.getMedicationDetails())
                // Previous Surgeries
                .hasPreviousSurgeries(entity.getHasPreviousSurgeries())
                .surgeryDetails(entity.getSurgeryDetails())
                // Lifestyle
                .isSmoker(entity.getIsSmoker())
                .consumesAlcohol(entity.getConsumesAlcohol())
                .doesPhysicalActivity(entity.getDoesPhysicalActivity())
                .physicalActivityDetails(entity.getPhysicalActivityDetails())
                // Eating Habits
                .eatingHabits(entity.getEatingHabits())
                .familyHistory(entity.getFamilyHistory())
                // History of Dysfunction
                .problemOnset(entity.getProblemOnset())
                .frequencyAndIntensity(entity.getFrequencyAndIntensity())
                .previousTreatments(entity.getPreviousTreatments())
                .currentMedications(entity.getCurrentMedications())
                // Self-assessment
                .painLocation(entity.getPainLocation())
                .sensationCharacteristics(entity.getSensationCharacteristics())
                .painFrequencyIntensity(entity.getPainFrequencyIntensity())
                .painReducesOrWorsens(entity.getPainReducesOrWorsens())
                .painScale(entity.getPainScale())
                .selfAssessmentObservations(entity.getSelfAssessmentObservations())
                // Patient Objectives
                .patientObjectives(entity.getPatientObjectives())
                // Complementary Exam Results
                .complementaryExamResults(entity.getComplementaryExamResults())
                // Physical Examination
                .inspectionPalpation(entity.getInspectionPalpation())
                .rangeOfMotion(entity.getRangeOfMotion())
                .muscleStrength(entity.getMuscleStrength())
                .perimetry(entity.getPerimetry())
                .specialFunctionalTests(entity.getSpecialFunctionalTests())
                // Treatment Objectives
                .treatmentObjectives(entity.getTreatmentObjectives())
                // Therapeutic Resources
                .therapeuticResources(entity.getTherapeuticResources())
                // Therapeutic Plan
                .therapeuticPlan(entity.getTherapeuticPlan())
                // General Notes
                .generalNotes(entity.getGeneralNotes())
                // Other Observations
                .otherObservations(entity.getOtherObservations())
                // Audit fields
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Assessment toEntity(AssessmentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Patient patient = Patient.builder()
                .id(dto.getPatientId())
                .build();

        return Assessment.builder()
                .patient(patient)
                .assessmentDate(dto.getAssessmentDate())
                // Diagnosis
                .mainComplaint(dto.getMainComplaint())
                .clinicalDiagnosis(dto.getClinicalDiagnosis())
                .physiotherapyDiagnosis(dto.getPhysiotherapyDiagnosis())
                // Medical History - Chronic Diseases
                .hasChronicDisease(dto.getHasChronicDisease())
                .hasDiabetes(dto.getHasDiabetes())
                .hasHypertension(dto.getHasHypertension())
                .hasOsteoporosis(dto.getHasOsteoporosis())
                .hasEndocrine(dto.getHasEndocrine())
                .hasCardiac(dto.getHasCardiac())
                .hasCirculatory(dto.getHasCirculatory())
                .hasRhinitisSinusitis(dto.getHasRhinitisSinusitis())
                .hasChronicFatigue(dto.getHasChronicFatigue())
                .hasHeadache(dto.getHasHeadache())
                .hasCancer(dto.getHasCancer())
                .hasHypoglycemia(dto.getHasHypoglycemia())
                .hasKidneyProblem(dto.getHasKidneyProblem())
                .hasHypothyroidism(dto.getHasHypothyroidism())
                .hasHyperthyroidism(dto.getHasHyperthyroidism())
                .otherChronicDiseases(dto.getOtherChronicDiseases())
                // Medications
                .usesMedications(dto.getUsesMedications())
                .medicationDetails(dto.getMedicationDetails())
                // Previous Surgeries
                .hasPreviousSurgeries(dto.getHasPreviousSurgeries())
                .surgeryDetails(dto.getSurgeryDetails())
                // Lifestyle
                .isSmoker(dto.getIsSmoker())
                .consumesAlcohol(dto.getConsumesAlcohol())
                .doesPhysicalActivity(dto.getDoesPhysicalActivity())
                .physicalActivityDetails(dto.getPhysicalActivityDetails())
                // Eating Habits
                .eatingHabits(dto.getEatingHabits())
                .familyHistory(dto.getFamilyHistory())
                // History of Dysfunction
                .problemOnset(dto.getProblemOnset())
                .frequencyAndIntensity(dto.getFrequencyAndIntensity())
                .previousTreatments(dto.getPreviousTreatments())
                .currentMedications(dto.getCurrentMedications())
                // Self-assessment
                .painLocation(dto.getPainLocation())
                .sensationCharacteristics(dto.getSensationCharacteristics())
                .painFrequencyIntensity(dto.getPainFrequencyIntensity())
                .painReducesOrWorsens(dto.getPainReducesOrWorsens())
                .painScale(dto.getPainScale())
                .selfAssessmentObservations(dto.getSelfAssessmentObservations())
                // Patient Objectives
                .patientObjectives(dto.getPatientObjectives())
                // Complementary Exam Results
                .complementaryExamResults(dto.getComplementaryExamResults())
                // Physical Examination
                .inspectionPalpation(dto.getInspectionPalpation())
                .rangeOfMotion(dto.getRangeOfMotion())
                .muscleStrength(dto.getMuscleStrength())
                .perimetry(dto.getPerimetry())
                .specialFunctionalTests(dto.getSpecialFunctionalTests())
                // Treatment Objectives
                .treatmentObjectives(dto.getTreatmentObjectives())
                // Therapeutic Resources
                .therapeuticResources(dto.getTherapeuticResources())
                // Therapeutic Plan
                .therapeuticPlan(dto.getTherapeuticPlan())
                // General Notes
                .generalNotes(dto.getGeneralNotes())
                // Other Observations
                .otherObservations(dto.getOtherObservations())
                .build();
    }

    public List<AssessmentResponseDTO> toResponseDTOList(List<Assessment> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
