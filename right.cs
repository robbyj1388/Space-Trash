using Godot;

public partial class right : Area2D
{
	private void OnBodyEntered(Node2D body){
		GD.Print(body.Name, " has entered the right area");
		GetParent().Set("current_area", "Right");
	}
}

