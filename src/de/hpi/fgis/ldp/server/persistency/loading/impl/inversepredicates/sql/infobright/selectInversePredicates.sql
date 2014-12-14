-- %1$s constraint view (links)
-- %2$s constraint view (linked subjects)
-- %3$s initial constraint condition (links)
-- %4$s additional constraint condition (links)
-- %5$s constraint condition (linked subjects)
-- %6$s minFrequency

SELECT main_view.X, main_view.X_id, main_view.Y, main_view.Y_id, main_view.cntXnY, main_view.cntX, COUNT(*) as cntY
    FROM
        (SELECT X_view.predicate as X, linked_subjects_view.X_id, Y_view.predicate as Y, linked_subjects_view.Y_id, linked_subjects_view.cntXnY, COUNT(*) as cntX
            FROM (SELECT tab.predicate_id as X_id, tab.predicate_id_2 as Y_id, 
                            COUNT(*) as cntXnY
                    FROM LINKEDSUBJECTS tab %2$s 
                              WHERE tab.predicate_id >= tab.predicate_id_2 %5$s
                                          GROUP BY tab.predicate_id, tab.predicate_id_2
                                          HAVING COUNT(*) > %6$s) linked_subjects_view,
                 predicatetable X_view,
                 predicatetable Y_view,
                (SELECT subj.subject_id, subj.predicate_id, subj.internallink_id
                    FROM LINKS subj %1$s %3$s) Xcnt_view
                    WHERE X_view.ID = linked_subjects_view.X_id
                    AND Y_view.ID = linked_subjects_view.Y_id

                    AND Xcnt_view.predicate_id = linked_subjects_view.X_id
                        GROUP BY X_view.predicate, linked_subjects_view.X_id, Y_view.predicate, linked_subjects_view.Y_id, linked_subjects_view.cntXnY) main_view, 
        (SELECT subj.subject_id, subj.predicate_id, subj.internallink_id
            FROM LINKS subj %1$s %3$s ) Ycnt_view
            WHERE Ycnt_view.predicate_id = main_view.Y_id
                GROUP BY main_view.X, main_view.X_id, main_view.Y, main_view.Y_id, main_view.cntXnY, cntX