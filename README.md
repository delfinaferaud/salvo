# Salvo!

Web-based multi-player game application

This is a multi-player online version of a Salvo-like game engine. Taking into account that Salvo was a pencil and paper game that was the basis for the popular Battleship game. The basic idea involves guessing where other players have hidden objects.

I created a front-end web application that game players interact with, and a back-end game server to manage the games, scoring, and player profiles. Using JavaScript for the front-end client, and the Spring framework for the Java-based RESTful web server.


📌 The core architecture is divided as follows:

A small Java back-end server that stores Salvo game data, and then sends that data to client apps via a RESTful API.
A front-end browser-based game interface that graphically shows players the state of the game, including ships they've placed, damage sustained, and scores.


🕹 The game play business' logic is as follows:

Players can create new games and join games that others have created.
When a game has both players, players can place their ships on their grids.
When ships have been placed, players can begin trading salvos (shots) and seeing the results (hits, sinks, and misses).
When all of a player's ships have been sunk, the game ends and the winner is added to the leaderboard.


🔧 Technologies used:

Project developed on Java.
Spring 2.1.4
Maven
H2 as a Database and Java Persistence Api (JPA)
Front-end developed on HTML5, CSS3, Bootstrap v4.1.3, jQuery and JavaScript.


![interface1](https://user-images.githubusercontent.com/76971547/116643728-13ceee00-a948-11eb-8084-0b2f3eba73f3.jpg)

![interface2](https://user-images.githubusercontent.com/76971547/116643801-33661680-a948-11eb-867e-1c95806e9a77.jpg)

![interface3](https://user-images.githubusercontent.com/76971547/116643809-3a8d2480-a948-11eb-8672-451bd914a03f.jpg)

