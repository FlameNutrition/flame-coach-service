alter table Appointment
rename column identifier to uuid;

alter table Appointment drop constraint UK_n3me5gxv0r2ttq6nbptvkvvc2;
alter table Appointment add constraint UK_n3me5gxv0r2ttq6nbptvkvvc2 unique (uuid);
