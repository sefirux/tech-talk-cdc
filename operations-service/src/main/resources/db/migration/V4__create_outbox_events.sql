-- The outbox table is the key to solving the dual-write problem.
-- Instead of writing to the DB and publishing to Kafka as two separate operations
-- (which can't be made atomic), we write to the DB and insert an outbox record
-- within a single database transaction.
--
-- A separate relay process (poller or CDC) then reads unprocessed events from this
-- table and publishes them to Kafka. Once published, the event is marked as processed.
--
-- This guarantees at-least-once delivery: if the relay crashes after publishing but
-- before marking the event as processed, it will re-publish on restart. Consumers
-- must be idempotent to handle duplicates.
create table if not exists outbox_events
(
    id           uuid primary key         default uuid_generate_v4(),
    aggregate_id uuid            not null,
    event_type   text            not null,
    payload      text            not null,
    created_at   timestamp with time zone not null default now(),
    processed_at timestamp with time zone
);

-- Index to efficiently poll for pending (unprocessed) events.
create index if not exists idx_outbox_events_pending
    on outbox_events (created_at)
    where processed_at is null;
