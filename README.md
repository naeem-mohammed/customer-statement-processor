# Rabobank Customer Statement Processor

Rabobank receives monthly deliveries of customer statement records. This information is delivered in
JSON Format. These records need to be validated.

The Customer Statement Record format is as follows:

| Field | Description |
| ------ | ------ |
| Transaction reference | A numeric value |
| Account number | An IBAN |
| Start Balance | The starting balance in Euros |
| Mutation | Either an addition (+) or a deduction (-) |
| Description | Free text |
| End Balance | The end balance in Euros |

## To run the application

Once you are in the project directory, you can run the application in 2 ways:

`mvn spring-boot:run`

or

```bash
 mvn clean package
 java -jar target/customer-statement-processor-1.0.0.jar
```

The application will then be available on the URL: [http://localhost:8080](http://localhost:8080)

## Swagger

Swagger UI is available on URL: [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)

You could use the *Try it out* feature to test the endpoints directly.

## Endpoint

REST endpoint to validate the monthly overview of customer statement records available at:

* POST http://localhost:8080/validate