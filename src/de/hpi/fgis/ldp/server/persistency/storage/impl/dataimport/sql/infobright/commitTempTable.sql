-- commit tmp table
-- %1$s schema

COMMIT;

SELECT * FROM import_tmp_init ORDER BY subject INTO OUTFILE './%1$s/import_tmp_init_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

DELETE FROM import_tmp_init;

-- load data into table
LOAD DATA INFILE './%1$s/import_tmp_init_csv.CSV' INTO TABLE import_tmp FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %1$s.import_tmp_init_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.import_tmp_init_csv;

COMMIT;