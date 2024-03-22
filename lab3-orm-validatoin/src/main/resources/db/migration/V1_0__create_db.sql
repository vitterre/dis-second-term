create table country
(
    id   integer,
    name varchar(255),
    constraint country_pk primary key (id)
);

create table author
(
    id         integer,
    name       varchar(255),
    country_id integer,
    constraint author_pk primary key (id)
);