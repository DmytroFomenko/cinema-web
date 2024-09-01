package dfomenko.repository;


import dfomenko.entity.Seance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;


@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    Seance findSeanceById(Long seanceId);
    Page<Seance> findAllByBeginTimeAfter(Timestamp fromTime, Pageable pageable);
    Page<Seance> findAllByBeginTimeBetween(Timestamp fromTime, Timestamp toTime, Pageable pageable);
    Page<Seance> findAllByFilmId(Long filmId, Pageable pageable);
    Page<Seance> findAllByFilmIdAndBeginTimeBefore(Long filmId, Timestamp beforeTime, Pageable pageable);

    Seance findFirstByBeginTimeBeforeAndBeginTimeOrderByBeginTimeDesc(Timestamp t, Timestamp t1);

    Seance findFirstByBeginTimeBeforeOrderByBeginTimeDesc(Timestamp t);

    void deleteSeanceById(Long seanceId);

}

