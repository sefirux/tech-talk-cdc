insert into accounts (id, name) values
    ('b2000000-0000-0000-0000-000000000001', 'alice'),
    ('b2000000-0000-0000-0000-000000000002', 'aurora')
on conflict (id) do nothing;
