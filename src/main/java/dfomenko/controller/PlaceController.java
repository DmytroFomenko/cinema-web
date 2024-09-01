package dfomenko.controller;

import dfomenko.entity.Place;
import dfomenko.service.PlaceKindService;
import dfomenko.service.PlaceService;
import dfomenko.service.PlacementService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Objects;


@Controller
@AllArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlacementService placementService;
    private final PlaceKindService placeKindService;
    private MessageSource messageSource;

    @GetMapping("/places")
    public String listPlaces(@ModelAttribute("error") String error,
                             Model model,
                             HttpSession session) {

        model.addAttribute("places", placeService.findAllPlaces());
        model.addAttribute("placementServ", placementService);
        model.addAttribute("placeKindServ", placeKindService);

        return "places";
    }

    @GetMapping("/create_place_form")
    public String createPlaceForm(@ModelAttribute("error") String error,
                                  @ModelAttribute("place") Place place,
                                  @ModelAttribute("row") String row,
                                  @ModelAttribute("num") String num,
                                  Model model) {

        model.addAttribute("place", place);
        model.addAttribute("placements", placementService.findAllPlacements());
        model.addAttribute("placeKinds", placeKindService.findAllPlaceKinds());

        return "create_place_form";
    }

    @PostMapping("/create_place")
    public String createPlace(@ModelAttribute("place") Place place,
                              @ModelAttribute("row") String row,
                              @ModelAttribute("num") String num,
                              RedirectAttributes redirectAttributes) {

        String placeNumber;
        if (!row.isEmpty()) {
            placeNumber = "ряд" + row + "міс" + num;
        } else {
            placeNumber = "міс" + num;
        }
        place.setNumber(placeNumber);

        if (placeService.existsPlaceByPlacementIdAndNumber(place.getPlacementId(), place.getNumber())) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessagePlaceNameExistsInLayout", null, Locale.getDefault()));
                                            //"Місце з такою назвою вже існує у заданій розстановці. Змініть ряд або номер місця.");
            redirectAttributes.addAttribute("row", row);
            redirectAttributes.addAttribute("num", num);
            redirectAttributes.addFlashAttribute("place", place);
            return "redirect:/create_place_form";
        }

        placeService.createPlace(place);

        return "redirect:/places";
    }

    @GetMapping("/update_place_form")
    public String updatePlaceForm(@ModelAttribute("placeId") Long placeId,
                                  @ModelAttribute("place") Place place,
                                  @ModelAttribute("row") String row,
                                  @ModelAttribute("num") String num,
                                  @ModelAttribute("error") String error,
                                  Model model) {

        if (place.getPlaceKindId() != null) {
            model.addAttribute("place", place);
        } else {
            Place existingPlace = placeService.findPlaceById(placeId);
            model.addAttribute("place", existingPlace);
            model.addAttribute("row", existingPlace.getNumber()
                                                   .substring(existingPlace.getNumber().indexOf('д') + 1,
                                                              existingPlace.getNumber().indexOf('м')));
            model.addAttribute("num", existingPlace.getNumber()
                                                   .substring(existingPlace.getNumber().indexOf('с') + 1));
        }

        model.addAttribute("placements", placementService.findAllPlacements());
        model.addAttribute("placeKinds", placeKindService.findAllPlaceKinds());

        return "update_place_form";
    }

    @PostMapping("/update_place")
    public String updatePlace(@ModelAttribute("place") Place place,
                              @ModelAttribute("placeId") Long placeId,
                              @ModelAttribute("row") String row,
                              @ModelAttribute("num") String num,
                              RedirectAttributes redirectAttributes) {

        Place existingPlace = placeService.findPlaceById(placeId);

        String placeNumber;
        if (!row.isEmpty()) {
            placeNumber = "ряд" + row + "міс" + num;
        } else {
            placeNumber = "міс" + num;
        }

        existingPlace.setPlacementId(place.getPlacementId());
        existingPlace.setPlaceKindId(place.getPlaceKindId());
        existingPlace.setNumber(placeNumber);

        if (placeService.existsPlaceByPlacementIdAndNumber(existingPlace.getPlacementId(), existingPlace.getNumber())) {
            if (!Objects.equals(existingPlace.getId(), placeService.findPlaceByPlacementIdAndNumber(existingPlace.getPlacementId(), existingPlace.getNumber()).getId())) {
                redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessagePlaceNameExistsInLayout", null,  Locale.getDefault()));
                                                //"Місце з такою назвою вже існує у заданій розстановці. Змініть ряд або номер місця.");
                redirectAttributes.addAttribute("row", row);
                redirectAttributes.addAttribute("num", num);
                redirectAttributes.addFlashAttribute("place", place);
                redirectAttributes.addAttribute("placeId", placeId);
                return "redirect:/update_place_form";
            }

        }
        placeService.updatePlace(existingPlace);

        return "redirect:/places";
    }

    @PostMapping("/delete_place")
    public String deletePlace(@ModelAttribute("placeId") Long placeId,
                              RedirectAttributes redirectAttributes) {

        try {
            placeService.deletePlaceById(placeId);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotDeletePlaceWithRelatedSeancesAndTickets", null, Locale.getDefault()));
                                            //"Це місце неможливо видалити, бо воно використовується " +
                                            //        "в існуючому сеансі (або сеансах) та на нього існує квиток(и). " +
                                            //        "Його можна буде видалити після видалення сеансів з даним " +
                                            //        "місцем (квитки будуть видалені автоматично).");
        }


        return "redirect:/places";
    }


}
