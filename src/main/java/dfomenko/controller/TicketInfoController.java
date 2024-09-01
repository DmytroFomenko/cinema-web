package dfomenko.controller;

import dfomenko.entity.Film;
import dfomenko.entity.Seance;
import dfomenko.entity.Ticket;
import dfomenko.service.FilmService;
import dfomenko.service.SeanceService;
import dfomenko.service.TicketService;
import dfomenko.utils.TimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Timestamp;

@Controller
@AllArgsConstructor
public class TicketInfoController {

    private final MessageSource messageSource;
    private final TicketService ticketService;
    private final SeanceService seanceService;
    private final FilmService filmService;


    @GetMapping("/ticket_info/{ticketCode}")
    public String showTicketInfo(@PathVariable String ticketCode,
                                 Model model) {
        TimeUtils timeUtils = new TimeUtils();

        model.addAttribute("ticketCode", ticketCode);

        Ticket ticket;
        String message;

        try {
            ticket = ticketService.findTicketByCode(ticketCode);
            Seance nextSeance = seanceService.findSeanceById(ticket.getSeanceId());
            Timestamp now = timeUtils.getTimeNow();
            Timestamp beginTime = nextSeance.getBeginTime();
            Film nextFilm = filmService.findFilmById(nextSeance.getFilmId());
            Seance lastSeance = seanceService.findFirstByBeginTimeBeforeOrderByBeginTimeDesc(beginTime);
            Film lastSeanceFilm = filmService.findFilmById(lastSeance.getFilmId());

            // AFTER the END of the LAST seance && BEFORE the END of the NEXT seance
            if (now.after(lastSeance.calcEndTime(lastSeanceFilm.getDuration())) && now.before(nextSeance.calcEndTime(nextFilm.getDuration()))) {
                model.addAttribute("message", "actual");
            // AFTER the END of the LAST seance
            } else if (now.after(lastSeance.calcEndTime(lastSeanceFilm.getDuration()))) {
                model.addAttribute("message", "expired");
            // BEFORE the END of the NEXT seance
            } else {
                model.addAttribute("message", "notActual");
            }

            model.addAttribute("seance", nextSeance);
            model.addAttribute("film", nextFilm);

            model.addAttribute("isExists", true);

        } catch (Exception e) {
            return "ticket_info";
        }

        return "ticket_info";
    }

}
