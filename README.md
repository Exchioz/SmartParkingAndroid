# Smart Parking Gate using Raspberry Pi

This project implements a Indonesian vehicle license plate recognition system using a Raspberry Pi. It utilizes advanced machine learning models to detect and recognize license plates, and controls a servo motor for gate management based on the presence of authorized vehicles. The system uses an ultrasonic sensor to detect vehicle presence and distance.

In the "Smart Parking Gate" project, the system is specifically engineered to handle Indonesian license plates, ensuring precise detection and recognition through specialized machine learning models.

[Indonesian Plate Recognition Models](https://github.com/Exchioz/Indonesian-License-Plates)


## Features
- **License Plate Detection**: Utilizes the Ultralytics YOLO model to detect vehicle license plates in real-time.
- **Character Recognition**: Employs a TensorFlow CNN model to recognize characters on the license plates.
- **Gate Control**: Manages a servo motor to automatically open or close the gate based on vehicle validation.
- **Distance Measurement**: Uses an ultrasonic sensor to detect the distance of the vehicle from the gate.

## Hardware Requirements
- Raspberry Pi (3B, 3B+, or 4)
- SG90 servo motor
- HC-SR04 ultrasonic sensor
- Webcam
- Jumper wires
- Breadboard (optional for prototyping)

## Software Requirements
- Python 3.7 or higher
- Libraries: TensorFlow, Ultralytics, GPIOzero, ONNX
- Raspberry Pi OS (formerly Raspbian)

## System Environment
To fully utilize the Smart Parking Gate system, users are required to engage with our dedicated Android application, specifically designed for comprehensive gate management and enhanced user interaction. This application facilitates an intuitive platform where users can reserve parking spaces.
- [Android App Repository](https://github.com/Exchioz/SmartParkingAndroid)
- [API Server Repository](https://github.com/Exchioz/SmartParkingAPI)


## Installation
### Setting Up the Python Environment
1. Ensure Python 3.7+ is installed on your Raspberry Pi.
2. Clone this repository to your Raspberry Pi.
3. Install required Python libraries:
```bash
pip install -r requirements.txt
```
### Hardware Setup
1. Connect the SG90 servo motor to the GPIO pins of the Raspberry Pi.
2. Attach the HC-SR04 ultrasonic sensor to the Raspberry Pi using the GPIO pins.
3. Ensure all connections are secure and that the Raspberry Pi is powered safely.

## Usage
To operate the system, you will need to navigate to the project directory and execute the relevant script depending on the gate you wish to control:

#### For the Entrance Gate:
Run the entrance gate script by entering the following command in the terminal:
```bash
python entrance.py
```

#### For the Exit Gate:
Run the exit gate script by entering the following command in the terminal:
```bash
python exit.py
```

### System Operation
1. The system continuously monitors the distance to detect a vehicle using the ultrasonic sensor.
2. Upon vehicle detection, the YOLO model identifies the license plate, and the TensorFlow model recognizes the characters on the plate.
3. If the vehicle is authorized, the servo motor will operate to open the gate.
4. The gate remains open until the vehicle passes through, as detected by the ultrasonic sensor.