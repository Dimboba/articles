create table authors(
    id uuid primary key,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    date_created timestamp default now()
);

create table articles(
    id uuid primary key,
    author_id uuid references authors(id),
    content varchar(20000) not null,
    title varchar(100) not null,
    publish_date date not null,
    date_created timestamp default now()
);