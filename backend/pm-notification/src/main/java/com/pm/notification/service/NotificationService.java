package com.pm.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.notification.domain.SysNotification;
import com.pm.notification.mapper.SysNotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SysNotificationMapper notificationMapper;

    /**
     * 获取当前用户的通知列表
     */
    public PageResult<SysNotification> listNotifications(int page, int size, String type, Boolean isRead) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotification::getUserId, userId);
        wrapper.eq(StringUtils.hasText(type), SysNotification::getType, type);
        wrapper.eq(isRead != null, SysNotification::getIsRead, isRead ? 1 : 0);
        wrapper.orderByDesc(SysNotification::getCreatedAt);

        Page<SysNotification> result = notificationMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    /**
     * 获取未读通知数量
     */
    public int getUnreadCount() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return 0;
        }
        return notificationMapper.countUnread(userId);
    }

    /**
     * 标记单条通知为已读
     */
    public void markAsRead(Long notificationId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysNotification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(1060, "通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        if (notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadAt(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    /**
     * 标记所有通知为已读
     */
    public void markAllAsRead() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        List<SysNotification> unreadList = notificationMapper.selectList(
                new LambdaQueryWrapper<SysNotification>()
                        .eq(SysNotification::getUserId, userId)
                        .eq(SysNotification::getIsRead, 0));

        LocalDateTime now = LocalDateTime.now();
        for (SysNotification notification : unreadList) {
            notification.setIsRead(1);
            notification.setReadAt(now);
            notificationMapper.updateById(notification);
        }
    }

    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysNotification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(1060, "通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        notificationMapper.deleteById(notificationId);
    }

    /**
     * 发送通知（内部调用）
     */
    public void sendNotification(Long userId, String title, String content, String type,
                                  String relatedEntityType, Long relatedEntityId) {
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
        log.info("Sent notification to user {}: {}", userId, title);
    }

    /**
     * 批量发送通知（内部调用）
     */
    public void sendBatchNotification(List<Long> userIds, String title, String content, String type,
                                       String relatedEntityType, Long relatedEntityId) {
        for (Long userId : userIds) {
            sendNotification(userId, title, content, type, relatedEntityType, relatedEntityId);
        }
    }
}
