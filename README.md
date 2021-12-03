# General

Example project demonstrating the use of mongodb, kafka and webflux 

# Funcionality
Create, update and delete meals, customers and orders. Every action in an order sends a message to the output topic.

# Docker images used

docker pull mongo

docker pull wurstmeister/kafka

docker pull wurstmeister/zookeeper
