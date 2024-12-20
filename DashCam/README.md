# DASS - Traffic and Speed Sign Detection 🚦

This section of the **DASS (Driver Assistance System)** project focuses on real-time detection of traffic lights, stop signs, and speed signs using **YOLOv5** and **EasyOCR**. It integrates **Firebase** to store and update detected signs with their details, including the timestamp and confidence level. The system streams video footage and highlights detected signs on the display.

---

## 🌟 **Key Features**
1. **Traffic Light Detection**: Detects red traffic lights in real-time.
2. **Stop Sign Detection**: Identifies stop signs within the frame.
3. **Speed Sign Detection**: Uses custom-trained YOLOv5 model to detect speed limit signs and reads the speed limit using OCR.
4. **Firebase Integration**: Updates the Firebase Realtime Database with detected sign information such as type, speed, timestamp, and confidence.
5. **Real-time Streaming**: Streams the processed video footage to a browser, showing the detected traffic signs.

---

## 🛠️ **Technologies Used**
- **YOLOv5**: Object detection framework for detecting traffic lights, stop signs, and speed signs.
- **EasyOCR**: Optical character recognition (OCR) to extract speed limits from detected speed signs.
- **Flask**: Web framework for serving video streams in real-time.
- **Firebase**: Cloud database for storing detected sign data in real-time.
- **OpenCV**: Computer vision library for video processing and drawing bounding boxes on detected objects.
- **Python**: Backend scripting for image processing, model inference, and data handling.

---

## 🧩 **How it Works**
1. **Detection**: 
   - The YOLOv5 model detects traffic lights and stop signs in real-time. 
   - A custom-trained YOLOv5 model detects speed signs, and **EasyOCR** reads the detected speed limit.
2. **Firebase Update**: 
   - Once a sign is detected, the relevant data (speed, type, timestamp, confidence) is uploaded to Firebase in real time.
3. **Streaming**: 
   - The system streams video frames to a web browser with bounding boxes drawn around detected signs.

---

## 📸 **Demo**
You can stream the processed video via the `/video` route in the Flask app, which will display the frames with the detected bounding boxes and labels for each detected sign.

---

## 🛠️ **Setup Instructions**
1. Install required libraries:
   ```bash
   pip install torch opencv-python easyocr firebase-admin flask
