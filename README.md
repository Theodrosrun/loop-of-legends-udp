# Loop of Legends: The Multi-Snake Challenge

**"Loop of Legends: The Multi-Snake Challenge"** is a networked snake game where players control snakes and try to survive as long as possible. This project implements the game client, which connects to a game server for a multiplayer experience.

````
  _                                                    ______
 | |     ___   ___  _ __                          _.-""      ""-._
 | |    / _ \ / _ \| '_ \                      .-'                `-.
 | |___| (_) | (_) | |_) |                   .'      __.----.__      `.
 \_____/\___/ \___/| .__/                   /     .-"          "-.     \
         __        | |                     /    .'                `.    \
        / _|       |_|                    .    /                    \    \
   ___ | |_                               /   ´                      `   `
  / _ \|  _|                             ´   '                        '   |
 | (_) | |                               |   |                        |   |
  \___/|_|                               |   |                        |   |
  _                               _      |   |                        |   |
 | |     ___  __ _  ___ _ __   __| |     |   .                        .   .
 | |    / _ \/ _` |/ _ \ '_ \ / _` |     `    \   .------.           /    ´
 | |___|  __/ (_| |  __/ | | | (_| |      \    \ /        \   __    /    /
 \_____/\___|\__, |\___|_| |_|\__,_|       \    (|)(|)_   .-'".'  .'    /
              __/ |                         \    \   /_>-'  .<_.-'     /
             |___/                           `.   `-'     .'         .'
                                               `--.|___.-'`._    _.-'

                             Press enter to play   ^

````

## 1. Building the Application

Loop of snake has been developed in a Maven repository utilizing the multi-module approach. To launch this game, it is necessary to open two separate terminals.

In the first terminal, you will start the server module. This module is responsible for managing interactions between players and maintaining the overall state of the game. 

In the second terminal, you will launch the client module. The client is the interface through which players interact with the game. This module communicates with the server to send commands and receive updates on the game's state.

### Prerequisites
Ensure you have the Java Runtime Environment 17 (JRE) or Java Development Kit 17 (JDK) installed on your computer. The version of Java should be compatible with the application.

### Step 1: Install Java
- Install the **Java Runtime Environment 17** or **Java Development Kit 17**.
- Ensure the Java version is compatible with the application.
- You can check your Java version by running `java -version` in the command line.

### Step 2: Set File Permissions (Unix/Linux Only)
- On Unix/Linux systems, you might need to make the JAR file executable:

  ```bash
  chmod +x path/to/jar.jar
  ```

### Step 3: Dependencies

1. **Logback Classic**
    - Group ID: `ch.qos.logback`
    - Artifact ID: `logback-classic`
    - Version: `1.4.11`

2. **Picocli**
    - Group ID: `info.picocli`
    - Artifact ID: `picocli`
    - Version: `4.7.5`

3. **Lanterna**
    - Group ID: `com.googlecode.lanterna`
    - Artifact ID: `lanterna`
    - Version: `3.1.1`

---

## 2.1 Running the Application with Docker

### Explanation on Using Lanterna with Docker

As part of our project, we have prepared Dockerfiles and a `docker-compose.yml` file to facilitate the deployment and management of our Docker containers. However, it's important to address a significant limitation related to the use of the Lanterna library in the Docker environment.

#### The Challenge of Lanterna in Docker
Lanterna is a Java library designed for creating text-based user interfaces in a terminal. While it's extremely useful for CLI (Command Line Interface) applications, its compatibility with Docker can be problematic. Lanterna requires a terminal (TTY) environment to function properly, which can present challenges in a Docker container, particularly when attempting to achieve graphical mode interaction or an interactive user interface.

#### Docker Images Created for Indicative Purposes
Due to this limitation, the Docker images we created for the server, client, and viewer of the project should be considered primarily for indicative purposes. Although the Dockerfiles and `docker-compose.yml` are technically correct and functional, they may not provide an optimal user experience with Lanterna due to the constraints of the Docker environment.

#### Solutions and Alternatives
For those interested in experimenting with the project in Docker, it is possible to attempt alternative configurations, such as setting up a pseudo-TTY or using Docker in interactive mode. However, these solutions may not fully resolve the issues of interaction with the Lanterna user interface.

#### Conclusion
We encourage users to consider this limitation when using our Docker images with Lanterna. For an optimal experience, it may be preferable to run the application directly on a host machine with an adequate terminal environment, outside of a Docker container.

---

## 2.2 Running the Application

All commands below must be performed in the target folder.

For launching applications on Windows, use the `javaw` command, while on Linux, use the same command but with `java` instead of `javaw`.

<br>

#### Launching server:
By default, the server listens on unicast port 10000 and multicast port 20000, with multicast host address set to 239.1.1.1. Additionally, it streams to a multicast stream host 239.1.1.2 on port 20001.
```bash
javaw -jar server-1.0-SNAPSHOT.jar [--unicast-port=port] [--multicast-host=host] [--multicast-port=port] [--multicast-stream-host=host] [--multicast-stream-port=port]
```
Examples:
```bash
javaw -jar server-1.0-SNAPSHOT.jar
```
```bash
javaw -jar server-1.0-SNAPSHOT.jar --unicast-port=40000 --multicast-host=239.1.1.1 --multicast-port=20000 --multicast-stream-host=239.1.1.2 --multicast-stream-port=20001
```

<br>

#### Launching client:
The client, by default, connects to a unicast host at 127.0.0.1 on port 10000 and a multicast host at 239.1.1.1 on port 20000.
```bash
javaw -jar client-1.0-SNAPSHOT.jar [--unicast-host=host] [--unicast-port=port] [--multicast-host=host] [--multicast-port=port]
```
Examples:
```bash
javaw -jar client-1.0-SNAPSHOT.jar
```
```bash
javaw -jar client-1.0-SNAPSHOT.jar --unicast-host=192.168.1.10 --unicast-port=40000 --multicast-host=239.1.1.1 --multicast-port=20000
```

<br>

#### Launching viewer:
By default, the viewer connects to a multicast stream host at 239.1.1.2 on port 20001.
```bash
javaw -jar viewer-1.0-SNAPSHOT.jar [--multicast-stream-host=host] [--multicast-stream-port=port]
```
Examples:
```
javaw -jar viewer-1.0-SNAPSHOT.jar
```
```bash
javaw -jar viewer-1.0-SNAPSHOT.jar --multicast-stream-host=239.1.1.2 --multicast-stream-port=20001
```

<br>

### How to play
Now that you are ready, Follow these simple steps to start playing and enjoy the game:

#### **Game Objective**:
   - Your goal is to grow your snake by eating special items that appear in the arena.
   - The more you eat, the longer your snake grows, making the game more challenging and exciting.

#### **Enter Your Username**:
   - Start by entering your unique username. This name will represent you in the game and will be visible to other players. Choose a name that is distinctive and memorable!

#### **Declare Ready**:
   - Once you have entered your name and are ready to play, press the `R` key. This will signal to other players and the game system that you are ready to begin. Wait for other players to also declare themselves ready.

#### **Help Menu**:
   - To view the help menu with game instructions and tips, press the `H` key. This will bring up a comprehensive guide covering various aspects of the game, including controls, objectives, and frequently asked questions. The menu is designed to assist players.
   
#### **Using the Arrow Keys**:
   - The game begins! Use the arrow keys to control your snake in the game arena.
     - **Up:** Press the up arrow to move your snake upwards.
     - **Right:** Use the right arrow to turn right.
     - **Down:** The down arrow will move your snake downwards.
     - **Left:** And finally, use the left arrow to turn left.

#### **Exiting the Game**:
- If you wish to leave the game, simply press the `Q` key (or the designated exit key). This action will safely exit you from the game session.

---

## 3. Application Protocol

## 3.1 Overview
The Loop of Legend (LOL) protocol is designed specifically for online multiplayer snake games, operating on a client-server architecture. This protocol facilitates real-time interactions and gameplay over a network, providing a seamless gaming experience.

- The main objective of the LOL protocol is to enable players to play the classic snake game in a networked, multiplayer environment.
- Players (clients) connect to a central game server. The server manages game lobbies, player actions, and game state updates.
- To start a game, a client connects to the server and requests to join a game. The game begins when the lobby has sufficient players, and all players indicate they are ready.

## 3.2 Transport protocol
### Unicast Protocol and Port
- **Protocol**: TCP (Transmission Control Protocol) is used for reliable, ordered, and error-checked delivery of game data.
- **Port**: The default unicast communication port for the game server is `10000`.

### Multicast Protocol and Port
- **Protocol**: UDP (User Datagram Protocol) for multicast communication, providing efficient data distribution among multiple clients.
- **Port**: The default multicast port used for game data broadcasting is `20000`, with the multicast address set to `239.1.1.1`.

### Streaming Protocol and Port
- **Protocol**: UDP is also used for streaming game updates.
- **Port**: The game streaming data is sent over the multicast port `20001`, using the multicast stream host address `239.1.1.2`.

### Connection Initiation
- The connection is initiated by the client.
- The client sends an `INIT` message to the server to establish a connection.
- The server, upon receiving the `INIT` message, responds with `DONE` if the connection is successfully established.

## 3.3 Messages/Actions
The client can send the following messages:
- `INIT`: Start the connection.
- `LOBB`: Check lobby availability.
- `JOIN`: Join the game lobby.
- `RADY`: Indicating readiness for the game.
- `DIRE <direction>`: Indicating the snake's direction.
    - `0`: Up direction
    - `1`: Down direction
    - `2`: Left direction
    - `3`: Right direction

The server can send the following messages:
- `DONE`: Indicating successful completion of the previous request. See details below.
- `STRT`: Signaling the start of the game.
- `REPT`: Reporting game events or status.
- `UPTE <map>`: Used to send the map. Updating the game state.
- `EROR`: Error message for any issues encountered. See details below.
- `ENDD`: Indicates the end of the game.

Common messages
- `MSGG`: This message is used for general communication within the game. It can be employed for various purposes, such as sending information about current actions. The specific content of the message would depend on the context in which it is used.
- `QUIT`: This message is sent when exiting the game. Upon receiving a QUIT message, the server typically performs cleanup operations related to the client, such as removing the player from the game lobby or freeing up resources.
- `UNKN`: This message represents an unknown or invalid command. This could happen if there's a typo in the command, the client uses a command not supported by the server or the message format is incorrect.

### Success/Error Codes
- `DONE`: This code indicates that an action requested by the client has been completed successfully. For example, when a player successfully joins a lobby, the server might respond with a `DONE` message. It's a general acknowledgment sent from the server to confirm that the requested operation was executed without any issues.
- `EROR`: This code signifies that an error occurred while processing the client's request. It is usually followed by an error message that provides more details about the nature of the error. Examples of situations that might generate an `EROR` message include attempts to join a full game lobby. The accompanying error message helps in understanding the specific reason for the failure.

### Edge-Cases Handling
- **Full Lobby**: In cases where a client attempts to join a game lobby that is already full, the server responds with a specific `EROR` message indicating that the lobby is at capacity.
- **Username Already Taken**: When a client attempts to register or join with a username that is already in use, the server responds with a specific `REPT` message indicating that the username is unavailable. This message can guide the user to choose an alternative username.
- **Socket Closed/Disconnection**: If the client's connection to the server is unexpectedly closed (e.g., due to a socket closure), the client's termnal will display a notification message to the user. This message informs the user about the disconnection. This straightforward communication helps the user understand that the issue is related to the network connection and guides them on the immediate steps they can take. The client's application is responsible for detecting when the socket has been closed.

## 3.4 Examples
### Clients play - Multiplayers:
![Example Image](https://github.com/Theodrosrun/loop-of-legends/blob/23-protocol-finalization/docs/clientsPlay.png)
### Client leaves:
![Example Image](https://github.com/Theodrosrun/loop-of-legends/blob/23-protocol-finalization/docs/clientLeaves.png)
### Lobby is full:
![Example Image](https://github.com/Theodrosrun/loop-of-legends/blob/23-protocol-finalization/docs/lobbyIsFull.png)
### Username Already Taken:
![Example Image](https://github.com/Theodrosrun/loop-of-legends/blob/feature/improving-readme/docs/usernameAlreadyTaken.png)