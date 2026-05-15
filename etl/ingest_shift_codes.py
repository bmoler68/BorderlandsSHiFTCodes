"""
Load appdata/BL_SHIFT_CODES.csv into Supabase PostgREST (borderlands_shift.shift_codes_v1).

Requires SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY. Optional CSV_PATH (default
app-relative path inside the image: /app/appdata/BL_SHIFT_CODES.csv).

Does not send id, ingested_at_utc, or updated_at_utc — DB defaults and triggers apply.
"""

from __future__ import annotations

import csv
import os
import sys
from pathlib import Path

import httpx

SCHEMA = "borderlands_shift"
TABLE = "shift_codes_v1"

REQUIRED_COLUMNS = (
    "code",
    "reward",
    "expiration_date",
    "expiration_time",
    "is_non_expiring",
    "is_unknown_expiration",
    "bl",
    "bl_tps",
    "bl2",
    "bl3",
    "wonderlands",
    "bl4",
    "is_key",
    "is_cosmetic",
    "is_gear",
)

BOOL_FIELDS = frozenset(
    {
        "is_non_expiring",
        "is_unknown_expiration",
        "bl",
        "bl_tps",
        "bl2",
        "bl3",
        "wonderlands",
        "bl4",
        "is_key",
        "is_cosmetic",
        "is_gear",
    }
)

NULLABLE_STRING_FIELDS = frozenset({"expiration_date", "expiration_time"})


def _parse_bool(raw: str) -> bool:
    return raw.strip().lower() in ("true", "t", "1", "yes", "y")


def _normalize_row(row: dict[str, str]) -> dict[str, object]:
    out: dict[str, object] = {}
    for key, raw in row.items():
        key = key.strip()
        raw = (raw or "").strip()
        if key in BOOL_FIELDS:
            out[key] = _parse_bool(raw)
        elif key in NULLABLE_STRING_FIELDS:
            out[key] = None if raw == "" else raw
        else:
            out[key] = raw
    return out


def load_csv(path: Path) -> list[dict[str, object]]:
    with path.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        if not reader.fieldnames:
            return []
        headers = {h.strip() for h in reader.fieldnames if h}
        missing = set(REQUIRED_COLUMNS) - headers
        if missing:
            sys.stderr.write(f"CSV missing required columns: {sorted(missing)}\n")
            raise SystemExit(1)
        rows = [_normalize_row(r) for r in reader]
    by_code: dict[str, dict[str, object]] = {}
    for r in rows:
        code = str(r.get("code", "")).strip()
        if not code:
            continue
        by_code[code] = r
    return list(by_code.values())


def upsert_batch(
    client: httpx.Client, base_url: str, service_key: str, batch: list[dict[str, object]]
) -> None:
    url = f"{base_url}/rest/v1/{TABLE}"
    params = {"on_conflict": "code"}
    headers = {
        "apikey": service_key,
        "Authorization": f"Bearer {service_key}",
        "Content-Type": "application/json",
        "Accept-Profile": SCHEMA,
        "Content-Profile": SCHEMA,
        "Prefer": "resolution=merge-duplicates,return=minimal",
    }
    resp = client.post(url, params=params, headers=headers, json=batch, timeout=120.0)
    if resp.status_code >= 400:
        sys.stderr.write(f"HTTP {resp.status_code}: {resp.text}\n")
        raise SystemExit(1)


def main() -> None:
    base_url = os.environ.get("SUPABASE_URL", "").rstrip("/")
    key = os.environ.get("SUPABASE_SERVICE_ROLE_KEY", "")
    csv_path = Path(os.environ.get("CSV_PATH", "/app/appdata/BL_SHIFT_CODES.csv"))

    if not base_url or not key:
        sys.stderr.write("SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY are required.\n")
        raise SystemExit(1)
    if not csv_path.is_file():
        sys.stderr.write(f"CSV not found: {csv_path}\n")
        raise SystemExit(1)

    records = load_csv(csv_path)
    if not records:
        sys.stderr.write("No data rows in CSV.\n")
        raise SystemExit(1)

    chunk_size = min(500, max(1, len(records)))
    with httpx.Client() as client:
        for i in range(0, len(records), chunk_size):
            upsert_batch(client, base_url, key, records[i : i + chunk_size])

    print(f"Ingested {len(records)} row(s) into {SCHEMA}.{TABLE} (upsert on code).")


if __name__ == "__main__":
    main()
