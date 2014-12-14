-- create main table
-- %1$s number of tuples
-- %2$s number of partitions
-- %3$s schema



CREATE TABLE maintable_subjectview_tmp ENGINE = MYISAM AS 
	SELECT	import_tmp.tuple_id, subjecttable.id as subject_id
			FROM import_tmp, subjecttable 
				WHERE import_tmp.subject = subjecttable.subject;


SELECT tuple_id, subject_id FROM maintable_subjectview_tmp ORDER BY tuple_id INTO OUTFILE './%3$s/maintable_part_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE maintable_subjectview_tmp;

CREATE TABLE maintable_subjectview (
						tuple_id INT SIGNED,
						subject_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_part_tmp_csv.CSV' INTO TABLE maintable_subjectview FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


CREATE TABLE %3$s.maintable_part_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_part_tmp_csv;


CREATE TABLE maintable_predicateview_tmp ENGINE = MYISAM AS 
	SELECT	import_tmp.tuple_id, predicatetable.id as predicate_id
			FROM import_tmp, predicatetable 
				WHERE import_tmp.predicate = predicatetable.predicate;


SELECT tuple_id, predicate_id FROM maintable_predicateview_tmp ORDER BY tuple_id INTO OUTFILE './%3$s/maintable_part_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE maintable_predicateview_tmp;

CREATE TABLE maintable_predicateview (
						tuple_id INT SIGNED,
						predicate_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_part_tmp_csv.CSV' INTO TABLE maintable_predicateview FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


CREATE TABLE %3$s.maintable_part_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_part_tmp_csv;

CREATE TABLE maintable_interlinkview_tmp ENGINE = MYISAM AS 
	SELECT	import_tmp.tuple_id, internallink.id as internallink_id
			FROM import_tmp
				left outer join subjecttable as internallink on import_tmp.object = internallink.subject;


SELECT tuple_id, internallink_id FROM maintable_interlinkview_tmp ORDER BY tuple_id INTO OUTFILE './%3$s/maintable_part_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE maintable_interlinkview_tmp;

CREATE TABLE maintable_interlinkview (
						tuple_id INT SIGNED,
						internallink_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_part_tmp_csv.CSV' INTO TABLE maintable_interlinkview FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


CREATE TABLE %3$s.maintable_part_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_part_tmp_csv;

						
						
CREATE TABLE maintable_patternview_tmp ENGINE = MYISAM AS 
	SELECT	import_tmp.tuple_id, patterntable.id as pattern_id
			FROM import_tmp
				left outer join patterntable on import_tmp.pattern = patterntable.pattern;


SELECT tuple_id, pattern_id FROM maintable_patternview_tmp ORDER BY tuple_id INTO OUTFILE './%3$s/maintable_part_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE maintable_patternview_tmp;

CREATE TABLE maintable_patternview (
						tuple_id INT SIGNED,
						pattern_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_part_tmp_csv.CSV' INTO TABLE maintable_patternview FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


CREATE TABLE %3$s.maintable_part_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_part_tmp_csv;


CREATE TABLE maintable_normpatternview_tmp ENGINE = MYISAM AS 
	SELECT	import_tmp.tuple_id, normalizedpatterntable.id as normalizedpattern_id
			FROM import_tmp
				left outer join normalizedpatterntable on import_tmp.normalizedpattern = normalizedpatterntable.normalizedpattern;


SELECT tuple_id, normalizedpattern_id FROM maintable_normpatternview_tmp ORDER BY tuple_id INTO OUTFILE './%3$s/maintable_part_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE maintable_normpatternview_tmp;

CREATE TABLE maintable_normpatternview (
						tuple_id INT SIGNED,
						normalizedpattern_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_part_tmp_csv.CSV' INTO TABLE maintable_normpatternview FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


CREATE TABLE %3$s.maintable_part_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_part_tmp_csv;


CREATE TABLE maintable_datatypeview_tmp ENGINE = MYISAM AS 
	SELECT	import_tmp.tuple_id, import_tmp.parsed_value, import_tmp.datatype_id
			FROM import_tmp
-- TODO set internal link datatype
--				WHERE import_tmp.internallink_id IS NOT NULL;
--INSERT INTO maintable_datatypeview_tmp (tuple_id, parsed_value, datatype_id) VALUES 
--	SELECT	import_tmp.tuple_id, import_tmp.parsed_value, %???$s
--			FROM import_tmp
--				WHERE import_tmp.internallink_id IS NULL;

SELECT tuple_id, parsed_value, datatype_id FROM maintable_datatypeview_tmp ORDER BY tuple_id INTO OUTFILE './%3$s/maintable_part_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';
DROP TABLE maintable_datatypeview_tmp;

CREATE TABLE maintable_datatypeview (
						tuple_id INT SIGNED,
						parsed_value DOUBLE, 
						datatype_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_part_tmp_csv.CSV' INTO TABLE maintable_datatypeview FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

CREATE TABLE %3$s.maintable_part_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_part_tmp_csv;


CREATE TABLE maintableview_tmp ENGINE = MYISAM IGNORE AS 
	SELECT maintable_subjectview.subject_id,
		maintable_predicateview.predicate_id,
		maintable_interlinkview.internallink_id,
		maintable_datatypeview.datatype_id,
		maintable_normpatternview.normalizedpattern_id,
		maintable_patternview.pattern_id,
		maintable_datatypeview.parsed_value,
		maintable_subjectview.tuple_id
			FROM maintable_subjectview, maintable_predicateview, maintable_interlinkview, maintable_datatypeview, maintable_patternview, maintable_normpatternview
				WHERE maintable_subjectview.tuple_id = maintable_predicateview.tuple_id
				AND maintable_subjectview.tuple_id = maintable_interlinkview.tuple_id
				AND maintable_subjectview.tuple_id = maintable_patternview.tuple_id
				AND maintable_subjectview.tuple_id = maintable_normpatternview.tuple_id
				AND maintable_subjectview.tuple_id = maintable_datatypeview.tuple_id;

DROP TABLE maintable_subjectview;
DROP TABLE maintable_predicateview;
DROP TABLE maintable_interlinkview;
DROP TABLE maintable_datatypeview;
DROP TABLE maintable_patternview;
DROP TABLE maintable_normpatternview;


SELECT subject_id, predicate_id, internallink_id, datatype_id, normalizedpattern_id, pattern_id, parsed_value, tuple_id
	FROM maintableview_tmp ORDER BY subject_id, predicate_id, datatype_id, normalizedpattern_id, pattern_id INTO OUTFILE './%3$s/maintable_tmp_csv.CSV' FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';

DROP TABLE maintableview_tmp;

CREATE TABLE maintable (subject_id INT SIGNED, 
						predicate_id INT SIGNED, 
						internallink_id INT SIGNED, 
						datatype_id INT SIGNED, 
						normalizedpattern_id INT SIGNED, 
						pattern_id INT SIGNED, 
						parsed_value DOUBLE, 
						tuple_id INT SIGNED) 
							ENGINE = BRIGHTHOUSE;
LOAD DATA INFILE './%3$s/maintable_tmp_csv.CSV' INTO TABLE maintable FIELDS TERMINATED BY ';' ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n';


CREATE TABLE %3$s.maintable_tmp_csv (TMP INT NOT NULL) ENGINE = CSV;
DROP TABLE %3$s.maintable_tmp_csv;