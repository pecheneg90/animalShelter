-- liquibase formatted sql

-- changeset pecheneg:1

CREATE TABLE animal
(
    id   SERIAL       NOT NULL,
    type VARCHAR(255) NOT NULL,
    CONSTRAINT animal_primary_key PRIMARY KEY (type)
);

INSERT INTO animal
values (1, 'CAT');

INSERT INTO animal
values (2, 'DOG');

INSERT INTO animal
values (3, 'NO_ANIMAL');

CREATE TABLE client
(
    id                      serial       NOT NULL,
    chat_id                 BIGINT       NOT NULL,
    name                    varchar(255) NOT NULL,
    phone_number            varchar(255) NOT NULL,
    email                   varchar(255) NOT NULL,
    status                  varchar(255) NOT NULL,
    animal_type             varchar(255) REFERENCES animal (type),
    start_trial_date        DATE DEFAULT NULL,
    end_trial_date          DATE DEFAULT NULL,
    CONSTRAINT user_primary_key PRIMARY KEY (id)
);

CREATE TABLE reporting
(
    id          serial       NOT NULL,
    id_user     bigint       not null references client (id),
    report_text TEXT         NOT NULL,
    file_path   TEXT         NOT NULL,
    file_size   BIGINT       NOT NULL,
    preview     OID,
    sent_date   DATE,
    status      varchar(255) NOT NULL DEFAULT 'DECLINED',
    CONSTRAINT report_primary_key PRIMARY KEY (id)
);