package com.joel.consultorio_fisio.assessment;

import com.joel.consultorio_fisio.patient.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    // Diagnóstico (Diagnosis)
    @Column(name = "main_complaint", columnDefinition = "TEXT")
    private String mainComplaint;

    @Column(name = "clinical_diagnosis", columnDefinition = "TEXT")
    private String clinicalDiagnosis;

    @Column(name = "physiotherapy_diagnosis", columnDefinition = "TEXT")
    private String physiotherapyDiagnosis;

    // Histórico - Chronic Diseases
    @Column(name = "has_chronic_disease")
    @Builder.Default
    private Boolean hasChronicDisease = false;

    @Column(name = "has_diabetes")
    @Builder.Default
    private Boolean hasDiabetes = false;

    @Column(name = "has_hypertension")
    @Builder.Default
    private Boolean hasHypertension = false;

    @Column(name = "has_osteoporosis")
    @Builder.Default
    private Boolean hasOsteoporosis = false;

    @Column(name = "has_endocrine")
    @Builder.Default
    private Boolean hasEndocrine = false;

    @Column(name = "has_cardiac")
    @Builder.Default
    private Boolean hasCardiac = false;

    @Column(name = "has_circulatory")
    @Builder.Default
    private Boolean hasCirculatory = false;

    @Column(name = "has_rhinitis_sinusitis")
    @Builder.Default
    private Boolean hasRhinitisSinusitis = false;

    @Column(name = "has_chronic_fatigue")
    @Builder.Default
    private Boolean hasChronicFatigue = false;

    @Column(name = "has_headache")
    @Builder.Default
    private Boolean hasHeadache = false;

    @Column(name = "has_cancer")
    @Builder.Default
    private Boolean hasCancer = false;

    @Column(name = "has_hypoglycemia")
    @Builder.Default
    private Boolean hasHypoglycemia = false;

    @Column(name = "has_kidney_problem")
    @Builder.Default
    private Boolean hasKidneyProblem = false;

    @Column(name = "has_hypothyroidism")
    @Builder.Default
    private Boolean hasHypothyroidism = false;

    @Column(name = "has_hyperthyroidism")
    @Builder.Default
    private Boolean hasHyperthyroidism = false;

    @Column(name = "other_chronic_diseases", columnDefinition = "TEXT")
    private String otherChronicDiseases;

    // Medications
    @Column(name = "uses_medications")
    @Builder.Default
    private Boolean usesMedications = false;

    @Column(name = "medication_details", columnDefinition = "TEXT")
    private String medicationDetails;

    // Previous Surgeries
    @Column(name = "has_previous_surgeries")
    @Builder.Default
    private Boolean hasPreviousSurgeries = false;

    @Column(name = "surgery_details", columnDefinition = "TEXT")
    private String surgeryDetails;

    // Lifestyle
    @Column(name = "is_smoker")
    @Builder.Default
    private Boolean isSmoker = false;

    @Column(name = "consumes_alcohol")
    @Builder.Default
    private Boolean consumesAlcohol = false;

    @Column(name = "does_physical_activity")
    @Builder.Default
    private Boolean doesPhysicalActivity = false;

    @Column(name = "physical_activity_details", columnDefinition = "TEXT")
    private String physicalActivityDetails;

    // Eating Habits
    @Enumerated(EnumType.STRING)
    @Column(name = "eating_habits", length = 20)
    private EatingHabits eatingHabits;

    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;

    // Histórico da Disfunção (History of Dysfunction)
    @Column(name = "problem_onset", columnDefinition = "TEXT")
    private String problemOnset;

    @Column(name = "frequency_and_intensity", columnDefinition = "TEXT")
    private String frequencyAndIntensity;

    @Column(name = "previous_treatments", columnDefinition = "TEXT")
    private String previousTreatments;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    // Autoavaliação (Self-assessment)
    @Column(name = "pain_location", columnDefinition = "TEXT")
    private String painLocation;

    @Column(name = "sensation_characteristics", columnDefinition = "TEXT")
    private String sensationCharacteristics;

    @Column(name = "pain_frequency_intensity", columnDefinition = "TEXT")
    private String painFrequencyIntensity;

    @Column(name = "pain_reduces_or_worsens", columnDefinition = "TEXT")
    private String painReducesOrWorsens;

    @Column(name = "pain_scale")
    private Integer painScale;

    @Column(name = "self_assessment_observations", columnDefinition = "TEXT")
    private String selfAssessmentObservations;

    // Objetivos do Paciente (Patient Objectives)
    @Column(name = "patient_objectives", columnDefinition = "TEXT")
    private String patientObjectives;

    // Resultados de Exames Complementares (Complementary Exam Results)
    @Column(name = "complementary_exam_results", columnDefinition = "TEXT")
    private String complementaryExamResults;

    // Exame Físico (Physical Examination)
    @Column(name = "inspection_palpation", columnDefinition = "TEXT")
    private String inspectionPalpation;

    @Column(name = "range_of_motion", columnDefinition = "TEXT")
    private String rangeOfMotion;

    @Column(name = "muscle_strength", columnDefinition = "TEXT")
    private String muscleStrength;

    @Column(name = "perimetry", columnDefinition = "TEXT")
    private String perimetry;

    @Column(name = "special_functional_tests", columnDefinition = "TEXT")
    private String specialFunctionalTests;

    // Objetivos do tratamento (Treatment Objectives)
    @Column(name = "treatment_objectives", columnDefinition = "TEXT")
    private String treatmentObjectives;

    // Recursos Terapêuticos (Therapeutic Resources)
    @Column(name = "therapeutic_resources", columnDefinition = "TEXT")
    private String therapeuticResources;

    // Plano Terapêutico (Therapeutic Plan)
    @Column(name = "therapeutic_plan", columnDefinition = "TEXT")
    private String therapeuticPlan;

    // Anotações Gerais (General Notes)
    @Column(name = "general_notes", columnDefinition = "TEXT")
    private String generalNotes;

    // Outras Observações (Other Observations)
    @Column(name = "other_observations", columnDefinition = "TEXT")
    private String otherObservations;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
