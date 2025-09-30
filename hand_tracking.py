import cv2
import mediapipe as mp
import socket
import json


# source venv/bin/activate

mp_hands = mp.solutions.hands
hands = mp_hands.Hands()
cap = cv2.VideoCapture(0)

sock = socket.socket()
sock.connect(('localhost', 5555))

while True:
    ret, frame = cap.read()
    if not ret:
        break
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    result = hands.process(rgb)
    if result.multi_hand_landmarks:
        hand = result.multi_hand_landmarks[0]
        x = hand.landmark[0].x  # wrist position (0–1)
        y = hand.landmark[0].y
        sock.sendall(json.dumps({'x': x, 'y': y}).encode() + b'\n')

