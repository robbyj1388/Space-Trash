extends Node2D

var right_device_id = -1
var left_device_id = -1
var lhits = 0
var rhits = 0
var rhits_rightarea = 0
var rhits_leftarea = 0
var lhits_rightarea = 0
var lhits_leftarea = 0
var current_area := ""
var cursors := {} #stores all the mice that are detected
var mouse_speed := 0.5 #Speed multiplier to control cursor speed
var screen_size = get_viewport_rect().size
var last_near_hit := {}
var last_hit_time := {}
var hit_cooldown := 0.15
# Ball counter to determine when we reach 100 balls, since the lab ends at that number
var ball_counter = 0
var ball_scene = preload("res://scene/ball.tscn")
@onready var multi_mouse = $MultiMouse #variable that references MultiMouse in the tree
# Ready function connects the devices to the game, as well as handles a couple of quality of life things
func _ready():
	add_to_group("game")
	# Timer for the inteval of when the balls spawn
	var timer := Timer.new()
	
	# Waits 3 seconds before spawning another ball
	timer.wait_time = 1.0
	# Starts as soon as the timer stops, repeating till the game ends
	timer.autostart = true
	# When timer stops, call the spawn_ball function
	timer.timeout.connect(spawn_ball)
	add_child(timer)
	# Using multi mouse, connects the devices to the game
	multi_mouse.motion.connect(_on_mouse_motion) 
	
	# Attaches the raw mouse input system to the game window
	multi_mouse.attach_to_window(0)
	# This timer helps with the creation of the cursors, since the game window needs to finish setting up before detecting mice
	await get_tree().create_timer(0.5).timeout 
	
	# Enables multimouse for the game window
	multi_mouse.enable()
	# Sets the main cursor to be hidden while in the game window
	Input.set_mouse_mode(Input.MOUSE_MODE_HIDDEN) 
	# Shows how many devices are connected on boot. Fair warning, it will be zero. There are segements of code that tell you when a device is connected below
	print("Devices: ", multi_mouse.get_devices())
# Helper function that spawns balls from the top of the screen
func spawn_ball():
	# Once the last ball spawns, the game ends, marking the end of the lab
	if(ball_counter >= 10):
		stop_game()
		return
	# Debug line just to make sure that the balls are spawning
	print("Spawning ball")
	# Creates a copy of the scene (ball) and stores it as a variable
	var ball = ball_scene.instantiate()
	# Gets the current size of the screen
	var screen_size = get_screen_size()
	# Picks a random spot for the ball to spawn
	ball.global_position = Vector2(
		randf_range(20, screen_size.x - 20), -20
	)
	ball_counter += 1
	add_child(ball)
# Function that handles cursor motion
func _on_mouse_motion(event):
	var device_id = event.device
	
	# "If cursor doesn't have a device id, create one for it"
	if !cursors.has(device_id):
		create_cursor(device_id)
	# Takes the event (as in the movement of the cursor) and multiplies it by our mouse speed to either slow down or speed up it's correspondance on the screen
	var movement = event.relative * mouse_speed
	# Hands the velocity of the cursor, as well as updating the current position of the cursor on the screen
	cursors[device_id].velocity = event.relative * 8
	cursors[device_id].move_and_collide(movement)
	# Function that ensures that the curors stay on the screen instead of disappearing
	keep_cursor_on_screen(cursors[device_id])


# Function that tells when a button is pressed for what device, haven't done anything else with it yet
func _on_mouse_button(event): 
	print("BUTTON: device=", event.device)

# Function that creates new cursors for new devices detected
func create_cursor(device_id):
	# "If device already has a cursor in the game, don't create one"
	if cursors.has(device_id):
		return

	# Paddle body
	var cursor := CharacterBody2D.new()
	# Gives each cursor a name instead of a long ID
	if(right_device_id == -1):
		right_device_id = device_id
		cursor.name = "Right Cursor"
		cursor.set_meta("device_id", right_device_id)
	elif(left_device_id == -1):
		left_device_id = device_id
		cursor.name = "Left Cursor"
		cursor.set_meta("device_id", left_device_id)
	# Visual square
	var visual := Polygon2D.new()
	visual.color = Color.RED
	# Creates the size of the paddle
	visual.polygon = PackedVector2Array([
		Vector2(-45, -15),
		Vector2(45, -15),
		Vector2(45, 15),
		Vector2(-45, 15)
	])

	# Collision
	var collision := CollisionShape2D.new()
	# Masks the shape of the paddle
	var shape := RectangleShape2D.new()
	shape.size = Vector2(90, 30)
	
	# Sets the collision size to the shape of the paddle
	collision.shape = shape
	cursor.add_to_group("paddles")
	# Adds both visual and collision aid to the paddles
	cursor.add_child(visual)
	cursor.add_child(collision)

	# Add paddle to scene
	add_child(cursor)

	# Start position
	var screen_size = get_screen_size()
	cursor.global_position = Vector2(screen_size.x / 2, screen_size.y / 2)
	# Collision setup
	cursor.collision_layer = 1
	cursor.collision_mask = 2
	# Set the motion mode of each paddle to floating since it would drag along with it if not
	cursor.motion_mode = CharacterBody2D.MOTION_MODE_FLOATING
	# Store paddle
	cursors[device_id] = cursor

	# Debug that lets the console know when a new cursor was created and for what device
	print("Created paddle for device: ", device_id)
# Function that makes sure that our cursors stay on the screen, instead of shifting from one to the other
func keep_cursor_on_screen(cursor): 
	var screen_size = get_viewport_rect().size

	cursor.global_position.x = clamp(cursor.global_position.x, 0, screen_size.x)
	cursor.global_position.y = clamp(cursor.global_position.y, 0, screen_size.y)

func _on_paddle_hit_ball(area, paddle):
	if area.name == "Ball":
		area.hit_by_paddle(paddle)

func get_screen_size():
	return get_viewport_rect().size
	
func stop_game():	
	print("Left paddle hit: ", lhits, " times.")
	print("Right paddle hit: ", rhits, " times.")
	print(rhits_leftarea)
	print(rhits_rightarea)
	print(lhits_leftarea)
	print(lhits_rightarea)
	await get_tree().create_timer(1.0).timeout
	get_tree().quit()
