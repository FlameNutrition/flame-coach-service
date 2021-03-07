insert into User(id, email, password)
values (1, 'test@gmail.com', '12345');
insert into Client(id, firstName, lastName, clientTypeFk, uuid, userFk)
values (1, 'Miguel', 'Teixeira', 1, '3c88690d-62bd-4055-a0ee-d1da916f70d9', 1);
commit;