select * from user_info where id=12;

insert into erp.user_info
  (id, login_name, nick_name, user_level, birthday, gender, create_time, operate_time, status)
VALUES
  (12, 'kuze', 'kuze', '1', '1998-03-12', 'M', '2024-01-05 09:12:31', '2024-06-11 14:20:00', 'active');