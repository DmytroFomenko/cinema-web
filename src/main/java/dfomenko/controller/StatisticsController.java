package dfomenko.controller;

import dfomenko.entity.Film;
import dfomenko.entity.PlaceStatus;
import dfomenko.entity.Seance;
import dfomenko.service.FilmService;
import dfomenko.service.SeanceService;
import dfomenko.service.TicketService;
import dfomenko.utils.SortingUtils;
import dfomenko.utils.TimeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
@AllArgsConstructor
public class StatisticsController {

    private final FilmService filmService;
    private final SeanceService seanceService;
    private final TicketService ticketService;
    private final SortingUtils sortingUtils;
    private final TimeUtils timeUtils;

    private record ChartPair(String category, int value) {

    }


    private record SeanceData(String counter,
                              Timestamp beginTime,
                              Double basePrice,
                              String allPlacesCount,
                              String ticketsSold,
                              String percentageTicketsSold) {
    };

    @GetMapping("/statistics_films")
    public String getCinemaData(@ModelAttribute("error") String error,
                                @ModelAttribute("filmId") String filmIdStr,

                                Model model,
                                HttpSession session) throws ParseException {


        // Determine the amount of received data and the type of it sorting
        final int maxFilmsCount = 10;
        final String filmsSortType = "idDesc";
        int maxSeancesCount = 10;
        final String seancesSortType = "beginTimeDesc";

        // Taking Films list
        Pageable pagingFilm = PageRequest.of(0, maxFilmsCount, sortingUtils.makeSort(filmsSortType));
        Page<Film> pageFilm = filmService.findAllFilms(pagingFilm);
        List<Film> films = pageFilm.getContent();
        model.addAttribute("films", films);

        long filmId = -1L;
        if (filmIdStr.isEmpty()) {
            if (!films.isEmpty()) {
                filmId = films.get(0).getId();
            }
        } else {
            filmId = Long.parseLong(filmIdStr);
        }

        model.addAttribute("filmId", filmId);

        // Take seances list
        Pageable pagingSeances = PageRequest.of(0, maxSeancesCount, sortingUtils.makeSort(seancesSortType));
        Timestamp timeNow = timeUtils.getTimeNow();
        Page<Seance> pageSeance = seanceService.findAllByFilmIdAndBeginTimeBefore(filmId, timeNow, pagingSeances);
        List<Seance> seances = new ArrayList<>(pageSeance.getContent());

        List<ChartPair> chartData = new ArrayList<>();
        List<SeanceData> seancesDatas = new ArrayList<>();

        if (!seances.isEmpty()) {
            Collections.reverse(seances);
            Long index = 0L;
            for (Seance seance : seances) {
                String count = String.valueOf(++index);
                Long seanceId = seance.getId();
                Long takenTicketsCount = ticketService.countTicketsBySeanceIdAndPlaceStatus(seanceId, PlaceStatus.TAKEN);
                Long allTicketsCount = ticketService.countTicketsBySeanceId(seanceId);
                Integer ticketSalesLevel;
                if (allTicketsCount == 0L) {
                    ticketSalesLevel = 0;
                } else {
                    ticketSalesLevel = Math.toIntExact(takenTicketsCount * 100L / allTicketsCount);
                }
                chartData.add(new ChartPair(count, ticketSalesLevel));
                seancesDatas.add(new SeanceData(count,
                                                seance.getBeginTime(),
                                                seance.getBasePrice(),
                                                allTicketsCount.toString(),
                                                takenTicketsCount.toString(),
                                                ticketSalesLevel.toString()));
            }

        }
        model.addAttribute("seancesDatas", seancesDatas);

        model.addAttribute("chartData", chartData);
        return "statistics_films";
    }

}

