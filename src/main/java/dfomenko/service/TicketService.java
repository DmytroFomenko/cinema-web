package dfomenko.service;

import dfomenko.entity.LoginData;
import dfomenko.entity.PlaceStatus;
import dfomenko.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.*;
import java.text.*;
import java.util.List;

public interface TicketService {

    List<Ticket> getAllTickets();
    List<Ticket> findTicketsBySeanceId(Long seanceId);
    Ticket findTicketById(Long ticketId);
    boolean toggleTicketReservStatus(Long ticketId, LoginData client);
    void resetAllReservedTicketStatus(Long clientId);
    List<Ticket> getReservedTickets(Long clientId, Long seanceId);
    List<Ticket> findTicketsBySeanceIdAndClientIdAndPlaceStatus(Long seanceId, Long clientId, PlaceStatus placeStatus);
    boolean buyTicket(Ticket ticket);
    List<Ticket> findTicketsByClientIdAndPlaceStatus(Long clientId, PlaceStatus placeStatus);
    List<Ticket> findTicketsBySeanceIdAndPlaceStatus(Long seanceId, PlaceStatus placeStatus);
    Long countTicketsByPlaceStatus(PlaceStatus placeStatus);
    Page<Ticket> findTicketsByClientIdAndPlaceStatus(Long clientId, PlaceStatus placeStatus, Pageable pageable);
    Page<Ticket> findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(Long clientId, PlaceStatus placeStatus, Timestamp fromTime, Pageable pageable);
    Long countTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(Long clientId, PlaceStatus placeStatus, Timestamp fromTime);
    Long countTakenTicketsByClientIdAnd_BeginTimeAfter(Long clientId, Timestamp fromTime);
    Long countTicketsBySeanceIdAndPlaceStatus(Long seanceId, PlaceStatus placeStatus);
    Long countTicketsBySeanceIdAndPlaceStatusNot(Long seanceId, PlaceStatus placeStatus);
    Page<Ticket> findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeBetween(Long clientId, PlaceStatus placeStatus, Timestamp fromTime, Timestamp toTime, Pageable pageable);
    Page<Ticket> findTakenTicketsByClientIdAndDate(Long clientId, String forDate, Pageable pageable) throws ParseException;
    Ticket findTicketByCode(String ticketCode);
    Long countTicketsBySeanceId(Long seanceId);
    void createFreeTicket(Long seanceId, Long placeId);
    boolean existsTicketByPlaceStatusAndSeanceId(PlaceStatus placeStatus, Long seanceId);
    void deleteTicketsBySeanceId(Long seanceId);

}
