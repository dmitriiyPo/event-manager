package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Service;

@Service
public class EventPermissionService {

    public boolean canModify(User currentAuthUser, Event event) {
        return event.userId().equals(currentAuthUser.id())
                || currentAuthUser.role() == UserRole.ADMIN;
    }

}
