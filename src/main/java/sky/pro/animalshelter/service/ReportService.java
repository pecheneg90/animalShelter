package sky.pro.animalshelter.service;

import com.pengrad.telegrambot.model.Message;
import sky.pro.animalshelter.model.Report;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<Report> getReportsByUserId(Long userId);

    Report getById(Long id);

    Report getLastReportByUserId(Long userId);

    LocalDate getDateOfLastReportByUserId(Long userId);

    Report saveReport(Report report);

    boolean reportWasSentToday(LocalDate messageDate, Long userId);

    Report handlePhoto(Message message, Integer fileSize, String filePath, String reportText) throws IOException;

    String getDirectoryPath(String filePath, Message message) throws IOException;

    File downloadFile(String filePath, Message message);

    byte[] generatePhotoPreview(String filePath) throws IOException;

    Integer countUserReports(Long id);

    Report editReportByVolunteer(Report report, Report.ReportStatus status);
}