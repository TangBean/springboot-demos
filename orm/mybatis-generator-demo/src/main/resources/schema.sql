create table t_coffee
(
    id          bigint not null auto_increment,
    name        varchar(255),
    price       bigint not null,
    create_time timestamp,
    update_time timestamp,
    primary key (id)
);

-- data
insert into t_coffee (name, price, create_time, update_time) values ('espresso-0', 2000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('latte-0', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('capuccino-0', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('mocha-0', 3000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('macchiato-0', 3000, now(), now());

insert into t_coffee (name, price, create_time, update_time) values ('espresso-1', 2000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('latte-1', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('capuccino-1', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('mocha-1', 3000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('macchiato-1', 3000, now(), now());

insert into t_coffee (name, price, create_time, update_time) values ('espresso-2', 2000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('latte-2', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('capuccino-2', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('mocha-2', 3000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('macchiato-2', 3000, now(), now());

insert into t_coffee (name, price, create_time, update_time) values ('espresso-3', 2000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('latte-3', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('capuccino-3', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('mocha-3', 3000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('macchiato-3', 3000, now(), now());

insert into t_coffee (name, price, create_time, update_time) values ('espresso-4', 2000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('latte-4', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('capuccino-4', 2500, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('mocha-4', 3000, now(), now());
insert into t_coffee (name, price, create_time, update_time) values ('macchiato-4', 3000, now(), now());