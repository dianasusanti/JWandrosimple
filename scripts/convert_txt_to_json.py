#!/usr/bin/env python3
"""
Converts a plain-text file of one Bible book into the JSON format the app reads.

You supply the text yourself (from material you have the right to use personally) --
this script only reshapes it; it doesn't fetch or embed any scripture text itself.

Expected input format (one verse per line, chapter:verse then a space then the text):

    1:1 Pada mulanya Allah menciptakan langit dan bumi.
    1:2 Bumi belum berbentuk dan kosong...
    2:1 Demikianlah langit dan bumi...

Blank lines and lines starting with # are ignored.

Usage:
    python3 scripts/convert_txt_to_json.py raw_text/kejadian.txt kejadian

This writes app/src/main/assets/bible/kejadian.json
"""
import json
import os
import re
import sys

LINE_RE = re.compile(r"^(\d+):(\d+)\s+(.*)$")

def main():
    if len(sys.argv) != 3:
        print("Usage: python3 convert_txt_to_json.py <input.txt> <book_code>")
        sys.exit(1)

    in_path = sys.argv[1]
    code = sys.argv[2]

    chapters: dict[str, dict[str, str]] = {}
    with open(in_path, encoding="utf-8") as f:
        for lineno, raw_line in enumerate(f, start=1):
            line = raw_line.strip()
            if not line or line.startswith("#"):
                continue
            m = LINE_RE.match(line)
            if not m:
                print(f"Skipping line {lineno} (doesn't match 'chapter:verse text'): {line[:60]}")
                continue
            chapter, verse, text = m.group(1), m.group(2), m.group(3)
            chapters.setdefault(chapter, {})[verse] = text

    out_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "bible")
    os.makedirs(out_dir, exist_ok=True)
    out_path = os.path.join(out_dir, f"{code}.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(chapters, f, ensure_ascii=False, indent=2)

    total_verses = sum(len(v) for v in chapters.values())
    print(f"Wrote {len(chapters)} chapters / {total_verses} verses to {out_path}")

if __name__ == "__main__":
    main()
