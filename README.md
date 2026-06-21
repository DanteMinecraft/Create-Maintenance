# 🛠️ Create: Maintenance Control
![Modrinth](https://github.com/user-attachments/assets/ba69b03d-1f09-458c-9429-817854d7793a)

Create: Maintenance Control is a small addon for Create that lets you temporarily disable Train Stations, making trains skip them without having to edit their train schedules manually.

Place a Maintenance Box next to a station to mark it as under maintenance. Trains with that station in their schedules will automatically skip it until maintenance is disabled again.

## ⬇️ Download the mod
- Download from Modrinth [here](https://modrinth.com/mod/create-maintenance-control)
- Download from CurseForge [here](https://www.curseforge.com/minecraft/mc-mods/create-maintenance-control)

## 🛠️ Dependencies
- This mod requires [Create](https://modrinth.com/mod/create)
- Using a recipe viewer such as JEI or EMI is recommended

## 🔎 Features
### Maintenance Box
Place a Maintenance Box next to a Train Station to mark it as under maintenance.

While maintenance is active:

- trains will skip that station instead of stopping there
- train schedules do not need to be edited manually
- redstone can be used to temporarily disable the maintenance effect

#### Example use cases
- Temporarily closing a platform during rebuilding
- Skipping a station during track work
- Toggling maintenance with redstone for operational events

## Current behaviour / limitations
- If a disabled station is a terminus, trains may encounter route errors unless the network allows them to pathfind from the previous stop to the next scheduled one.
  - This is currently the intended behavior rather than a bug/limitation.
- The mod currently focuses on skipping maintenance-marked stations rather than rerouting trains to alternative destinations. More advanced routing behaviour may be explored in future updates.
- Best results are achieved on networks where trains can still pathfind from the previous stop to the next one.

## ⚠️ Known issues
- Maintenance Boxes do not always behave correctly in unloaded chunks.
  - This is tagged as a high-priority issue and will be addressed before any major new features are added.

## 🤝 Compatibility
The mod has been tested in larger Create-based modpacks and appears to work fine alongside many common Create addons.

This includes initial testing with:
- Create: Aeronautics (MC 1.21.1)
- Sable (MC 1.21.1)
- Create Railways Navigator
- Steam 'n' Rails

## 📌 Planned

### Maintenance Box GUI
Planned options include:
  - a station name filter field
  - a redstone behavior toggle (powered = maintenance / unpowered = maintenance)
  - a "__Skip unreachable downstream stations__" toggle for more advanced network layouts

### Documentation / Usability
- a Ponder scene for the Maintenance Box
- expanded documentation and examples on the Wiki page here on GitHub
