create table if not exists property_services
(
    id           int auto_increment
        primary key,
    service_name varchar(255)                        not null,
    description  text                                null,
    price        decimal(10, 2)                      null,
    status       enum ('active', 'inactive')         not null,
    created_at   timestamp default CURRENT_TIMESTAMP null
);

