# PagePulse Backend

Spring Boot REST API for the PagePulse Website Audit Tool.

## Technologies

- Java
- Spring Boot
- Jsoup
- Maven

## Features

- Website scraping
- HTML parsing
- Metadata extraction
- Response time calculation
- REST API

## API

POST /api/audit

Request

{
    "url":"https://github.com"
}

Response

{
    "status":200,
    "responseTime":312,
    "title":"GitHub",
    "metaDescription":"...",
    "h1Count":4,
    "missingAltImages":17,
    "wordCount":1176
}

## Run

./mvnw spring-boot:run
