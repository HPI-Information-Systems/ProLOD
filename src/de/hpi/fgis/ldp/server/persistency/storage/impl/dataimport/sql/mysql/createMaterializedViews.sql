--create the MQTs
-- %1$s text datatype_id
-- %2$s schema

-- links
CREATE TABLE LINKS ENGINE = MYISAM AS 
    (select mt.subject_id as subject_id, mt.predicate_id as predicate_id, internallink_id as internallink_id, count(*) as CNT
        from MAINTABLE as mt
            where mt.subject_id is not null
            and mt.predicate_id is not null
            and mt.internallink_id is not null
                group by mt.subject_id, mt.predicate_id, mt.internallink_id);

-- create indices
CREATE INDEX INDEX_LINKS_IDA ON LINKS(subject_id);
CREATE INDEX INDEX_LINKS_IDAIDB ON LINKS(subject_id, predicate_id);
CREATE INDEX INDEX_LINKS_IDBIDA ON LINKS(predicate_id, subject_id);

-- linked subjects
CREATE TABLE LINKEDSUBJECTS ENGINE = MYISAM AS  
    (select fir.subject_id as subject_id, sec.subject_id as subject_id_2, fir.predicate_id as predicate_id, sec.predicate_id as predicate_id_2, count(*) as CNT
        from MAINTABLE as fir, MAINTABLE as sec
            where fir.subject_id = sec.internallink_id
            and sec.subject_id = fir.internallink_id
            and fir.subject_id is not null
            and sec.subject_id is not null
            and fir.predicate_id is not null
            and sec.predicate_id is not null
            and fir.subject_id <> sec.subject_id
                group by fir.subject_id, sec.subject_id, fir.predicate_id, sec.predicate_id);

-- create indices
CREATE INDEX INDEX_LINKEDSUBJECTS_IDA ON LINKEDSUBJECTS(subject_id);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDAIDA2 ON LINKEDSUBJECTS(subject_id, subject_id_2);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDAIDA2IDB ON LINKEDSUBJECTS(subject_id, subject_id_2, predicate_id);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDBDB2 ON LINKEDSUBJECTS(predicate_id, predicate_id_2);
CREATE INDEX INDEX_LINKEDSUBJECTS_IDAIDBDB2 ON LINKEDSUBJECTS(subject_id, predicate_id, predicate_id_2);


-- create textdata
CREATE TABLE textdata AS (SELECT subject_id, tuple_id FROM maintable WHERE datatype_id = %1$s);

-- create indices
CREATE INDEX index_textdata_ida ON textdata(subject_id);