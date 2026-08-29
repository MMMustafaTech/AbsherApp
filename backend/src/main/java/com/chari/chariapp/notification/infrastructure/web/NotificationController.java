package com.chari.chariapp.notification.infrastructure.web;
import com.chari.chariapp.account.domain.AccountId; import com.chari.chariapp.notification.application.NotificationService; import com.chari.chariapp.notification.domain.UserNotification; import java.util.*; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.security.oauth2.jwt.Jwt; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/me/notifications") public class NotificationController {
 private final NotificationService notifications; public NotificationController(NotificationService notifications){this.notifications=notifications;}
 @GetMapping public List<UserNotification> mine(@AuthenticationPrincipal Jwt jwt){return notifications.mine(accountId(jwt));}
 @GetMapping("/unread-count") public UnreadCount unreadCount(@AuthenticationPrincipal Jwt jwt){return new UnreadCount(notifications.unreadCount(accountId(jwt)));}
 @PostMapping("/{notificationId}/read") public UserNotification markRead(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID notificationId){return notifications.markRead(accountId(jwt),notificationId);}
 @PostMapping("/read-all") public ReadAllResult markAllRead(@AuthenticationPrincipal Jwt jwt){return new ReadAllResult(notifications.markAllRead(accountId(jwt)));}
 private AccountId accountId(Jwt jwt){return new AccountId(UUID.fromString(jwt.getSubject()));}
 public record UnreadCount(long count){} public record ReadAllResult(int updatedCount){}
}
