CREATE TABLE users
(
    id                 SERIAL       NOT NULL PRIMARY KEY,
    email              VARCHAR(256) NOT NULL,
    encrypted_password VARCHAR(256) NOT NULL,
    role               VARCHAR(16)  NOT NULL
);

CREATE TABLE trackers
(
    id                 SERIAL       NOT NULL PRIMARY KEY,
    imei               CHAR(20)     NOT NULL,
    encrypted_password VARCHAR(256) NOT NULL,
    phone_number       CHAR(9)      NOT NULL,
    user_id            INTEGER      NOT NULL
);

ALTER TABLE trackers
    ADD CONSTRAINT fk_trackers_to_users FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE;

CREATE TABLE data
(
    id                     SERIAL  NOT NULL PRIMARY KEY,
    date                   DATE    NOT NULL,
    time                   TIME    NOT NULL,

    latitude_degrees       INTEGER NOT NULL,
    latitude_minutes       INTEGER NOT NULL,
    latitude_minute_share  INTEGER NOT NULL,
    latitude_type          CHAR(1) NOT NULL,

    longitude_degrees      INTEGER NOT NULL,
    longitude_minutes      INTEGER NOT NULL,
    longitude_minute_share INTEGER NOT NULL,
    longitude_type         CHAR(1) NOT NULL,

    speed                  INTEGER NOT NULL,
    course                 INTEGER NOT NULL,
    height                 INTEGER NOT NULL,
    amount_of_satellites   INTEGER NOT NULL,
    tracker_id             INTEGER NOT NULL
);

ALTER TABLE data
    ADD CONSTRAINT fk_data_to_trackers FOREIGN KEY (tracker_id) REFERENCES trackers (id)
        ON DELETE CASCADE;

ALTER TABLE data
    ADD CONSTRAINT latitude_type_should_be_correct
        CHECK (data.latitude_type IN ('N', 'S'));

ALTER TABLE data
    ADD CONSTRAINT longitude_type_should_be_correct
        CHECK (data.longitude_type IN ('E', 'W'));

CREATE TABLE extended_data
(
    id                  INTEGER      NOT NULL PRIMARY KEY,
    reduction_precision DECIMAL      NOT NULL,
    inputs              INTEGER      NOT NULL,
    outputs             INTEGER      NOT NULL,
    analog_inputs       VARCHAR(512) NOT NULL,
    driver_key_code     VARCHAR(256) NOT NULL,
    parameters          TEXT         NOT NULL
);

ALTER TABLE extended_data
    ADD CONSTRAINT fk_extended_data_to_data FOREIGN KEY (id) REFERENCES data (id)
        ON DELETE CASCADE;


