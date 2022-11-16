package sky.pro.animalshelter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;


@Entity
@Table(name = "reporting")
@Setter
@Getter
@NoArgsConstructor
public class Report {

    public enum ReportStatus {

        SENT,
        ACCEPTED,
        DECLINED,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "id_user")
    private Long clientId;

    @Column(name = "report_text")
    private String reportText;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Integer fileSize;

    @Lob
    @Column(name = "preview")
    @JsonIgnore
    private byte[] preview;

    @Column(name = "sent_date")
    private LocalDate sentDate;

    @ManyToOne
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    @JsonIgnore
    public User user;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.SENT;

    public Report(Long id, Long clientId, String reportText, String filePath, Integer fileSize, byte[] preview, LocalDate sentDate, ReportStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.reportText = reportText;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.preview = preview;
        this.sentDate = sentDate;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        Report report = (Report) o;
        return id.equals(report.id) && fileSize.equals(report.fileSize) && Objects.equals(clientId, report.clientId) && Objects.equals(reportText, report.reportText) && Objects.equals(filePath, report.filePath) && Arrays.equals(preview, report.preview) && Objects.equals(sentDate, report.sentDate) && Objects.equals(user, report.user) && status == report.status;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, clientId, reportText, filePath, fileSize, sentDate, user, status);
        result = 31 * result + Arrays.hashCode(preview);
        return result;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", reportText='" + reportText + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", preview=" + Arrays.toString(preview) +
                ", sentDate=" + sentDate +
                ", user=" + user +
                ", status=" + status +
                '}';
    }
}