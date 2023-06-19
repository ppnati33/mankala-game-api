# Mancala Game Application with Spring Boot
This service implements Mancala Game Back End REST API

![img_4.png](img_4.png)

## Game description 

![img_3.png](img_3.png)

### Board Setup
Each of the two players has his six pits in front of him. To the right of the six pits,
each player has a larger pit. At the start of the game, there are six stones in each
of the six round pits .

### Rules

#### Game Play
The player who begins with the first move picks up all the stones in any of his own
six pits, and sows the stones on to the right, one in each of the following pits,
including his own big pit. No stones are put in the opponents' big pit. If the player's
last stone lands in his own big pit, he gets another turn. This can be repeated
several times before it's the other player's turn.

#### Capturing Stones
During the game the pits are emptied on both sides. Always when the last stone
lands in an own empty pit, the player captures his own stone and all stones in the
opposite pit (the other player’s pit) and puts them in his own (big or little?) pit.

#### The Game Ends
The game is over as soon as one of the sides runs out of stones. The player who
still has stones in his pits keeps them and puts them in his big pit. The winner of
the game is the player who has the most stones in his big pit.


## Technology stack
* Java 17
* Spring Boot
* Mongo DB

## Pre-requisites
* JDK 17
* Docker

## Building

### Testing

`./gradlew test`

### Building (no tests)

`./gradlew assemble`

### Building (with tests)

`./gradlew build`

### Running in Docker

The following commands required to run from project root folder in your terminal:

* docker-compose build
* docker-compose up`

## API Usage

There is [Swagger UI for API description](http://localhost:8080/swagger-ui/index.html)

### Game Management
* get game by id - GET [/games/${gameId}](http://localhost:8080/games/648f1acd3196207e7976a5b1) to get a json with some particular game parameters
* create a new game - POST [/games](http://localhost:8080/games) with empty body to create a new game with default board setup
* sow - PUT [/games/${gameId}/pits/${pitId}](http://localhost:8080/games/648f1acd3196207e7976a5b1/pits/13) make a move for some particular gameId and pitId

## Need further support?
Contact me if you need help at ppnati33@gmail.com