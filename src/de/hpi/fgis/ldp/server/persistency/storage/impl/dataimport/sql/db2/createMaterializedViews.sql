--create the MQTs
-- %1$s text datatype_id
-- %2$s schema

-- links
CREATE TABLE links (subject_id INT NOT NULL, 
					predicate_id INT NOT NULL, 
					internallink_id INT NOT NULL, 
					cnt INT NOT NULL) NOT LOGGED INITIALLY;

-- lock the table
LOCK TABLE links IN EXCLUSIVE MODE;

ALTER TABLE links APPEND ON;

-- fill table
INSERT INTO links (subject_id, predicate_id, internallink_id, cnt) 
	(select mt.subject_id as subject_id, mt.predicate_id as predicate_id, internallink_id as internallink_id, count(*) as CNT 
			from MAINTABLE as mt 
				where mt.subject_id is not null 
				and mt.predicate_id is not null 
				and mt.internallink_id is not null 
					group by mt.subject_id, mt.predicate_id, mt.internallink_id 
						order by subject_id);

ALTER TABLE links APPEND OFF;

--ALTER TABLE links ADD PARTITIONING KEY (subject_id) USING HASHING;
--ALTER TABLE links DROP PARTITIONING KEY;

-- create indices
CREATE INDEX INDEX_LINKS_IDA ON links(subject_id);
CREATE INDEX INDEX_LINKS_IDAIDB ON links(subject_id, predicate_id);
CREATE INDEX INDEX_LINKS_IDBIDA ON links(predicate_id, subject_id);

COMMIT;


-- linked subjects
CREATE TABLE linkedsubjects (
    subject_id INT NOT NULL, 
    subject_id_2 INT NOT NULL, 
    predicate_id INT NOT NULL, 
    predicate_id_2 INT NOT NULL, 
    CNT INT NOT NULL) NOT LOGGED INITIALLY;

-- lock the table
LOCK TABLE linkedsubjects IN EXCLUSIVE MODE;

ALTER TABLE linkedsubjects APPEND ON;

-- fill table
INSERT INTO linkedsubjects (subject_id, subject_id_2, predicate_id, predicate_id_2, cnt)
    (select fir.subject_id as subject_id, sec.subject_id as subject_id_2, fir.predicate_id as predicate_id, sec.predicate_id as predicate_id_2, count(*) as CNT
        from MAINTABLE as fir, MAINTABLE as sec
            where fir.subject_id = sec.internallink_id
            and sec.subject_id = fir.internallink_id
            and fir.subject_id is not null
            and sec.subject_id is not null
            and fir.predicate_id is not null
            and sec.predicate_id is not null
            and fir.subject_id <> sec.subject_id
                group by fir.subject_id, sec.subject_id, fir.predicate_id, sec.predicate_id
                    order by subject_id, subject_id_2);


ALTER TABLE linkedsubjects APPEND OFF;

--ALTER TABLE linkedsubjects ADD PARTITIONING KEY (subject_id) USING HASHING;
--ALTER TABLE linkedsubjects DROP PARTITIONING KEY;

-- create indices
CREATE INDEX INDEX_LINKEDSUBJECTS_IDA ON linkedsubjects(subject_id);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDAIDA2 ON linkedsubjects(subject_id, subject_id_2);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDAIDA2IDB ON linkedsubjects(subject_id, subject_id_2, predicate_id);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDBDB2 ON linkedsubjects(predicate_id, predicate_id_2);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDAIDBDB2 ON linkedsubjects(subject_id, predicate_id, predicate_id_2);

COMMIT;

-- create textdata
CREATE TABLE textdata 
    (subject_id INT NOT NULL, 
    tuple_id INT NOT NULL) NOT LOGGED INITIALLY;

-- lock the table
LOCK TABLE textdata IN EXCLUSIVE MODE;

ALTER TABLE textdata APPEND ON;

-- fill table
INSERT INTO textdata (subject_id, tuple_id)
    (SELECT subject_id, tuple_id FROM maintable WHERE datatype_id = %1$s order by subject_id);


ALTER TABLE textdata APPEND OFF;

--ALTER TABLE textdata ADD PARTITIONING KEY (subject_id) USING HASHING;
--ALTER TABLE textdata DROP PARTITIONING KEY;

-- create indices
CREATE INDEX index_textdata_ida ON textdata(subject_id);

COMMIT;