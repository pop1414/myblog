package com.spike.blog.server.model.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @author DDY
 * @version 1.0
 * @date 1/16/2026-4:29 PM
 * @description com.spike.blog.server.model.entity
 * 标签实体类 (对应数据库 tags 表)
 */
@Data
@Builder
public class TagEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 标签别名 (用于 URL，如 /tags/java)
     */
    private String slug;

    /**
     * 标签名称 (用于展示，如 Java)
     */
    private String name;

    /**
     * 创建时间
     */
    private OffsetDateTime createdAt;
}
