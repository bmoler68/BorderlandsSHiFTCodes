# Borderlands SHiFT Codes Dashboard

Static dashboard for GitHub Pages. It loads rows from Supabase **`borderlands_shift.shift_codes_current`** (PostgREST) using **`config.js`** with your project URL and anon key.

## Files

| File | Purpose |
|------|---------|
| `index.html` | Main page (use as site entry) |
| `shift-codes-dashboard.css` | Single stylesheet: layout shell (nav, footer, variables) + dashboard UI |
| `shift-codes-dashboard.js` | Fetch from Supabase, filters, charts, table |
| `config.example.js` | Template for local `config.js` (do not put real keys in git) |
| `config.js` | **Generated in CI** or copied locally — listed in `.gitignore` |
| `serve.py` | Local static server (`python serve.py`) |

`shift-codes-dashboard.html` redirects to `index.html` for old links.

## GitHub Actions

Workflow **Deploy dashboard to GitHub Pages** (`.github/workflows/dashboard-pages.yml`):

- Runs on **workflow_dispatch** or when **`dashboard/**`** (or the workflow file) changes on **`main`** / **`master`**.
- Writes **`dashboard/config.js`** from secrets **`DASHBOARD_SUPABASE_URL`** and **`DASHBOARD_SUPABASE_ANON_KEY`**, then uploads the **`dashboard/`** folder as the Pages artifact.

Enable **Pages → Build and deployment → Source: GitHub Actions** once in the repository settings.

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

## Local preview

From the `dashboard` folder, run (not `file://` — the browser must load `config.js` and call Supabase over HTTP):

```bash
cd dashboard
python serve.py
```

Optional: `python serve.py --port 9000` or `--host 0.0.0.0` to reach the dev machine from another device on your network.

Copy `config.example.js` to **`config.js`**, set `supabaseUrl` and `supabaseAnonKey`, then open the URL printed in the terminal (default `http://127.0.0.1:8765/`).

## Charts

- **New codes over time** — counts rows by **`ingested_at_utc`** month for the selected game (proxy for “first seen” in this dataset).
- **Pie charts** — status and reward-type breakdown for the selected game.

## Disclaimer

This is an unofficial fan-made tool. Borderlands and SHiFT are trademarks of Gearbox Software and 2K Games.
