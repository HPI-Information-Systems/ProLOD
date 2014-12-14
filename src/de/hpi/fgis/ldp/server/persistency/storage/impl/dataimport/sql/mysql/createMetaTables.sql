-- create meta tables
-- %1$s max size of subject
-- %2$s max size of predicate
-- %3$s max size of object
-- %4$s max size of pattern
-- %5$s max size of normalized_pattern
-- %6$s schema


-- subjecttable filling
CREATE TABLE subjecttable ENGINE = MYISAM as (SELECT subject, COUNT(*) AS cnt FROM import_tmp GROUP BY subject);
-- add primary key
ALTER TABLE subjecttable ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (id);
-- add unique index for subject
CREATE UNIQUE INDEX index_subjecttable_subject ON subjecttable(subject);

-- predicatetable filling
CREATE TABLE predicatetable ENGINE = MYISAM as (SELECT predicate, COUNT(*) AS cnt FROM import_tmp GROUP BY predicate);
-- add primary key
ALTER TABLE predicatetable ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (id);
-- add unique index for predicate
CREATE UNIQUE INDEX index_predicatetable_predicate ON predicatetable(predicate);

-- objecttable filling
--CREATE TABLE objecttable ENGINE = MYISAM as (SELECT object, COUNT(*) AS cnt FROM import_tmp GROUP BY object);
-- non unique object
CREATE TABLE objecttable ENGINE = MYISAM as (SELECT tuple_id, object FROM import_tmp);
-- add primary key
--ALTER TABLE objecttable ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (id);
ALTER TABLE objecttable ADD PRIMARY KEY (tuple_id);
-- add fulltext index for object
CREATE FULLTEXT INDEX index_objecttable_object ON objecttable(object);

-- normalizedpatterntable filling
CREATE TABLE normalizedpatterntable ENGINE = MYISAM as (SELECT normalizedpattern, COUNT(*) AS cnt FROM import_tmp GROUP BY normalizedpattern);
-- add primary key
ALTER TABLE normalizedpatterntable ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (id);
-- add unique index for normalizedpattern
CREATE UNIQUE INDEX index_normalizedpatterntable_normalizedpattern ON normalizedpatterntable(normalizedpattern);

-- patterntable filling
CREATE TABLE patterntable ENGINE = MYISAM as (SELECT pattern, COUNT(*) AS cnt FROM import_tmp GROUP BY pattern);
-- add primary key
ALTER TABLE patterntable ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (id);
-- add unique index for pattern
CREATE UNIQUE INDEX index_patterntable_pattern ON patterntable(pattern);