package com.absher.absherapp.notification.application.port.out;

import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.notification.domain.UserNotification;
import java.util.*;
public interface NotificationStore { UserNotification save(UserNotification notification); Optional<UserNotification> findById(UUID id); List<UserNotification> findByCitizenId(CitizenId citizenId); List<UserNotification> findUnreadByCitizenId(CitizenId citizenId); long countUnreadByCitizenId(CitizenId citizenId); }
