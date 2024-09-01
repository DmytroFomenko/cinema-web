package dfomenko.controller;

import dfomenko.entity.Addition;
import dfomenko.service.AdditionService;
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
public class AdditionController {

    private final AdditionService additionService;
    private MessageSource messageSource;


    @GetMapping("/additions")
    public String listAdditions(@ModelAttribute("error") String error,
                                Model model,
                                HttpSession session) {

        model.addAttribute("additions", additionService.findAllAdditions());

        return "additions";
    }

    @GetMapping("/create_addition_form")
    public String createAdditionForm(@ModelAttribute("error") String error,
                                     @ModelAttribute("addition") Addition addition,
                                     Model model) {

        model.addAttribute("addition", addition);

        return "create_addition_form";
    }


    @PostMapping("/create_addition")
    public String createAddition(@ModelAttribute("addition") Addition addition,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        addition.setAvailable(addition.getAvailable() != null);
        try {
            additionService.createAddition(addition);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageServiceNameExists", null, Locale.getDefault()));
                                            //"Послуга з такою назвою вже існує");
            redirectAttributes.addFlashAttribute("addition", addition);
            return "redirect:/create_addition_form";
        }

        return "redirect:/additions";
    }


    @GetMapping("/update_addition_form")
    public String updateAdditionForm(@ModelAttribute("additionId") Long additionId,
                                     @ModelAttribute("addition") Addition addition,
                                     @ModelAttribute("error") String error,
                                     Model model,
                                     HttpSession session) {

        if (addition.getName() != null) {
            model.addAttribute("addition", addition);
        } else {
            model.addAttribute("addition", additionService.findAdditionById(additionId));
        }

        return "update_addition_form";
    }

    @PostMapping("/update_addition")
    public String updateAddition(@ModelAttribute("addition") Addition addition,
                                 @ModelAttribute("additionId") Long additionId,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpSession session) {

        Addition existingAddition = additionService.findAdditionById(additionId);

        existingAddition.setName(addition.getName());
        existingAddition.setPrice(addition.getPrice());
        existingAddition.setAvailable(addition.getAvailable() != null);

        try {
            additionService.updateAddition(existingAddition);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageServiceNameExists", null,  Locale.getDefault()));
                                            //"Послуга з такою назвою вже існує");
            redirectAttributes.addAttribute("additionId", additionId);
            redirectAttributes.addFlashAttribute("addition", addition);
            return "redirect:/update_addition_form";
        }

        return "redirect:/additions";
    }


    @PostMapping("/delete_addition")
    public String deleteAddition(@ModelAttribute("additionId") Long additionId, RedirectAttributes redirectAttributes) {

        try {
            additionService.deleteAdditionById(additionId);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotDeleteServiceWithExistingTickets", null,  Locale.getDefault()));
                                            //"Цю послугу неможливо видалити, бо вона придбана " +
                                            //        "в діючому або архівному квитку. Її можна буде видалити " +
                                            //        "після видалення квитків, у яких вона придбана. " +
                                            //        "Послугу можна зробити неактивною й наступні клієнти " +
                                            //        "її не побачать.");
        }


        return "redirect:/additions";
    }

}
