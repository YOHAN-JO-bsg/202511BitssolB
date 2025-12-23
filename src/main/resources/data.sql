-- -- ⚙️ 기존 데이터 초기화 (중복 방지용)
-- DELETE FROM comments;
-- DELETE FROM board;
-- DELETE FROM users;

-- ✅ 테스트용 사용자 데이터
--INSERT INTO users (id, user_name, password)
--VALUES (user_seq, 'testuser1', 'password123');
--
--INSERT INTO users (id, user_name, password)
--VALUES (user_seq, 'testuser2', 'password123');

-- 태그 데이터 추가
INSERT INTO TAG (tag_id, name) VALUES (TAG_SEQ.NEXTVAL, '일상');
INSERT INTO TAG (tag_id, name) VALUES (TAG_SEQ.NEXTVAL, '장마');
INSERT INTO TAG (tag_id, name) VALUES (TAG_SEQ.NEXTVAL, '뇌우');
INSERT INTO TAG (tag_id, name) VALUES (TAG_SEQ.NEXTVAL, '모닥불');
INSERT INTO TAG (tag_id, name) VALUES (TAG_SEQ.NEXTVAL, '텐트');
INSERT INTO TAG (tag_id, name) VALUES (TAG_SEQ.NEXTVAL, '숲속');