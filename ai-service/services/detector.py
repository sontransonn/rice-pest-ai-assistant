import os
from ultralytics import YOLO

class PestDetector:
    def __init__(self, pt_path, onnx_path):
        if not os.path.exists(onnx_path):
            tmp_model = YOLO(pt_path)
            tmp_model.export(format="onnx", imgsz=640, dynamic=True, simplify=True, half=True)
        self.model = YOLO(onnx_path, task='detect')

    def predict(self, img, conf=0.4):
        results = self.model.predict(source=img, conf=conf)
        predictions = []
        for r in results:
            for box in r.boxes:
                predictions.append({
                    "label": self.model.names[int(box.cls[0])],
                    "confidence": round(float(box.conf[0]), 2),
                    "bbox": [int(x) for x in box.xyxy[0].tolist()]
                })
        return predictions