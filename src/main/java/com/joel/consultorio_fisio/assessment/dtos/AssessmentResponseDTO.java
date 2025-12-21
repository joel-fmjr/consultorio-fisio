package com.joel.consultorio_fisio.assessment.dtos;

import com.joel.consultorio_fisio.assessment.EatingHabits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private LocalDate assessmentDate;

    // Diagnosis
    private String mainComplaint;
    private String clinicalDiagnosis;
    private String physiotherapyDiagnosis;

    // Medical History - Chronic Diseases
    private Boolean hasChronicDisease;
    private Boolean hasDiabetes;
    private Boolean hasHypertension;
    private Boolean hasOsteoporosis;
    private Boolean hasEndocrine;
    private Boolean hasCardiac;
    private Boolean hasCirculatory;
    private Boolean hasRhinitisSinusitis;
    private Boolean hasChronicFatigue;
    private Boolean hasHeadache;
    private Boolean hasCancer;
    private Boolean hasHypoglycemia;
    private Boolean hasKidneyProblem;
    private Boolean hasHypothyroidism;
    private Boolean hasHyperthyroidism;
    private String otherChronicDiseases;

    // Medications
    private Boolean usesMedications;
    private String medicationDetails;

    // Previous Surgeries
    private Boolean hasPreviousSurgeries;
    private String surgeryDetails;

    // Lifestyle
    private Boolean isSmoker;
    private Boolean consumesAlcohol;
    private Boolean doesPhysicalActivity;
    private String physicalActivityDetails;

    // Eating Habits
    private EatingHabits eatingHabits;
    private String familyHistory;

    // History of Dysfunction
    private String problemOnset;
    private String frequencyAndIntensity;
    private String previousTreatments;
    private String currentMedications;

    // Self-assessment
    private String painLocation;
    private String sensationCharacteristics;
    private String painFrequencyIntensity;
    private String painReducesOrWorsens;
    private Integer painScale;
    private String selfAssessmentObservations;

    // Patient Objectives
    private String patientObjectives;

    // Complementary Exam Results
    private String complementaryExamResults;

    // Physical Examination
    private String inspectionPalpation;
    private String rangeOfMotion;
    private String muscleStrength;
    private String perimetry;
    private String specialFunctionalTests;

    // Treatment Objectives
    private String treatmentObjectives;

    // Therapeutic Resources
    private String therapeuticResources;

    // Therapeutic Plan
    private String therapeuticPlan;

    // General Notes
    private String generalNotes;

    // Other Observations
    private String otherObservations;

    // Read-only fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
