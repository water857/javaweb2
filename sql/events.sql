create table if not exists events
(
    id          int auto_increment
        primary key,
    event_name  varchar(255) not null,
    event_date  timestamp    not null,
    description text         null,
    created_by  int          not null,
    constraint events_ibfk_1
        foreign key (created_by) references users (id)
            on delete cascade
);

create index created_by
    on events (created_by);

