-- %1$s cluster with subject_id


select predicate, subject
from ontologyaddition left outer join subjecttable s on SOURCE_C_ID = s.id , predicatetable p
where s_id = %1$s 
and p.id = addedproperty