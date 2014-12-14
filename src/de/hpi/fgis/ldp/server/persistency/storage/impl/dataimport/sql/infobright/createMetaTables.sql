-- create meta tables
-- %1$s max size of subject
-- %2$s max size of predicate
-- %3$s max size of object
-- %4$s max size of pattern
-- %5$s max size of normalized_pattern
-- %6$s schema


-- subjecttable filling
-- persist data to csv
CREATE TABLE subjectview_tmp ENGINE = MYISAM AS SELECT MIN(tuple_id) AS id, subject, COUNT(*) AS cnt FROM import_tmp GROUP BY subject;
SELECT * FROM subjectview_tmp INTO OUTFILE './%6$s/subject_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- load data into table
CREATE TABLE subjecttable (id INT SIGNED NOT NULL, subject VARCHAR(%1$s), cnt INT SIGNED NOT NULL) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%6$s/subject_tmp_csv.CSV' INTO TABLE subjecttable FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %6$s.subject_tmp_csv (TMP INT SIGNED NOT NULL) ENGINE = CSV;
DROP TABLE %6$s.subject_tmp_csv;
DROP TABLE subjectview_tmp;

-- predicatetable filling
-- persist data to csv
CREATE TABLE predicateview_tmp ENGINE = MYISAM AS SELECT MIN(tuple_id) AS id, predicate, COUNT(*) AS cnt FROM import_tmp GROUP BY predicate;
SELECT * FROM predicateview_tmp INTO OUTFILE './%6$s/predicate_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- load data into table
CREATE TABLE predicatetable (id INT SIGNED NOT NULL, predicate VARCHAR(%2$s), cnt INT SIGNED NOT NULL) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%6$s/predicate_tmp_csv.CSV' INTO TABLE predicatetable FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %6$s.predicate_tmp_csv (TMP INT SIGNED NOT NULL) ENGINE = CSV;
DROP TABLE %6$s.predicate_tmp_csv;
DROP TABLE predicateview_tmp;

-- non unique object
-- persist data to csv
CREATE TABLE objectview_tmp ENGINE = MYISAM AS SELECT tuple_id, object FROM import_tmp;
SELECT * FROM objectview_tmp INTO OUTFILE './%6$s/object_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- load data into table
CREATE TABLE objecttable (tuple_id INT SIGNED NOT NULL, object VARCHAR(%3$s)) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%6$s/object_tmp_csv.CSV' INTO TABLE objecttable FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %6$s.object_tmp_csv (TMP INT SIGNED NOT NULL) ENGINE = CSV;
DROP TABLE %6$s.object_tmp_csv;
DROP TABLE objectview_tmp;

-- patterntable filling
-- persist data to csv
CREATE TABLE patternview_tmp ENGINE = MYISAM AS SELECT MIN(tuple_id) AS id, pattern, COUNT(*) AS cnt FROM import_tmp GROUP BY pattern;
SELECT * FROM patternview_tmp INTO OUTFILE './%6$s/pattern_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- load data into table
CREATE TABLE patterntable (id INT SIGNED NOT NULL, pattern VARCHAR(%4$s), cnt INT SIGNED NOT NULL) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%6$s/pattern_tmp_csv.CSV' INTO TABLE patterntable FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %6$s.pattern_tmp_csv (TMP INT SIGNED NOT NULL) ENGINE = CSV;
DROP TABLE %6$s.pattern_tmp_csv;
DROP TABLE patternview_tmp;


-- normalizedpatterntable filling
-- persist data to csv
CREATE TABLE normpatternview_tmp ENGINE = MYISAM AS SELECT MIN(tuple_id) AS id, normalizedpattern, COUNT(*) AS cnt FROM import_tmp GROUP BY normalizedpattern;
SELECT * FROM normpatternview_tmp INTO OUTFILE './%6$s/normalizedpattern_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- load data into table
CREATE TABLE normalizedpatterntable (id INT SIGNED NOT NULL, normalizedpattern VARCHAR(%5$s), cnt INT SIGNED NOT NULL) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%6$s/normalizedpattern_tmp_csv.CSV' INTO TABLE normalizedpatterntable FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %6$s.normalizedpattern_tmp_csv (TMP INT SIGNED NOT NULL) ENGINE = CSV;
DROP TABLE %6$s.normalizedpattern_tmp_csv;
DROP TABLE normpatternview_tmp;