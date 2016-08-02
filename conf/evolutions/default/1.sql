# users schema

# --- !Ups

CREATE TABLE users (
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL DEFAULT '',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE task_statuses (
    id INT(11) NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO task_statuses VALUES
    (null, 'draft'),
    (null, 'new'),
    (null, 'in_progress'),
    (null, 'finished'),
    (null, 'canceled');

CREATE TABLE tasks (
    id INT(11) NOT NULL AUTO_INCREMENT,
    user INT(11) NOT NULL,
    title VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    status INT(11) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user) REFERENCES users(id),
    FOREIGN KEY (status) REFERENCES task_statuses(id)
);


# --- !Downs

SET foreign_key_checks = 0;

DROP TABLE tasks;
DROP TABLE task_statuses;
DROP TABLE users;

SET foreign_key_checks = 1;