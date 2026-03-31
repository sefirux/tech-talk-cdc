create extension if not exists "uuid-ossp";

-- Mirror of the operations table from operations_db.
-- Structure matches what the JDBC Sink connector (sync-sink-connector) writes.
-- Having the table pre-created ensures the service starts cleanly regardless of
-- whether the sink connector has run yet.
create table if not exists operations
(
    id         uuid primary key,
    account_id uuid                     not null,
    asset_id   uuid                     not null,
    side       text                     not null,
    quantity   decimal                  not null,
    price      decimal                  not null,
    status     text                     not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone
);
