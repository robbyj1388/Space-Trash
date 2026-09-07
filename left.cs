using Godot;

public partial class left : Area2D
{
	private void OnBodyEntered(Node2D body){
		GD.Print(body.Name, " has entered the left area");
		GetParent().Set("current_area", "Left");
	}
}
