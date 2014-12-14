-- Creates a new cluster
-- %1$s schema

LOCK TABLE cluster_sessions_writable WRITE, cluster_sessions WRITE;

-- delete temp files
CREATE TABLE %1$s.cluster_sessions_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.cluster_sessions_tmp_csv;

SELECT * from cluster_sessions_writable INTO OUTFILE './%1$s/cluster_sessions_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

DROP TABLE cluster_sessions;

create table cluster_sessions (
	ID INT SIGNED NOT NULL,
	NAME VARCHAR(255)) ENGINE = BRIGHTHOUSE;

LOCK TABLE cluster_sessions_writable WRITE, cluster_sessions WRITE;

LOAD DATA INFILE './%1$s/cluster_sessions_tmp_csv.CSV' INTO TABLE cluster_sessions FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %1$s.cluster_sessions_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.cluster_sessions_tmp_csv;

UNLOCK TABLES;