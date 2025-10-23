import cv2
import mediapipe as mp
import socket
import json

# =============================
# 1. SET UP MEDIAPIPE HANDS
# =============================

# Load Mediapipe's hand tracking module
mp_hands = mp.solutions.hands

# Initialize the Hands object
# - static_image_mode=False: uses tracking (faster on video)
# - max_num_hands=1: only detect one hand (reduces computation)
# - min_detection_confidence / min_tracking_confidence: tune for performance
hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

# =============================
# 2. OPEN THE WEBCAM
# =============================

# Capture from the default webcam (device 0)
cap = cv2.VideoCapture(0)

# Optional: reduce camera resolution to speed up processing
# Uncomment these lines if needed:
# cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
# cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

# =============================
# 3. CONNECT TO JAVA SERVER
# =============================

# Create a TCP/IP socket
sock = socket.socket()

# Connect to the Java server running locally on port 5555
sock.connect(('localhost', 5555))

# =============================
# 4. MAIN LOOP
# =============================
try:
    while True:
        # --- Read a frame from the webcam ---
        ret, frame = cap.read()
        if not ret:
            break  # Stop if the webcam feed fails

        # --- Flip the image horizontally for a mirror view ---
        frame = cv2.flip(frame, 1)

        # --- Resize frame to smaller size for faster processing ---
        frame_small = cv2.resize(frame, (320, 240))

        # --- Convert the frame from BGR (OpenCV) to RGB (Mediapipe uses RGB) ---
        rgb = cv2.cvtColor(frame_small, cv2.COLOR_BGR2RGB)

        # --- Run Mediapipe hand tracking ---
        result = hands.process(rgb)

        # --- If a hand is detected, get its wrist coordinates ---
        if result.multi_hand_landmarks:
            # Take the first detected hand (index 0)
            hand = result.multi_hand_landmarks[0]

            # The wrist is landmark index 0
            # x and y are normalized (0.0–1.0) relative to the image size
            x = hand.landmark[0].x
            y = hand.landmark[0].y

            # --- Send wrist coordinates to the Java server as JSON ---
            # Example: {"x": 0.53, "y": 0.27}
            sock.sendall(json.dumps({'x': x, 'y': y}).encode() + b'\n')

        # --- (Optional) Show live camera feed ---
        # Comment out to save more performance
        cv2.imshow("Hand Tracking (Wrist Only)", frame)

        # --- Quit if 'q' is pressed ---
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

# =============================
# 5. CLEAN UP
# =============================
finally:
    # Release the webcam
    cap.release()

    # Close OpenCV windows
    cv2.destroyAllWindows()

    # Close the socket connection
    sock.close()

