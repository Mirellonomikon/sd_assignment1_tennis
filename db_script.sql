create table users
(
    id                             serial
        primary key,
    username                       varchar(255)                                  not null
        unique,
    name                           varchar(255)                                  not null,
    password                       varchar(255)                                  not null,
    user_type                      varchar(255)                                  not null
        constraint users_user_type_check
            check ((user_type)::text = ANY
                   ((ARRAY ['player'::character varying, 'referee'::character varying, 'administrator'::character varying])::text[])),
    tournament_register            boolean     default false                     not null,
    email                          varchar(255)                                  not null,
    tournament_registration_status varchar(20) default 'NONE'::character varying not null
);

alter table users
    owner to postgres;

create table matches
(
    id            serial
        primary key,
    name          varchar(255) not null,
    match_date    date         not null,
    match_time    time         not null,
    location      varchar(255) not null,
    referee_id    integer
                               references users
                                   on delete set null,
    player1_id    integer
                               references users
                                   on delete set null,
    player2_id    integer
                               references users
                                   on delete set null,
    player1_score integer,
    player2_score integer
);

alter table matches
    owner to postgres;

