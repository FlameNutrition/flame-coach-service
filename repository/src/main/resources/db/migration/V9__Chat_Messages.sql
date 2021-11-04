create table Message
(
    id       bigint       not null,
    uuid     varchar(255) NOT NULL,
    owner    varchar(100) not null,
    time     datetime(6)  not null,
    content  varchar(255) NOT NULL,
    clientFk bigint       not null,
    coachFk  bigint       not null,
    primary key (id)
) engine = InnoDB;

create table `Message_Seq`
(
    `next_val` bigint
) engine = InnoDB;

insert into `Message_Seq`
values (1);

alter table Message
    add constraint FKhpg5lh8jdejgpjvdmu0r6gmoa foreign key (clientFk) references Client (id);
alter table Message
    add constraint FK8njb5qsfoksytc2fg6ic8kcwr foreign key (coachFk) references Coach (id);
alter table Message
    add constraint UK_rkn7klepgs9e0qgfyhi2fdoha unique (uuid);