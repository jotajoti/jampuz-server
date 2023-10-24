-- liquibase formatted sql

-- changeset frankbille:4-create-participant-table

CREATE TABLE `participant`
(
    `id`                  VARCHAR(36)  NOT NULL,
    `name`                VARCHAR(150) NOT NULL,
    `pin_code`            VARCHAR(4)   NOT NULL,
    `location_id`         VARCHAR(36)  NOT NULL,
    `admin_id`            VARCHAR(36)  NULL,
    `created_by_id`       VARCHAR(36)  NOT NULL,
    `created_date`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_modified_by_id` VARCHAR(36)  NOT NULL,
    `last_modified_date`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT unq_participant_location_name UNIQUE KEY (`name`, `location_id`),
    CONSTRAINT fk_participant_location FOREIGN KEY (`location_id`) REFERENCES location (`id`),
    CONSTRAINT fk_participant_admin FOREIGN KEY (`admin_id`) REFERENCES admin (`id`),
    CONSTRAINT fk_participant_created_by FOREIGN KEY (`created_by_id`) REFERENCES admin (`id`),
    CONSTRAINT fk_participant_last_modified_by FOREIGN KEY (`last_modified_by_id`) REFERENCES admin (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- rollback DROP TABLE IF EXISTS `participant`
