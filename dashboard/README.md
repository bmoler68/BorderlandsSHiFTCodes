# Borderlands SHiFT Codes Dashboard

Static dashboard for GitHub Pages. It loads rows from Supabase **`borderlands_shift.shift_codes_current`** (PostgREST) using **`config.js`** with your project URL and anon key.

The **[main project README](../README.md)** covers the Android app, maintainer CSV/ETL pipeline, Room schema, and release history. This document focuses on the web dashboard.

## Current status

| Area | Behavior |
|------|----------|
| **Data source** | Same view and anon REST access as the Android app |
| **Row validation** | Invalid rows skipped (app parity) — see [Row validation](#row-validation-app-parity) |
| **Detail table sort** | Expiration ↓, ingest ↓, code ↑ — same order as the app list |
| **Trend chart** | Counts by **`ingested_at_utc`** month; rows with placeholder ingest **1999-12-31** UTC are omitted |
| **Deploy** | GitHub Actions → Pages when **`dashboard/**`** changes (or manual dispatch) |

## Files

| File | Purpose |
|------|---------|
| `index.html` | Main page (use as site entry) |
| `shift-codes-dashboard.css` | Single stylesheet: layout shell (nav, footer, variables) + dashboard UI |
| `shift-codes-dashboard.js` | Fetch from Supabase, validation, filters, charts, table |
| `config.example.js` | Template for local `config.js` (do not put real keys in git) |
| `config.js` | **Generated in CI** or copied locally — listed in `.gitignore` |
| `serve.py` | Local static server (`python serve.py`) |

`shift-codes-dashboard.html` redirects to `index.html` for old links.

## GitHub Actions

Workflow **Deploy dashboard to GitHub Pages** (`.github/workflows/dashboard-pages.yml`):

- Runs on **workflow_dispatch** or when **`dashboard/**`** (or the workflow file) changes on **`main`** / **`master`**.
- Writes **`dashboard/config.js`** from secrets **`DASHBOARD_SUPABASE_URL`** and **`DASHBOARD_SUPABASE_ANON_KEY`**, then uploads the **`dashboard/`** folder as the Pages artifact.

Enable **Pages → Build and deployment → Source: GitHub Actions** once in the repository settings.

Related: **`.github/workflows/supabase-shift-codes-etl.yml`** loads **`appdata/BL_SHIFT_CODES.csv`** into Postgres (service role); the dashboard only needs anon **SELECT**.

## Supabase checklist

1. **Exposed schemas** — include `borderlands_shift` (Project Settings → Data API → Exposed schemas).
2. **RLS** — `shift_codes_v1` has RLS enabled. Grant alone is not enough without a policy. Example read policy for the view (run in SQL editor; adjust if you prefer `shift_codes_v1`):

```sql
create policy "Public read shift codes"
  on borderlands_shift.shift_codes_v1
  for select
  to anon, authenticated
  using (true);
```

3. **CORS / URLs** — if the browser blocks requests, add your GitHub Pages origin under **Authentication → URL Configuration** as appropriate for your project.

DDL and column semantics: **`sql/supabase_borderlands_shift_codes.sql`**.

## Local preview

From the `dashboard` folder, run (not `file://` — the browser must load `config.js` and call Supabase over HTTP):

```bash
cd dashboard
python serve.py
```

Optional: `python serve.py --port 9000` or `--host 0.0.0.0` to reach the dev machine from another device on your network.

Copy `config.example.js` to **`config.js`**, set `supabaseUrl` and `supabaseAnonKey`, then open the URL printed in the terminal (default `http://127.0.0.1:8765/`).

After load, open the browser **developer console** to see how many rows were accepted or skipped.

## Row validation (app parity)

After each Supabase fetch, **`validateSupabaseRow()`** in `shift-codes-dashboard.js` applies the same rules as the Android app (`AppConfig.Validation`, `shiftCodeFromJsonOrNull`). Invalid rows are **skipped** and logged:

`Skipping row <code>: <reason>`

| Rule | Limit / requirement |
|------|---------------------|
| Code length | ≤ **29** (five groups of five characters plus hyphens, e.g. `AAAAA-BBBBB-CCCCC-DDDDD-EEEEE`) |
| Reward length | ≤ **200**, non-blank code and reward |
| Expiration flags | `is_non_expiring` and `is_unknown_expiration` not both true |
| Expiration date | Required when neither flag is set; accepts `yyyy-MM-dd` or ISO datetime prefix |
| Games | At least one of `bl`, `bl_tps`, `bl2`, `bl3`, `bl4`, `wonderlands` |

Test inserts must use a **29-character** code. A six-character final segment (30 characters total) is rejected here and in the app.

## Sorting & charts

### Detail table (matches Android list)

1. **Expiration** descending (semantic flags use the same noon-UTC sentinel dates as the app)
2. **`ingested_at_utc`** descending (missing or placeholder **1999-12-31** UTC sorts last among the same expiration)
3. **Code** ascending

### Charts

- **New codes over time** — counts rows by **`ingested_at_utc`** month for the selected game (proxy for “first seen” in this dataset). Placeholder ingest **1999-12-31** UTC is excluded from this chart only.
- **Pie charts** — status and reward-type breakdown for the selected game.

The hero **Data refreshed** line uses the latest **`ingested_at_utc`** across the full validated dataset.

## Disclaimer

This is an unofficial fan-made tool. Borderlands and SHiFT are trademarks of Gearbox Software and 2K Games.
