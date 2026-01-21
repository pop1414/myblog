package com.spike.blog.server.mapper;

import com.spike.blog.server.common.PostContentType;
import com.spike.blog.server.common.PostStatus;
import com.spike.blog.server.config.MyBatisConfig;
import com.spike.blog.server.model.entity.Post;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 基于真实数据库数据的简单 Mapper 测试。
 * 数据行示例：
 * 1,Hello World,hello-world,,,这是我的第一篇 **Markdown** 博客。,MARKDOWN,PUBLISHED,2026-01-16 07:27:14.352779 +00:00,,2026-01-16 07:27:14.352779 +00:00,2026-01-16 07:27:14.352779 +00:00
 */
@MybatisTest
@Import(MyBatisConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        // 如需让 Flyway 校验本地库，可移除此行
        "spring.flyway.enabled=false"
})
class PostMapperTests {

    @Autowired
    private PostMapper postMapper;

    @Test
    void shouldFetchSeedPostById() {
        Long existingId = 1L; // 使用你库里的实际 ID；此处对应示例数据

        Post post = postMapper.selectPostById(existingId);

        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(existingId);
        assertThat(post.getTitle()).isEqualTo("Hello World");
        assertThat(post.getSlug()).isEqualTo("hello-world");
        assertThat(post.getSummary()).isNull();
        assertThat(post.getCoverUrl()).isNull();
        assertThat(post.getContent()).isEqualTo("这是我的第一篇 **Markdown** 博客。");
        assertThat(post.getContentType()).isEqualTo(PostContentType.MARKDOWN);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);

        OffsetDateTime expectedTime = OffsetDateTime.parse("2026-01-16T07:27:14.352779Z");
        assertThat(post.getPublishedAt()).isEqualTo(expectedTime);
        assertThat(post.getDeletedAt()).isNull();
        assertThat(post.getCreatedAt()).isEqualTo(expectedTime);
        assertThat(post.getUpdatedAt()).isEqualTo(expectedTime);
    }

    @Test
    void shouldListPostsAndContainSeedPost() {
        List<Post> posts = postMapper.selectAllPosts();

        assertThat(posts).isNotEmpty();
        Post post = posts.stream()
                .filter(p -> p.getId().equals(1L))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未在列表中找到 ID=1 的种子数据"));

        assertThat(post.getTitle()).isEqualTo("Hello World");
        assertThat(post.getSlug()).isEqualTo("hello-world");
    }

    @Test
    void shouldReturnNullWhenIdMissing() {
        Long missingId = 999_999L; // 根据需要调整

        Post post = postMapper.selectPostById(missingId);

        assertThat(post).isNull();
    }
}
