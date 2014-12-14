-- finish tmp table
-- %1$s max size of subject
-- %2$s max size of predicate
-- %3$s max size of object
-- %4$s max size of pattern
-- %5$s max size of normalized_pattern
-- %6$s schema
	
SELECT * FROM import_tmp_init ORDER BY subject INTO OUTFILE './%6$s/import_tmp_init_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- load data into table
LOAD DATA INFILE './%6$s/import_tmp_init_csv.CSV' INTO TABLE import_tmp FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %6$s.import_tmp_init_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %6$s.import_tmp_init_csv;
DROP TABLE import_tmp_init;