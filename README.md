# Promise App

Promise App is an Android application for tracking personal promises and statistics associated with them. It helps users to monitor their progress, avoid breaking promises, and maintain a healthy streak of fulfilling commitments.

---
## Table of Contents
- [Features](#features)
- [Screenshots](#screenshots)
- [Usage](#usage)
- [Installation](#installation)
- [To do](#to-do)
- [Technologies Used](#TechnologiesUsed)
- [License](#license)

## Features

### Promises Management
- **Create Promises**: Add new promises with a description.
- **Delete Promises**: Remove promises that are no longer relevant.
- **Track Violations**: Automatically track the number of times each promise is broken.
- **Track your overall streak** : Track the current streak of days that you keep your word.

### Statistics Tracking
- **Current Streak**: Displays the number of consecutive days without breaking any promises.
- **Longest Streak**: Records the longest duration of unbroken promises.
- **Most Challenging Promise**:
  - Identifies the promise with the highest number of violations.
  - Updates dynamically as promises are added or removed.
- **Total Violations**: Tracks the cumulative number of promise violations for all promises.
- **Lifetime Violations**: Keeps a record of total violations over the lifetime of the app, even if promises are deleted.

### Quality of life
- **Localisation**: Russian and english localistion
- **Theme changer**: Day and night themes

---

## Screenshots

(Add screenshots of the app here to showcase its features.)

---

 ## Usage

1. Download Promise.apk from Releases page
2. Install apk

1. Add new promise via "+" button
2. Edit existing promise by taping on it
3. Reset promise timer and add Violations by long taping on promise
4. Delete promise by swiping it to the left
---

   ## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/pledge-app.git
   ```
2. Open the project in Android Studio.
3. Build the project to download all dependencies.
4. Run the app on an emulator or a physical device.

---

## To do

- [x] Add current streak number
- [x] Add edit existing promise page
- [x] Add statistic profile
- [x] Add localisation
- [ ] Fix "delete all" button
- [ ] Fix UI update system
- [ ] Fix profile "Total violations" tracker
---

## Technologies Used

- **Kotlin**: Primary programming language for the application.
- **Room Database**: Local persistence for storing promises and statistics.
- **Coroutines**: For managing asynchronous tasks efficiently.
- **MVVM Architecture**: Ensures clean separation of concerns and modularity.
- **Android Navigation Component**: For seamless navigation between app screens.

---

## Contribution

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Commit your changes and push them to your fork.
4. Open a pull request with a detailed description of your changes.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

