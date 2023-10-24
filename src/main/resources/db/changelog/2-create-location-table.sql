-- liquibase formatted sql

-- changeset frankbille:2-create-location-table

CREATE TABLE `location`
(
    `id`                  VARCHAR(36)       NOT NULL,
    `name`                VARCHAR(150)      NOT NULL,
    `code`                VARCHAR(6)        NOT NULL,
    `year`                SMALLINT UNSIGNED NOT NULL,
    `created_by_id`       VARCHAR(36)       NOT NULL,
    `created_date`        TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_modified_by_id` VARCHAR(36)       NOT NULL,
    `last_modified_date`  TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT unq_location_code_year UNIQUE KEY (`code`, `year`),
    CONSTRAINT fk_location_created_by FOREIGN KEY (`created_by_id`) REFERENCES admin (`id`),
    CONSTRAINT fk_location_last_modified_by FOREIGN KEY (`last_modified_by_id`) REFERENCES admin (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- rollback DROP TABLE IF EXISTS `location`
