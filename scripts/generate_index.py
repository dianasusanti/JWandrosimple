#!/usr/bin/env python3
"""
Generates app/src/main/assets/bible/index.json: the 66-book list with
Indonesian names, a short ASCII "code" (used as the data filename for each
book), and the standard chapter count for each book.

This is structural metadata (names + chapter counts), not scripture text,
so it's safe to generate directly -- no copyrighted content involved.

Run: python3 scripts/generate_index.py
"""
import json
import os

# (code, Indonesian display name, chapter count)
BOOKS = [
    ("kejadian", "Kejadian", 50),
    ("keluaran", "Keluaran", 40),
    ("imamat", "Imamat", 27),
    ("bilangan", "Bilangan", 36),
    ("ulangan", "Ulangan", 34),
    ("yosua", "Yosua", 24),
    ("hakim_hakim", "Hakim-Hakim", 21),
    ("rut", "Rut", 4),
    ("1samuel", "1 Samuel", 31),
    ("2samuel", "2 Samuel", 24),
    ("1raja", "1 Raja-Raja", 22),
    ("2raja", "2 Raja-Raja", 25),
    ("1tawarikh", "1 Tawarikh", 29),
    ("2tawarikh", "2 Tawarikh", 36),
    ("ezra", "Ezra", 10),
    ("nehemia", "Nehemia", 13),
    ("ester", "Ester", 10),
    ("ayub", "Ayub", 42),
    ("mazmur", "Mazmur", 150),
    ("amsal", "Amsal", 31),
    ("pengkhotbah", "Pengkhotbah", 12),
    ("kidung_agung", "Kidung Agung", 8),
    ("yesaya", "Yesaya", 66),
    ("yeremia", "Yeremia", 52),
    ("ratapan", "Ratapan", 5),
    ("yehezkiel", "Yehezkiel", 48),
    ("daniel", "Daniel", 12),
    ("hosea", "Hosea", 14),
    ("yoel", "Yoel", 3),
    ("amos", "Amos", 9),
    ("obaja", "Obaja", 1),
    ("yunus", "Yunus", 4),
    ("mikha", "Mikha", 7),
    ("nahum", "Nahum", 3),
    ("habakuk", "Habakuk", 3),
    ("zefanya", "Zefanya", 3),
    ("hagai", "Hagai", 2),
    ("zakharia", "Zakharia", 14),
    ("maleakhi", "Maleakhi", 4),
    ("matius", "Matius", 28),
    ("markus", "Markus", 16),
    ("lukas", "Lukas", 24),
    ("yohanes", "Yohanes", 21),
    ("kisah", "Kisah", 28),
    ("roma", "Roma", 16),
    ("1korintus", "1 Korintus", 16),
    ("2korintus", "2 Korintus", 13),
    ("galatia", "Galatia", 6),
    ("efesus", "Efesus", 6),
    ("filipi", "Filipi", 4),
    ("kolose", "Kolose", 4),
    ("1tesalonika", "1 Tesalonika", 5),
    ("2tesalonika", "2 Tesalonika", 3),
    ("1timotius", "1 Timotius", 6),
    ("2timotius", "2 Timotius", 4),
    ("titus", "Titus", 3),
    ("filemon", "Filemon", 1),
    ("ibrani", "Ibrani", 13),
    ("yakobus", "Yakobus", 5),
    ("1petrus", "1 Petrus", 5),
    ("2petrus", "2 Petrus", 3),
    ("1yohanes", "1 Yohanes", 5),
    ("2yohanes", "2 Yohanes", 1),
    ("3yohanes", "3 Yohanes", 1),
    ("yudas", "Yudas", 1),
    ("wahyu", "Wahyu", 22),
]

def main():
    out_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "bible")
    os.makedirs(out_dir, exist_ok=True)
    data = [{"code": c, "name": n, "chapters": ch} for c, n, ch in BOOKS]
    out_path = os.path.join(out_dir, "index.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    print(f"Wrote {len(data)} books to {out_path}")

if __name__ == "__main__":
    main()
