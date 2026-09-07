extends Area2D

# Function that lets the console know when a cursor has entered the right side of the screen
func _on_body_entered(body: CharacterBody2D) -> void:
	print(body.name, " has entered the right area")
	get_parent().current_area = "Right"
