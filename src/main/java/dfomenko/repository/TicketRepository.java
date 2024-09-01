package dfomenko.repository;


import dfomenko.entity.PlaceStatus;
import dfomenko.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findTicketsBySeanceId(Long seanceId);
    Ticket findTicketById(Long ticketId);
    Page<Ticket> findTicketsByClientIdAndPlaceStatus(Long clientId, PlaceStatus placeStatus, Pageable pageable);
    Page<Ticket> findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(Long clientId, PlaceStatus placeStatus, Timestamp fromTime, Pageable pageable);
    Page<Ticket> findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeBetween(Long clientId, PlaceStatus placeStatus, Timestamp fromTime, Timestamp toTime, Pageable pageable);
    Long countTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(Long clientId, PlaceStatus placeStatus, Timestamp fromTime);
    Long countTicketsBySeanceIdAndPlaceStatus(Long seanceId, PlaceStatus placeStatus);
    Long countTicketsBySeanceIdAndPlaceStatusNot(Long seanceId, PlaceStatus placeStatus);
    Long countTicketsBySeanceId(Long seanceId);
    Ticket findTicketByCode(String ticketCode);
    List<Ticket> findTicketsBySeanceIdAndClientIdAndPlaceStatus(Long seanceId, Long clientId, PlaceStatus placeStatus);
    List<Ticket> findTicketsByClientIdAndPlaceStatus(Long clientId, PlaceStatus placeStatus);
    List<Ticket> findTicketsBySeanceIdAndPlaceStatus(Long seanceId, PlaceStatus placeStatus);
    Long countTicketsByPlaceStatus(PlaceStatus placeStatus);
    boolean existsTicketByPlaceStatusAndSeanceId(PlaceStatus placeStatus, Long seanceId);
    void deleteTicketsBySeanceId(Long seanceId);

}
