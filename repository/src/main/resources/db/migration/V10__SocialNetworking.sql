create table Client_Looking_For_Coach
(
    id          bigint               not null,
    description varchar(255),
    isEnable    tinyint(1) default 0 not null,
    primary key (id)
) engine = InnoDB;

alter table Client
    add column clientLookingForCoachFk bigint not null;

drop procedure if exists `UpdateLookingForCoach`;

create procedure UpdateLookingForCoach()
begin

    declare finished integer default 0;
    declare clientId bigint default 0;
    declare lookingForClientNextId bigint;

    -- declare cursor for clients
    declare clients_cursor
        cursor for
        SELECT id FROM Client;

    -- declare NOT FOUND handler
    declare continue handler for not found set finished = 1;

    open clients_cursor;

    updateLookingForCoach:
    loop

        fetch clients_cursor into clientId;

        -- leave
        if finished = 1 then
            leave updateLookingForCoach;
        end if;

        select tbl.next_val
        FROM hibernate_sequence tbl FOR
        UPDATE
        INTO lookingForClientNextId;

        insert into Client_Looking_For_Coach (id, description, isEnable)
        VALUES (lookingForClientNextId, 'I\'m looking for a coach! Please contact me.', 0);

        update Client
        set clientLookingForCoachFk = lookingForClientNextId
        where id = clientId;

        update hibernate_sequence
        set next_val = next_val + 1
        where next_val = next_val;

    end loop updateLookingForCoach;
    close clients_cursor;

end;

call UpdateLookingForCoach();

alter table Client
    add constraint UK_qwcmf50hybeckwix1tkik6wu5 unique (clientLookingForCoachFk);
alter table Client
    add constraint FKn22jxvx8tmnpj1yaqydrv0puk foreign key (clientLookingForCoachFk) references Client_Looking_For_Coach (id);



