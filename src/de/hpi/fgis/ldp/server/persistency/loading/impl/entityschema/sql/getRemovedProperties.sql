-- %1$s cluster with subject_id


select s.id, subject 
from ontologyremovals, subjecttable s 
where s_id = %1$s 
and s.id = removedproperty