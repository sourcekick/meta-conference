CREATE TABLE "conferences" (
  uuid VARCHAR(37) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  "from" TIMESTAMP NOT NULL,
  "to" TIMESTAMP NOT NULL
);