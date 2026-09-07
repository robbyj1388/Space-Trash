# Multi-Mouse Godot Add-on

This folder contains the Godot-facing layer: plugin registration, the
`MultiMouse` node, and the platform DLLs produced by the build scripts.

## Using the node

1. Enable the plugin under **Project → Project Settings → Plugins**.
2. Add a `MultiMouse` node anywhere in your scene tree (it is no longer an
   autoload-only singleton).
3. In your script, wire it up:
   ```gdscript
   @onready var multi_mouse: MultiMouse = $MultiMouse

   func _ready():
       multi_mouse.attach_to_window(0) # HWND on Windows, ignored elsewhere
       multi_mouse.enable()
       multi_mouse.motion.connect(_on_motion)
       multi_mouse.button.connect(_on_button)
   ```
4. Handle the signals just like normal Godot mouse events, with the bonus of
   `event.device` / `event.get_meta("device_guid")` letting you separate players.

The node also exposes `get_devices()`, `request_poll()`, and the
`device_connected` / `device_disconnected` signals for more advanced workflows.

## Binaries

Compiled libraries are placed under `bin/<platform>/` (e.g.
`bin/win64/libmulti_mouse.windows.template_debug.x86_64.dll`). The `.gdextension`
file already references those paths, so once the DLL is present Godot will load
it automatically.
