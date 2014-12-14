-- create meta tables
-- %1$s max size of subject
-- %2$s max size of predicate
-- %3$s max size of object
-- %4$s max size of pattern
-- %5$s max size of normalized_pattern
-- %6$s schema


-- subjecttable filling
CREATE TABLE subjecttable (
	id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	subject VARCHAR(%1$s),
	cnt INT) NOT LOGGED INITIALLY;
-- lock the table
LOCK TABLE subjecttable IN EXCLUSIVE MODE;
ALTER TABLE subjecttable APPEND ON;
-- fill table
INSERT INTO subjecttable (subject, cnt) (SELECT subject, COUNT(*) AS cnt FROM import_tmp GROUP BY subject);
ALTER TABLE subjecttable APPEND OFF;
-- create partitions
ALTER TABLE subjecttable ADD PARTITIONING KEY (id) USING HASHING;
--ALTER TABLE subjecttable DROP PARTITIONING KEY;
-- add unique index for subject
CREATE INDEX index_subjecttable_subject ON subjecttable(subject);
COMMIT;

-- predicatetable filling
CREATE TABLE predicatetable (
	id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	predicate VARCHAR(%2$s),
	cnt INT) NOT LOGGED INITIALLY;
-- lock the table
LOCK TABLE predicatetable IN EXCLUSIVE MODE;
ALTER TABLE predicatetable APPEND ON;
-- fill table
INSERT INTO predicatetable (predicate, cnt) (SELECT predicate, COUNT(*) AS cnt FROM import_tmp GROUP BY predicate);
ALTER TABLE predicatetable APPEND OFF;
-- create partitions
--ALTER TABLE predicatetable ADD PARTITIONING KEY (id) USING HASHING;
--ALTER TABLE predicatetable DROP PARTITIONING KEY;
-- add unique index for predicate
CREATE INDEX index_predicatetable_predicate ON predicatetable(predicate);
COMMIT;

-- objecttable filling
-- non unique object
CREATE TABLE objecttable (
	tuple_id INT NOT NULL PRIMARY KEY,
	object VARCHAR(%3$s)) NOT LOGGED INITIALLY;
-- lock the table
LOCK TABLE objecttable IN EXCLUSIVE MODE;
ALTER TABLE objecttable APPEND ON;
-- fill table
INSERT INTO objecttable (tuple_id, object) (SELECT tuple_id, object FROM import_tmp);
ALTER TABLE objecttable APPEND OFF;
-- create partitions
ALTER TABLE objecttable ADD PARTITIONING KEY (tuple_id) USING HASHING;
--ALTER TABLE objecttable DROP PARTITIONING KEY;
-- add fulltext index for object
CREATE INDEX index_objecttable_object ON objecttable(object);
COMMIT;

-- normalizedpatterntable filling
CREATE TABLE normalizedpatterntable (
	id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	normalizedpattern VARCHAR(%5$s),
	cnt INT) NOT LOGGED INITIALLY;
-- lock the table
LOCK TABLE normalizedpatterntable IN EXCLUSIVE MODE;
ALTER TABLE normalizedpatterntable APPEND ON;
-- fill table
INSERT INTO normalizedpatterntable (normalizedpattern, cnt) (SELECT normalizedpattern, COUNT(*) AS cnt FROM import_tmp GROUP BY normalizedpattern);
ALTER TABLE normalizedpatterntable APPEND OFF;
-- create partitions
ALTER TABLE normalizedpatterntable ADD PARTITIONING KEY (id) USING HASHING;
--ALTER TABLE normalizedpatterntable DROP PARTITIONING KEY;
-- add unique index for normalizedpattern
CREATE INDEX index_normalizedpatterntable_normalizedpattern ON normalizedpatterntable(normalizedpattern);
COMMIT;

-- patterntable filling
CREATE TABLE patterntable  (
	id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	pattern VARCHAR(%4$s),
	cnt INT) NOT LOGGED INITIALLY;
-- lock the table
LOCK TABLE patterntable IN EXCLUSIVE MODE;
ALTER TABLE patterntable APPEND ON;
-- fill table
INSERT INTO patterntable (pattern, cnt)  (SELECT pattern, COUNT(*) AS cnt FROM import_tmp GROUP BY pattern);
ALTER TABLE patterntable APPEND OFF;
-- create partitions
ALTER TABLE patterntable ADD PARTITIONING KEY (id) USING HASHING;
--ALTER TABLE patterntable DROP PARTITIONING KEY;
-- add unique index for pattern
CREATE INDEX index_patterntable_pattern ON patterntable(pattern);
COMMIT;
