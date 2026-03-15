create extension if not exists "uuid-ossp";

create table if not exists positions
(
    id            uuid primary key                  default uuid_generate_v4(),
    account_id    uuid                     not null,
    asset_id      uuid                     not null,
    quantity      decimal                  not null check (quantity >= 0),
    average_price decimal                  not null check (average_price >= 0),
    created_at    timestamp with time zone not null default now(),
    updated_at    timestamp with time zone,
    unique (account_id, asset_id)
);
