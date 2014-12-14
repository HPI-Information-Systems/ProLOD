-- Gets all Sujects in one cluster or a cluster-subset
-- %1$s constraint view
-- %2$s constraint condition
-- %3$s fetch size



SELECT DISTINCT ID, subject, CNT 
	FROM subjecttable tablesample bernoulli(1) %1$s
		%2$s
		fetch first %3$s rows only