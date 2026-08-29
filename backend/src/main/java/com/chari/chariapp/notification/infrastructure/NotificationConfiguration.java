package com.chari.chariapp.notification.infrastructure;
import com.chari.chariapp.notification.application.*; import com.chari.chariapp.notification.application.port.out.NotificationStore; import com.chari.chariapp.request.application.PassportRequestActorAccess; import java.time.Clock; import org.springframework.context.annotation.*;
@Configuration public class NotificationConfiguration { @Bean NotificationService notificationService(NotificationStore s,PassportRequestActorAccess a,Clock c){return new NotificationService(s,a,c);} }
