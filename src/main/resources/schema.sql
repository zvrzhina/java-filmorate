CREATE TABLE IF NOT EXISTS RATING(
RATING_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
NAME VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM(
FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
NAME VARCHAR(50) NOT NULL,
DESCRIPTION VARCHAR(200),
RELEASE_DATE DATE,
DURATION INTEGER,
RATING_ID INTEGER,
FOREIGN KEY (RATING_ID) REFERENCES RATING (RATING_ID),
CONSTRAINT validations CHECK (DURATION > 0 AND RELEASE_DATE >= '1895-12-28')
);

CREATE TABLE IF NOT EXISTS GENRE(
GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
NAME VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM_GENRE(
FILM_ID INTEGER,
GENRE_ID INTEGER,
FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID),
FOREIGN KEY (GENRE_ID) REFERENCES GENRE (GENRE_ID),
PRIMARY KEY (FILM_ID, GENRE_ID)
);

CREATE TABLE IF NOT EXISTS USERS(
USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
EMAIL VARCHAR(40),
LOGIN VARCHAR(50) NOT NULL,
NAME VARCHAR(50),
BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS LIKES(
FILM_ID INTEGER,
USER_ID INTEGER,
FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID),
FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIEND(
USER_ID INTEGER,
FRIEND_ID INTEGER,
IS_ACCEPTED BOOLEAN,
FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID),
FOREIGN KEY (FRIEND_ID) REFERENCES USERS (USER_ID)
);