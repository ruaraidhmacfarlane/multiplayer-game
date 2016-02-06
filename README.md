# multiplayer-game

A simple multi-user dungeon adventure game using Java RMI. 

##Run the game

To run the game you need to do the following:

* Build it: `make`
* Start the Java RMI registry: `rmiregistry {REGISTRY_PORT}`
* Start the server: `java lib.Server {REGISTRY_PORT} {SERVER_CALLBACK_PORT}`
* Start a client: `java lib.Client {REGISTRY_PORT} {CLIENT_CALLBACK_PORT}`
* Add more clients as above. Each new client needs a new callback port

The application currently defaults to `localhost`.

##Objective

Find the treasure!

