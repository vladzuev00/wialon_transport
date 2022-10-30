CREATE TABLE users
(
    id                 SERIAL       NOT NULL PRIMARY KEY,
    email              VARCHAR(256) NOT NULL,
    encrypted_password VARCHAR(256) NOT NULL,
    role               VARCHAR(16)  NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT correct_email CHECK (email ~ '[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+');

ALTER TABLE users
    ADD CONSTRAINT correct_role CHECK (role IN ('USER', 'ADMIN'));

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

ALTER TABLE trackers
    ADD CONSTRAINT correct_imei CHECK (imei ~ '[0-9]{20}');

ALTER TABLE trackers
    ADD CONSTRAINT correct_phone_number CHECK (phone_number ~ '[0-9]{7}');

CREATE TABLE data
(
    id                     BIGSERIAL NOT NULL PRIMARY KEY,
    date                   DATE      NOT NULL,
    time                   TIME      NOT NULL,

    latitude_degrees       INTEGER   NOT NULL,
    latitude_minutes       INTEGER   NOT NULL,
    latitude_minute_share  INTEGER   NOT NULL,
    latitude_type          CHAR(1)   NOT NULL,

    longitude_degrees      INTEGER   NOT NULL,
    longitude_minutes      INTEGER   NOT NULL,
    longitude_minute_share INTEGER   NOT NULL,
    longitude_type         CHAR(1)   NOT NULL,

    speed                  INTEGER   NOT NULL,
    course                 INTEGER   NOT NULL,
    height                 INTEGER   NOT NULL,
    amount_of_satellites   INTEGER   NOT NULL,
    tracker_id             INTEGER   NOT NULL
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
    id                  BIGINT       NOT NULL PRIMARY KEY,
    reduction_precision DECIMAL      NOT NULL,
    inputs              INTEGER      NOT NULL,
    outputs             INTEGER      NOT NULL,
    analog_inputs       VARCHAR(512) NOT NULL,
    driver_key_code     VARCHAR(256) NOT NULL
);

ALTER TABLE extended_data
    ADD CONSTRAINT fk_extended_data_to_data FOREIGN KEY (id) REFERENCES data (id)
        ON DELETE CASCADE;

CREATE TABLE parameters
(
    id               BIGSERIAL    NOT NULL PRIMARY KEY,
    name             VARCHAR(256) NOT NULL,
    type             VARCHAR(64)  NOT NULL,
    value            VARCHAR(256) NOT NULL,
    extended_data_id BIGINT       NOT NULL
);

ALTER TABLE parameters
    ADD CONSTRAINT fk_parameters_to_extended_data
        FOREIGN KEY (extended_data_id) REFERENCES extended_data (id)
            ON DELETE CASCADE;

ALTER TABLE parameters
    ADD CONSTRAINT correct_name CHECK (char_length(name) != 0);

ALTER TABLE parameters
    ADD CONSTRAINT correct_type CHECK (type IN ('INTEGER', 'DOUBLE', 'STRING'));

CREATE TABLE commands
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    message    VARCHAR(512) NOT NULL,
    tracker_id INTEGER      NOT NULL
);

ALTER TABLE commands
    ADD CONSTRAINT fk_commands_to_trackers FOREIGN KEY (tracker_id) REFERENCES trackers (id)
        ON DELETE CASCADE;

ALTER TABLE commands
    ADD CONSTRAINT correct_message CHECK (char_length(message) != 0);

CREATE TABLE outbound_commands
(
    id                  BIGINT      NOT NULL PRIMARY KEY,
    status              VARCHAR(64) NOT NULL,
    response_command_id BIGINT
);

ALTER TABLE outbound_commands
    ADD CONSTRAINT fk_outbound_commands_to_commands
        FOREIGN KEY (id) REFERENCES commands (id);

ALTER TABLE outbound_commands
    ADD CONSTRAINT fk2_outbound_commands_to_commands
        FOREIGN KEY (response_command_id) REFERENCES commands (id);

ALTER TABLE outbound_commands
    ADD CONSTRAINT unique_response_command_id UNIQUE (response_command_id);

ALTER TABLE outbound_commands
    ADD CONSTRAINT correct_status CHECK (status IN
                                         ('NEW', 'SENT', 'SUCCESS_ANSWERED', 'ERROR_ANSWERED', 'TIMEOUT_NOT_ANSWERED'));



