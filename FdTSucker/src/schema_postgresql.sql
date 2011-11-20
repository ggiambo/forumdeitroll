--
-- PostgreSQL database dump
--


--
-- authors
-- 
CREATE TABLE authors (
    nick character(64) NOT NULL,
    ranking integer NOT NULL,
    messages integer NOT NULL,
    avatar bytea
);

ALTER TABLE ONLY authors ADD CONSTRAINT authors_nick_key UNIQUE (nick);

ALTER TABLE public.authors OWNER TO fdtsucker;


--
-- messages
--
CREATE TABLE messages (
    id integer NOT NULL,
    text text NOT NULL,
    date timestamp without time zone NOT NULL,
    subject character(256) NOT NULL,
    threadid integer NOT NULL,
    parentid integer NOT NULL,
    author character(64),
    forum character(128),
    text_search tsvector
);


ALTER TABLE ONLY messages ADD CONSTRAINT messages_pkey PRIMARY KEY (id);

CREATE INDEX parentid_idx ON messages USING btree (parentid);

CREATE INDEX threadid_idx ON messages USING btree (threadid);

CREATE INDEX text_search_idx ON messages USING gin (text_search);

CREATE TRIGGER text_search BEFORE INSERT ON messages FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger('text_search', 'pg_catalog.italian', 'text');

ALTER TABLE public.messages OWNER TO fdtsucker;



