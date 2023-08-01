DROP ALL OBJECTS DELETE FILES;

CREATE TABLE IF NOT EXISTS PUBLIC.USERS
(
    ID    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    NAME  CHARACTER VARYING                            NOT NULL,
    EMAIL CHARACTER VARYING UNIQUE                     NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.REQUESTS
(
    ID           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    DESCRIPTION  CHARACTER VARYING,
    REQUESTER_ID INTEGER REFERENCES PUBLIC.USERS (ID),
    CREATED      TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS PUBLIC.ITEMS
(
    ID           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    NAME         CHARACTER VARYING                            NOT NULL,
    DESCRIPTION  CHARACTER VARYING                            NOT NULL,
    IS_AVAILABLE BOOLEAN                                      NOT NULL,
    OWNER_ID     INTEGER REFERENCES PUBLIC.USERS (ID),
    REQUEST_ID   INTEGER REFERENCES PUBLIC.REQUESTS (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.STATUSES
(
    ID   IDENTITY NOT NULL PRIMARY KEY,
    NAME CHARACTER VARYING
);

CREATE TABLE IF NOT EXISTS PUBLIC.BOOKINGS
(
    ID         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    START_DATE TIMESTAMP WITHOUT TIME ZONE,
    END_DATE   TIMESTAMP WITHOUT TIME ZONE,
    ITEM_ID    INTEGER REFERENCES PUBLIC.ITEMS (ID),
    BOOKER_ID  INTEGER REFERENCES PUBLIC.USERS (ID),
    STATUS_ID  INTEGER REFERENCES PUBLIC.STATUSES (ID) --TODO: OR NAME

);

CREATE TABLE IF NOT EXISTS PUBLIC.COMMENTS
(
    ID        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    TEXT      CHARACTER VARYING,
    ITEM_ID   INTEGER REFERENCES PUBLIC.ITEMS (ID),
    AUTHOR_ID INTEGER REFERENCES PUBLIC.USERS (ID)
);
