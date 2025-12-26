create table if not exists property_payments
(
    id           int auto_increment
        primary key,
    user_id      int                                 not null,
    amount       decimal(10, 2)                      not null,
    payment_date timestamp default CURRENT_TIMESTAMP null,
    constraint property_payments_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade
);

create index user_id
    on property_payments (user_id);

