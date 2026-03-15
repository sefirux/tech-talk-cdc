insert into assets (id, name)
values (uuid_generate_v4(), 'AAPL'),
       (uuid_generate_v4(), 'TSLA'),
       (uuid_generate_v4(), 'GOOGL'),
       (uuid_generate_v4(), 'MSFT'),
       (uuid_generate_v4(), 'AMZN'),
       (uuid_generate_v4(), 'NVDA'),
       (uuid_generate_v4(), 'BTC'),
       (uuid_generate_v4(), 'ETH')
on conflict (name) do nothing;
