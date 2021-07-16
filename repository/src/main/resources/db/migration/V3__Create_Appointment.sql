create table Appointment
(
    id       bigint                  not null,
    currency varchar(3)              not null,
    `delete` boolean default 'FALSE' not null,
    dttm     timestamp               not null,
    price    float   default '0.0'   not null,
    clientFk bigint                  not null,
    coachFk  bigint                  not null,
    `uuid`   varchar(255)            not null,
    primary key (id)
) engine = InnoDB;

alter table Appointment
    add constraint FKsv9ppjyhej0gjm7nagj3yorxg foreign key (clientFk) references Client (id);

alter table Appointment
    add constraint FKhpj0vdv7v3pnni5y472g0vyax foreign key (coachFk) references Coach (id);

alter table Appointment
    add constraint UK_msceqwsfwrlk6o1003e8eqt4n unique (uuid);
