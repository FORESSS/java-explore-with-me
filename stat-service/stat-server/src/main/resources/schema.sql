create table if not exists statistics (
  id integer generated always as identity primary key,
  app varchar(128) not null,
  uri varchar(128) not null,
  ip varchar(16) not null,
  timestamp timestamp not null
);