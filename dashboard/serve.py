#!/usr/bin/env python3
"""
Serve this folder over HTTP for local dashboard testing.

  python serve.py
  python serve.py --port 9000 --host 127.0.0.1

Copy config.example.js to config.js and set supabaseUrl / supabaseAnonKey before loading the app.
"""

from __future__ import annotations

import argparse
import http.server
import socketserver
from pathlib import Path


def main() -> None:
    parser = argparse.ArgumentParser(description="Serve the SHiFT dashboard directory locally.")
    parser.add_argument(
        "--host",
        default="127.0.0.1",
        help="Bind address (default: 127.0.0.1; use 0.0.0.0 for LAN devices)",
    )
    parser.add_argument("--port", type=int, default=8765, help="TCP port (default: 8765)")
    args = parser.parse_args()

    root = Path(__file__).resolve().parent

    class Handler(http.server.SimpleHTTPRequestHandler):
        def __init__(self, *a, **k):
            super().__init__(*a, directory=str(root), **k)

    with socketserver.TCPServer((args.host, args.port), Handler) as httpd:
        url = f"http://{args.host}:{args.port}/"
        print(f"Serving {root}")
        print(f"Open {url}")
        print("Use config.js (copy from config.example.js with your Supabase URL and anon key).")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nStopped.")


if __name__ == "__main__":
    main()
