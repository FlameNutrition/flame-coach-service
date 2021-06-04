create table Registration_Invite
(
    id              bigint       not null,
    acceptedDttm    datetime(6),
    registrationKey varchar(255) not null,
    sendDttm        datetime(6)  not null,
    sendTo          varchar(255) not null,
    coachFk         bigint       not null,
    primary key (id)
) engine = InnoDB;

alter table Registration_Invite add constraint UK_kfddj715kk3xyy7iwjbbdmnka unique (registrationKey);

alter table Registration_Invite add constraint FKs7hg7oea0sdbski2h05ctssjp foreign key (coachFk) references Coach (id);
