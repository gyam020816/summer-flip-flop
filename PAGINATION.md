find out a way to simplify such query and make it optimal:

```sql
select *
from documents
order by data -> 'name', id
    offset (select rownum
            from (select id as graphql_cursor, row_number() over (order by data -> 'name', id) as rownum from documents) as subquery
            where subquery.graphql_cursor = 2)
limit 5;
```

where `2` is the graphql cursor hash (in our case, an alias of id) of the last item that the client knows

resources:

- https://graphql.org/learn/pagination/
- https://medium.com/@mattmazzola/graphql-pagination-implementation-8604f77fb254
- https://www.postgresql.org/docs/9.1/tutorial-window.html
- https://stackoverflow.com/questions/3125571/offset-vs-row-number