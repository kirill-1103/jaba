begin;


INSERT INTO professor_id (professor_id)
VALUES (1),
       (2),
       (3)
ON CONFLICT DO NOTHING;

INSERT INTO study_groups (id, name)
VALUES (DEFAULT, 'Group A'),
       (DEFAULT, 'Group B'),
       (DEFAULT, 'Group C')
ON CONFLICT DO NOTHING;



INSERT INTO subject (id, name, creator_id)
VALUES (DEFAULT, 'Subject A', 5),
       (DEFAULT, 'Subject B', 6),
       (DEFAULT, 'Subject C', 7)
ON CONFLICT DO NOTHING;


INSERT INTO schedule (id, date_time, group_id, subject_id, class_format, auditorium, link_for_the_class,
                      professor_professor_id)
VALUES

        (DEFAULT, '2023-10-23 13:00:00',
            (select id from study_groups where name = 'Group A'),
            (select id from subject where name = 'Subject A'),
        'IN_PERSON', 'Room A101', 'https://example.com/class1', 1),

       (DEFAULT, '2023-10-24 14:00:00',
        (select id from study_groups where name = 'Group A'),
        (select id from subject where name = 'Subject A'),
        'DISTANT', 'Online', 'https://example.com/class2', 2),

       (DEFAULT, '2023-10-25 15:00:00',
        (select id from study_groups where name = 'Group B'),
        (select id from subject where name = 'Subject A'),
        'IN_PERSON', 'Room B202', 'https://example.com/class3', 3),

       (DEFAULT, '2023-10-26 15:00:00',
        (select id from study_groups where name = 'Group A'),
        (select id from subject where name = 'Subject B'),
        'IN_PERSON', 'Room B202', 'https://example.com/class3', 1)

ON CONFLICT DO NOTHING;



INSERT INTO attendance (id, student_id, schedule_id, attendance_status)
VALUES (DEFAULT, 1,
        (select id from schedule where date_time = '2023-10-23 13:00:00' ),
        'ATTENDED'),

       (DEFAULT, 1,
        (select id from schedule where date_time = '2023-10-23 13:00:00' ),
        'ATTENDED'),

       (DEFAULT, 2,
        (select id from schedule where date_time = '2023-10-23 13:00:00' ),
        'MISSED'),

       (DEFAULT, 2,
        (select id from schedule where date_time = '2023-10-24 14:00:00'),
        'ATTENDED'),

       (DEFAULT, 3,
        (select id from schedule where date_time = '2023-10-24 14:00:00' ),
        'ATTENDED'),

       (DEFAULT, 3,
        (select id from schedule where date_time = '2023-10-24 14:00:00'),
        'MISSED'),

       (DEFAULT, 4,
        (select id from schedule where date_time = '2023-10-23 13:00:00' ),
        'ATTENDED'),

       (DEFAULT, 4,
        (select id from schedule where date_time = '2023-10-23 13:00:00' ),
        'ATTENDED'),

       (DEFAULT, 5,
        (select id from schedule where date_time = '2023-10-26 15:00:00' ),
        'MISSED'),

       (DEFAULT, 5,
        (select id from schedule where date_time = '2023-10-26 15:00:00' ),
        'MISSED')

ON CONFLICT DO NOTHING;

INSERT INTO study_materials (id, type, subject_id, materials_text)
VALUES (DEFAULT, 'MATERIAL',
        (select id from subject where name='Subject A'),
        'Lecture notes for subject A'),

       (DEFAULT, 'TASK',
        (select id from subject where name='Subject A'),
        'Homework assignment for subject B'),

       (DEFAULT, 'MATERIAL',
        (select id from subject where name='Subject B'),
        'Study materials for subject C')

ON CONFLICT DO NOTHING;

INSERT INTO homework (id, student_id, date, grade, comment, task_id)
VALUES (DEFAULT, 1, '2023-10-21 08:00:00', 90, 'Good work!',
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 1, '2023-10-23 08:00:00', null, null,
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 1, '2023-10-23 08:00:00', null, null,
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 2, '2023-10-22 08:00:00', 89, 'Well done!',
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 2, '2023-10-22 08:00:00', null, null,
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 2, '2023-10-22 08:00:00', 85, 'Well done!',
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 3, '2023-10-23 08:00:00', 92, 'Excellent!',
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 3, '2023-10-23 08:00:00', 69, 'Not Bad.',
        (select id from study_materials where materials_text = 'Homework assignment for subject B')),

       (DEFAULT, 3, '2023-10-23 08:00:00', 87, 'Excellent!',
        (select id from study_materials where materials_text = 'Homework assignment for subject B'))
ON CONFLICT DO NOTHING;

end;
