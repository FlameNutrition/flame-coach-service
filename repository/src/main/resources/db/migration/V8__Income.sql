create table Income
(
    id     bigint                         not null,
    price  float        default '0.0'     not null,
    status varchar(100) default 'PENDING' not null,
    primary key (id)
) engine = InnoDB;

alter table Appointment
    add column incomeFk bigint;

alter table Appointment
    drop column price;

alter table Appointment
    add constraint FKtk4jsmla8axdcpumam997fkpe foreign key (incomeFk) references Income (id);
