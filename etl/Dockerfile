FROM python:3.12-slim

WORKDIR /app

COPY etl/requirements.txt /app/etl/requirements.txt
RUN pip install --no-cache-dir -r /app/etl/requirements.txt

COPY etl/ingest_shift_codes.py /app/etl/ingest_shift_codes.py
COPY appdata/BL_SHIFT_CODES.csv /app/appdata/BL_SHIFT_CODES.csv

ENV CSV_PATH=/app/appdata/BL_SHIFT_CODES.csv

CMD ["python", "/app/etl/ingest_shift_codes.py"]
