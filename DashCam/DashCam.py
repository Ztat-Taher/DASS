import torch
import cv2
import easyocr
import firebase_admin
from firebase_admin import credentials, db
from datetime import datetime
from flask import Flask, Response

# Load the YOLOv5n model for traffic lights and stop signs
model_general = torch.hub.load('ultralytics/yolov5', 'yolov5n')

# Load the custom-trained model for speed sign detection
model_speed_sign = torch.hub.load(r"yolov5-master", 
                                  'custom', 
                                  path=r"yolov5-master\runs\train\sign_detection_model\weights\best.pt", 
                                  source='local')

# Initialize EasyOCR reader
reader = easyocr.Reader(['en'])

# Variable to store the last detected speed limit
last_speed_limit = None

# Frame skipping parameters
frame_skip = 24  # Process every 24th frame
frame_count = 0
last_detected_boxes = []  # Store the last detected bounding boxes

# Firebase Initialization
cred = credentials.Certificate("firebase_credentials.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://dass-2a23c-default-rtdb.europe-west1.firebasedatabase.app/'
})

firebase_ref = db.reference("detected-signs")

# Function to detect traffic lights, stop signs, and speed signs
def detect_signs(frame):
    global last_speed_limit
    detected_boxes = []
    firebase_data = {}

    # Run detection for traffic lights and stop signs
    results_general = model_general(frame)
    for det in results_general.pred[0]:
        x1, y1, x2, y2, conf, cls_id = map(float, det[:6])
        x1, y1, x2, y2, conf = map(int, [x1, y1, x2, y2, conf * 100])  # Convert confidence to percentage
        label = model_general.names[int(cls_id)]

        if conf > 50:  # Only process detections with >50% confidence
            if cls_id == 9:  # Traffic light (red)
                detected_boxes.append(((x1, y1, x2, y2), f"RED Traffic Light ({conf}%)"))
                firebase_data = {
                    "speed": 0,
                    "timestamp": int(datetime.now().timestamp()),
                    "type": "red_light",
                    "confidence": conf
                }
            elif cls_id == 11:  # Stop sign
                detected_boxes.append(((x1, y1, x2, y2), f"STOP Sign ({conf}%)"))
                firebase_data = {
                    "speed": 0,
                    "timestamp": int(datetime.now().timestamp()),
                    "type": "stop_sign",
                    "confidence": conf
                }

    # Run detection specifically for speed signs using the custom model
    results_speed_sign = model_speed_sign(frame)
    for det in results_speed_sign.xyxy[0]:
        x1, y1, x2, y2, conf, _ = map(float, det[:6])
        x1, y1, x2, y2, conf = map(int, [x1, y1, x2, y2, conf * 100])  # Convert confidence to percentage

        if conf > 50:  # Only process detections with >50% confidence
            # Perform OCR on the detected region to read the speed limit
            cropped_region = frame[y1:y2, x1:x2]
            ocr_results = reader.readtext(cropped_region)
            for _, text, _ in ocr_results:
                last_speed_limit = text
                detected_boxes.append(((x1, y1, x2, y2), f"Speed Limit {last_speed_limit} ({conf}%)"))
                firebase_data = {
                    "speed": int(last_speed_limit),
                    "timestamp": int(datetime.now().timestamp()),
                    "type": "speed_sign",
                    "confidence": conf
                }

    # Update Firebase only if valid data exists
    if firebase_data:
        firebase_ref.update(firebase_data)

    return detected_boxes

# Function to draw bounding boxes and labels on frame
def draw_boxes(frame, boxes):
    for (x1, y1, x2, y2), label in boxes:
        cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
        cv2.putText(frame, label, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
    return frame

# Initialize Flask app
app = Flask(__name__)

# Open video stream (camera or video file)
cap = cv2.VideoCapture(0)  # Replace "0" with a video path if testing with a file

if not cap.isOpened():
    print("Error: Could not open video.")

# Generator function to capture frames and stream them to the browser
def generate_frames():
    global frame_count, last_detected_boxes
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        
        # Detect signs every 'frame_skip' frames
        if frame_count % frame_skip == 0:
            last_detected_boxes = detect_signs(frame)

        # Draw the last detected boxes on the current frame
        frame = draw_boxes(frame, last_detected_boxes)

        # Encode frame to JPEG and yield it for Flask to stream
        _, buffer = cv2.imencode('.jpg', frame)
        frame_bytes = buffer.tobytes()
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame_bytes + b'\r\n\r\n')
        
        # Increment the frame counter
        frame_count += 1

# Route for streaming video
@app.route('/video')
def video():
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

# Start Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, threaded=True)
    
# Release resources when done
cap.release()
cv2.destroyAllWindows()
