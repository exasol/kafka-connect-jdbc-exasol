DROP SCHEMA country_schema CASCADE;

CREATE SCHEMA country_schema;

CREATE TABLE COUNTRY (
    ID            INTEGER IDENTITY NOT NULL,
    COUNTRY_NAME  VARCHAR(100) UTF8,
    CAPITAL_CITY  VARCHAR(2000) UTF8,
    UPDATED_AT    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

INSERT INTO COUNTRY ( country_name, capital_city ) VALUES ( 'Germany', 'Berlin' );
INSERT INTO COUNTRY ( country_name, capital_city ) VALUES ( 'Turkmenistan', 'Ashgabat' );

commit;
