# Emergency SOS App: RESQ

Emergency App is an Android application designed to provide quick access to safety features such as SOS alerts, safe route guidance, and SMS notifications to trusted contacts.

## Features
- Location tracking with real-time updates  
- SOS alert system to send emergency messages with location  
- Safe route suggestions during travel  
- User authentication (registration and login)  
- Background services for SMS alerts and location updates
- Fake Call Simulator that allows users to simulate an incoming call to discreetly exit uncomfortable or unsafe situations.
- Emergency Chatbot that provides immediate guidance and first-aid instructions during medical emergencies until professional help is available.

## Tech Stack
- **Language:** Java (Android)  
- **IDE:** Android Studio  
- **Build Tool:** Gradle  
- **Database/Services:** (e.g., Firebase, SQLite — update if applicable)  

## Getting Started

### Prerequisites
- Android Studio installed  
- Minimum SDK version: (update with your app’s `minSdkVersion`)  
- API keys (e.g., Google Maps, Firebase) stored securely in `local.properties` (not included in this repository)  

### Setup
1. Clone the repository:  
   ```bash
   git clone https://github.com/AtreyeePhukan/EmergencyApp.git
   ```
2. Open the project in **Android Studio**  
3. Add required API keys in `local.properties`  
   ```properties
   MAPS_API_KEY=your_api_key_here
   ```
4. Build and run the project on an emulator or a physical device  

## Project Structure
```
EmergencyApp/
 ├── app/                # Application source code
 │   ├── java/           # Activities and services
 │   └── res/            # Layouts, drawables, values
 ├── gradle/             # Gradle wrapper files
 ├── build.gradle.kts    # Module build configuration
 └── settings.gradle.kts # Project settings
```

## License
This project is open-source and available under the [MIT License](LICENSE).
