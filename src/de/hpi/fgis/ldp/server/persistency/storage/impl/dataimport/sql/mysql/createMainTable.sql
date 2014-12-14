-- create main table
-- %1$s number of tuples
-- %2$s number of partitions
-- %3$s schema

-- maintable filling
CREATE TABLE maintable (subject_id INT UNSIGNED, 
						predicate_id INT UNSIGNED, 
						internallink_id INT UNSIGNED, 
						datatype_id INT UNSIGNED, 
						normalizedpattern_id INT UNSIGNED, 
						pattern_id INT UNSIGNED, 
						parsed_value DOUBLE, 
						tuple_id INT UNSIGNED) 
							ENGINE = MYISAM, MAX_ROWS = %1$s, INSERT_METHOD = LAST, AVG_ROW_LENGTH = 36, CHECKSUM = 0

							IGNORE AS 
								(
								SELECT	subjecttable.id as subject_id,
									predicatetable.id as predicate_id,
									internallink.id as internallink_id,
									import_tmp.datatype_id,
									normalizedpatterntable.id as normalizedpattern_id,
									patterntable.id as pattern_id,
									import_tmp.parsed_value,
									import_tmp.tuple_id
										FROM import_tmp
											inner join subjecttable on import_tmp.subject = subjecttable.subject
											inner join predicatetable on import_tmp.predicate = predicatetable.predicate
											left outer join normalizedpatterntable on import_tmp.normalizedpattern = normalizedpatterntable.normalizedpattern
											left outer join patterntable on import_tmp.pattern = patterntable.pattern
											left outer join subjecttable as internallink on import_tmp.object = internallink.subject
													ORDER BY subject_id
								
								);
 

LOCK TABLE maintable WRITE;


-- add indices on maintable
CREATE INDEX index_maintable_subject_id ON maintable(subject_id);
CREATE INDEX index_maintable_predicate_id ON maintable(predicate_id);
CREATE INDEX index_maintable_tuple_id ON maintable(tuple_id);
CREATE INDEX index_maintable_internallink_id ON maintable(internallink_id);
CREATE INDEX index_maintable_normalizedpattern_id ON maintable(normalizedpattern_id);
CREATE INDEX index_maintable_pattern_id ON maintable(pattern_id);
-- TODO set internal link datatype
--UPDATE TABLE maintable set datatype_id = %???$s where internallink_id is not null;
CREATE INDEX index_maintable_datatype_id ON maintable(datatype_id);
CREATE INDEX index_maintable_parsed_value ON maintable(parsed_value);


-- object-based profiling
-- TODO check which indices are needed actually
CREATE INDEX index_maintable_subject_internallink ON maintable(subject_id, internallink_id);
CREATE INDEX index_maintable_subject_predicate ON maintable(subject_id, predicate_id);

CREATE INDEX index_maintable_subject_predicate_datatype_tuple ON maintable(subject_id, predicate_id, datatype_id, tuple_id);
CREATE INDEX index_maintable_subject_predicate_normalizedpattern_datatype ON maintable(subject_id, predicate_id, normalizedpattern_id, datatype_id);

CREATE INDEX index_maintable_subject_predicate_pattern_datatype ON maintable(subject_id, predicate_id, pattern_id, datatype_id);
CREATE INDEX index_maintable_SubjPredPattNormpattDatetype ON maintable(subject_id, predicate_id, pattern_id, normalizedpattern_id, datatype_id);
CREATE INDEX index_maintable_subject_predicate_tuple_datatype ON maintable(subject_id, predicate_id, tuple_id, datatype_id);
CREATE INDEX index_maintable_subject_predicate_tuple_normpattern_datatype ON maintable(subject_id, predicate_id, tuple_id, normalizedpattern_id, datatype_id);
CREATE INDEX index_maintable_subject_predicate_tuple_pattern_datatype ON maintable(subject_id, predicate_id, tuple_id, pattern_id, datatype_id);


CREATE INDEX index_maintable_predicate_normalizedpattern_datatype ON maintable(predicate_id, normalizedpattern_id, datatype_id);
CREATE INDEX index_maintable_predicate_pattern_datatype ON maintable(predicate_id, pattern_id, datatype_id);
CREATE INDEX index_maintable_predicate_tuple_datatype ON maintable(predicate_id, tuple_id, datatype_id);
CREATE INDEX index_maintable_predicate_tuple_normalizedpattern_datatype ON maintable(predicate_id, tuple_id, normalizedpattern_id, datatype_id);
CREATE INDEX index_maintable_predicate_tuple_pattern_datatype ON maintable(predicate_id, tuple_id, pattern_id, datatype_id);






UNLOCK TABLES;