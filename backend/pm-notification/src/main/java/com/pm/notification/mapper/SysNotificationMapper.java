package com.pm.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.notification.domain.SysNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 通知 Mapper
 */
@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {

    /**
     * 统计用户未读通知数量
     */
    @Select("SELECT COUNT(*) FROM sys_notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(@Param("userId") Long userId);
}
