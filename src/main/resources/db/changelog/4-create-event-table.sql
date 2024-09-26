-- liquibase formatted sql

-- changeset frankbille:4-create-event-table

CREATE TABLE `event`
(
    `id`                 VARCHAR(36)       NOT NULL,
    `location_id`        VARCHAR(36)       NOT NULL,
    `code`               VARCHAR(6)        NOT NULL,
    `year`               SMALLINT UNSIGNED NOT NULL,
    `created_date`       TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_modified_date` TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT unq_event_code_year UNIQUE KEY (`code`, `year`),
    CONSTRAINT fk_event_location FOREIGN KEY (`location_id`) REFERENCES location (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- rollback DROP TABLE IF EXISTS `event`
