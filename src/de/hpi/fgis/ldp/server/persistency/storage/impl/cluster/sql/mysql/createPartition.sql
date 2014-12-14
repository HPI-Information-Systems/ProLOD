-- Creates a new cluster partition
-- %1$s view name
-- %2$s cluster id
-- %3$s schema

CREATE TABLE %1$s ( subject_id INT UNSIGNED, 
					predicate_id INT UNSIGNED, 
					internallink_id INT UNSIGNED, 
					datatype_id INT UNSIGNED, 
					normalizedpattern_id INT UNSIGNED, 
					pattern_id INT UNSIGNED, 
					parsed_value DOUBLE, 
					tuple_id INT UNSIGNED) 
							ENGINE = MYISAM, INSERT_METHOD = LAST, AVG_ROW_LENGTH = 36, CHECKSUM = 0

				-- fill table
				IGNORE AS 
					(SELECT	mt.subject_id, predicate_id, internallink_id, 
							datatype_id, normalizedpattern_id, pattern_id, 
							parsed_value, tuple_id 
								FROM maintable mt
									inner join cluster_subjects on cluster_subjects.subject_id = mt.subject_id
									where cluster_subjects.cluster_id = %2$s);

COMMIT;

-- add indices on %1$s
CREATE INDEX index_mt_%2$s_subject_id ON %1$s(subject_id);
CREATE INDEX index_mt_%2$s_predicate_id ON %1$s(predicate_id);
CREATE INDEX index_mt_%2$s_tuple_id ON %1$s(tuple_id);
CREATE INDEX index_mt_%2$s_internallink_id ON %1$s(internallink_id);

COMMIT;

CREATE INDEX index_mt_%2$s_normalizedpattern_id ON %1$s(normalizedpattern_id);
CREATE INDEX index_mt_%2$s_pattern_id ON %1$s(pattern_id);
CREATE INDEX index_mt_%2$s_datatype_id ON %1$s(datatype_id);
CREATE INDEX index_mt_%2$s_parsed_value ON %1$s(parsed_value);

COMMIT;

-- object-based profiling
-- TODO check which indices are needed actually
CREATE INDEX index_mt_%2$s_subj_intlink ON %1$s(subject_id, internallink_id);
CREATE INDEX index_mt_%2$s_subj_pred ON %1$s(subject_id, predicate_id);
CREATE INDEX index_mt_%2$s_subj_intlink_pred ON %1$s(subject_id, internallink_id, predicate_id);
CREATE INDEX index_mt_%2$s_subj_intlink_pred_dtype ON %1$s(subject_id, internallink_id, predicate_id, datatype_id);

COMMIT;

CREATE INDEX index_mt_%2$s_subj_pred_dtype ON %1$s(subject_id, predicate_id, datatype_id);
CREATE INDEX index_mt_%2$s_subj_pred_dtype_normpat ON %1$s(subject_id, predicate_id, datatype_id, normalizedpattern_id);
CREATE INDEX index_mt_%2$s_subj_pred_dtype_pattern ON %1$s(subject_id, predicate_id, datatype_id, pattern_id);
CREATE INDEX index_mt_%2$s_subj_pred_dtype_normpat_pat ON %1$s(subject_id, predicate_id, datatype_id, normalizedpattern_id, pattern_id);

COMMIT;

CREATE INDEX index_mt_%2$s_subj_pred_dtype_normpat_pattern_parsval ON %1$s(subject_id, predicate_id, datatype_id, normalizedpattern_id, pattern_id, parsed_value);
CREATE INDEX index_mt_%2$s_subj_pred_dtype_pattern_parsval ON %1$s(subject_id, predicate_id, datatype_id, pattern_id, parsed_value);
CREATE INDEX index_mt_%2$s_subj_pred_dtype_parsval ON %1$s(subject_id, predicate_id, datatype_id, parsed_value);
CREATE INDEX index_mt_%2$s_subj_pred_parsval ON %1$s(subject_id, predicate_id, parsed_value);

COMMIT;