package sky.pro.animalshelter.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sky.pro.animalshelter.constants.ConstantsTest;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.repository.ReportRepository;
import sky.pro.animalshelter.service.UserService;
import sky.pro.animalshelter.service.impl.ReportServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    private Report report1;
    private Report report2;

    @Mock
    private ReportRepository reportRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private ReportServiceImpl out;

    @BeforeEach
    public void setUp() {
        report1 = new Report();
        report2 = new Report();
        report1.setId(ConstantsTest.REPORT_ID_1);
        report2.setId(ConstantsTest.REPORT_ID_2);
        report1.setClientId(ConstantsTest.USER_ID_1);
        report2.setClientId(ConstantsTest.USER_ID_1);
        out = new ReportServiceImpl(reportRepositoryMock, userServiceMock);
    }

    @Test
    public void testShouldSaveReport() {
        when(reportRepositoryMock.save(report1)).thenReturn(report1);
        assertEquals(report1, out.saveReport(report1));
        verify(reportRepositoryMock, times(1)).save(report1);
    }

    @Test
    public void testShouldThrowNullPointerExceptionAfterHandlePhoto()  {
        assertThrows(NullPointerException.class,
                () -> out.handlePhoto(ConstantsTest.REPORT_MESSAGE, ConstantsTest.FILE_SIZE_1, ConstantsTest.FILE_PATH_1, ConstantsTest.REPORT_TEXT_1));
    }


    @Test
    public void testShouldThrowIOExceptionGeneratePhotoPreview() {
        assertThrows(IOException.class,
                () -> out.generatePhotoPreview(ConstantsTest.FILE_PATH_2));
    }

    @Test
    public void testShouldGetReportsByUserId() {
        List<Report> reports = List.of(report1,report2);
        when(reportRepositoryMock.findByUserId(any(Long.class))).
                thenReturn(Optional.of(Optional.of(reports).orElse(null)));
        assertEquals(reports, out.getReportsByUserId(ConstantsTest.USER_ID_1));
    }

    @Test
    public void testShouldGetById() {

        when(reportRepositoryMock.findById(any(Long.class))).
                thenReturn(Optional.of(Optional.of(report1).orElse(null)));
        assertEquals(report1, out.getById(ConstantsTest.REPORT_ID_1));

    }

    @Test
    public void testShouldGetLastReportByUserId() {
        when(reportRepositoryMock.findLastReportByUserId(ConstantsTest.USER_ID_1)).thenReturn(Optional.ofNullable(report1));
        assertEquals(report1, out.getLastReportByUserId(ConstantsTest.USER_ID_1));
    }

    @Test
    public void testShouldGetDateOfLastReportByUserId() {
        when(reportRepositoryMock.findDateOfLastReportByUserId(ConstantsTest.USER_ID_1)).thenReturn(Optional.of(ConstantsTest.SENT_DATE_1));
        assertEquals(ConstantsTest.SENT_DATE_1, out.getDateOfLastReportByUserId(ConstantsTest.USER_ID_1));
    }

    @Test
    public void testShouldCheckIfReportWasSentToday() {
        when(reportRepositoryMock.findDateOfLastReportByUserId(ConstantsTest.USER_ID_1)).thenReturn(Optional.of(ConstantsTest.SENT_DATE_1));
        assertTrue(out.reportWasSentToday(ConstantsTest.SENT_DATE_1, ConstantsTest.USER_ID_1));

    }

    @Test
    public void testShouldCountUserReports() {
        when(reportRepositoryMock.countReportsByClientId(ConstantsTest.USER_ID_1)).thenReturn(Optional.of(1));
        assertEquals(1, out.countUserReports(ConstantsTest.USER_ID_1));
    }

    @Test
    public void testShouldEditReportStatus() {
        when(reportRepositoryMock.save(report2)).thenReturn(report2);
        report2.setStatus(Report.ReportStatus.ACCEPTED);
        assertEquals(report2, out.editReportByVolunteer(report2, ConstantsTest.REPORT_STATUS_2));
    }
}
