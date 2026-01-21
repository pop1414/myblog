package com.spike.blog.server.mapper;

import com.spike.blog.server.model.entity.Post;
import org.apache.ibatis.annotations.Param;

/**
 * @author DDY
 * @version 1.0
 * @date 1/16/2026-4:57 PM
 * @description com.spike.blog.server.mapper
 */
// TODO
public interface PostMapper {
    /**
     * 根据 ID 查询文章
     *
     * @param id 文章ID
     * @return 文章实体，若无则返回 null
     */
    Post selectById(@Param("id") Long id);
}
