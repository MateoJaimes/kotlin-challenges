# TicNet — Public WiFi Zone Locator

Android app built for the **TicNet Corp** challenge (Misión TIC 2022, Universidad
Pontificia Bolivariana). TicNet operates public WiFi access points across 1,000
towns in Colombia; this app helps a signed-in user find the nearest, least
congested access point and get directions to it.

Built in **Kotlin** with **Jetpack Compose**, following the four consecutive
challenge briefs ("retos") that make up the project.

---

## Tech stack

- Kotlin
- Jetpack Compose (Material 3)
- Single-activity architecture, state managed with `remember` / `mutableStateOf`
  (no navigation library — screen switching is handled with a simple enum-based
  state machine)

---

## Features by challenge

### Challenge 1 — Login & security captcha
- Welcome message before requesting credentials.
- Username and password are both derived from the group code assigned in the
  "Fundamentos de programación" course (password = username reversed).
- A math captcha is generated on top of a successful username/password check:
  one term comes from the last 3 digits of the code, the other is computed
  through several arithmetic operations that resolve to the second-to-last
  digit.

### Challenge 2 — Recurring, adaptive menu
- Numbered menu (1–7) shown after login; the user decides when to leave the
  menu or close the session.
- **Favorite option**: the user can pick one of the first 5 options to always
  appear first. The change requires answering two riddles whose answers are
  the last two digits of the group code. Failing either riddle reverts the
  menu to its default order.
- Invalid input is tracked; three consecutive invalid entries end the
  session.

### Challenge 3 — Password & location data
- **Change password**: requires confirming the current password before
  accepting a new one (which must differ from the current one).
- **Register frequent coordinates**: latitude/longitude for three places
  (work, home, park), entered one value at a time and validated against a
  latitude/longitude range table keyed by a digit of the group code
  (10 possible municipalities).
- **Update coordinates**: shows the three stored coordinates plus two
  "key info" hints (northernmost / southernmost / easternmost / westernmost /
  average point), chosen from a second lookup table, before letting the user
  pick which coordinate to update.

### Challenge 4 — Nearest WiFi zone & directions
- Four predefined WiFi zones (with average connected users) are loaded from a
  lookup table keyed by the same group-code digit used for the coordinate
  ranges.
- The user picks which of their three saved coordinates is their current
  location. Distance to each zone is computed with the **Haversine formula**
  (Earth radius = 6372.795477598 km).
- The two closest zones are shown, sorted by **average connected users**
  (ascending), with distance in meters.
- Picking one of the two zones shows a direction hint (east/west, then
  north/south) and the estimated travel time for two transport modes chosen
  from a group-code lookup table (bus, on foot, bike, motorcycle, car).

---

## Project structure

```
app/
 └─ src/main/java/com/example/reto_1/
     └─ MainActivity.kt   # everything: login, menu, retos 3 & 4 logic
```

Everything currently lives in a single file to keep the four challenges easy
to follow end to end. The file is organized in clearly commented sections:

```
// RETO 1  -> captcha logic
// RETO 2  -> menu, favorites, screen state
// RETO 3  -> coordinate range tables, password/coordinate screens
// RETO 4  -> WiFi zone tables, Haversine distance, directions
```

---

## Group-code-dependent tables

Several behaviors depend on digits of the group code entered at login
(the same code used as the username):

| Digit used          | Drives                                                        |
|----------------------|----------------------------------------------------------------|
| 2nd-to-last digit    | Captcha's second term · favorite-riddle #1 answer · coordinate range table · WiFi zone table |
| Last digit           | Favorite-riddle #2 answer · "key info" hints · transport modes table |

This mirrors the way the original brief ties every dataset to the student's
own group code.

---

## Running the app

1. Open the project in Android Studio.
2. Run on an emulator or device (min SDK per the existing `Reto_1Theme`
   project setup).
3. Log in with your group code as both username and password-hint source
   (password = code reversed), then solve the captcha shown.

---

## Known limitations / things to double-check

- **Format mismatch with the official brief**: challenges 2–4 ask for a
  console Python script (`reto2.py`, `reto3.py`, `reto4.py`). This project
  continues in Kotlin/Compose to match Challenge 1, which was already built
  that way — confirm with your instructor whether this is acceptable for
  grading, or whether a Python version is still required.
- **Password change doesn't persist across logins**: login always validates
  against `username.reversed()`, so a changed password only applies for the
  current session.
- **Menu options 4 and 5** ("Save file with nearby location" / "Update WiFi
  zone records from file") are placeholders — none of the four briefs detail
  functional requirements for them.
- On any "program must finish execution" case from the original (console-
  oriented) spec, this Android version returns the user to the login screen
  instead of closing the app.

---

## Credits

Built for the **Misión TIC 2022** program (MinTIC + Universidad Pontificia
Bolivariana), TicNet Corp challenge series.
