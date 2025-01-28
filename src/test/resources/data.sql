-- roles
delete from role;
INSERT INTO role(id, name, created_on, is_deleted) VALUES('b536c6a7-d848-4126-bf33-0029112b5d44', 'ROLE_ADMIN', CURRENT_TIMESTAMP, false);
INSERT INTO role(id, name, created_on, is_deleted) VALUES('22d219b5-65c6-4f1f-94ae-696c94974f9b', 'ROLE_MODERATOR', CURRENT_TIMESTAMP, false);
INSERT INTO role(id, name, created_on, is_deleted) VALUES('54465407-21a5-4514-980d-32f95b8194b2', 'ROLE_USER', CURRENT_TIMESTAMP, false);
INSERT INTO role(id, name, created_on, is_deleted) VALUES('a6a587c5-9595-4892-8294-a06e6fa4b27b', 'ROLE_ANONYMOUS', CURRENT_TIMESTAMP, false);

-- users
delete from manager_user;
INSERT INTO manager_user(id, username, password, created_on, is_deleted) VALUES('c9f00788-9b3a-4ca6-8114-ed240566303e', 'root', '$2a$12$TtitqMlAHvqQbp0qIndT6OLykqbzgILWWXEjRQP/RkyQPqsfv1p/W', CURRENT_TIMESTAMP, false);
INSERT INTO manager_user(id, username, password, created_on, is_deleted) VALUES('22d219b5-65c6-4f1f-94ae-696c94974f9b', 'gambit', '$2a$12$dIth.hWaHTZhnjPms.E7D.PouMltTQQZ7UQV7hzNozMaX5NhNG5aq', CURRENT_TIMESTAMP, false);


-- user_roles
INSERT INTO user_roles(user_id, role_id) VALUES ('c9f00788-9b3a-4ca6-8114-ed240566303e','b536c6a7-d848-4126-bf33-0029112b5d44');
INSERT INTO user_roles(user_id, role_id) VALUES ('22d219b5-65c6-4f1f-94ae-696c94974f9b', '54465407-21a5-4514-980d-32f95b8194b2');
