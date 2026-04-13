package com.gymcrm.workload.service;

import com.gymcrm.workload.document.TrainerWorkloadDocument;
import com.gymcrm.workload.document.TrainerWorkloadDocument.MonthEntry;
import com.gymcrm.workload.document.TrainerWorkloadDocument.YearEntry;
import com.gymcrm.workload.dto.WorkloadRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.repository.TrainerWorkloadDocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.gymcrm.workload.dto.WorkloadRequest.ActionType.ADD;
import static com.gymcrm.workload.dto.WorkloadRequest.ActionType.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadService")
class WorkloadServiceTest {

    @Mock
    private TrainerWorkloadDocumentRepository repository;

    @InjectMocks
    private WorkloadService service;

    @Captor
    private ArgumentCaptor<TrainerWorkloadDocument> docCaptor;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    // New trainer ──────────────────────────────────────────────────

    @Test
    @DisplayName("ADD for unknown trainer creates document with the request duration")
    void processWorkload_newTrainer_ADD_createsDocumentWithCorrectDuration() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 6, 1), 90));

        verify(repository).save(docCaptor.capture());
        TrainerWorkloadDocument saved = docCaptor.getValue();
        assertThat(saved.getYears()).hasSize(1);
        assertThat(saved.getYears().get(0).getYear()).isEqualTo(2024);
        assertThat(saved.getYears().get(0).getMonths()).hasSize(1);
        assertThat(saved.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(6);
        assertThat(saved.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isEqualTo(90);
    }

    @Test
    @DisplayName("DELETE for unknown trainer creates document with zero duration (floor at 0)")
    void processWorkload_newTrainer_DELETE_createsDocumentWithZeroDuration() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        service.processWorkload(buildRequest("john.doe", DELETE, LocalDate.of(2024, 6, 1), 90));

        verify(repository).save(docCaptor.capture());
        assertThat(monthDuration(docCaptor.getValue(), 2024, 6)).isEqualTo(0);
    }

    @Test
    @DisplayName("New trainer: all profile fields are written from the request")
    void processWorkload_newTrainer_setsProfileFields() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 6, 1), 60));

        verify(repository).save(docCaptor.capture());
        TrainerWorkloadDocument saved = docCaptor.getValue();
        assertThat(saved.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getActive()).isTrue();
    }

    // Existing trainer ─────────────────────────────────────────────

    @Test
    @DisplayName("ADD for same period accumulates duration (60 + 30 = 90)")
    void processWorkload_existingTrainer_ADD_samePeriod_accumulatesDuration() {
        when(repository.findByTrainerUsername("john.doe"))
                .thenReturn(Optional.of(buildDocument("john.doe", 2024, 6, 60)));

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 6, 1), 30));

        verify(repository).save(docCaptor.capture());
        assertThat(monthDuration(docCaptor.getValue(), 2024, 6)).isEqualTo(90);
    }

    @Test
    @DisplayName("DELETE subtracts duration when result stays positive (100 - 40 = 60)")
    void processWorkload_existingTrainer_DELETE_subtractsDuration() {
        when(repository.findByTrainerUsername("john.doe"))
                .thenReturn(Optional.of(buildDocument("john.doe", 2024, 6, 100)));

        service.processWorkload(buildRequest("john.doe", DELETE, LocalDate.of(2024, 6, 1), 40));

        verify(repository).save(docCaptor.capture());
        assertThat(monthDuration(docCaptor.getValue(), 2024, 6)).isEqualTo(60);
    }

    @Test
    @DisplayName("DELETE never goes below zero (20 - 50 floors to 0, not -30)")
    void processWorkload_existingTrainer_DELETE_neverGoesBelowZero() {
        when(repository.findByTrainerUsername("john.doe"))
                .thenReturn(Optional.of(buildDocument("john.doe", 2024, 6, 20)));

        service.processWorkload(buildRequest("john.doe", DELETE, LocalDate.of(2024, 6, 1), 50));

        verify(repository).save(docCaptor.capture());
        assertThat(monthDuration(docCaptor.getValue(), 2024, 6)).isEqualTo(0);
    }

    @Test
    @DisplayName("Training in a new year adds a YearEntry alongside the existing one")
    void processWorkload_existingTrainer_newYear_createsYearEntry() {
        when(repository.findByTrainerUsername("john.doe"))
                .thenReturn(Optional.of(buildDocument("john.doe", 2024, 6, 60)));

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2025, 3, 1), 30));

        verify(repository).save(docCaptor.capture());
        assertThat(docCaptor.getValue().getYears())
                .hasSize(2)
                .extracting(YearEntry::getYear)
                .containsExactlyInAnyOrder(2024, 2025);
    }

    @Test
    @DisplayName("Training in a new month of an existing year adds a MonthEntry")
    void processWorkload_existingTrainer_newMonthInExistingYear_createsMonthEntry() {
        when(repository.findByTrainerUsername("john.doe"))
                .thenReturn(Optional.of(buildDocument("john.doe", 2024, 6, 60)));

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 9, 1), 30));

        verify(repository).save(docCaptor.capture());
        YearEntry year2024 = docCaptor.getValue().getYears().stream()
                .filter(y -> y.getYear().equals(2024))
                .findFirst().orElseThrow();
        assertThat(year2024.getMonths())
                .hasSize(2)
                .extracting(MonthEntry::getMonth)
                .containsExactlyInAnyOrder(6, 9);
    }

    @Test
    @DisplayName("Profile fields are refreshed from the request on every write")
    void processWorkload_existingTrainer_updatesProfileFields() {
        TrainerWorkloadDocument existing = buildDocument("john.doe", 2024, 6, 60);
        existing.setFirstName("OldFirst");
        existing.setLastName("OldLast");
        existing.setActive(false);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existing));

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 6, 1), 30));

        verify(repository).save(docCaptor.capture());
        TrainerWorkloadDocument saved = docCaptor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getActive()).isTrue();
    }

    @Test
    @DisplayName("repository.save is always called exactly once per processWorkload invocation")
    void processWorkload_always_callsRepositorySave() {
        when(repository.findByTrainerUsername(any())).thenReturn(Optional.empty());

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 6, 1), 60));

        verify(repository).save(any(TrainerWorkloadDocument.class));
    }

    // getSummary ───────────────────────────────────────────────────

    @Test
    @DisplayName("getSummary returns empty years list (not null) when trainer has no document")
    void getSummary_noDocument_returnsEmptyYears() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        WorkloadSummaryResponse response = service.getSummary("john.doe");

        assertThat(response.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(response.getYears()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("getSummary returns years and months sorted ascending with correct durations")
    void getSummary_documentExists_returnsNestedStructureSortedAscending() {
        // Document with years and months intentionally out of order — proves sorting is applied
        TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
        doc.setTrainerUsername("john.doe");
        doc.setFirstName("John");
        doc.setLastName("Doe");
        doc.setActive(true);

        YearEntry year2024 = new YearEntry(2024);
        year2024.getMonths().add(new MonthEntry(1, 45));

        YearEntry year2023 = new YearEntry(2023);
        year2023.getMonths().add(new MonthEntry(12, 30));  // month 12 added before month 3
        year2023.getMonths().add(new MonthEntry(3, 60));

        doc.getYears().add(year2024);                      // year 2024 added before year 2023
        doc.getYears().add(year2023);

        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(doc));

        WorkloadSummaryResponse response = service.getSummary("john.doe");

        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getTrainerStatus()).isTrue();

        List<WorkloadSummaryResponse.YearSummary> years = response.getYears();
        assertThat(years).hasSize(2);

        // Years sorted ascending
        assertThat(years.get(0).year()).isEqualTo(2023);
        assertThat(years.get(1).year()).isEqualTo(2024);

        // Months within 2023 sorted ascending
        List<WorkloadSummaryResponse.MonthSummary> months2023 = years.get(0).months();
        assertThat(months2023).hasSize(2);
        assertThat(months2023.get(0).month()).isEqualTo(3);
        assertThat(months2023.get(0).trainingSummaryDuration()).isEqualTo(60);
        assertThat(months2023.get(1).month()).isEqualTo(12);
        assertThat(months2023.get(1).trainingSummaryDuration()).isEqualTo(30);

        // Year 2024
        assertThat(years.get(1).months().get(0).month()).isEqualTo(1);
        assertThat(years.get(1).months().get(0).trainingSummaryDuration()).isEqualTo(45);
    }

    // MDC ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Service reads MDC transactionId for logging but never clears it (listener owns MDC lifecycle)")
    void processWorkload_withMdcTransactionId_serviceDoesNotClearMdc() {
        MDC.put("transactionId", "tx-abc");
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        service.processWorkload(buildRequest("john.doe", ADD, LocalDate.of(2024, 6, 1), 60));

        assertThat(MDC.get("transactionId")).isEqualTo("tx-abc");
    }

    private WorkloadRequest buildRequest(String username, WorkloadRequest.ActionType action,
                                         LocalDate date, int duration) {
        WorkloadRequest req = new WorkloadRequest();
        req.setTrainerUsername(username);
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setIsActive(true);
        req.setTrainingDate(date);
        req.setTrainingDuration(duration);
        req.setActionType(action);
        return req;
    }

    /**
     * Builds a document with a single year/month entry and a fixed profile.
     * The profile values ("John", "Doe", active=true) are intentionally different from
     * the ones used in updatesProfileFields test, where the document is mutated before the call.
     */
    private TrainerWorkloadDocument buildDocument(String username, int year, int month, int duration) {
        MonthEntry monthEntry = new MonthEntry(month, duration);
        YearEntry  yearEntry  = new YearEntry(year);
        yearEntry.getMonths().add(monthEntry);

        TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
        doc.setTrainerUsername(username);
        doc.setFirstName("John");
        doc.setLastName("Doe");
        doc.setActive(true);
        doc.getYears().add(yearEntry);
        return doc;
    }

    /**
     * Navigates to the specific year/month in the captured document and returns its duration.
     * Throws AssertionError if the entry doesn't exist, giving a clear failure message.
     */
    private int monthDuration(TrainerWorkloadDocument doc, int year, int month) {
        return doc.getYears().stream()
                .filter(y -> y.getYear().equals(year))
                .flatMap(y -> y.getMonths().stream())
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .map(MonthEntry::getTrainingSummaryDuration)
                .orElseThrow(() -> new AssertionError(
                        "No entry found for year=" + year + ", month=" + month));
    }
}
