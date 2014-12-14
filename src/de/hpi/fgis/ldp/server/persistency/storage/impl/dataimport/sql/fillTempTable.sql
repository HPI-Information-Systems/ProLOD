-- fill tmp table
-- ?1 subject
-- ?2 predicate
-- ?3 object
-- ?4 normalized_pattern
-- ?5 pattern
-- ?6 datatype_id
-- ?6 parsed_value

INSERT INTO IMPORT_TMP (subject, predicate, object, normalizedpattern, pattern, datatype_id, parsed_value) VALUES (?, ?, ?, ?, ?, ?, ?)