CREATE TABLE messages (id bigint not null auto_increment primary key, message varchar(50) not null);
CREATE TABLE users (id bigint not null auto_increment primary key, name varchar(50), lastname varchar(50), username varchar(50) not null, userId varchar(50) not null, messageId bigint);
