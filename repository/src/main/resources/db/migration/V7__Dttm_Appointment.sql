alter table Appointment
    rename column dttm to dttmStarts;

alter table Appointment
    add column dttmEnds timestamp not null;

