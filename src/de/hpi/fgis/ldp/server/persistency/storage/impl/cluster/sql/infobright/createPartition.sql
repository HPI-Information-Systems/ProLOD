-- Creates a new cluster partition
-- %1$s view name
-- %2$s cluster id
-- %3$s schema

-- persist data to csv
CREATE TABLE %1$s_tmp ENGINE = MYISAM AS 
	SELECT	mt.subject_id, predicate_id, internallink_id, 
		datatype_id, normalizedpattern_id, pattern_id, 
		parsed_value, tuple_id 
			FROM maintable mt
				inner join cluster_subjects on cluster_subjects.subject_id = mt.subject_id
				where cluster_subjects.cluster_id = %2$s;
				
select * from %1$s_tmp ORDER BY subject_id, predicate_id, datatype_id, normalizedpattern_id, pattern_id INTO OUTFILE './%3$s/%1$s_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
drop table %1$s_tmp;

-- load data into table
CREATE TABLE %1$s (subject_id INT SIGNED, 
					predicate_id INT SIGNED, 
					internallink_id INT SIGNED, 
					datatype_id INT SIGNED, 
					normalizedpattern_id INT SIGNED, 
					pattern_id INT SIGNED, 
					parsed_value DOUBLE, 
					tuple_id INT SIGNED) ENGINE = BRIGHTHOUSE;

LOAD DATA INFILE './%3$s/%1$s_tmp_csv.CSV' INTO TABLE %1$s FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

-- delete temp files & tables
CREATE TABLE %3$s.%1$s_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.%1$s_tmp_csv;