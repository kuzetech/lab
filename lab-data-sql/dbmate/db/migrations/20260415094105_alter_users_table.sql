-- migrate:up
alter table users
    add column if not exists note text;

-- migrate:down
alter table users
    drop column note;
