package com.absher.absherapp.notification.infrastructure;
import com.absher.absherapp.notification.application.*; import com.absher.absherapp.notification.application.port.out.NotificationStore; import com.absher.absherapp.request.application.PassportRequestActorAccess; import java.time.Clock; import org.springframework.context.annotation.*;
@Configuration public class NotificationConfiguration { @Bean NotificationService notificationService(NotificationStore s,PassportRequestActorAccess a,Clock c){return new NotificationService(s,a,c);} }
