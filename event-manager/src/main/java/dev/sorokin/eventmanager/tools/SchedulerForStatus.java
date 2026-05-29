package dev.sorokin.eventmanager.tools;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SchedulerForStatus {

    private final EventRepository eventRepository;

    @Autowired
    public SchedulerForStatus(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event.status.cron}")
    @Transactional
    public void updateStatuses() {
        List<EventEntity> startedEvent = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START.name());

        if (!startedEvent.isEmpty()) {
            List<Long> ids = startedEvent.stream().map(EventEntity::getId).toList();
            eventRepository.changeStatus(ids, EventStatus.STARTED.name());
        }


        List<EventEntity> finished = eventRepository.findFinishedEventsWithStatus(EventStatus.STARTED.name());

        if (!finished.isEmpty()) {
            List<Long> ids = finished.stream().map(EventEntity::getId).toList();
            eventRepository.changeStatus(ids, EventStatus.FINISHED.name());
        }
    }

}
