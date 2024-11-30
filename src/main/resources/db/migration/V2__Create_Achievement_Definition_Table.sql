CREATE TABLE achievement_definition
(
    id                    BIGINT PRIMARY KEY,
    name                  VARCHAR(255) NOT NULL,
    description           TEXT,
    threshold_bronze      INTEGER      NOT NULL,
    threshold_silver      INTEGER      NOT NULL,
    threshold_gold        INTEGER      NOT NULL,
    achievement_type      VARCHAR(255) NOT NULL,
    achievement_parameter VARCHAR(255) NOT NULL
);