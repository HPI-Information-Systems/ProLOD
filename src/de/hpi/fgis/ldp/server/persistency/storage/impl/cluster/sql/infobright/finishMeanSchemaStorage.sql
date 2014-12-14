-- finishes the mean schema entry creation for a cluster
-- %1$s schema

LOCK TABLE cluster_meanschema_writable WRITE, cluster_meanschema WRITE;

-- delete temp files
CREATE TABLE %1$s.cluster_meanschema_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.cluster_meanschema_tmp_csv;

SELECT * from cluster_meanschema_writable INTO OUTFILE './%1$s/cluster_meanschema_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

drop table CLUSTER_MEANSCHEMA;

create table CLUSTER_MEANSCHEMA (
	CLUSTER_ID INT SIGNED NOT NULL,
	predicate_id INT SIGNED NOT NULL,
	RANK INT SIGNED) ENGINE = BRIGHTHOUSE;

LOCK TABLE cluster_meanschema_writable WRITE, cluster_meanschema WRITE;

LOAD DATA INFILE './%1$s/cluster_meanschema_tmp_csv.CSV' INTO TABLE cluster_meanschema FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files
CREATE TABLE %1$s.cluster_meanschema_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.cluster_meanschema_tmp_csv;

UNLOCK TABLES;