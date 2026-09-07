extends RigidBody2D


var start_speed := 300.0
var hit_force := 8.0
var max_speed := 900.0
@onready var game = get_tree().get_first_node_in_group("game")
@onready var hit_detector = $HitDetector

func _ready() -> void:
	gravity_scale = 0
	lock_rotation = true
	can_sleep = false
	linear_damp = 0
	angular_damp = 0
	var random_speed = randf_range(100, 400)
	linear_velocity = Vector2(0, random_speed)

	hit_detector.body_entered.connect(_on_hit_detector_body_entered)

func _on_hit_detector_body_entered(body):
	if body is CharacterBody2D:
		var paddle_device_id = body.get_meta("device_id")
		var difference = global_position - body.global_position

		if abs(difference.x) > abs(difference.y):
			if difference.x > 0:
				print(body.name, " hit ball from LEFT")
			else:
				print(body.name, " hit ball from RIGHT")
		else:
			if difference.y > 0:
				print(body.name, " hit ball from TOP")
			else:
				print(body.name, " hit ball from BOTTOM")

		if sign(linear_velocity.y) == sign(body.velocity.y):
			print(body.name, " hit ball in same Y direction")
		if(body.name == "Right Cursor"):
			game.rhits += 1
			print(game.current_area)
			if(game.current_area == "Right"):
				game.rhits_rightarea += 1
				print(game.rhits_rightarea)
			elif (game.current_area == "Left"):
				game.rhits_leftarea += 1
				print(game.rhits_leftarea)
		elif(body.name == "Left Cursor"):
			game.lhits += 1
			if(game.current_area == "Right"):
				game.lhits_rightarea += 1
				print(game.lhits_rightarea)
			elif (game.current_area == "Left"):
				game.lhits_leftarea += 1
				print(game.lhits_leftarea)
		linear_velocity += body.velocity * hit_force
		linear_velocity = linear_velocity.limit_length(max_speed)
		
