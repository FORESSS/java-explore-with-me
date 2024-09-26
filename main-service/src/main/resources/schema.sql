drop table if exists users, categories, location, events, requests, compilations, compilations_events;

create table if not exists users (
  id bigint generated always as identity primary key,
  email varchar(30) not null unique,
  name varchar(50) not null
);

create table if not exists categories (
  id bigint generated always as identity primary key,
  name varchar(30) not null
);

create table if not exists location (
  id  bigint generated always as identity primary key,
  lat float not null,
  lon float not null
);

create table if not exists events (
  id bigint generated always as identity primary key,
  annotation varchar(255),
  category_id bigint references categories (id) on delete cascade on update cascade,
  created_on timestamp not null,
  description varchar(255) not null,
  event_date timestamp not null,
  initiator_id bigint references users (id) on delete cascade on update cascade,
  location_id bigint references location (id) on delete cascade on update cascade,
  paid boolean not null,
  participant_limit integer not null,
  published_on timestamp,
  request_moderation boolean   not null,
  state varchar(30) not null,
  title varchar(30) not null,
  confirmed_requests integer
);

create table if not exists requests (
  id bigint generated always as identity primary key,
  created timestamp not null,
  event_id bigint references events (id) on delete cascade on update cascade,
  requester_id bigint references users (id) on delete cascade on update cascade,
  status varchar(20) not null
);

create table if not exists compilations (
  id bigint generated always as identity primary key,
  pinned boolean      not null,
  title  varchar(30) not null
);

create table if not exists compilations_events (
  compilation_id bigint references compilations (id),
  event_id bigint references events (id)
);