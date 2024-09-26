-- liquibase formatted sql

-- changeset frankbille:2-create-location-table

CREATE TABLE `location`
(
    `id`                 VARCHAR(36)  NOT NULL,
    `name`               VARCHAR(150) NOT NULL,
    `created_date`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_modified_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- rollback DROP TABLE IF EXISTS `location`
