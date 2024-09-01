package dfomenko.controller;

import dfomenko.entity.PlaceKind;
import dfomenko.service.PlaceKindService;
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


@Controller
@AllArgsConstructor
public class PlaceKindController {

    private final PlaceKindService placeKindService;
    private MessageSource messageSource;

    @GetMapping("/placeKinds")
    public String listPlaceKinds(@ModelAttribute("error") String error,
                                 Model model,
                                 HttpSession session) {

        model.addAttribute("placeKinds", placeKindService.findAllPlaceKinds());

        return "placeKinds";
    }

    @GetMapping("/create_placeKind_form")
    public String createPlaceKindForm(@ModelAttribute("error") String error,
                                      @ModelAttribute("placeKind") PlaceKind placeKind,
                                      Model model) {

        model.addAttribute("placeKind", placeKind);

        return "create_placeKind_form";
    }

    @PostMapping("/create_placeKind")
    public String createPlaceKind(@ModelAttribute("placeKind") PlaceKind placeKind,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        try {
            placeKindService.createPlaceKind(placeKind);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageSeatTypeNameExists", null,  Locale.getDefault())); 
                                            //"Тип місця з такою назвою вже існує");
            redirectAttributes.addFlashAttribute("placeKind", placeKind);
            return "redirect:/create_placeKind_form";
        }

        return "redirect:/placeKinds";
    }

    @GetMapping("/update_placeKind_form")
    public String updatePlaceKindForm(@ModelAttribute("placeKindId") Long placeKindId,
                                      @ModelAttribute("placeKind") PlaceKind placeKind,
                                      @ModelAttribute("error") String error,
                                      Model model,
                                      HttpSession session) {

        if (placeKind.getName() != null) {
            model.addAttribute("placeKind", placeKind);
        } else {
            model.addAttribute("placeKind", placeKindService.findPlaceKindById(placeKindId));
        }

        return "update_placeKind_form";
    }

    @PostMapping("/update_placeKind")
    public String updatePlaceKind(@ModelAttribute("placeKind") PlaceKind placeKind,
                                  @ModelAttribute("placeKindId") Long placeKindId,
                                  RedirectAttributes redirectAttributes,
                                  Model model,
                                  HttpSession session) {

        PlaceKind existingPlaceKind = placeKindService.findPlaceKindById(placeKindId);

        existingPlaceKind.setName(placeKind.getName());
        existingPlaceKind.setAddPrice(placeKind.getAddPrice());

        try {
            placeKindService.updatePlaceKind(existingPlaceKind);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageSeatTypeNameExists", null, Locale.getDefault()));
                                            //"Тип місця з такою назвою вже існує");
            redirectAttributes.addAttribute("placeKindId", placeKindId);
            redirectAttributes.addFlashAttribute("placeKind", placeKind);
            return "redirect:/update_placeKind_form";
        }

        return "redirect:/placeKinds";
    }

    @PostMapping("/delete_placeKind")
    public String deletePlaceKind(@ModelAttribute("placeKindId") Long placeKindId, RedirectAttributes redirectAttributes) {

        try {
            placeKindService.deletePlaceKindById(placeKindId);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotDeleteSeatTypeWithRelatedSeats", null,  Locale.getDefault()));
                                            //"Цей тип місця неможливо видалити, бо він відноситься " +
                                            //        "до існуючого місця (або місць). Його можна буде видалити " +
                                            //        "після видалення місць з даним типом, або після  " +
                                            //        "зміни типу цих місць на інший.");
        }


        return "redirect:/placeKinds";
    }


}
