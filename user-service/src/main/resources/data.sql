begin;

insert into roles (id,name)
values
    (DEFAULT,'ADMIN'),
    (DEFAULT,'STUDENT'),
    (DEFAULT,'TEACHER'),
    (DEFAULT,'CURATOR'),
    (DEFAULT,'ENROLLEE'),
    (DEFAULT,'MODERATOR'),
    (DEFAULT,'COMMITTE')
ON CONFLICT DO NOTHING;

insert into users(id,login,name,surname,email,password)
values
    (DEFAULT,'API_ADMIN','API_ADMIN','API_ADMIN','API_ADMIN@admin.admin','$2a$10$WpQf5Ph9ByyQRa4dyvLZS.3hIzjxkb1TbWd4aSLt7rsMtyZdoLoKq'),
    (DEFAULT, 'TEACHER_1', 'TEACHER_1', 'TEACHER_1', 'TEACHER_1@teacher.teacher', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO'),
    (DEFAULT, 'TEACHER_2', 'TEACHER_2', 'TEACHER_2', 'TEACHER_2@teacher.teacher', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO'),
    (DEFAULT, 'TEACHER_3', 'TEACHER_3', 'TEACHER_3', 'TEACHER_3@teacher.teacher', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO')
ON CONFLICT DO NOTHING;

insert into users(id,login,name,surname,email,password,group_number)
values
    (DEFAULT, 'STUDENT_1', 'STUDENT_1', 'STUDENT_1', 'STUDENT_1@student.student', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO', 'Group A'),
    (DEFAULT, 'STUDENT_2', 'STUDENT_2', 'STUDENT_2', 'STUDENT_2@student.student', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO', 'Group A'),
    (DEFAULT, 'STUDENT_3', 'STUDENT_3', 'STUDENT_3', 'STUDENT_3@student.student', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO', 'Group B'),
    (DEFAULT, 'STUDENT_4', 'STUDENT_4', 'STUDENT_4', 'STUDENT_4@student.student', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO', 'Group A'),
    (DEFAULT, 'STUDENT_5', 'STUDENT_5', 'STUDENT_5', 'STUDENT_5@student.student', '$2a$12$LmxAlgfG9z/cuvLo8ZohHOjHNrSI8h5CjiZ8sZ4wKHuTihxCcKwcO', 'Group C')
ON CONFLICT DO NOTHING;

insert into user_roles (user_id, role_id)
    select u.id, r.id from users u, roles r
    where u.login = 'API_ADMIN' and r.name in ('ADMIN')
ON CONFLICT DO NOTHING ;

insert into user_roles (user_id, role_id)
    select u.id, r.id from users u, roles r
    where u.login like 'STUDENT_%' and r.name in ('STUDENT')
ON CONFLICT DO NOTHING ;

insert into user_roles (user_id, role_id)
    select u.id, r.id from users u, roles r
    where u.login like 'TEACHER_%' and r.name in ('TEACHER')
ON CONFLICT DO NOTHING ;

commit;