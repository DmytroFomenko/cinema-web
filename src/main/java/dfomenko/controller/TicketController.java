package dfomenko.controller;

import com.google.zxing.WriterException;
import dfomenko.entity.LoginData;
import dfomenko.entity.PlaceStatus;
import dfomenko.entity.Seance;
import dfomenko.entity.Ticket;
import dfomenko.entity.TicketAddition;
import dfomenko.service.AdditionService;
import dfomenko.service.EmailService;
import dfomenko.service.FilmService;
import dfomenko.service.PdfService;
import dfomenko.service.PlaceKindService;
import dfomenko.service.PlaceService;
import dfomenko.service.PlacementService;
import dfomenko.service.SeanceService;
import dfomenko.service.TicketAdditionService;
import dfomenko.service.TicketService;
import dfomenko.utils.MediaTypeUtils;
import dfomenko.utils.QRCodeUtils;
import dfomenko.utils.SortingUtils;
import dfomenko.utils.TimeUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Controller
@AllArgsConstructor
public class TicketController {

    private final SeanceService seanceService;
    private final FilmService filmService;
    private final TicketService ticketService;
    private final PlaceService placeService;
    private final PlacementService placementService;
    private final PlaceKindService placeKindService;
    private final AdditionService additionService;
    private final TicketAdditionService ticketAdditionService;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final ApplicationContext applicationContext;
    private final MessageSource messageSource;
    private final SortingUtils sortingUtils;
    private final ServletContext servletContext;

    @GetMapping("/buy_ticket_form")
    public String buyTicketForm(@ModelAttribute("seanceId") Long seanceId,
                                Model model,
                                HttpSession session) {

        model.addAttribute("seanceId", seanceId);

        List<Ticket> tickets = ticketService.findTicketsBySeanceId(seanceId);
        model.addAttribute("tickets", tickets);
        model.addAttribute("film", filmService.findFilmById(seanceService.findSeanceById(seanceId).getFilmId()));
        Ticket ticket0 = tickets.get(0);

        String placementImageName = ticket0 != null ? placementService.findPlacementById(placeService.findPlaceById(ticket0.getPlaceId()).getPlacementId()).getImageName() : null;
        model.addAttribute("placementImageName", placementImageName);

        LoginData loggedUser = (LoginData) session.getAttribute("loggedUser");
        model.addAttribute("loggedUser", loggedUser);

        model.addAttribute("placeServ", placeService);
        model.addAttribute("placeKindServ", placeKindService);
        model.addAttribute("ticketAdditionServ", ticketAdditionService);

        model.addAttribute("availableAdditions", additionService.getAllAvailableAdditions());

        List<Ticket> reservedTickets = ticketService.getReservedTickets(loggedUser.getId(), seanceId);
        model.addAttribute("reservedTickets", reservedTickets);

        Seance seance = seanceService.findSeanceById(seanceId);
        double seancePrice = seance.getBasePrice();
        model.addAttribute("seancePrice", seancePrice);

        model.addAttribute("seance", seance);

        double totalPrice = 0;

        for (Ticket tick : reservedTickets) {
            totalPrice += seancePrice;
            totalPrice += placeKindService.findPlaceKindById(placeService.findPlaceById(tick.getPlaceId()).getPlaceKindId()).getAddPrice();
            List<TicketAddition> tickAdditions = ticketAdditionService.findTicketAdditionsByTicketId(tick.getId());
            for (TicketAddition tickAddition : tickAdditions) {
                totalPrice += additionService.findAdditionById(tickAddition.getAdditionId()).getPrice();
            }
        }
        model.addAttribute("totalPrice", totalPrice);

        return "buy_ticket_form";
    }


    @PostMapping("/select_ticket")
    public String selectTicket(@ModelAttribute("ticketId") Long ticketId,
                               RedirectAttributes redirectAttributes,
                               Model model, HttpSession session) {

        Long seanceId = ticketService.findTicketById(ticketId).getSeanceId();
        redirectAttributes.addAttribute("seanceId", seanceId);

        LoginData client = (LoginData) session.getAttribute("loggedUser");
        ticketService.toggleTicketReservStatus(ticketId, client);

        return "redirect:/buy_ticket_form";
    }


    @PostMapping("/select_ticket_addition")
    public String selectTicketAddition(@ModelAttribute("ticketId") Long ticketId,
                                       @ModelAttribute("additionId") Long additionId,
                                       RedirectAttributes redirectAttributes,
                                       Model model, HttpSession session) {

        Long seanceId = ticketService.findTicketById(ticketId).getSeanceId();
        redirectAttributes.addAttribute("seanceId", seanceId);

        ticketAdditionService.selectTicketAddition(ticketId, additionId);

        return "redirect:/buy_ticket_form";
    }


    @PostMapping("/buy_tickets")
    public String buyTickets(@ModelAttribute("totalPrice") double totalPrice,
                             @ModelAttribute("seanceId") Long seanceId,
                             Model model,
                             HttpSession session) {
        LoginData loggedUser = (LoginData) session.getAttribute("loggedUser");

        List<Ticket> reservedTickets = ticketService.findTicketsBySeanceIdAndClientIdAndPlaceStatus(seanceId, loggedUser.getId(), PlaceStatus.RESERVED);

        for (Ticket tick : reservedTickets) {
            ticketService.buyTicket(tick);
        }

        return "redirect:/";
    }


    @GetMapping("/client_tickets")
    public String clientTickets(@ModelAttribute("sendEmailTickets") String sendEmailTickets,
                                @ModelAttribute(value = "ticketsShowActualOnly") String ticketsShowActualOnly,
                                @ModelAttribute("ticketsShowForDate") String ticketsShowForDate,
                                @ModelAttribute("downloadTickets") String downloadTickets,
                                @ModelAttribute("ticketsSortType") String ticketsSortType,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) throws ParseException, IOException, WriterException {

        LoginData loggedUser = (LoginData) session.getAttribute("loggedUser");

        if (ticketsSortType.isEmpty() && session.getAttribute("ticketsSortType") == null) {
            ticketsSortType = "seance.beginTimeDesc";
        } else if (ticketsSortType.isEmpty()) {
            ticketsSortType = session.getAttribute("ticketsSortType").toString();
        }

        if (ticketsShowActualOnly.isEmpty() && session.getAttribute("ticketsShowActualOnly") == null) {
            ticketsShowActualOnly = Boolean.toString(true);
        } else if (ticketsShowActualOnly.isEmpty()) {
            ticketsShowActualOnly = session.getAttribute("ticketsShowActualOnly").toString();
        }

        if (ticketsShowForDate.isEmpty() && session.getAttribute("ticketsShowForDate") == null) {
            ticketsShowForDate = "off";
        } else if (ticketsShowForDate.isEmpty()) {
            ticketsShowForDate = session.getAttribute("ticketsShowForDate").toString();
        }


        Pageable paging = PageRequest.of(0,
                                         Integer.MAX_VALUE,
                                         sortingUtils.makeSort(ticketsSortType));
        Page<Ticket> pageTickets;

        TimeUtils timeUtils = new TimeUtils();
        String ticketsShowMinDate = "";
        if (ticketsShowActualOnly.equals("true")) {

            Timestamp timeNow = timeUtils.getTimeNow();
            ticketsShowMinDate = timeUtils.getDateString(timeNow);

            if (ticketsShowForDate.equals("off")) {
                pageTickets = ticketService.findTicketsByClientIdAndPlaceStatusAndSeance_BeginTimeAfter(loggedUser.getId(),
                                                                                                        PlaceStatus.TAKEN,
                                                                                                        timeNow,
                                                                                                        paging);
            } else {
                ticketsShowForDate = timeUtils.getDateStringNotEarlierThenNow(ticketsShowForDate);
                    /*
                    pageSeances = seanceService.findAllByDateString(ticketsShowForDate, paging);
                     */
                pageTickets = ticketService.findTakenTicketsByClientIdAndDate(loggedUser.getId(),
                                                                              ticketsShowForDate,
                                                                              paging);
            }
        } else {

            ticketsShowMinDate = "";
            if (ticketsShowForDate.equals("off")) {
                pageTickets = ticketService.findTicketsByClientIdAndPlaceStatus(loggedUser.getId(),
                                                                                PlaceStatus.TAKEN,
                                                                                paging);
            } else {
                pageTickets = ticketService.findTakenTicketsByClientIdAndDate(loggedUser.getId(),
                                                                              ticketsShowForDate,
                                                                              paging);
            }

        }


        List<Ticket> boughtTickets = pageTickets.getContent();

        if (boughtTickets.isEmpty()) {
            boughtTickets = null;
        }


        model.addAttribute("boughtTickets", boughtTickets);
        model.addAttribute("seanceServ", seanceService);
        model.addAttribute("placeServ", placeService);
        model.addAttribute("filmServ", filmService);

        model.addAttribute("placeKindServ", placeKindService);
        model.addAttribute("seanceServ", seanceService);
        model.addAttribute("filmServ", filmService);
        model.addAttribute("ticketAdditionService", ticketAdditionService);
        model.addAttribute("additionServ", additionService);


        model.addAttribute("loggedUser", loggedUser); // need to template

        // Sending tickets via email
        if (!sendEmailTickets.isEmpty()) {
            String email = loggedUser.getEmail();
            model.addAttribute("sendEmailTickets", true);

            List<ByteArrayDataSource> attachments = new ArrayList<>();
            if (boughtTickets != null) {
                for (Ticket tick : boughtTickets) {
                    String ticketCode = tick.getCode();
                    byte[] qrImageBytes = QRCodeUtils.getQRCodeImage("localhost:8080/ticket_info/" + ticketCode,
                                                                     128,
                                                                     128);
                    ByteArrayDataSource qrImageDataSource = new ByteArrayDataSource(qrImageBytes, "image/png");
                    qrImageDataSource.setName(ticketCode + ".png");
                    attachments.add(qrImageDataSource);
                }
            }

            try {
                emailService.sendTemplatedMessage(email,//"f.dv.register@gmail.com",//
                                                  applicationContext.getMessage("phraseYourTicketsToTheCinema", null, Locale.getDefault()),
                                                  "mail/mail_client_tickets",
                                                  attachments,
                                                  model.asMap());

            } catch (MessagingException e) {
                model.addAttribute("error", messageSource.getMessage("errorMessageEmailSendingError", null,  Locale.getDefault()));
                                   //"Виникла помилка при відправленні листа. Спробуйте пізніше.");
            }
        }

        model.addAttribute("ticketsSortType", ticketsSortType);
        session.setAttribute("ticketsSortType", ticketsSortType);

        model.addAttribute("ticketsShowMinDate", ticketsShowMinDate);
        session.setAttribute("ticketsShowMinDate", ticketsShowMinDate);

        model.addAttribute("ticketsShowForDate", ticketsShowForDate);
        session.setAttribute("ticketsShowForDate", ticketsShowForDate);

        model.addAttribute("ticketsShowActualOnly", ticketsShowActualOnly);
        session.setAttribute("ticketsShowActualOnly", ticketsShowActualOnly);


        if (!downloadTickets.isEmpty()) {

            model.addAttribute("downloadTickets", true);
            try {
                // Make PDF stream
                byte[] outputByteBuffer = pdfService.convertTemplatedHtmlToPdf("mail/download_client_tickets", model.asMap());
                redirectAttributes.addFlashAttribute("outputByteBuffer", outputByteBuffer);

            } catch (IOException e) {
                model.addAttribute("error", messageSource.getMessage("errorMessageDocumentLoadError", null,  Locale.getDefault()));
                                   //"Виникла помилка при завантаженні документа.");
                return "client_tickets";
            }

            return "redirect:/download_client_tickets";
        }

        return "client_tickets";
    }


    @GetMapping("/download_client_tickets")
    public ResponseEntity<ByteArrayResource> downloadFile(@ModelAttribute("outputByteBuffer") byte[] outputByteBuffer) throws IOException {

        String outputFileName = "CinemaTickets.pdf";
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(outputFileName, this.servletContext);
//        System.out.println("fileName: " + outputFileName);
//        System.out.println("mediaType: " + mediaType);

        ByteArrayResource resource = new ByteArrayResource(outputByteBuffer);

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + outputFileName)
                             .contentType(mediaType)
                             .contentLength(outputByteBuffer.length) //
                             .body(resource);

    }

}
