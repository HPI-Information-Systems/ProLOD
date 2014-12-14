--create the MQTs
-- %1$s text datatype_id
-- %2$s schema

-- links
-- persist data to csv
CREATE TABLE linksview_tmp ENGINE = MYISAM AS 
	select mt.subject_id as subject_id, mt.predicate_id as predicate_id, internallink_id as internallink_id, count(*) as CNT
        from MAINTABLE as mt
            where mt.subject_id is not null
            and mt.predicate_id is not null
            and mt.internallink_id is not null
                group by mt.subject_id, mt.predicate_id, mt.internallink_id;
SELECT * FROM linksview_tmp	INTO OUTFILE './%2$s/links_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE linksview_tmp;

-- load data into table
CREATE TABLE links (subject_id INT SIGNED, predicate_id INT SIGNED, internallink_id INT SIGNED, CNT INT SIGNED) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%2$s/links_tmp_csv.CSV' INTO TABLE links FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %2$s.links_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %2$s.links_tmp_csv;

-- linked subjects
-- persist data to csv
CREATE TABLE linkedsubjectsview_tmp ENGINE = MYISAM AS
	select fir.subject_id as subject_id, sec.subject_id as subject_id_2, fir.predicate_id as predicate_id, sec.predicate_id as predicate_id_2, count(*) as CNT
        from MAINTABLE as fir, MAINTABLE as sec
            where fir.subject_id = sec.internallink_id
            and sec.subject_id = fir.internallink_id
            and fir.subject_id is not null
            and sec.subject_id is not null
            and fir.predicate_id is not null
            and sec.predicate_id is not null
            and fir.subject_id <> sec.subject_id
                group by fir.subject_id, sec.subject_id, fir.predicate_id, sec.predicate_id;
                
SELECT * FROM linkedsubjectsview_tmp INTO OUTFILE './%2$s/linkedsubjects_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE linkedsubjectsview_tmp;

-- load data into table
CREATE TABLE linkedsubjects (subject_id INT SIGNED, subject_id_2 INT SIGNED, predicate_id INT SIGNED, predicate_id_2 INT SIGNED, CNT INT SIGNED) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%2$s/linkedsubjects_tmp_csv.CSV' INTO TABLE linkedsubjects FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %2$s.linkedsubjects_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %2$s.linkedsubjects_tmp_csv;


-- create textdata
-- persist data to csv
CREATE TABLE textdataview_tmp ENGINE = MYISAM AS
	SELECT subject_id, tuple_id FROM maintable WHERE datatype_id = %1$s;
SELECT * FROM textdataview_tmp INTO OUTFILE './%2$s/textdata_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE textdataview_tmp;

-- load data into table
CREATE TABLE textdata (subject_id INT SIGNED, tuple_id INT SIGNED) ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%2$s/textdata_tmp_csv.CSV' INTO TABLE textdata FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %2$s.textdata_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %2$s.textdata_tmp_csv;
