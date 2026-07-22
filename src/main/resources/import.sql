


INSERT INTO community_article (title, content, user_id, created_at, updated_at) VALUES ('제목1', '내용1', '1', NOW(), NOW());
INSERT INTO community_article (title, content, user_id, created_at, updated_at) VALUES ('제목2', '내용2', '2', NOW(), NOW());
INSERT INTO community_article (title, content, user_id, created_at, updated_at) VALUES ('제목3', '내용3', '3', NOW(), NOW());
INSERT INTO comments (communityArticle_id, user_id, content, created_at) VALUES (1, 4, '댓글1', NOW());
INSERT INTO comments (communityArticle_id, user_id, content, created_at) VALUES (1, 5, '댓글2', NOW());