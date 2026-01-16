package com.spike.blog.server.model.entity;

import com.spike.blog.server.common.PostContentType;
import com.spike.blog.server.common.PostStatus;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @author DDY
 * @version 1.0
 * @date 1/16/2026-3:51 PM
 * @description com.spike.blog.server.model.entity
 * 文章实体类 (对应数据库 posts 表)
 * 使用 OffsetDateTime 对应 PostgreSQL 的 TIMESTAMPTZ，
 * 这是处理带时区时间戳的最佳实践，能有效避免服务器时区不一致导致的 Bug。
 *
 */

// Lombok 注解，自动生成 Getter/Setter/ToString
@Data
@Builder

public class PostEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID (对应 BIGSERIAL)
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * URL 别名 (唯一索引)
     */
    private String slug;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 封面图 URL
     */
    private String coverUrl;

    /**
     * 文章内容 (Markdown 源码)
     */
    private String content;

    /**
     * 内容类型: MARKDOWN/PLAIN
     */
    private PostContentType contentType;

    /**
     * 文章状态: DRAFT/PUBLISHED
     */
    private PostStatus status;

    /**
     * 发布时间 (可为空，只有状态为 PUBLISHED 时才有值)
     */
    private OffsetDateTime publishedAt;

    /**
     * 软删除时间 (为空表示未删除)
     */
    private OffsetDateTime deletedAt;

    /**
     * 创建时间 (由数据库 DEFAULT NOW() 生成)
     */
    private OffsetDateTime createdAt;

    /**
     * 最后更新时间
     */
    private OffsetDateTime updatedAt;
}
