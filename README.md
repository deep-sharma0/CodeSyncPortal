# CodeSync Portal 💻🧑‍💻

**Real-Time Collaborative Code Interview Tool** built with **Java, JavaFX, Socket Programming, and Threads**.

## 🚀 Features

- 🔒 Secure client-server connection
- ✍️ Real-time code collaboration
- 💬 Live chat messaging
- 👥 Multiple users support
- 🕵️ Cheat prevention-ready layout
- 💾 Message & Code logging (server side)

## 🧰 Technologies Used

- Java 17
- JavaFX 17
- Socket Programming
- Multi-threading
- OOP Principles
- MVC Pattern

## 🛠️ Getting Started

### Prerequisites

- JDK 17+
- JavaFX SDK 17 (placed in `C:\Users\ds754\Desktop\javafx-sdk-17.0.16`)

### Run the Server

```bash
cd server
javac ServerMain.java
java ServerMain

cd client/view
javac CodeEditor.java
java --module-path "C:\Users\ds754\Desktop\javafx-sdk-17.0.16\lib" --add-modules javafx.controls,javafx.fxml client.view.CodeEditor

📁 Folder Structure
CodeSyncPortal/
├── client/
├── server/
├── shared/
├── assets/
├── logs/
└── README.md
👨‍💻 Author
Deep Sharma
LinkedIn | GitHub
