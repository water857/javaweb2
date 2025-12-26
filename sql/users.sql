create table if not exists users
(
    id         int auto_increment
        primary key,
    username   varchar(255)                          not null,
    password   varchar(255)                          not null,
    role       enum ('resident', 'admin', 'service') not null,
    created_at timestamp default CURRENT_TIMESTAMP   null,
    constraint username
        unique (username)
);

