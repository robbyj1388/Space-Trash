
using Godot;

public partial class ball : RigidBody2D
{
    private float startSpeed = 300.0f;
    private float hitForce = 8.0f;
    private float maxSpeed = 900.0f;

    private area_2d game;
    private Area2D hitDetector;


    public override void _Ready()
    {
        // Get the Game node from the "game" group
        game = GetTree().GetFirstNodeInGroup("game") as area_2d;

        // Get HitDetector child
        hitDetector = GetNode<Area2D>("HitDetector");

        // Disable gravity
        GravityScale = 0;

        // Prevent ball from rotating
        LockRotation = true;

        // Prevent the rigid body from sleeping
        CanSleep = false;

        // Disable linear and angular damping
        LinearDamp = 0;
        AngularDamp = 0;

        // Give the ball a random starting downward speed
        float randomSpeed = (float)GD.RandRange(100, 400);

        LinearVelocity = new Vector2(0, randomSpeed);

        // Connect HitDetector's body_entered signal
        hitDetector.BodyEntered += OnHitDetectorBodyEntered;
    }


    private void OnHitDetectorBodyEntered(Node2D body)
    {
        // Only react to CharacterBody2D paddles
        if (body is CharacterBody2D paddle)
        {
            // Get the device ID stored on the paddle
            Variant paddleDeviceId = paddle.GetMeta("device_id");

            // Determine where the paddle is relative to the ball
            Vector2 difference =
                GlobalPosition - paddle.GlobalPosition;


            // Determine which side of the paddle the ball hit
            if (Mathf.Abs(difference.X) > Mathf.Abs(difference.Y))
            {
                if (difference.X > 0)
                {
                    GD.Print(paddle.Name," hit ball from LEFT");
                }
                else
                {
                    GD.Print(paddle.Name," hit ball from RIGHT");
                }
            }
            else
            {
                if (difference.Y > 0)
                {
                    GD.Print(paddle.Name," hit ball from TOP");
                }
                else
                {
                    GD.Print(paddle.Name," hit ball from BOTTOM");
                }
            }


            // Check if the ball and paddle are moving
            // in the same Y direction
            if (Mathf.Sign(LinearVelocity.Y) == Mathf.Sign(paddle.Velocity.Y))
            {
                GD.Print(paddle.Name," hit ball in same Y direction");
            }
            // Make sure the Game reference exists
            if (game == null)
            {
                GD.PrintErr("Ball could not find Game node!");
                return;
            }

            // -------------------------
            // Right Cursor
            // -------------------------

            if (paddle.Name == "Right Cursor")
            {
                game.rhits++;

                GD.Print(game.currentArea);

                if (game.currentArea == "Right")
                {
                    game.rhitsRightArea++;

                    GD.Print(
                        game.rhitsRightArea
                    );
                }
                else if (game.currentArea == "Left")
                {
                    game.rhitsLeftArea++;
                    GD.Print(game.rhitsLeftArea);
                }
            }


            // -------------------------
            // Left Cursor
            // -------------------------

            else if (paddle.Name == "Left Cursor")
            {
                game.lhits++;

                if (game.currentArea == "Right")
                {
                    game.lhitsRightArea++;

                    GD.Print(
                        game.lhitsRightArea
                    );
                }
                else if (game.currentArea == "Left")
                {
                    game.lhitsLeftArea++;

                    GD.Print(
                        game.lhitsLeftArea
                    );
                }
            }


            // Apply paddle velocity to the ball
            LinearVelocity +=
                paddle.Velocity * hitForce;

            // Prevent ball from exceeding max speed
            LinearVelocity =
                LinearVelocity.LimitLength(maxSpeed);
        }
    }
}