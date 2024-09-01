package dfomenko.controller;


import dfomenko.config.UiConfig;
import dfomenko.entity.LoginData;
import dfomenko.entity.Role;
import dfomenko.service.EmailService;
import dfomenko.service.LoginDataService;
import dfomenko.service.RoleService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


@Controller
@AllArgsConstructor
public class LoginDataController {

    private final LoginDataService loginDataService;
    private final RoleService roleService;
    private final EmailService emailService;
    private final UiConfig uiConfig;
    private final ApplicationContext applicationContext;


    //-----------
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextLogoutHandler logoutHandler =
            new SecurityContextLogoutHandler();
    //--------------

    public final static String ADMIN_ROLE_NAME = "admin";
    public final static String CLIENT_ROLE_NAME = "client";
    private MessageSource messageSource;


    @GetMapping("/login_user_form")
    public String LoginUserForm(@ModelAttribute("loginData") LoginData enteredLoginData,
                                @ModelAttribute("error") String error,
                                Model model) {

        model.addAttribute("error", error.isEmpty() ? null : error);
        return "login_user_form";
    }


    @PostMapping("/login_user")
    public String LoginUser(@ModelAttribute("loginData") LoginData enteredLoginData,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        boolean logOk = loginDataService.checkLoginData(enteredLoginData);

        LoginData loginData;
        if (logOk) {
            removeSettingsFromSession(session);
            loginData = loginDataService.findLoginDataByNickname(enteredLoginData.getNickname());
            setLoginDataToSession(session, loginData);
            model.addAttribute("loggedUser", loginData);
        } else {
            removeLoginDataFromSession(session);
            redirectAttributes.addFlashAttribute("loginData", enteredLoginData);
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageInvalidCredentials", null,  Locale.getDefault()));
                                            //"Неправильний нікнейм чи пароль");
            return "redirect:/login_user_form";
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleService.getRoleById(loginData.getRoleId()).getRole()));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(loginData.getNickname(), "password", authorities);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
        setSessionTimeoutForRole(session);

        return "redirect:/";
    }

    @GetMapping("/logout_user")
    public String LogoutUser(HttpSession session,
                             Model model,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        LoginData loginData = (LoginData) session.getAttribute("loggedUser");
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleService.getRoleById(loginData.getRoleId()).getRole()));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(loginData.getNickname(), "password", authorities);
        authentication.setAuthenticated(false);
        this.logoutHandler.setClearAuthentication(true);
        this.logoutHandler.setInvalidateHttpSession(true);
        this.logoutHandler.logout(request, response, authentication);
        
        model.addAttribute("loggedUser", null);

        return "redirect:/";
    }


    @GetMapping("/signup_client_form")
    public String SignUpClientForm(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                   @ModelAttribute("confirmCodeText") String confirmCodeText,
                                   @ModelAttribute("error") String error,
                                   Model model) {

        model.addAttribute("error", error.isEmpty() ? null : error);

        return "signup_client_form";
    }


    @PostMapping("/signup_client")
    public String SignUpClient(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                               @ModelAttribute("confirmCodeText") String confirmCodeText,
                               @ModelAttribute("password2") String password2,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        String errorStr = "";
        String email = enteredLoginData.getEmail();
        if (!email.equals(session.getAttribute("emailToSendingCode"))) {
            // Is emails the same
            errorStr = messageSource.getMessage("errorMessageConfirmationCodeSentToOtherEmail", null,  Locale.getDefault());
            //"Код підтвердження був висланий на інший e-mail. Повторіть підтвердження.";
            session.removeAttribute("sentCodeText");
        } else if (!confirmCodeText.equals(session.getAttribute("sentCodeText"))) {
            // Check code
            errorStr = messageSource.getMessage("errorMessageIncorrectConfirmationCodeEmail", null,  Locale.getDefault());
            //"Неправильний код підтвердження e-mail!!!";
        } else if (loginDataService.existsLoginDataByNickname(enteredLoginData.getNickname())) {
            // Check if the username already exists in the DB
            errorStr = messageSource.getMessage("errorMessageUsernameAlreadyTaken", null,  Locale.getDefault());
            //"Такий нікнейм вже зайнятий. Введіть, будь ласка, інший";
        } else if (!Pattern.matches(uiConfig.getPasswordRegPattern(), enteredLoginData.getPassword())) {
            // Check if the password correct
            errorStr = messageSource.getMessage("errorMessagePasswordDoesNotMeetRequirements", null,  Locale.getDefault());
            //"Пароль не відповідає вимогам. Введіть, будь ласка, інший";
        } else if (!password2.equals(enteredLoginData.getPassword())) {
            // Check if passwords are same
            errorStr = messageSource.getMessage("errorMessagePasswordsDoNotMatch", null,  Locale.getDefault());
            //"Паролі не співпадають. Введіть, будь ласка, повторно";
        }


        if (!errorStr.isEmpty()) {
            // Save inputs on the form
            redirectAttributes.addAttribute("error", errorStr);
            redirectAttributes.addAttribute("confirmCodeText", confirmCodeText);
            redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);
            return "redirect:/signup_client_form";
        }

        Role role = roleService.findRoleByRole(CLIENT_ROLE_NAME);
        enteredLoginData.setRoleId(role.getId());
        enteredLoginData.setEmail(email);

        String nickname = enteredLoginData.getNickname();
        loginDataService.saveLoginData(enteredLoginData);
        setLoginDataToSession(session, loginDataService.findLoginDataByNickname(nickname));
        session.removeAttribute("emailToSendingCode");
        session.removeAttribute("sentCodeText");
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(nickname, "password", authorities);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        setSessionTimeoutForRole(session);

        return "redirect:/";
    }


    @PostMapping("/send_client_email_code")
    public String sendClientEmail(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {

        redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);

        String errorStr = "";
        String email = enteredLoginData.getEmail();

        if (loginDataService.existsLoginDataByEmail(email)) {
            errorStr = messageSource.getMessage("errorMessageEmailAlreadyRegisteredAsUser", null,  Locale.getDefault());
                    // "На цей e-mail вже зареєстровано користувача. Введіть, будь ласка, інший";
        } else if (!Pattern.matches(uiConfig.getEmailClientPattern(), email)) {
            errorStr = messageSource.getMessage("errorMessageInvalidEmail", null,  Locale.getDefault());
                    //"Це некоректний e-mail. Введіть, будь ласка, коректний";
        }

        if (!errorStr.isEmpty()) {
            redirectAttributes.addAttribute("error", errorStr);
            return "redirect:/signup_client_form";
        }

        SecureRandom secureRandom = new SecureRandom();
        String code = String.valueOf(secureRandom.nextInt(9999 - 1000) + 1000);

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        try {
            emailService.sendTemplatedMessage(email,
                                              applicationContext.getMessage("confirmCodeEmailSubjectText", null, Locale.getDefault()),
                                              "mail/mail_user_code",
                                              null,
                                              params);
        } catch (MessagingException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCodeSendingError", null,  Locale.getDefault()));
                                            //"Сталася помилка при відправці коду. Спробуйте ще раз.");
        }

        session.setAttribute("emailToSendingCode", email);
        session.setAttribute("sentCodeText", code);

        return "redirect:/signup_client_form";
    }


    @GetMapping("/signup_admin_form")
    public String SignUpAdminForm(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                  @ModelAttribute("confirmCodeText") String confirmCodeText,
                                  @ModelAttribute("error") String error,
                                  Model model) {

        model.addAttribute("error", error.isEmpty() ? null : error);

        return "signup_admin_form";
    }


    @PostMapping("/signup_admin")
    public String SignUpAdmin(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                              @ModelAttribute("confirmCodeText") String confirmCodeText,
                              @ModelAttribute("password2") String password2,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        // TODO: 07.06.2023 move messages to properties
        String errorStr = "";
        String email = enteredLoginData.getEmail();
        if (!email.equals(session.getAttribute("emailToSendingCode"))) {
            // Is emails the same
            errorStr = "Код підтвердження був висланий на інший e-mail. Повторіть підтвердження.";
            session.removeAttribute("sentCodeText");
        } else if (!confirmCodeText.equals(session.getAttribute("sentCodeText"))) {
            // Check code
            errorStr = "Неправильний код підтвердження e-mail!!!";
        } else if (loginDataService.existsLoginDataByNickname(enteredLoginData.getNickname())) {
            // Check if the username already exists in the DB
            errorStr = "Такий нікнейм вже зайнятий. Введіть, будь ласка, інший";
        } else if (!Pattern.matches(uiConfig.getPasswordRegPattern(), enteredLoginData.getPassword())) {
            // Check if the password correct
            errorStr = "Пароль не відповідає вимогам. Введіть, будь ласка, інший";
        } else if (!password2.equals(enteredLoginData.getPassword())) {
            // Check if passwords are same
            errorStr = "Паролі не співпадають. Введіть, будь ласка, повторно";
        }


        if (!errorStr.isEmpty()) {
            // Save inputs on the form
            redirectAttributes.addAttribute("error", errorStr);
            redirectAttributes.addAttribute("confirmCodeText", confirmCodeText);
            redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);
            return "redirect:/signup_admin_form";
        }

        Role role = roleService.findRoleByRole(ADMIN_ROLE_NAME);
        enteredLoginData.setRoleId(role.getId());
        enteredLoginData.setEmail(email);
        enteredLoginData.setNickname(email);

        String nickname = enteredLoginData.getNickname();
        loginDataService.saveLoginData(enteredLoginData);
        setLoginDataToSession(session, loginDataService.findLoginDataByNickname(nickname));
        session.removeAttribute("emailToSendingCode");
        session.removeAttribute("sentCodeText");
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(nickname, "password", authorities);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        setSessionTimeoutForRole(session);

        return "redirect:/";
    }


    @PostMapping("/send_admin_email_code")
    public String sendAdminEmail(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {

        redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);

        String errorStr = "";
        String email = enteredLoginData.getEmail();

        if (loginDataService.existsLoginDataByEmail(email)) {
            errorStr = "На цей e-mail вже зареєстровано адміністратора. Введіть, будь ласка, інший";
        } else if (!Pattern.matches(uiConfig.getEmailAdminPattern(), email)) {
            errorStr = messageSource.getMessage("errorMessageNotAdministratorEmail", null,  Locale.getDefault());
                    //"Це не e-mail адміністратора. Введіть, будь ласка, коректний";
        }

        if (!errorStr.isEmpty()) {
            redirectAttributes.addAttribute("error", errorStr);
            return "redirect:/signup_admin_form";
        }

        SecureRandom secureRandom = new SecureRandom();
        String code = String.valueOf(secureRandom.nextInt(9999 - 1000) + 1000);


        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        try {
            emailService.sendTemplatedMessage(email,
                                              applicationContext.getMessage("confirmCodeEmailSubjectText", null, Locale.getDefault()),
                                              "mail/mail_user_code",
                                              null,
                                              params);
        } catch (MessagingException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCodeSendingError", null,  Locale.getDefault()));
                                            //"Сталася помилка при відправці коду. Спробуйте ще раз.");
        }


        session.setAttribute("emailToSendingCode", email);
        session.setAttribute("sentCodeText", code);

        return "redirect:/signup_admin_form";
    }


    @GetMapping("/recover_access_form")
    public String RecoverAccessForm(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                    @ModelAttribute("password2") String password2,
                                    @ModelAttribute("confirmCodeText") String confirmCodeText,
                                    @ModelAttribute("error") String error,
                                    Model model) {

        model.addAttribute("error", error.isEmpty() ? null : error);

        if (enteredLoginData.getId() == null && enteredLoginData.getEmail() != null) {
            LoginData loginData = loginDataService.findLoginDataByEmail(enteredLoginData.getEmail());
            model.addAttribute("enteredLoginData", Objects.requireNonNullElse(loginData, enteredLoginData));
        } else {
            model.addAttribute("enteredLoginData", enteredLoginData);
        }

        return "recover_access_form";
    }


    @PostMapping("/recover_access")
    public String recoverAccess(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                @ModelAttribute("password2") String password2,
                                @ModelAttribute("confirmCodeText") String confirmCodeText,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                HttpServletRequest request,
                                HttpServletResponse response) {


        LoginData existingLoginData = loginDataService.findLoginDataByEmail(enteredLoginData.getEmail());

        String errorStr = "";
        String email = enteredLoginData.getEmail();
        if (!email.equals(session.getAttribute("emailToSendingCode"))) {
            // Is emails the same
            errorStr = messageSource.getMessage("errorMessageConfirmationCodeSentToOtherEmail", null,  Locale.getDefault());
            //"Код підтвердження був висланий на інший e-mail. Повторіть підтвердження.";
            session.removeAttribute("sentCodeText");
        } else if (!confirmCodeText.equals(session.getAttribute("sentCodeText"))) {
            // Check code
            errorStr = messageSource.getMessage("errorMessageIncorrectConfirmationCodeEmail", null,  Locale.getDefault());
            //"Неправильний код підтвердження e-mail!!!";
        } else if (!Pattern.matches(uiConfig.getPasswordRegPattern(), enteredLoginData.getPassword())) {
            // Check if the password correct
            errorStr = messageSource.getMessage("errorMessagePasswordDoesNotMeetRequirements", null,  Locale.getDefault());
            //"Пароль не відповідає вимогам. Введіть, будь ласка, інший";
        } else if (!password2.equals(enteredLoginData.getPassword())) {
            // Check if passwords are same
            errorStr = messageSource.getMessage("errorMessagePasswordsDoNotMatch", null,  Locale.getDefault());
            //"Паролі не співпадають. Введіть, будь ласка, повторно";
        }

        if (!errorStr.isEmpty()) {
            // Save inputs on the form
            redirectAttributes.addAttribute("error", errorStr);
            redirectAttributes.addAttribute("confirmCodeText", confirmCodeText);
            redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);
            return "redirect:/recover_access_form";
        }

        existingLoginData.setPassword(enteredLoginData.getPassword());

        try {
            loginDataService.updateLoginData(existingLoginData);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageSaveAccountDataError", null,  Locale.getDefault()));
                                            //"Виникла помилка при збереженні нових даних акаунту");
            redirectAttributes.addAttribute("confirmCodeText", confirmCodeText);
            redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);
            return "redirect:/recover_access_form";
        }

        setLoginDataToSession(session, existingLoginData);
        session.removeAttribute("emailToSendingCode");
        session.removeAttribute("sentCodeText");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleService.getRoleById(existingLoginData.getRoleId()).getRole()));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(existingLoginData.getNickname(), "password", authorities);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        setSessionTimeoutForRole(session);

        return "redirect:/";
    }


    @PostMapping("/send_recover_email_code")
    public String sendRecoverEmail(@ModelAttribute("enteredLoginData") LoginData enteredLoginData,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {

        redirectAttributes.addFlashAttribute("enteredLoginData", enteredLoginData);

        String errorStr = "";
        String email = enteredLoginData.getEmail();

        if (!loginDataService.existsLoginDataByEmail(email)) {
            errorStr = messageSource.getMessage("errorMessageAccountNotRegisteredWithEmail", null,  Locale.getDefault());
                    //"На цей e-mail НЕ зареєстровано акаунт. Перевірте, будь ласка, правильність введеного e-mail";
        }

        if (!errorStr.isEmpty()) {
            redirectAttributes.addAttribute("error", errorStr);
            return "redirect:/recover_access_form";
        }

        SecureRandom secureRandom = new SecureRandom();
        String code = String.valueOf(secureRandom.nextInt(9999 - 1000) + 1000);

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        try {
            emailService.sendTemplatedMessage(email,
                                              applicationContext.getMessage("confirmCodeEmailSubjectText", null, Locale.getDefault()),
                                              "mail/mail_user_code",
                                              null,
                                              params);
        } catch (MessagingException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCodeSendingError", null,  Locale.getDefault()));
                                            //"Сталася помилка при відправці коду. Спробуйте ще раз.");
        }

        session.setAttribute("emailToSendingCode", email);
        session.setAttribute("sentCodeText", code);

        return "redirect:/recover_access_form";
    }


    // Это приватный метод только для использования внутри этого контроллера, поэтому он не в сервисе
    private void setLoginDataToSession(HttpSession session, LoginData loggedUser) {

        if (loggedUser != null) {
            session.setAttribute("loggedUser", loggedUser);
//            session.setAttribute("isUserLogged", true);

            Role role = roleService.findRoleByRole(ADMIN_ROLE_NAME);
            if (role != null) {
                boolean isAdminRole = loggedUser.getRoleId().equals(role.getId());
                session.setAttribute("isAdminRole", isAdminRole);
            } else {
                throw new RuntimeException("Error: method \"setLoginDataToSession\", role \"" + ADMIN_ROLE_NAME + "\" is not exists in database");
            }

            role = roleService.findRoleByRole(CLIENT_ROLE_NAME);
            if (role != null) {
                boolean isClientRole = loggedUser.getRoleId().equals(role.getId());
                session.setAttribute("isClientRole", isClientRole);
            } else {
                throw new RuntimeException("Error: method \"setLoginDataToSession\", role \"" + CLIENT_ROLE_NAME + "\" is not exists in database");
            }
        } else {
            removeLoginDataFromSession(session);
        }
    }


    // Это приватный метод только для использования внутри этого контроллера, поэтому он не в сервисе
    private void removeLoginDataFromSession(HttpSession session) {
        session.removeAttribute("loggedUser");
        session.removeAttribute("isAdminRole");
        session.removeAttribute("isClientRole");

        removeSettingsFromSession(session);
    }

    // Это приватный метод только для использования внутри этого контроллера, поэтому он не в сервисе
    private void removeSettingsFromSession(HttpSession session) {
        //Remove sorting etc. settings
        session.removeAttribute("seancesSortType");
        session.removeAttribute("seancesShowActualOnly");
        session.removeAttribute("seancesShowWithFreePlacesOnly");
        session.removeAttribute("seancesShowForDate");

        session.removeAttribute("ticketsSortType");
        session.removeAttribute("ticketsShowActualOnly");
        session.removeAttribute("ticketsShowForDate");
    }

    private void setSessionTimeoutForRole(HttpSession session) {
        LoginData loginData = (LoginData) session.getAttribute("loggedUser");
        if (loginData != null) {
            // For client only - cleanup reserves from client tickets
            if ((boolean) session.getAttribute("isClientRole")) {
                session.setMaxInactiveInterval(uiConfig.getSessionClientTimeout());
            } else {
                // For Admins, etc. - set up session timeout only
                session.setMaxInactiveInterval(uiConfig.getSessionAdminTimeout());
            }
        }
    }



}
