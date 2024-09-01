package dfomenko.controller;

import dfomenko.entity.BillToPay;
import dfomenko.service.BillToPayService;
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
public class BillToPayController {

    private final BillToPayService billToPayService;
    private MessageSource messageSource;

    @GetMapping("/billToPays")
    public String listBillToPays(@ModelAttribute("error") String error,
                                 Model model) {

        model.addAttribute("billToPays", billToPayService.findAllBillToPays());

        return "billToPays";
    }

    @GetMapping("/create_billToPay_form")
    public String createBillToPayForm(@ModelAttribute("error") String error,
                                      @ModelAttribute("billToPayObject") BillToPay billToPayObject,
                                      Model model) {

        model.addAttribute("billToPayObject", billToPayObject);

        return "create_billToPay_form";
    }

    @PostMapping("/create_billToPay")
    public String createBillToPay(@ModelAttribute("billToPayObject") BillToPay billToPayObject,
                                  RedirectAttributes redirectAttributes) {

        if (billToPayService.existsBillToPayByName(billToPayObject.getName())) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageBillingAccountWithNameExists", null, Locale.getDefault()));
                                            //"Розрахунковий рахунок з такою назвою вже існує");
            redirectAttributes.addFlashAttribute("billToPayObject", billToPayObject);
            return "redirect:/create_billToPay_form";
        } else if (billToPayService.existsBillToPayByBillToPay(billToPayObject.getBillToPay())) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageDuplicateBillingAccount", null,  Locale.getDefault()));
                                            //"Такий розрахунковий рахунок вже існує");
            redirectAttributes.addFlashAttribute("billToPayObject", billToPayObject);
            return "redirect:/create_billToPay_form";
        }

        billToPayService.createBillToPay(billToPayObject);

        return "redirect:/billToPays";
    }

    @GetMapping("/update_billToPay_form")
    public String updateBillToPayForm(@ModelAttribute("billToPayId") Long billToPayId,
                                      @ModelAttribute("billToPayObject") BillToPay billToPayObject,
                                      @ModelAttribute("error") String error,
                                      Model model) {

        if (billToPayObject.getName() != null) {
            model.addAttribute("billToPayObject", billToPayObject);
        } else {
            model.addAttribute("billToPayObject", billToPayService.findBillToPayById(billToPayId));
        }

        return "update_billToPay_form";
    }

    @PostMapping("/update_billToPay")
    public String updateBillToPay(@ModelAttribute("billToPayObject") BillToPay billToPayObject,
                                  @ModelAttribute("billToPayId") Long billToPayId,
                                  RedirectAttributes redirectAttributes) {

        BillToPay exictingBillToPay = billToPayService.findBillToPayById(billToPayId);

        exictingBillToPay.setName(billToPayObject.getName());
        exictingBillToPay.setBillToPay(billToPayObject.getBillToPay());

        try {
            billToPayService.updateBillToPay(exictingBillToPay);
        } catch (DataIntegrityViolationException e) {
            if (billToPayService.existsBillToPayByNameAndIdNot(exictingBillToPay.getName(), exictingBillToPay.getId())) {
                redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageBillingAccountWithNameExists", null,  Locale.getDefault()));
                                                //"Розрахунковий рахунок з такою назвою вже існує");
            } else {
                redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageDuplicateBillingAccount", null,  Locale.getDefault()));
                                                //"Такий розрахунковий рахунок вже існує");
            }
            redirectAttributes.addAttribute("billToPayId", billToPayId);
            redirectAttributes.addFlashAttribute("billToPayObject", billToPayObject);
            return "redirect:/update_billToPay_form";
        }

        return "redirect:/billToPays";
    }

    @PostMapping("/delete_billToPay")
    public String deleteBillToPay(@ModelAttribute("billToPayId") Long billToPayId, RedirectAttributes redirectAttributes) {

        try {
            billToPayService.deleteBillToPayById(billToPayId);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotDeleteBillingAccountWithRelatedSeances", null, Locale.getDefault()));
                                            //"Цей розрахунковий рахунок неможливо видалити, бо він відноситься " +
                                            //        "до існуючого сеансу (або сеансів). Його можна буде видалити " +
                                            //        "після видалення сеансів з даним рахунком, або після  " +
                                            //        //"зміни рахунку в цих сеансах на інший.");
        }


        return "redirect:/billToPays";
    }


}
