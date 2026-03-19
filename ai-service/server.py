import cv2, os
import numpy as np

from fastapi import FastAPI, File, UploadFile, Body
from dotenv import load_dotenv
from google import genai
from services.preprocessor import preprocess_image
from services.detector import PestDetector

app = FastAPI(title="Rice Pest Detection API")

load_dotenv()
client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

detector = PestDetector(
    pt_path="runs/detect/train/weights/best.pt",
    onnx_path="runs/detect/train/weights/best.onnx"
)

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    contents = await file.read()
    nparr = np.frombuffer(contents, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    if img is None:
        return {"error": "Ảnh không hợp lệ"}

    processed_img = preprocess_image(img)
    results = detector.predict(processed_img)

    return {"status": "success", "data": results}

@app.post("/chat")
async def chat_simple(payload: dict = Body(...)):
    user_message = payload.get("message")
    if not user_message:
        return {"error": "Không có nội dung tin nhắn"}

    try:
        response = await client.aio.models.generate_content(
            model="gemini-2.5-flash",
            contents=user_message
        )
        return {"answer": response.text}
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
