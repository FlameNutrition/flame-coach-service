alter table Appointment
add column identifier varchar(255) not null;

alter table Appointment add constraint UK_n3me5gxv0r2ttq6nbptvkvvc2 unique (identifier);
