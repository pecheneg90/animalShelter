package sky.pro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.pro.animalshelter.model.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select r from Report r where r.user.id = ?1")
    Optional<List<Report>> findByUserId(Long userId);

    @Query(value = "select max (r) from Report r where r.user.id=?1")
    Optional<Report> findLastReportByUserId(Long userId);

    @Query("select max (r.sentDate) from Report r where r.user.id = ?1")
    Optional<LocalDate> findDateOfLastReportByUserId(Long userId);

    @Query("select r from Report r where r.id = ?1")
    Optional<Report> findById(Long id);

    @Query("select count(r) from Report r where r.clientId = ?1")
    Optional<Integer> countReportsByClientId(Long id);

}