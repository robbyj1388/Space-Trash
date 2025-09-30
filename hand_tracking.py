import cv2
import mediapipe as mp
import socket
import json

# Initialize Mediapipe Hands
mp_hands = mp.solutions.hands
hands = mp_hands.Hands()
mp_draw = mp.solutions.drawing_utils  # For drawing landmarks

# Open webcam
cap = cv2.VideoCapture(0)

# Connect to Java server
sock = socket.socket()
sock.connect(('localhost', 5555))

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            break

        # Flip for mirror view
        frame = cv2.flip(frame, 1)

        # Convert to RGB for Mediapipe
        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        result = hands.process(rgb)

        if result.multi_hand_landmarks:
            hand = result.multi_hand_landmarks[0]
            
            # Draw hand landmarks on the frame
            mp_draw.draw_landmarks(frame, hand, mp_hands.HAND_CONNECTIONS)

            # Get wrist coordinates
            x = hand.landmark[0].x  # 0–1
            y = hand.landmark[0].y

            # Send to Java server
            sock.sendall(json.dumps({'x': x, 'y': y}).encode() + b'\n')

        # Show camera feed with landmarks
        cv2.imshow("Hand Tracking", frame)

        # Press 'q' to quit
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
finally:
    cap.release()
    cv2.destroyAllWindows()
    sock.close()

