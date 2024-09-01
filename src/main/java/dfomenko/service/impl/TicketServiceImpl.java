package dfomenko.service.impl;

import dfomenko.entity.LoginData;
import dfomenko.entity.PlaceStatus;
import dfomenko.entity.Ticket;
import dfomenko.repository.LoginDataRepository;
import dfomenko.repository.TicketAdditionRepository;
import dfomenko.repository.TicketRepository;
import dfomenko.service.TicketService;
import dfomenko.utils.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketAdditionRepository ticketAdditionRepository;
    private final LoginDataRepository loginDataRepository;
    private final TimeUtils timeUtils;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findTicketsBySeanceId(Long seanceId) {
        return ticketRepository.findTicketsBySeanceId(seanceId);
    }

    @Override
    public Ticket findTicketById(Long ticketId) {
        return ticketRepository.findTicketById(ticketId);
    }

    @Transactional
    @Override
    public boolean toggleTicketReservStatus(Long ticketId, LoginData client) {
        Ticket existingTicket = ticketRepository.findTicketById(ticketId);

        if (existingTicket.getPlaceStatus().equals(PlaceStatus.FREE)) {
            existingTicket.setPlaceStatus(PlaceStatus.RESERVED);
            existingTicket.setClientId(client.getId());
            ticketRepository.save(existingTicket);
            return true;
        } else if (existingTicket.getPlaceStatus().equals(PlaceStatus.RESERVED)
                && existingTicket.getClientId().equals(client.getId())) {
            existingTicket.setPlaceStatus(PlaceStatus.FREE);
            existingTicket.setClientId(null);
            ticketRepository.save(existingTicket);
            ticketAdditionRepository.deleteTicketAdditionsByTicketId(ticketId);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    @Override
    public void resetAllReservedTicketStatus(Long clientId) {
        ticketRepository.findTicketsByClientIdAndPlaceStatus(clientId, PlaceStatus.RESERVED)
                        .forEach(tick -> {
                            ticketAdditionRepository.deleteTicketAdditionsByTicketId(tick.getId());
                            tick.setPlaceStatus(PlaceStatus.FREE);
                            tick.setClientId(null);
                            ticketRepository.save(tick);
                        });

    }

    @Override
    public List<Ticket> getReservedTickets(Long clientId, Long seanceId) {
        return ticketRepository.findAll().stream()
                               .filter(tick -> Objects.equals(tick.getSeanceId(), seanceId))
                               .filter(tick -> tick.getPlaceStatus() == PlaceStatus.RESERVED)
                               .filter(tick -> Objects.equals(tick.getClientId(), clientId))
                               .toList();
    }

    @Override
    public List<Ticket> findTicketsBySeanceIdAndClientIdAndPlaceStatus(Long seanceId, Long clientId, PlaceStatus placeStatus) {
        return ticketRepository.findTicketsBySeanceIdAndClientIdAndPlaceStatus(seanceId, clientId, placeStatus);
    }

    @Override
    public boolean buyTicket(Ticket ticket) {
        if (ticket.getPlaceStatus().equals(PlaceStatus.RESERVED)) {
            ticket.setPlaceStatus(PlaceStatus.TAKEN);

            String nickname = loginDataRepository.findLoginDataById(ticket.getClientId()).getNickname();
            long currentTimeMillis = System.currentTimeMillis();
            String ticketCode = nickname + currentTimeMillis;
            ticket.setCode(ticketCode);
            ticketRepository.save(ticket);
            return true;
        }
        return false;
    }

    @Override
    public List<Ticket> findTicketsByClientIdAndPlaceStatus(Long clientId, PlaceStatus placeStatus) {
        return ticketRepository.findTicketsByClientIdAndPlaceStatus(clientId, placeStatus);
    }

    @Override
    public List<Ticket> findTicketsBySeanceIdAndPlaceStatus(Long seanceId, PlaceStatus placeStatus) {
        return ticketRepository.findTicketsBySeanceIdAndPlaceStatus(seanceId, placeStatus);
    }

    @Override
    public Long countTicketsByPlaceStatus(PlaceStatus placeStatus) {
        return ticketRepository.countTicketsByPlaceStatus(placeStatus);
    }

    @Override
    public Page<Ticket> findTicketsByClientIdAndPlaceStatus(Long clientId, PlaceStatus placeStatus, Pageable pageable) {
        return ticketRepository.findTicketsByClientIdAndPlaceStatus(clientId, placeStatus, pageable);
    }

    @Override
    public Page<Ticket> findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(Long clientId, PlaceStatus placeStatus, Timestamp fromTime, Pageable pageable) {
        return ticketRepository.findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(clientId, placeStatus, fromTime, pageable);
    }

    @Override
    public Long countTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(Long clientId, PlaceStatus placeStatus, Timestamp fromTime) {
        return ticketRepository.countTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(clientId, placeStatus, fromTime);
    }

    @Override
    public Long countTakenTicketsByClientIdAnd_BeginTimeAfter(Long clientId, Timestamp fromTime) {
        return ticketRepository.countTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(clientId, PlaceStatus.TAKEN, fromTime);
    }

    @Override
    public Long countTicketsBySeanceIdAndPlaceStatus(Long seanceId, PlaceStatus placeStatus) {
        return ticketRepository.countTicketsBySeanceIdAndPlaceStatus(seanceId, placeStatus);
    }

    @Override
    public Long countTicketsBySeanceIdAndPlaceStatusNot(Long seanceId, PlaceStatus placeStatus) {
        return ticketRepository.countTicketsBySeanceIdAndPlaceStatusNot(seanceId, placeStatus);
    }

    @Override
    public Page<Ticket> findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeBetween(Long clientId, PlaceStatus placeStatus, Timestamp fromTime, Timestamp toTime, Pageable pageable) {
        return ticketRepository.findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeBetween(clientId, placeStatus, fromTime, toTime, pageable);
    }

    @Override
    public Page<Ticket> findTakenTicketsByClientIdAndDate(Long clientId, String forDate, Pageable pageable) throws ParseException {
        Timestamp fromTime = timeUtils.getTimestampFromDateString(forDate);
        Timestamp toTime = Timestamp.valueOf(fromTime.toLocalDateTime().plusDays(1));
        return ticketRepository.findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeBetween(clientId,
                                                                                              PlaceStatus.TAKEN,
                                                                                              fromTime,
                                                                                              toTime,
                                                                                              pageable);
    }

    @Override
    public Ticket findTicketByCode(String ticketCode) {return ticketRepository.findTicketByCode(ticketCode);}

    @Override
    public Long countTicketsBySeanceId(Long seanceId) {
        return ticketRepository.countTicketsBySeanceId(seanceId);
    }

    @Override
    public void createFreeTicket(Long seanceId, Long placeId) {
        ticketRepository.save(new Ticket(null, seanceId, placeId, PlaceStatus.FREE, null, null, null, null));
    }

    @Override
    public boolean existsTicketByPlaceStatusAndSeanceId(PlaceStatus placeStatus, Long seanceId) {
        return ticketRepository.existsTicketByPlaceStatusAndSeanceId(placeStatus, seanceId);
    }

    @Transactional
    @Override
    public void deleteTicketsBySeanceId(Long seanceId) {
        ticketRepository.deleteTicketsBySeanceId(seanceId);
    }

}
