-- liquibase formatted sql

-- changeset frankbille:3-create-location_owner-table

CREATE TABLE `location_owner`
(
    `location_id` VARCHAR(36) NOT NULL,
    `admin_id`    VARCHAR(36) NOT NULL,
    PRIMARY KEY (`location_id`, `admin_id`),
    CONSTRAINT fk_location_owner_location FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
    CONSTRAINT fk_location_owner_admin FOREIGN KEY (`admin_id`) REFERENCES `admin` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- rollback DROP TABLE IF EXISTS `location_owner`
