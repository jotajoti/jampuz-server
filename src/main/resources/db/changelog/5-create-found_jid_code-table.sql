-- liquibase formatted sql

-- changeset frankbille:5-create-found_jid_code-table

CREATE TABLE `found_jid_code`
(
    `id`             VARCHAR(36) NOT NULL,
    `participant_id` VARCHAR(36) NOT NULL,
    `code`           CHAR(6)     NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT fk_found_jid_code_participant FOREIGN KEY (`participant_id`) REFERENCES participant (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- rollback DROP TABLE IF EXISTS `found_jid_code`
