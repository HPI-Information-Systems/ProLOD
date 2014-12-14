-- Shows the number of occurrence of every property
-- Needed indexes: INDEX_MAINTABLE_IDAIDB
-- %1$s cluster data partition
-- %2$s constraint view
-- %3$s initial constraint condition
-- %4$s addidional constraint condition


SELECT mt.predicate_id, predicatetable.predicate, count(*) as cnt 
    FROM %1$s mt, predicatetable %2$s
        where mt.predicate_id = predicatetable.ID %4$s
            group by mt.predicate_id, predicatetable.predicate
            order by cnt desc