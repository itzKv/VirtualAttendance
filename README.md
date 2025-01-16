# Virtual Attendance

## Project Overview
Virtual Attendance is an application designed for small businesses to efficiently manage employee attendance. By leveraging modern technologies like fingerprint authentication, geolocation, and time tracking, the application streamlines absence management and provides detailed attendance records.

## Key Features

- Fingerprint Authentication: Employees can mark their attendance using fingerprint recognition, ensuring secure and accurate records.

- Geolocation & Time-based Tracking: Attendance validation is tied to specific locations and time frames to prevent false check-ins.

- Attendance History: A detailed summary of attendance is available, allowing businesses to review historical data.

- Firebase Integration: Real-time data synchronization and cloud storage for attendance records.

- SQLite Support: Supporting autofill for Admin when inserting Staff/Worker data.

## Tech Stack

#### Language: 
[![My Skills](https://skillicons.dev/icons?i=java)](https://skillicons.dev)

#### IDE: 
[![My Skills](https://skillicons.dev/icons?i=androidstudio)](https://skillicons.dev)

#### Backend:
[![My Skills](https://skillicons.dev/icons?i=firebase)](https://skillicons.dev)

#### Local Storage:
[![My Skills](https://skillicons.dev/icons?i=sqlite)](https://skillicons.dev)


## Getting Started

#### Prerequisites

- Android Studio (Ladybug)

- Firebase Account & Project
- Physical Smartphone with Fingerprint support for best experience
- [Optional] Physical Device with android version > 10
  
#### Installation

- Clone the repository:
  ```
  https://github.com/DLiech/VirtualAttendance.git
  ```
- Open the project in Android Studio.

- Connect Firebase to the project by following the Firebase setup guide.

- Sync Gradle and run the application.

- Please use this credential for first time Login:
  ```
  admin@gmail.com
  Testing123
  ```

## 🙏 Notes:
  - Make role Admin and map a user to the role for accessing Admin menu (apart from default account admin@gmail.com)
  - Any other role than Admin will be considered as staff-like role that have attendance feature
  - Attendance Button must be clicked twice for the first time to active the fingerprint feature and to read fingerprint
  - Device Location detected with GPS. Make sure you are in a place with a good signal like open spaces.
  - Last tested by DLiech at 15 Jan 2025 (master branch)

## 👀👀 Error Potential:
  - When logging in with wrong credentials, the app will crash. Please make new user or upgrade database version in DBHelper.java
  - When taking absence, fingerprint validation will popup and may be crashed in Android Studio emulator. Hence, please use device like smartphones that support fingerprints as emulator.

### Contributors:
<table>
  <tbody>
        <td align="center" valign="top" width="14.28%"><a href="https://github.com/DLiech"><img src="https://avatars.githubusercontent.com/u/122514634?v=4" width="100px;" alt="Deanzen Lie"/>
          <h2>DLiech</h2>
        </td>
        <td align="center" valign="top" width="14.28%"><a href="https://github.com/itzKv"><img src="https://avatars.githubusercontent.com/u/116947826?v=4" width="100px;" alt="Kevin Brivio"/>
          <h2>itzKv</h2>
        </td>
</tbody>
</table>
