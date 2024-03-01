insert into usr(full_name, date_of_birth, email) values
('USER 1', '2000-01-01', 'user@user.u'),
('USER 2', '1990-12-01', 'qwe@user.u');

insert into campus(name, address, parking_spaces) values
('POSIX', '666605, Ивановская область, город Зарайск, пр. Бухарестская, 78', 100),
('INTEX', '385509, Кировская область, город Люберцы, проезд Космонавтов, 12', 50);

insert into room(number, type, capacity, campus_id) values
('101', 'Лабораторная', 15, 1),
('102-1', 'Лекционная', 50, 2),
('205', 'Лекционная', 70, 2),
('101', 'Лекционная', 40, 1),
('520-a', 'Лабораторная', 10, 1);

insert into reservation(date_of_reserv, start_time, end_time, comment, user_id) values
('2024-03-01', '12:00:00', '15:30:00', '', 1),
('2024-03-01', '11:00:00', '12:00:00', 'Нужно оборудывание', 2),
('2024-03-02', '18:00:00', '19:00:00', '', 1);

insert into room_in_reservation(reservation_id, room_id) values
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(3, 1);
