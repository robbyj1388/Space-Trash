
using Godot;
using System.Collections.Generic;

// This holds the whole body of code for the file, with the area_2d being the script name and Node2D being the name 
// node it's connected to
public partial class area_2d : Node2D
{
    // Helps with defining right and left controls
    private int rightDeviceId = -1;
    private int leftDeviceId = -1;

    //total hits for both left and right mice
    public int lhits = 0;
    public int rhits = 0;

    // Counter variables for what area the mice hit the object in
    public int rhitsRightArea = 0;
    public int rhitsLeftArea = 0;
    public int lhitsRightArea = 0;
    public int lhitsLeftArea = 0;

    // ==>DEBUG LINE<== Helps determine what area the mice is currently in
    public string currentArea = "";

    // Array that stores all mice that are detected
    private Dictionary<int, CharacterBody2D> cursors = new Dictionary<int, CharacterBody2D>();

    // Sets all mice speed to a uniform speed
    private float mouseSpeed = 0.5f;

    // Arrays that help with double hit situations, testing for when the last hit was
    private Dictionary<int, float> lastNearHit = new Dictionary<int, float>();
    private Dictionary<int, float> lastHitTime = new Dictionary<int, float>();

    // Cooldown for when the object can be hit again, helping with double hit situations
    private float hitCooldown = 0.15f;

    // Ball counter to determine when to end the game
    private int ballCounter = 0;

    // Calls our RigidBody2D scene "ball" to be played
    private PackedScene ballScene;

    // Reference to the MultiMouse GDScript node
    private Node multiMouse;


    public override async void _Ready()
{

    AddToGroup("game");

    // Helps with strecthing the game view based on the monitors size
	DisplayServer.WindowSetMode(DisplayServer.WindowMode.Fullscreen);

    
    //Vector2I monitorSize = DisplayServer.ScreenGetSize();

    //DisplayServer.WindowSetSize(monitorSize);

    //GD.Print("Window size: ", DisplayServer.WindowGetSize());
    //GD.Print("Viewport size: ", GetViewportRect().Size);

    // Creates a multi mouse object to help create the instances of each mice
    multiMouse = GetNode<Node>("MultiMouse");

    // ==>DEBUG LINE<== Helps ensure that the mice where found
    // NOTE: This only works for USB mice inputs. It cannot work with Bluetooth or mouse pads
    GD.Print("Found MultiMouse node: ", multiMouse);

    // Loads our ball scene by pulling it from our scene file
    ballScene = GD.Load<PackedScene>("res://scene/ball.tscn");
    // ==>DEBUG LINE<== Helps ensure that the ball scene was loaded correctly
    GD.Print("Loaded ball scene: ", ballScene);

    /*

        TIMER FOR BALL SPAWING FREQUENCY

    */
    Timer timer = new Timer();

    // Waits one second before spawing another ball
    timer.WaitTime = 1.0;
    timer.OneShot = false;

    // When timer hits zero, we call the SpawnBall function
    timer.Timeout += SpawnBall;

    AddChild(timer);

    // Starts the timer after a ball had spawned
    timer.Start();

    /*

        CONNECTING EACH MICE TO THE GAME

    */

    GD.Print("Connecting motion signal...");

    // "Connect mice when the game detects motion"
    // Helps ID the mice by calling the OnMouseMotion function
    multiMouse.Connect("motion",Callable.From<InputEventMouseMotion>(OnMouseMotion));

    // "Connect button correlated with that mouse when pressed"
    // SIDE NOTE: We have yet to do anything with this and it may be something that will be deleted later
    multiMouse.Connect("button",Callable.From<InputEventMouseButton>(OnMouseButton));

    // Attaches the mouse to the game window
    multiMouse.Call("attach_to_window", 0);

    // Waits a bit before allowing the mice to connect
    await ToSignal(GetTree().CreateTimer(0.5),SceneTreeTimer.SignalName.Timeout);

    // enables multi mouse
    multiMouse.Call("enable");

    // Get the devices that connected to multi mouse
    Variant devices = multiMouse.Call("get_devices");

    // ==>DEBUG LINE<== Helps ensure that the correct devices are connecting
    GD.Print("Devices: ", devices);
}


    // Spawn ball function: Spawns balls at the top of the screen
    private void SpawnBall()
{

    if(ballCounter >= 10)
    {
        StopGame();
        return;
    }
    // Instantiates our ball scene
    Node ball = ballScene.Instantiate();

    // Get screen size
    Vector2 screenSize = GetScreenSize();

    // Sets the balls global position to a random position at the top of the screen
    if (ball is Node2D ball2D)
    {
        ball2D.GlobalPosition = new Vector2((float)GD.RandRange(20, screenSize.X - 20),-20);
    }

    // Add it to the scene
    AddChild(ball);

    // Adds to the ball counter to determine when to stop creating balls
    ballCounter++;
}


    // Handles mouse movement from MultiMouse
    private void OnMouseMotion(InputEventMouseMotion @event)
    {
        // Gets the current device ID of the mouse
        int deviceId = @event.Device;

        // Create a cursor if this device does not have one yet
        if (!cursors.ContainsKey(deviceId))
        {
            CreateCursor(deviceId);
        }

        // Sets the cursor as a CharacterBody2D
        CharacterBody2D cursor = cursors[deviceId];

        // Slow down/speed up mouse movement
        Vector2 movement = @event.Relative * mouseSpeed;

        // Set cursor velocity
        cursor.Velocity = @event.Relative * 8;

        // Move cursor
        cursor.MoveAndCollide(movement);

        // Keep cursor inside screen
        KeepCursorOnScreen(cursor);
    }


    // Handles mouse button presses/releases
    private void OnMouseButton(InputEventMouseButton @event)
    {
        GD.Print("BUTTON: device=", @event.Device);
    }


    // Creates a cursor for a new mouse
    private void CreateCursor(int deviceId)
    {
        // Don't create duplicate cursor
        if (cursors.ContainsKey(deviceId))
        {
            return;
        }

        // Create paddle
        CharacterBody2D cursor = new CharacterBody2D();

        // Assign first mouse as right cursor
        if (rightDeviceId == -1)
        {
            rightDeviceId = deviceId;
            cursor.Name = "Right Cursor";
            cursor.SetMeta("device_id", rightDeviceId);
        }
        // Assign second mouse as left cursor
        else if (leftDeviceId == -1)
        {
            leftDeviceId = deviceId;

            cursor.Name = "Left Cursor";
            cursor.SetMeta("device_id", leftDeviceId);
        }

        /*

            VISUALS FOR THE PADDLES

        */

        Polygon2D visual = new Polygon2D();

        // Sets the color of the paddle
        visual.Color = Colors.Red;

        // Creates the rectangle shape
        visual.Polygon = new Vector2[]
        {
            new Vector2(-45, -15),
            new Vector2(45, -15),
            new Vector2(45, 15),
            new Vector2(-45, 15)
        };


        /* 

            CREATES COLLISION FOR THE PADDLES

        */ 

        CollisionShape2D collision = new CollisionShape2D();

        RectangleShape2D shape = new RectangleShape2D();

        shape.Size = new Vector2(90, 30);

        collision.Shape = shape;

        cursor.AddToGroup("paddles");

        cursor.AddChild(visual);
        cursor.AddChild(collision);

        AddChild(cursor);

        Vector2 screenSize = GetScreenSize();

        // Sets the cursors starting position to the middle of the screen 
        cursor.GlobalPosition = new Vector2(screenSize.X / 2,screenSize.Y / 2);

        // Sets the collision layer and mask of the cursors, so they hit the objects as well as can't go outside the border of the screen
        cursor.CollisionLayer = 1;
        cursor.CollisionMask = 2;
        
        // Helps with making sure the paddles don't get dragged along with the objects
        cursor.MotionMode = CharacterBody2D.MotionModeEnum.Floating;


        // Store cursor
        cursors[deviceId] = cursor;

        GD.Print("Created paddle for device: ", deviceId);
    }

    // Prevents cursors from leaving the screen
    private void KeepCursorOnScreen(CharacterBody2D cursor)
    {
        Vector2 screenSize = GetViewportRect().Size;

        Vector2 position = cursor.GlobalPosition;

        position.X = Mathf.Clamp(position.X, 0, screenSize.X);

        position.Y = Mathf.Clamp(position.Y, 0, screenSize.Y);

        cursor.GlobalPosition = position;
    }


    // Called when a paddle hits a ball
    private void OnPaddleHitBall(Area2D area,CharacterBody2D paddle)
    {
        if (area.Name == "Ball")
        {
            area.Call("hit_by_paddle", paddle);
        }
    }


    private Vector2 GetScreenSize()
    {
        return GetViewportRect().Size;
    }


    private async void StopGame()
    {
        GD.Print("Left paddle hit: ", lhits, " times.");

        GD.Print("Right paddle hit: ", rhits, " times.");

        GD.Print(rhitsLeftArea);
        GD.Print(rhitsRightArea);
        GD.Print(lhitsLeftArea);
        GD.Print(lhitsRightArea);

        await ToSignal(GetTree().CreateTimer(1.0), SceneTreeTimer.SignalName.Timeout);
        GetTree().Quit();
    }
}


