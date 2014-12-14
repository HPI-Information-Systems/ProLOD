-- Creates a new cluster
-- %1$s schema 

LOCK TABLE clusters_writable WRITE, clusters WRITE;

-- delete temp files
CREATE TABLE %1$s.clusters_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.clusters_tmp_csv;

SELECT * from clusters_writable ORDER BY username, session_id INTO OUTFILE './%1$s/clusters_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

DROP TABLE CLUSTERS;

create table CLUSTERS (
	ID INT SIGNED NOT NULL,
	USERNAME VARCHAR(50) NOT NULL,
  	SESSION_ID INT SIGNED NOT NULL,
  	SESSION_LOCAL_ID INT SIGNED,
  	LABEL VARCHAR (255),
  	CHILD_SESSION INT SIGNED,
  	AVG_ERROR FLOAT,
  	CLUSTER_SIZE INT SIGNED,
  	PARTITIONNAME VARCHAR (255) NOT NULL DEFAULT 'maintable') ENGINE = BRIGHTHOUSE;

LOCK TABLE clusters_writable WRITE, clusters WRITE;

LOAD DATA INFILE './%1$s/clusters_tmp_csv.CSV' INTO TABLE clusters FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


-- delete temp files
CREATE TABLE %1$s.clusters_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.clusters_tmp_csv;

UNLOCK TABLES;