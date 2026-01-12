# Emergency SOS App – RESQ

RESQ is an Android-based emergency assistance application designed to provide users with quick access to safety features such as SOS alerts, real-time location sharing, and emergency communication tools. The app focuses on personal safety during travel and emergency situations by combining location services, background alerts, and supportive utilities.

---

## Project Overview

The Emergency SOS App enables users to quickly notify trusted contacts during emergencies and access safety-related assistance through a single mobile application. The app is designed to work efficiently in critical situations where speed, simplicity, and reliability are essential.

The project demonstrates Android application development concepts including background services, location tracking, SMS handling, and user authentication.

---

## Key Features

- **SOS Alert System**  
  Sends emergency messages along with the user’s live location to trusted contacts.

- **Real-Time Location Tracking**  
  Tracks and updates the user’s location during active emergency sessions.

- **Safe Route Guidance**  
  Provides safer route suggestions during travel using location services.

- **User Authentication**  
  User registration and login to manage personal and emergency contact information.

- **Background Services**  
  Runs background processes for continuous location updates and SMS alerts during emergencies.

- **Fake Call Simulator**  
  Simulates an incoming call to help users discreetly exit uncomfortable or unsafe situations.

- **Emergency Chatbot**  
  Provides basic guidance and first-aid instructions during medical or safety emergencies until professional help is available.

---

## Tech Stack

- **Platform:** Android  
- **Language:** Java  
- **IDE:** Android Studio  
- **Build Tool:** Gradle  
- **Services & Storage:**  
  - Firebase (for authentication and backend services, if configured)  
  - SQLite / local storage (for on-device data handling)

---

## Getting Started

### Prerequisites
- Android Studio installed
- Android device or emulator
- Required API keys (e.g., Google Maps, Firebase)

API keys are not included in the repository and should be stored securely.

---

### Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/AtreyeePhukan/EmergencyApp.git
2. Open the project in Android Studio.
3. Add required API keys to local.properties:
   MAPS_API_KEY=your_api_key_here
4. Build and run the application on an emulator or physical Android device.

### Project Structure

EmergencyApp/
 ├── app/                # Application source code
 │   ├── java/           # Activities, services, and logic
 │   └── res/            # Layouts, drawables, values
 ├── gradle/             # Gradle wrapper files
 ├── build.gradle.kts    # Module build configuration
 └── settings.gradle.kts # Project settings

## Known Limitations

- Safe route suggestions depend on external map services and network availability.
- The emergency chatbot provides basic guidance and is not a replacement for professional medical or emergency services.
- Background services may be affected by device-level battery optimization settings.

## Future Improvements

- Integration with emergency helpline APIs.
- Enhanced offline functionality.
- Improved chatbot intelligence and multilingual support.
- Wearable device integration for faster SOS triggering.

## License
This project is open-source and available under the [MIT License](LICENSE).

## Author
Atreyee Phukan

