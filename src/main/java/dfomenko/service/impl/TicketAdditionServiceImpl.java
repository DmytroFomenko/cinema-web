package dfomenko.service.impl;

import dfomenko.entity.TicketAddition;
import dfomenko.repository.TicketAdditionRepository;
import dfomenko.service.TicketAdditionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class TicketAdditionServiceImpl implements TicketAdditionService {

    private final TicketAdditionRepository ticketAdditionRepository;

    @Override
    public boolean selectTicketAddition(Long ticketId, Long additionId) {
        TicketAddition ticketAddition = ticketAdditionRepository.findTicketAdditionByTicketIdAndAdditionId(ticketId, additionId);

        if (ticketAddition == null) {
            TicketAddition newTicketAddition = new TicketAddition();
            newTicketAddition.setAdditionId(additionId);
            newTicketAddition.setTicketId(ticketId);
            ticketAdditionRepository.save(newTicketAddition);
            return true;
        } else {
            ticketAdditionRepository.delete(ticketAddition);
        }
        return false;
    }

    @Override
    public boolean existsTicketAdditionByTicketIdAndAdditionId(Long ticketId, Long additionId) {
        return ticketAdditionRepository.existsTicketAdditionByTicketIdAndAdditionId(ticketId, additionId);
    }

    @Override
    public TicketAddition findTicketAdditionByTicketIdAndAdditionId(Long ticketId, Long additionId) {
        return ticketAdditionRepository.findTicketAdditionByTicketIdAndAdditionId(ticketId, additionId);
    }


    @Transactional
    @Override
    public long deleteTicketAdditionsByTicketId(Long ticketId) {
        return ticketAdditionRepository.deleteTicketAdditionsByTicketId(ticketId);
    }


    @Override
    public List<TicketAddition> findTicketAdditionsByTicketId(Long ticketId) {
        return ticketAdditionRepository.findTicketAdditionsByTicketId(ticketId);
    }

}
