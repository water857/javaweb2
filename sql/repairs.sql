create table if not exists repairs
(
    id          int auto_increment
        primary key,
    user_id     int                                          not null,
    description text                                         not null,
    status      enum ('pending', 'in_progress', 'completed') not null,
    created_at  timestamp default CURRENT_TIMESTAMP          null,
    constraint repairs_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade
);

create index user_id
    on repairs (user_id);

