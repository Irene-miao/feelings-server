
use heroku_48f502cb3943a0e;

create table users (
    user_id char(8) not null unique,
    username varchar(55) not null,
    user_password varchar(255) not null,
    email varchar(55) not null,
    avatar_img mediumblob not null,
    img_type varchar(32) not null,
    reset_password_token varchar(255),

    primary key(user_id)  
);

