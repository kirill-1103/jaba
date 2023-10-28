begin;


DROP TABLE IF EXISTS news CASCADE;

DROP SEQUENCE IF EXISTS news_sequence;

CREATE SEQUENCE IF NOT EXISTS  news_sequence START 1;

CREATE TABLE IF NOT EXISTS news (
    id BIGINT DEFAULT nextval('news_sequence') PRIMARY KEY,
    creation_date TIMESTAMP(6) NOT NULL,
    header VARCHAR(255) NOT NULL,
    text varchar(100000) NOT NULL,
    hashtags TEXT[],
    imgs TEXT[]
);

end;