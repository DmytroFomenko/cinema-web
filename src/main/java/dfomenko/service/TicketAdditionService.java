package dfomenko.service;


import dfomenko.entity.TicketAddition;

import java.util.List;

public interface TicketAdditionService {
    boolean selectTicketAddition(Long ticketId, Long additionId);
    boolean existsTicketAdditionByTicketIdAndAdditionId(Long ticketId, Long additionId);
    TicketAddition findTicketAdditionByTicketIdAndAdditionId(Long ticketId, Long additionId);
    long deleteTicketAdditionsByTicketId(Long ticketId);
    List<TicketAddition> findTicketAdditionsByTicketId(Long ticketId);

}
