package com.spike.blog.server.model.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author DDY
 * @version 1.0
 * @date 1/16/2026-4:30 PM
 * @description com.spike.blog.server.model.entity
 */
@Data
@Builder
public class PostTag implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章 ID (复合主键之一)
     */
    private Long postId;

    /**
     * 标签 ID (复合主键之一)
     */
    private Long tagId;
}
