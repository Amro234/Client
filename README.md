# Tic Tac Toe Pro - Client Application

A feature-rich, multiplayer Tic Tac Toe game built with JavaFX, offering single-player AI, local multiplayer, and online multiplayer modes with game recording and replay capabilities.

![Java](https://img.shields.io/badge/Java-11-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-13-blue)
![Maven](https://img.shields.io/badge/Maven-3.8.0-red)
![License](https://img.shields.io/badge/license-MIT-green)

## ✨ Features

### Core Gameplay

- **Multiple Game Modes**
  - Single Player vs AI with adjustable difficulty levels (Easy, Medium, Hard)
  - Local Two-Player mode
  - Online Multiplayer with real-time synchronization

### Advanced Features

- **Game Recording & Replay**
  - Record your matches
  - Replay saved games
  - Match history tracking
- **Online Features**
  - Real-time multiplayer gameplay
  - Game lobby system
  - Rematch functionality
- **Rich User Interface**

  - Modern, responsive JavaFX UI
  - Sound effects and background music
  - Customizable settings

- **User Management**
  - User authentication (Login/Sign up)
  - Profile management
  - Game statistics tracking
  - Match history

### Audio & Visual

- Background music with volume control
- Sound effects for game actions
- Visual feedback for moves and wins
- Animated game result videos (Win/Lose/Draw)
- Glowing effects and smooth transitions

## 📦 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 11 or higher**
  - Download from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or [OpenJDK](https://openjdk.java.net/)
- **Apache Maven 3.6+**
  - Download from [Maven Official Site](https://maven.apache.org/download.cgi)
- **JavaFX 13**
  - Included as Maven dependencies

## 🚀 Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/Amro234/Client.git
   cd Client
   ```

2. **Navigate to the Client directory**

   ```bash
   cd Client
   ```

3. **Install dependencies**
   ```bash
   mvn clean install
   ```

## ▶️ Running the Application

### Using Maven

```bash
mvn clean javafx:run
```

### Using IDE (NetBeans/IntelliJ/Eclipse)

1. Import the project as a Maven project
2. Wait for dependencies to download
3. Run the `Launcher.java` or `App.java` class

### Debug Mode

```bash
mvn clean javafx:run@debug
```

Then attach your debugger to `localhost:8000`

## 🔨 Building the Application

### Create JAR file

```bash
mvn clean package
```

This will create:

- A shaded JAR file: `target/Tic Tac Tac Toe Pro.jar`
- A Windows executable: `target/Tic Tac Toe Pro.exe` (Windows only)

### Run the JAR

```bash
java -jar target/Tic\ Tac\ Tac\ Toe\ Pro.jar
```

## 📁 Project Structure

```
Client/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mycompany/client/
│   │   │       ├── App.java                    # Main application entry
│   │   │       ├── Launcher.java               # Launcher for JAR
│   │   │       ├── auth/                       # Authentication module
│   │   │       │   └── controller/
│   │   │       │       ├── LoginController.java
│   │   │       │       ├── SignController.java
│   │   │       │       └── SplashController.java
│   │   │       ├── core/                       # Core utilities
│   │   │       │   ├── navigation/             # Navigation service
│   │   │       │   ├── notification/           # Notification system
│   │   │       │   ├── server/                 # Server connection
│   │   │       │   └── session/                # User session management
│   │   │       ├── difficulty/                 # Difficulty selection
│   │   │       ├── gameboard/                  # Game board logic
│   │   │       │   ├── controller/
│   │   │       │   │   └── GameBoardController.java
│   │   │       │   └── model/                  # Game models
│   │   │       ├── gameLobby/                  # Online game lobby
│   │   │       ├── mainmenu/                   # Main menu
│   │   │       ├── match_recording/            # Recording & replay
│   │   │       ├── matches/                    # Match management
│   │   │       ├── profile/                    # User profile
│   │   │       ├── settings/                   # Settings & audio
│   │   │       └── GameResultVideoManager/     # Result animations
│   │   └── resources/
│   │       ├── assets/
│   │       │   ├── audios/                     # Sound effects & music
│   │       │   └── images/                     # UI images & icons
│   │       ├── com/mycompany/client/           # FXML files
│   │       │   ├── game_board.fxml
│   │       │   ├── gameLobby.fxml
│   │       │   ├── login.fxml
│   │       │   ├── main-menu.fxml
│   │       │   ├── match_history.fxml
│   │       │   ├── profileScreen.fxml
│   │       │   ├── recordings.fxml
│   │       │   ├── settings.fxml
│   │       │   └── ...
│   │       ├── fonts/                          # Custom fonts
│   │       ├── styles/                         # CSS stylesheets
│   │       └── videos/                         # Game result videos
│   │           ├── win/
│   │           ├── lose/
│   │           └── draw/
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

## 🏗️ Architecture

### Design Patterns

- **MVC (Model-View-Controller)**: Separation of UI (FXML), logic (Controllers), and data (Models)
- **Singleton**: Used for managers (NavigationService, BackgroundMusicManager, SoundEffectsManager)
- **Observer**: Event-driven architecture for game state updates
- **Factory**: Session creation for different game modes

### Key Components

#### 1. **Navigation Service**

Centralized navigation system for switching between screens.

#### 2. **Server Connection**

Manages WebSocket connection to the game server for online multiplayer.

- Heartbeat mechanism for connection monitoring
- Automatic reconnection handling
- Message queue for reliable communication

#### 3. **Game Session Management**

- `SinglePlayerSession`: AI opponent gameplay
- `LocalTwoPlayerSession`: Local multiplayer
- `ClientOnlineSession`: Online multiplayer with server sync

#### 4. **Audio Management**

- `BackgroundMusicManager`: Handles background music playback
- `SoundEffectsManager`: Manages game sound effects

#### 5. **Recording System**

- `GameReplayManager`: Records and replays game moves
- `MatchHistoryController`: Displays past games

## 🎮 Game Modes

### 1. Single Player (vs AI)

Play against an AI opponent with three difficulty levels:

- **Easy**: Random moves with occasional smart plays
- **Medium**: Balanced strategy
- **Hard**: Minimax algorithm for optimal play

### 2. Local Two-Player

Play with a friend on the same device. Players take turns on the same screen.

### 3. Online Multiplayer

Connect to a game server and play with other players online:

- Join game lobby
- Automatic matchmaking
- Real-time move synchronization
- Rematch functionality

## 🛠️ Technologies Used

### Core Technologies

- **Java 11**: Programming language
- **JavaFX 13**: UI framework
- **Maven**: Build and dependency management

### Libraries & Dependencies

- **org.json**: JSON parsing
- **JavaFX Controls**: UI components
- **JavaFX FXML**: Declarative UI
- **JavaFX Media**: Audio/video playback

### Build Tools

- **Maven Compiler Plugin**: Java compilation
- **JavaFX Maven Plugin**: JavaFX application support
- **Maven Shade Plugin**: Fat JAR creation
- **Launch4j**: Windows executable generation

## ⚙️ Configuration

### Server Configuration

By default, the application connects to `localhost:5000`. To change the server address:

1. Modify `ServerConnection.java`:

   ```java
   private static String SERVER_HOST = "your-server-address";
   private static final int SERVER_PORT = 5000;
   ```

2. Or use the settings screen in the application (if implemented)

### Audio Settings

Audio files should be placed in:

- Background music: `src/main/resources/assets/audios/`
- Sound effects: `src/main/resources/assets/audios/`

### Video Settings

Game result videos should be placed in:

- Win videos: `src/main/resources/videos/win/`
- Lose videos: `src/main/resources/videos/lose/`
- Draw videos: `src/main/resources/videos/draw/`

## 🎨 Customization

### Themes & Styles

CSS files are located in `src/main/resources/styles/`:

- `style.css`: Main application styles
- `customStyles.css`: Custom component styles
- `game_board.css`: Game board specific styles
- `profilescreen.css`: Profile screen styles
- `table.css`: Table component styles

## 📝 Development Notes

### Adding New Screens

1. Create FXML file in `src/main/resources/com/mycompany/client/`
2. Create controller in appropriate package
3. Use `NavigationService.navigateTo("screenName")` to navigate

### Database/Persistence

Currently, the application relies on server-side persistence for:

- User accounts
- Player statistics

Local recordings are saved to the file system.

## 🔮 Future Enhancements

- Tournament mode
- Leaderboards
- Chat system
- Customizable board themes

- Mobile version
- Game statistics dashboard
- Friend system
- Achievements

## 👥 Authors

- **Amro Mohamed Ali Mahmoud**
- **Eslam Ehab Mohamed Lotfy**
- **Ahmed Khaled Mahmoud**
- **Antoneos Philip Samir**
- **Mohamed Ali Abdelfattah**

---

**Made with ❤️ using JavaFX**
