-- finishes the cluster subject entry creation for a cluster
-- %1$s schema

LOCK TABLE cluster_subjects_writable WRITE, cluster_subjects WRITE;

-- delete temp files
CREATE TABLE %1$s.cluster_subjects_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.cluster_subjects_tmp_csv;

SELECT * from cluster_subjects_writable ORDER BY cluster_id, subject_id INTO OUTFILE './%1$s/cluster_subjects_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

drop table CLUSTER_SUBJECTS;

create table CLUSTER_SUBJECTS (
	CLUSTER_ID INT SIGNED NOT NULL,
	subject_id INT SIGNED NOT NULL) ENGINE = BRIGHTHOUSE;

LOCK TABLE cluster_subjects_writable WRITE, cluster_subjects WRITE;

LOAD DATA INFILE './%1$s/cluster_subjects_tmp_csv.CSV' INTO TABLE cluster_subjects FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files
CREATE TABLE %1$s.cluster_subjects_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %1$s.cluster_subjects_tmp_csv;

UNLOCK TABLES;