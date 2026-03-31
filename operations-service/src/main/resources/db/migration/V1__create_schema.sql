create extension if not exists "uuid-ossp";

create table if not exists accounts
(
    id         uuid primary key                  default uuid_generate_v4(),
    name       text                     not null unique,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone

);

create table if not exists assets
(
    id         uuid primary key                  default uuid_generate_v4(),
    name       text                     not null unique,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone
);

create table if not exists operations
(
    id         uuid primary key                                     default uuid_generate_v4(),
    account_id uuid                                        not null references accounts (id),
    asset_id   uuid                                        not null references assets (id),
    side       varchar(4) check ( side in ('BUY', 'SELL')) not null,
    quantity   decimal                                     not null,
    price      decimal                                     not null,
    status     text                                        not null,
    created_at timestamp with time zone                    not null default now(),
    updated_at timestamp with time zone
);