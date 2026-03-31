insert into assets (id, name)
values ('7014650b-d3d3-401e-97a1-552fe2d75ec0', 'AAPL'),
       ('0bf59811-82b0-4779-b9d3-d45fbe3fe3c3', 'TSLA'),
       ('bad86794-7730-4b79-874d-8c794df4ed2c', 'GOOGL'),
       ('8ad8f2c9-430e-4ad9-a02c-c0205c3952b0', 'MSFT'),
       ('ae460328-51de-470e-ac48-f575a5a40b47', 'AMZN'),
       ('e6a891a6-b9e8-44ef-9746-0b4d2f95375b', 'NVDA'),
       ('07261f3e-2447-41a5-84e2-87811e535b2f', 'BTC'),
       ('a1ca6a63-ef25-4dd8-9037-c55265269efe', 'ETH')
on conflict (name) do nothing;
