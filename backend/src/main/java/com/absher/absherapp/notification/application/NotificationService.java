package com.absher.absherapp.notification.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.notification.application.port.out.NotificationStore;
import com.absher.absherapp.notification.domain.*;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import java.time.*;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

public class NotificationService {
    private final NotificationStore store; private final PassportRequestActorAccess access; private final Clock clock;
    public NotificationService(NotificationStore store, PassportRequestActorAccess access, Clock clock) { this.store=store; this.access=access; this.clock=clock; }
    public void publish(CitizenId citizenId, NotificationType type, String title, String message) { store.save(UserNotification.create(citizenId,type,title,message,Instant.now(clock))); }
    public List<UserNotification> mine(AccountId actor) { return store.findByCitizenId(access.requireActiveCitizen(actor)); }
    public long unreadCount(AccountId actor) { return store.countUnreadByCitizenId(access.requireActiveCitizen(actor)); }
    @Transactional public UserNotification markRead(AccountId actor, UUID id) { CitizenId citizen=access.requireActiveCitizen(actor); UserNotification notification=store.findById(id).orElseThrow(()->new NotFoundException("Notification not found")); if(!notification.citizenId().equals(citizen)) throw new NotFoundException("Notification not found"); return store.save(notification.markRead(Instant.now(clock))); }
    @Transactional public int markAllRead(AccountId actor) { CitizenId citizen=access.requireActiveCitizen(actor); Instant now=Instant.now(clock); List<UserNotification> unread=store.findUnreadByCitizenId(citizen); unread.forEach(notification->store.save(notification.markRead(now))); return unread.size(); }
}
