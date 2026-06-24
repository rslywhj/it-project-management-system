package com.pm.notification.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.notification.domain.SysNotification;
import com.pm.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "站内消息通知")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "通知列表", description = "获取当前用户的通知列表")
    public Result<PageResult<SysNotification>> listNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isRead) {
        return Result.ok(notificationService.listNotifications(page, size, type, isRead));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "未读数量", description = "获取未读通知数量")
    public Result<Map<String, Integer>> getUnreadCount() {
        int count = notificationService.getUnreadCount();
        return Result.ok(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "标记已读", description = "标记单条通知为已读")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return Result.ok();
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部已读", description = "标记所有通知为已读")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return Result.ok();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "删除通知", description = "删除一条通知")
    public Result<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return Result.ok();
    }
}
