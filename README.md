# MovieFinder API

This is a Java Spring Boot application that allows users to manage a personal movie watchlist. It uses data from the OMDb and TMDb APIs to enrich movie information.

## Features

- Add movies by title
- Fetches data from OMDb and TMDb
- Stores movies with details like cast, genre, rating, images, etc.
- Download up to 3 images per movie
- Mark movies as watched
- Add a personal rating (1 to 5)
- List movies with pagination
- Update or delete movies
- Includes integration tests

## Technologies

- Java 17
- Spring Boot 3
- Spring Data JPA with H2 (in-memory database)
- RestTemplate for API calls
- Lombok
- JUnit 5

## API Endpoints

Base URL: `http://localhost:8080/movies`

| Method | Endpoint                                | Description                     |
|--------|-----------------------------------------|---------------------------------|
| POST   | `/movies?title=TITLE`                  | Add movie by title              |
| GET    | `/movies?page=0&size=10`               | List all movies (paginated)     |
| GET    | `/movies/{id}`                         | Get movie by ID                 |
| POST   | `/movies/{id}/watched?watched=true`    | Mark as watched/unwatched       |
| POST   | `/movies/{id}/rating?rating=4`         | Set personal rating             |
| DELETE | `/movies/{id}`                         | Delete movie                    |

## Setup

1. Clone the repository or unzip the project.
2. Make sure Java 17 is installed.
3. Add your API keys to `application.properties`:

```properties
omdb.api.key=YOUR_OMDB_KEY
tmdb.api.key=YOUR_TMDB_KEY
```

4. Run the application:

```bash
./mvnw spring-boot:run
```

## Testing

Run integration tests using:

```bash
./mvnw test
```

The tests cover:

- Adding valid and invalid movies
- OMDb and TMDb API failure handling
- Updating watched status and rating
- Deleting and fetching movies

## File Storage

Images from TMDb are downloaded to a folder named after the movie title and saved on disk. Up to 3 images are stored per movie.

## Notes

- Requires internet access to fetch data from external APIs.
- All exceptions return a JSON response with useful details.
- This project is self-contained and runs on port 8080 by default.
