-- 1. 创建文章表 posts
CREATE TABLE posts
(
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    slug         VARCHAR(200) UNIQUE,                      -- URL别名
    summary      TEXT,
    cover_url    TEXT,
    content      TEXT         NOT NULL,
    content_type VARCHAR(20)  NOT NULL DEFAULT 'MARKDOWN', -- MARKDOWN/PLAIN
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',    -- DRAFT/PUBLISHED

    published_at TIMESTAMPTZ,                              -- 带时区的时间戳
    deleted_at   TIMESTAMPTZ,                              --软删除
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 索引建议：加速公开列表查询 (只查已发布且未删除的) published降序
CREATE INDEX idx_posts_status_pub ON posts (status, published_at DESC);

-- 索引建议：加速软删除过滤(筛选未删除的文章，或者恢复已删除的文章)
CREATE INDEX idx_posts_deleted_at ON posts (deleted_at);

-- 2. 创建标签表 tags
CREATE TABLE tags
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(64) NOT NULL,
    slug       VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. 创建文章-标签关联表 post_tags (多对多中间表)
CREATE TABLE post_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,

    -- 联合主键：防止同一篇文章重复打同一个标签
    PRIMARY KEY (post_id, tag_id),

    -- 外键约束：保证数据一致性（删文章/删标签时级联处理）
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

-- 索引建议：反向查询（查某个标签下有哪些文章）
-- 联合主键自动生成索引，但是遵循最左匹配原则。所以单独（反向）查询tag_id的时候索引失效
CREATE INDEX idx_post_tags_tag_id ON post_tags (tag_id);


-- 4. 插入少量测试数据 (Seed Data)
-- 这一步是为了让你等会儿 API 跑通时能立刻看到数据，而不是空数组
INSERT INTO posts (title, slug, content, status, published_at)
VALUES ('Hello World', 'hello-world', '这是我的第一篇 **Markdown** 博客。', 'PUBLISHED', NOW()),
       ('Docker 部署指南', 'docker-guide', 'Docker Compose 非常好用...', 'PUBLISHED', NOW() - INTERVAL '1 day');

INSERT INTO tags (name, slug)
VALUES ('Java', 'java'),
       ('DevOps', 'devops');

-- 关联: Hello World -> Java
INSERT INTO post_tags (post_id, tag_id)
VALUES (1, 1);

