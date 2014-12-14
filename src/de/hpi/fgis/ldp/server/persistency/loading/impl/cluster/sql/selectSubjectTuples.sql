-- %1$s cluster data partition
-- %2$s subjectID

SELECT mt.predicate_id, pt.predicate, mt.tuple_id, ot.object
    FROM %1$s mt, predicatetable pt, objecttable ot 
        WHERE mt.predicate_id = pt.id
        AND mt.tuple_id = ot.tuple_id
        AND mt.subject_id = %2$s
            order by predicate, object