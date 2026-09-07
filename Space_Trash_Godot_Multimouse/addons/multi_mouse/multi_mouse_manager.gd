## Multi-Mouse Godot Add-on
extends Node

class_name MultiMouse

## Emitted when a new mouse is connected
signal device_connected(device_id: int, info: Dictionary)
## Emitted when a mouse is disconnected
signal device_disconnected(device_id: int)
## Emitted when one of the connected mice moves
signal motion(event: InputEventMouseMotion)
## Emitted when one of the connected mice pressed or released a button
signal button(event: InputEventMouseButton)

var _server: Object
var _requested_enabled := false
var _attached := false

func _ready() -> void:
	set_process(true)
	if Engine.has_singleton("MultiMouseServer"):
		_server = Engine.get_singleton("MultiMouseServer")
		_bind_server_callbacks()
		_emit_existing_devices()
	else:
		push_warning("MultiMouseServer native singleton is not available. Build the GDExtension for your platform.")

## Attach to a window to receive mouse input from
func attach_to_window(hwnd: int) -> void:
	if _server and _server.has_method("attach_to_window"):
		_server.attach_to_window(hwnd)
		_attached = true

## Enable emitting mouse signals
func enable() -> void:
	_requested_enabled = true
	if _server and _attached and _server.has_method("enable_backend"):
		_server.enable_backend()

## Disable emitting mouse signals
func disable() -> void:
	_requested_enabled = false
	if _server and _server.has_method("disable_backend"):
		_server.disable_backend()

func _bind_server_callbacks() -> void:
	if not _server:
		return
	_server.device_connected.connect(_on_device_connected)
	_server.device_disconnected.connect(_on_device_disconnected)
	_server.motion.connect(_on_motion)
	_server.button.connect(_on_button)

func _emit_existing_devices() -> void:
	if _server and _server.has_method("get_connected_devices"):
		for dev in _server.get_connected_devices():
			device_connected.emit(int(dev["device_id"]), dev)

func _process(_delta: float) -> void:
	request_poll()

## request the Multi-Mouse Server to do a poll
func request_poll() -> void:
	if _server and _server.has_method("poll"):
		_server.poll()

## returns an array of connected devices
func get_devices() -> Array:
	if _server and _server.has_method("get_connected_devices"):
		return _server.get_connected_devices()
	return []

func _on_device_connected(device_id: int, info: Dictionary) -> void:
	device_connected.emit(device_id, info)

func _on_device_disconnected(device_id: int) -> void:
	device_disconnected.emit(device_id)

func _on_motion(event: InputEventMouseMotion) -> void:
	motion.emit(event)

func _on_button(event: InputEventMouseButton) -> void:
	button.emit(event)
