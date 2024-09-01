package dfomenko.controller;


import dfomenko.entity.LoginData;
import dfomenko.entity.Place;
import dfomenko.entity.PlaceStatus;
import dfomenko.entity.Seance;
import dfomenko.entity.Ticket;
import dfomenko.service.AdditionService;
import dfomenko.service.BillToPayService;
import dfomenko.service.FilmService;
import dfomenko.service.PlaceKindService;
import dfomenko.service.PlaceService;
import dfomenko.service.PlacementService;
import dfomenko.service.RoleService;
import dfomenko.service.SeanceService;
import dfomenko.service.TicketAdditionService;
import dfomenko.service.TicketService;
import dfomenko.utils.SortingUtils;
import dfomenko.utils.TimeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Controller
@AllArgsConstructor
public class SeanceController {
    private final SeanceService seanceService;
    private final FilmService filmService;
    private final RoleService roleService;
    private final TicketService ticketService;
    private final PlaceService placeService;
    private final PlacementService placementService;
    private final BillToPayService billToPayService;
    private final AdditionService additionService;
    private final PlaceKindService placeKindService;
    private final TicketAdditionService ticketAdditionService;
    private final SortingUtils sortingUtils;
    private final MessageSource messageSource;

    @GetMapping("/")
    public String listSeances(@ModelAttribute("error") String error,
                              @ModelAttribute("seancesSortType") String seancesSortType,
                              @ModelAttribute("seancesShowActualOnly") String seancesShowActualOnly,
                              @ModelAttribute("seancesShowWithFreePlacesOnly") String seancesShowWithFreePlacesOnly,
                              @ModelAttribute("seancesShowForDate") String seancesShowForDate,
                              Model model,
                              HttpSession session) throws ParseException {

        if (seancesSortType.isEmpty() && session.getAttribute("seancesSortType") == null) {
            seancesSortType = "beginTimeDesc";
        } else if (seancesSortType.isEmpty()) {
            seancesSortType = session.getAttribute("seancesSortType").toString();
        }

        model.addAttribute("seancesSortType", seancesSortType);
        session.setAttribute("seancesSortType", seancesSortType);


        if (seancesShowActualOnly.isEmpty() && (session.getAttribute("seancesShowActualOnly") == null)) {
            seancesShowActualOnly = Boolean.toString(true);
        } else if (seancesShowActualOnly.isEmpty()) {
            seancesShowActualOnly = session.getAttribute("seancesShowActualOnly").toString();
        }

        model.addAttribute("seancesShowActualOnly", seancesShowActualOnly);
        session.setAttribute("seancesShowActualOnly", seancesShowActualOnly);


        if (seancesShowWithFreePlacesOnly.isEmpty() && (session.getAttribute("seancesShowWithFreePlacesOnly") == null)) {
            seancesShowWithFreePlacesOnly = Boolean.toString(true);
        } else if (seancesShowWithFreePlacesOnly.isEmpty()) {
            seancesShowWithFreePlacesOnly = session.getAttribute("seancesShowWithFreePlacesOnly").toString();
        }

        model.addAttribute("seancesShowWithFreePlacesOnly", seancesShowWithFreePlacesOnly);
        session.setAttribute("seancesShowWithFreePlacesOnly", seancesShowWithFreePlacesOnly);


        if (seancesShowForDate.isEmpty() && (session.getAttribute("seancesShowForDate") == null)) {
            seancesShowForDate = "off";
        } else if (seancesShowForDate.isEmpty()) {
            seancesShowForDate = session.getAttribute("seancesShowForDate").toString();
        }


        LoginData loggedUser = (LoginData) session.getAttribute("loggedUser");
        Long adminRoleId = roleService.findRoleByRole("admin").getId();

        Pageable paging = PageRequest.of(0,
                                         Integer.MAX_VALUE,
                                         sortingUtils.makeSort(seancesSortType));
        Page<Seance> pageSeances;

        TimeUtils timeUtils = new TimeUtils();
        String seancesShowMinDate = "";
        if (seancesShowActualOnly.equals(Boolean.toString(true))) {

            seancesShowMinDate = timeUtils.getDateString(timeUtils.getTimeNow());
            Timestamp timeNow = timeUtils.getTimeNow();

            if (seancesShowForDate.equals("off")) {
                pageSeances = seanceService.findAllByBeginTimeAfter(timeNow, paging);
            } else {
                seancesShowForDate = timeUtils.getDateStringNotEarlierThenNow(seancesShowForDate);
                pageSeances = seanceService.findAllByDateString(seancesShowForDate, paging);
            }
        } else {
            seancesShowMinDate = "";
            if (seancesShowForDate.equals("off")) {
                pageSeances = seanceService.findAll(paging);
            } else {
                pageSeances = seanceService.findAllByDateString(seancesShowForDate, paging);
            }
        }

        List<Seance> seances = pageSeances.getContent();


        if (seancesShowWithFreePlacesOnly.equals(Boolean.toString(true))) {
            seances = seances.stream().filter(seance -> seance.getFreeTicketsCount() > 0).collect(Collectors.toList());
        }

        model.addAttribute("seancesShowMinDate", seancesShowMinDate);

        session.setAttribute("seancesShowForDate", seancesShowForDate);
        model.addAttribute("seancesShowForDate", seancesShowForDate);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("ticketServ", ticketService);
        model.addAttribute("seances", seances);
        model.addAttribute("filmServ", filmService);
        model.addAttribute("freePlaceStatus", PlaceStatus.FREE);


        if (loggedUser != null) {

            Long purchasedValidTicketsCount = ticketService.countTakenTicketsByClientIdAnd_BeginTimeAfter(loggedUser.getId(),
                                                                                                          timeUtils.getTimeNow());

            model.addAttribute("adminRoleId", adminRoleId);
            model.addAttribute("purchasedValidTicketsCount", purchasedValidTicketsCount);
            model.addAttribute("seanceServ", seanceService);
            model.addAttribute("placeServ", placeService);
        }

        return "seances";
    }


    @GetMapping("/more_info")
    public String getMoreInfo(@ModelAttribute("seanceId") Long seanceId,
                              Model model,
                              HttpSession session) {

        Seance seance = seanceService.findSeanceById(seanceId);

        model.addAttribute("seance", seance);
        model.addAttribute("film", filmService.findFilmById(seance.getFilmId()));
        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
        model.addAttribute("clientRoleId", roleService.findRoleByRole("client").getId());

        return "more_info_seance";
    }


    @GetMapping("/create_seance_form")
    public String createSeanceForm(@ModelAttribute("seance") Seance seance,
                                   @ModelAttribute("placementId") String placementId,
                                   @ModelAttribute("error") String error,
                                   Model model) {

        model.addAttribute("seance", seance);
        model.addAttribute("films", filmService.findAllFilms());
        model.addAttribute("placements", placementService.findAllPlacements());
        model.addAttribute("billToPays", billToPayService.findAllBillToPays());

        if (placementId.isEmpty()) {
            model.addAttribute("placementId", "-1");
        }

        return "create_seance_form";
    }


    @Transactional
    @PostMapping("/create_seance")
    public String createSeance(@ModelAttribute("seance") Seance seance,
                               @ModelAttribute("placementId") Long placementId,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (!placeService.existsPlaceByPlacementId(placementId)) {
            redirectAttributes.addFlashAttribute("seance", seance);
            redirectAttributes.addAttribute("placementId", placementId);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageSelectedPlacementDoNotHavePlaces", null, Locale.getDefault()));
            //"Обрана розстановка не має місць! Спочатку додайте місця до розстановки або оберіть іншу"
            return "redirect:/create_seance_form";
        }

        if (seanceService.existsTimeCollision(seance)) {
            redirectAttributes.addFlashAttribute("seance", seance);
            redirectAttributes.addAttribute("placementId", placementId);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageOverlapSeanceTime", null, Locale.getDefault()));
            //"Присутнє накладання часових меж створюваного сеансу на часові межі існуючого сеансу!");
            return "redirect:/create_seance_form";
        }

        try {
            seanceService.createSeance(seance);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("seance", seance);
            redirectAttributes.addAttribute("placementId", placementId);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCreateSeance", null, Locale.getDefault()));
            // "Помилка при створенні сеансу");
            return "redirect:/create_seance_form";
        }

        List<Place> places = placeService.findPlacesByPlacementId(placementId);

        for (Place place : places) {
            ticketService.createFreeTicket(seance.getId(), place.getId());
        }

        return "redirect:/";
    }


    // 1st
    @GetMapping("/update_seance_form")
    public String updateSeanceForm(@ModelAttribute("seance") Seance seance,
                                   @ModelAttribute("seanceId") Long seanceId,
                                   @ModelAttribute("placementId") String placementId,
                                   @ModelAttribute("error") String error,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        if (seanceService.existsReservedOrBoughtTicket(seanceId)) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotEditSeanceWithReservedOrPurchasedTickets", null, Locale.getDefault()));
            //"Сеанс неможливо редагувати, бо на нього вже заброньовані або куплені квитки");
            return "redirect:/";
        }

        if (seance.getId() == null) {
            model.addAttribute("seance", seanceService.findSeanceById(seanceId));
        } else {
            model.addAttribute("seance", seance);
        }

        if (placementId.isEmpty()) {
            placementId = String.valueOf(placeService.findPlaceById(ticketService.findTicketsBySeanceId(seanceId).get(0).getPlaceId()).getPlacementId());
        }

        model.addAttribute("placementId", placementId);
        model.addAttribute("films", filmService.findAllFilms());
        model.addAttribute("placements", placementService.findAllPlacements());
        model.addAttribute("billToPays", billToPayService.findAllBillToPays());

        return "update_seance_form";
    }


    // 2nd
    @Transactional
    @PostMapping("/update_seance")
    public String updateSeance(@ModelAttribute("seance") Seance seance,
                               @ModelAttribute("placementId") Long placementId,
                               RedirectAttributes redirectAttributes) {

        Long id = seance.getId();

        if (!placeService.existsPlaceByPlacementId(placementId)) {
            redirectAttributes.addFlashAttribute("seance", seance);
            redirectAttributes.addAttribute("seanceId", id);
            redirectAttributes.addAttribute("placementId", placementId);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageSelectedPlacementDoNotHavePlaces", null, Locale.getDefault()));
            //"Обрана розстановка не має місць! Спочатку додайте місця до розстановки або оберіть іншу");
            return "redirect:/update_seance_form";
        }

        if (seanceService.existsTimeCollision(seance)) {
            redirectAttributes.addFlashAttribute("seance", seance);
            redirectAttributes.addAttribute("seanceId", id);
            redirectAttributes.addAttribute("placementId", placementId);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageOverlapNewSeanceTime", null, Locale.getDefault()));
            //"Присутнє накладання нових часових меж сеансу на часові межі існуючого сеансу!");
            return "redirect:/update_seance_form";
        }

        Seance existingSeance = seanceService.findSeanceById(seance.getId());

        existingSeance.setFilmId(seance.getFilmId());

        existingSeance.setBeginTime(seance.getBeginTime());
        existingSeance.setBasePrice(seance.getBasePrice());
        existingSeance.setBillToPayId(seance.getBillToPayId());

        Long currentPlacementId = placeService.findPlaceById(ticketService.findTicketsBySeanceId(existingSeance.getId()).get(0).getPlaceId()).getPlacementId();

        if (!currentPlacementId.equals(placementId)) {
            ticketService.deleteTicketsBySeanceId(existingSeance.getId());

            List<Place> places = placeService.findPlacesByPlacementId(placementId);
            for (Place place : places) {
                ticketService.createFreeTicket(existingSeance.getId(), place.getId());
            }
        }

        try {
            seanceService.updateSeance(existingSeance);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("seance", seance);
            redirectAttributes.addAttribute("seanceId", id);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageEditSeance", null, Locale.getDefault()));
            //"Помилка при редагуванні сеансу");
            return "redirect:/update_seance_form";
        }

        return "redirect:/";
    }

    @PostMapping("/delete_seance")
    public String deleteSeance(@ModelAttribute("seanceId") Long seanceId,
                               RedirectAttributes redirectAttributes) {

        if (seanceService.existsReservedOrBoughtTicket(seanceId)) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotDeleteSeanceWithReservedOrPurchasedTickets", null, Locale.getDefault()));
            //"Сеанс неможливо видалити, бо на нього вже заброньовані або куплені квитки");
            return "redirect:/";
        }

        try {
            ticketService.deleteTicketsBySeanceId(seanceId);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageErrorDeletingSeanceTickets", null, Locale.getDefault()));
            //"Помилка при видаленні квитків сеансу. " +
            //        "Сеанс не було видалено.");
            return "redirect:/";
        }

        try {
            seanceService.deleteSeanceById(seanceId);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageDeleteSeance", null, Locale.getDefault()));
            //"Помилка при видаленні сеансу.");
            return "redirect:/";
        }

        return "redirect:/";
    }


    @GetMapping("/show_seance_statistics")
    public String seanceStatistics(@ModelAttribute("error") String error,
                                   @ModelAttribute("seanceId") Long seanceId,
                                   Model model,
                                   HttpSession session) {

        List<Ticket> boughtTickets = null;
        boughtTickets = ticketService.findTicketsBySeanceIdAndPlaceStatus(seanceId, PlaceStatus.TAKEN);
        if (boughtTickets.isEmpty()) {
            boughtTickets = null;
        }

        double seancePrice = seanceService.findSeanceById(seanceId).getBasePrice();
        double totalPrice = 0;

        if (boughtTickets != null) {
            totalPrice = seanceService.calcTotalPriceOfSeanceTickets(boughtTickets, seanceId);
            model.addAttribute("numOfBoughtTickets", boughtTickets.size());
            Long allTicketsCount = ticketService.countTicketsBySeanceId(seanceId);
            model.addAttribute("allTicketsCount", allTicketsCount);
        }

        model.addAttribute("seanceId", seanceId);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("seancePrice", seancePrice);
        model.addAttribute("boughtTickets", boughtTickets);
        model.addAttribute("filmServ", filmService);
        model.addAttribute("seanceServ", seanceService);
        model.addAttribute("placeServ", placeService);
        model.addAttribute("additionServ", additionService);
        model.addAttribute("ticketAdditionService", ticketAdditionService);
        model.addAttribute("placeKindServ", placeKindService);

        return "seance_statistics";
    }



    @GetMapping("/show_day_statistics")
    public String dayStatistics(@ModelAttribute("error") String error,
                                @ModelAttribute("seanceId") Long seanceId,
                                Model model,
                                HttpSession session) {

        Timestamp seanceBeginTime = seanceService.findSeanceById(seanceId).getBeginTime();

        List<Seance> daySeances = seanceService.findDaySeancesByBeginTime(seanceBeginTime);
        List<Ticket> boughtTickets = new ArrayList<>();
        for (Seance s : daySeances) {
            boughtTickets.addAll(ticketService.findTicketsBySeanceIdAndPlaceStatus(s.getId(), PlaceStatus.TAKEN));
        }
        if (boughtTickets.isEmpty()) {
            boughtTickets = null;
        }

        double totalPrice = 0;
        if (boughtTickets != null) {
            totalPrice = seanceService.calcTotalPriceOfDaySeancesTickets(daySeances);
            model.addAttribute("numOfBoughtTickets", boughtTickets.size());
        }

        model.addAttribute("seanceId", seanceId);
        model.addAttribute("daySeances", daySeances);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("boughtTickets", boughtTickets);
        model.addAttribute("numOfSeances", daySeances.size());
        model.addAttribute("filmServ", filmService);
        model.addAttribute("seanceServ", seanceService);
        model.addAttribute("placeServ", placeService);
        model.addAttribute("additionServ", additionService);
        model.addAttribute("ticketAdditionService", ticketAdditionService);
        model.addAttribute("placeKindServ", placeKindService);

        return "day_statistics";
    }


}
