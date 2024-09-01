package dfomenko.repository;

import dfomenko.entity.TicketAddition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TicketAdditionRepository extends JpaRepository<TicketAddition, Long> {
    boolean existsTicketAdditionByTicketIdAndAdditionId(Long ticketId, Long additionId);
    TicketAddition findTicketAdditionByTicketIdAndAdditionId(Long ticketId, Long additionId);
    long deleteTicketAdditionsByTicketId(Long ticketId);
    List<TicketAddition> findTicketAdditionsByTicketId(Long ticketId);

}
