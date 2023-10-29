-- liquibase formatted sql

-- changeset frankbille:1-create-admin-table

CREATE TABLE `admin`
(
    `id`            VARCHAR(36)  NOT NULL,
    `name`          VARCHAR(150) NOT NULL,
    `email`         VARCHAR(150) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `created_date`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- rollback DROP TABLE IF EXISTS `admin`
