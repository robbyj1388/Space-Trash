/**
 * This is our script connected to our hittable objects.
 * Currently, we've only set up the object for the objects that the player needs to hit,
 * distractors will be added later. 
 * 
 * This sript controls the object's speed, velocity, and 
 * is how we currently detect number of hits from each paddle depending on 
 * the situation that it's in. This script also controls how much force
 * is transferred to the ball when it is hit by a paddle. 
 *
*/
using Godot;

public partial class ball : RigidBody2D
{   
    // Static number for force applied when hit by a paddle.
    private float hitForce = 8.0f;
    // Static number for maximum speed of the ball can reach.
    private float maxSpeed = 900.0f;

    // References our main game node to help with tracking hits
    private area_2d game;

    // Collision area that detects when the ball is hit by a paddle
    /* 
        NOTE: reason for this is because previous collision would either not
             not move the ball or would not count correctly when hit. 
    */
    private Area2D hitDetector;


    public override void _Ready()
    {
        // Sets our reference to the game node through our game group
        game = GetTree().GetFirstNodeInGroup("game") as area_2d;

        // References our hit detector of the object
        hitDetector = GetNode<Area2D>("HitDetector");

        // Disable gravity
        GravityScale = 0;

        /* 
            Since these objects are still images that don't change when hit,
            we lock the rotation of the objects so they don't change when hit
        */
        LockRotation = true;

        // POSSIBLY REMOVEABLE. Helps remove velocity
        CanSleep = false;

        // Disables air resistance of the object
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
        // Ensures that the objects only interact with the players paddles
        if (body is CharacterBody2D paddle)
        {
            // Get the device ID stored on the paddle
            Variant paddleDeviceId = paddle.GetMeta("device_id");

            // Determine where the paddle is relative to the ball
            Vector2 difference = GlobalPosition - paddle.GlobalPosition;

            // Determine which side of the paddle the ball hit 
            if (Mathf.Abs(difference.X) > Mathf.Abs(difference.Y))
            {   
                // Left or Right
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
                // Top or Bottom
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

            /*
                Determines which paddle hit the ball and which side it was on, incrementing the right variable
                NOTE: For some reason it only counts correctly for the first cursor that hits on each side
            */
            // Right area
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
            
            // Left area
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