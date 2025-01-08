--roles
INSERT INTO role(id, name) VALUES('b536c6a7-d848-4126-bf33-0029112b5d44','ROLE_ADMIN');
INSERT INTO role(id, name) VALUES('a6a587c5-9595-4892-8294-a06e6fa4b27b','ROLE_ANONYMOUS');
INSERT INTO role(id, name) VALUES('22d219b5-65c6-4f1f-94ae-696c94974f9b','ROLE_MODERATOR');
INSERT INTO role(id, name) VALUES('54465407-21a5-4514-980d-32f95b8194b2','ROLE_USER');

--users
delete manager_user where 1 = 1;
--INSERT INTO manager_user(id, username, password) VALUES('54465407-21a5-4514-980d-32f95b8194b2', 'testuser', 'password');
