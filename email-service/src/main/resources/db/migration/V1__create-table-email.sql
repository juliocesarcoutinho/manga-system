CREATE TABLE tb_emails
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    email_from    VARCHAR(255) NOT NULL,
    email_to      VARCHAR(255) NOT NULL,
    email_cc      VARCHAR(255) NULL,
    email_bcc     VARCHAR(255) NULL,
    subject       VARCHAR(255) NOT NULL,
    text          TEXT         NOT NULL,
    status        VARCHAR(255) NOT NULL,
    error_message TEXT NULL,
    send_date     datetime     NOT NULL,
    CONSTRAINT pk_emails PRIMARY KEY (id)
);