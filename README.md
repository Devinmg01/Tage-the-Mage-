# TAGE the MAGE

A 3D networked multiplayer survival game built in Java using the TAGE engine. Up to four wizards must defend their tower against waves of goblins in a fantasy-themed battlefield. Built by Devin Grace and Viktor Tarasov for CSC 165.

---

##  Overview

- **Genre:** Survival Action  
- **Theme:** Fantasy / Wizards  
- **View:** 3D Third-Person Ground-Level  
- **Core Activity:** Combat & Tower Defense  
- **Multiplayer:** Networked, up to 4 players via TCP

---

##  How to Play

Your goal is to survive and defend the wizard tower from goblins. Walk into goblins to damage them and survive as long as possible. The longer you last, the higher your score. Heal yourself and the tower by standing in the healing spotlight.

---

##  Getting Started

### 1. Compile the project

Navigate to the `tage-the-mage` directory and compile all files.

### 2. Open two terminal windows

- **Window 1:** Run the server:
  ```
  ./runServer
  ```

- **Window 2:** Run a player client:
  ```
  ./runClient        # Purple Wizard
  ./runClientRed     # Red Wizard
  ./runClientBlue    # Blue Wizard
  ./runClientGreen   # Green Wizard
  ```

Repeat client step for each player (supports up to 4 total).

---

##  Controls

### Keyboard
- `W A S D` - Movement
- `E / Q` - Zoom / Unzoom Camera
- `L` - Toggle Lights
- `Esc` - Quit

### Gamepad
- **Left Joystick** – Move and turn
- **Right Joystick** – Orbit camera
- **Left Bumper** – Pitch camera up
- **Right Bumper** – Pitch camera down
- **Left Trigger** – Zoom out
- **Right Trigger** – Zoom in
- **Select** – Quit game

---

##  Lighting System

- Wizard and tower lights change color based on health:
  - Green → Yellow → Red
- A **healing spotlight** exists on the map. Standing in it heals the tower.
- Lights can be toggled off for performance.

---

##  Environment

- **Skybox:** Cloudy daytime sky (outsourced)
- **Terrain:** Grassy valley with height-mapped hills (outsourced texture)

---

##  Features

### HUD
- Displays current score
- Shows player health and tower health

### Multiplayer
- 1–4 player support with increased difficulty per player
- Each player selects a different wizard (skin and color)

### Sound
- 3D positional audio:
  - Goblin laugh (NanaKisan)
  - Fireball sound (LiamG_SFX)
  - Wizard footsteps (Creative Commons)
- Background music (non-3D)

### Animation
- Goblin walk, attack
- Wizard idle, walk
- Fireball casting animation (not implemented but present in assets)

### Models
-  Custom: Wizard, Goblin, Textures, Animations
-  Outsourced: Tower, Skybox, Terrain

---

## ⚙ Engine & Technical Features

- **Custom 3D Orbit Camera** (pitch, yaw, globalYaw)
- **Physics Blocking:** Goblins can be blocked by wizard avatars
- **SceneGraph Hierarchy:**
  - Fireball is child of avatar
  - Goblin indicator plate is child of enemy
- **Network Protocol:** TCP client-server model

---

##  Known Limitations

- Fireball mechanics and animation not fully implemented (code/assets included)
- SceneGraph features in development — not fully optimized for gameplay impact

---

##  Credits

**Created by:**
- **Devin Grace** – Wizard model, animations, sounds, textures, Goblin model
- **Viktor Tarasov** –  physics, AI, game balancing
- Remaining game logic developed jointly

**Custom Assets:**
- Goblin model, texture, animation
- Wizard model, multiple textures, animations
- Tower texture

**Outsourced Assets:**
- Tower model (open domain)
- Terrain texture & skybox
- Footstep & goblin sounds (see attribution)
- Fireball SFX by LiamG_SFX
- Music by ECS FALLOUT & ECS SNEEZYMUD

---

##  Tools & Technologies

- Java
- TAGE Game Engine (custom)
- JOML for 3D math
- Custom Orbit Camera Class
- TCP Socket Networking

---

##  Directory Highlights

```
tage-the-mage/
├── runServer             # Launches multiplayer game server
├── runClient             # Default player client
├── /assets               # Models, textures, sounds
├── /src                  # Game logic, animations, network, AI
├── /scripts              # Build and run scripts
```

---

For questions or feedback, feel free to reach out to the creators. Good luck defending the tower!
