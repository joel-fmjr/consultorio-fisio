package com.joel.consultorio_fisio.assessment.dtos;

import com.joel.consultorio_fisio.assessment.EatingHabits;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequestDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Assessment date is required")
    @PastOrPresent(message = "Assessment date cannot be in the future")
    private LocalDate assessmentDate;

    // Diagnosis
    @Size(max = 5000, message = "Main complaint cannot exceed 5000 characters")
    private String mainComplaint;

    @Size(max = 5000, message = "Clinical diagnosis cannot exceed 5000 characters")
    private String clinicalDiagnosis;

    @Size(max = 5000, message = "Physiotherapy diagnosis cannot exceed 5000 characters")
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

    @Size(max = 2000, message = "Other chronic diseases cannot exceed 2000 characters")
    private String otherChronicDiseases;

    // Medications
    private Boolean usesMedications;

    @Size(max = 2000, message = "Medication details cannot exceed 2000 characters")
    private String medicationDetails;

    // Previous Surgeries
    private Boolean hasPreviousSurgeries;

    @Size(max = 2000, message = "Surgery details cannot exceed 2000 characters")
    private String surgeryDetails;

    // Lifestyle
    private Boolean isSmoker;
    private Boolean consumesAlcohol;
    private Boolean doesPhysicalActivity;

    @Size(max = 2000, message = "Physical activity details cannot exceed 2000 characters")
    private String physicalActivityDetails;

    // Eating Habits
    private EatingHabits eatingHabits;

    @Size(max = 5000, message = "Family history cannot exceed 5000 characters")
    private String familyHistory;

    // History of Dysfunction
    @Size(max = 5000, message = "Problem onset cannot exceed 5000 characters")
    private String problemOnset;

    @Size(max = 5000, message = "Frequency and intensity cannot exceed 5000 characters")
    private String frequencyAndIntensity;

    @Size(max = 5000, message = "Previous treatments cannot exceed 5000 characters")
    private String previousTreatments;

    @Size(max = 2000, message = "Current medications cannot exceed 2000 characters")
    private String currentMedications;

    // Self-assessment
    @Size(max = 5000, message = "Pain location cannot exceed 5000 characters")
    private String painLocation;

    @Size(max = 5000, message = "Sensation characteristics cannot exceed 5000 characters")
    private String sensationCharacteristics;

    @Size(max = 5000, message = "Pain frequency intensity cannot exceed 5000 characters")
    private String painFrequencyIntensity;

    @Size(max = 5000, message = "Pain reduces or worsens cannot exceed 5000 characters")
    private String painReducesOrWorsens;

    @Min(value = 0, message = "Pain scale must be between 0 and 10")
    @Max(value = 10, message = "Pain scale must be between 0 and 10")
    private Integer painScale;

    @Size(max = 5000, message = "Self assessment observations cannot exceed 5000 characters")
    private String selfAssessmentObservations;

    // Patient Objectives
    @Size(max = 5000, message = "Patient objectives cannot exceed 5000 characters")
    private String patientObjectives;

    // Complementary Exam Results
    @Size(max = 5000, message = "Complementary exam results cannot exceed 5000 characters")
    private String complementaryExamResults;

    // Physical Examination
    @Size(max = 5000, message = "Inspection palpation cannot exceed 5000 characters")
    private String inspectionPalpation;

    @Size(max = 5000, message = "Range of motion cannot exceed 5000 characters")
    private String rangeOfMotion;

    @Size(max = 5000, message = "Muscle strength cannot exceed 5000 characters")
    private String muscleStrength;

    @Size(max = 5000, message = "Perimetry cannot exceed 5000 characters")
    private String perimetry;

    @Size(max = 5000, message = "Special functional tests cannot exceed 5000 characters")
    private String specialFunctionalTests;

    // Treatment Objectives
    @Size(max = 5000, message = "Treatment objectives cannot exceed 5000 characters")
    private String treatmentObjectives;

    // Therapeutic Resources
    @Size(max = 5000, message = "Therapeutic resources cannot exceed 5000 characters")
    private String therapeuticResources;

    // Therapeutic Plan
    @Size(max = 5000, message = "Therapeutic plan cannot exceed 5000 characters")
    private String therapeuticPlan;

    // General Notes
    @Size(max = 5000, message = "General notes cannot exceed 5000 characters")
    private String generalNotes;

    // Other Observations
    @Size(max = 5000, message = "Other observations cannot exceed 5000 characters")
    private String otherObservations;
}
