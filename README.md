# java-filmorate
Repository for Filmorate project.

## DB Scheme

![Scheme link](https://github.com/zvrzhina/java-filmorate/blob/main/src/main/resources/db_pic.png)

## Basic Selects
### Get current amount of the films
```sql
SELECT COUNT(*)
FROM Film;
```

### Get film by id
```sql
SELECT COUNT(*)
FROM Film
WHERE FilmID=1;
```

### Get top-10 the most popular films
```sql
SELECT Name
FROM Film
WHERE FilmID in 
(SELECT FilmID
FROM Likes
GROUP BY FilmID
ORDER BY COUNT(UserID) DESC
LIMIT 10);
```
