import cv2
import mediapipe as mp
import socket
import json

# Initialize Mediapipe Hands
mp_hands = mp.solutions.hands
hands = mp_hands.Hands(
    max_num_hands=2,
    min_detection_confidence=0.7,
    min_tracking_confidence=0.7
)

# Open webcam
cap = cv2.VideoCapture(0)

# Create socket and connect to Java server
sock = socket.socket()
sock.connect(('localhost', 5555))

try:
    frame_count = 0
    N = 3  # Process every 3rd frame

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        frame_count += 1
        if frame_count % N != 0:
            continue  # Skip frame

        frame = cv2.flip(frame, 1)
        frame_small = cv2.resize(frame, (320, 240))
        rgb = cv2.cvtColor(frame_small, cv2.COLOR_BGR2RGB)
        result = hands.process(rgb)

        lx = ly = rx = ry = None  # Default if hands not detected

        if result.multi_hand_landmarks:
            # hands can be multiple, let's try to detect left and right hands separately
            for hand_landmarks, handedness in zip(result.multi_hand_landmarks, result.multi_handedness):
                label = handedness.classification[0].label  # "Left" or "Right"
                x = hand_landmarks.landmark[0].x
                y = hand_landmarks.landmark[0].y

                if label == "Left":
                    lx, ly = x, y
                elif label == "Right":
                    rx, ry = x, y

        # Prepare data string (send None or -1 if not detected)
        lx = -1 if lx is None else lx
        ly = -1 if ly is None else ly
        rx = -1 if rx is None else rx
        ry = -1 if ry is None else ry

        data_str = f"{lx},{ly},{rx},{ry}\n"
        sock.sendall(data_str.encode())

        # Optional: show frame for debugging
        cv2.imshow("Hand Tracking", frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

finally:
    cap.release()
    cv2.destroyAllWindows()
    sock.close()
