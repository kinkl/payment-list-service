-- Run this script for each database

CREATE SEQUENCE public.payments_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE public.payments_id_seq
    OWNER TO postgres;

CREATE TABLE public.payments
(
    id integer NOT NULL,
    sender_id integer NOT NULL,
    recipient_id integer NOT NULL,
    amount integer NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.payments
    OWNER to postgres;