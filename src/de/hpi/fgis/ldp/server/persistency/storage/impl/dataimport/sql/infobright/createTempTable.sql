-- create tmp tables
-- %1$s max size of subject
-- %2$s max size of predicate
-- %3$s max size of object
-- %4$s max size of pattern
-- %5$s max size of normalized_pattern


CREATE TABLE IMPORT_TMP_INIT (
	tuple_id INT SIGNED AUTO_INCREMENT PRIMARY KEY,
	subject VARCHAR(%1$s),
	predicate VARCHAR(%2$s),
	object VARCHAR(%3$s),
	normalizedpattern VARCHAR(%5$s),
	pattern VARCHAR(%4$s),  
	datatype_id INT SIGNED, 
	parsed_value DOUBLE
) ENGINE = MYISAM;

CREATE TABLE import_tmp (
	tuple_id INT SIGNED,
	subject VARCHAR(%1$s),
	predicate VARCHAR(%2$s),
	object VARCHAR(%3$s),
	normalizedpattern VARCHAR(%5$s),
	pattern VARCHAR(%4$s),  
	datatype_id INT SIGNED, 
	parsed_value DOUBLE
) ENGINE = BRIGHTHOUSE;