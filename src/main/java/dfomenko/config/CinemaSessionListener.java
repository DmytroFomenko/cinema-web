package dfomenko.config;


import dfomenko.entity.LoginData;
import dfomenko.service.RoleService;
import dfomenko.service.TicketService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static dfomenko.controller.LoginDataController.CLIENT_ROLE_NAME;

@Setter
@AllArgsConstructor
@Component
public class CinemaSessionListener implements HttpSessionListener {

    private final RoleService roleService;
    private final TicketService ticketService;

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.getSession();
        LoginData loginData = (LoginData) session.getAttribute("loggedUser");
        if (loginData != null) {
            // For client only - cleanup reserves from client tickets
            if (Objects.equals(loginData.getRoleId(), roleService.findRoleByRole(CLIENT_ROLE_NAME).getId())) {
                ticketService.resetAllReservedTicketStatus(loginData.getId());
            }
        }
    }

}


