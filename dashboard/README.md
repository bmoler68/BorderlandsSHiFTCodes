# Borderlands SHiFT Codes Dashboard

Dashboard for Borderlands and Tiny Tina's Wonderlands SHiFT codes. It loads a published Google Sheet CSV (with hosted fallback), shows franchise-wide stats, per-game summary cards, trend/pie charts, and a filterable table for the selected game.

![SHiFT icon](../images/featured-app-icons/SHiFT_icon.png)

## What it does

- Fetches CSV from a **primary Google Sheets URL**, then **fallback** [`BL_SHIFT_CODES.csv`](https://www.brianmoler.com/appdata/borderlandsshiftcodes/BL_SHIFT_CODES.csv) on this site if the primary request fails.
- **Hero row:** total rows loaded, count of codes whose computed status is **active** (not unknown-expiration or non-expiring), and **last updated** time.
- **Game cards:** one card per title with counts for Active, Unknown expiration, Non-expiring, and Expired (full dataset per game, before detail filters). **Borderlands 4** is selected initially. Use **Show Codes** (or the card flow) to choose the game for the table below.
- **Charts:** monthly new-code trend chart + status/code-type pie charts for the selected game (Chart.js).
- **Code details:** table for the **selected game only** with search, **Code type** filters (Key / Cosmetic / Gear from `IS_KEY`, `IS_COSMETIC`, `IS_GEAR`), and **Status** filters. **Expired** is unchecked by default. Rows are ordered by expiration timestamp when data loads (newest first).
- **Copy** button per row (clipboard API, with a fallback path).
- **Refresh Data** to reload the CSV (no automatic refresh).

Games are driven by `Y` columns in the sheet (BL1, BL2, TPS, BL3, BL4, Wonderlands, etc.), as implemented in `shift-codes-dashboard.js`.

## Files

| File | Purpose |
|------|---------|
| `shift-codes-dashboard.html` | Page structure, CSP and security meta tags, script tags |
| `shift-codes-dashboard.js` | Fetch, parse, filters, rendering, copy behavior |
| `shift-codes-dashboard.css` | Layout and responsive rules |
| `../styles.css`, `../copyright-year.js` | Shared site chrome and footer year |

## Running locally

Serve or open the site from a tree that includes parent paths referenced by the HTML (`../styles.css`, `../images/`, `../copyright-year.js`). Network access is required to fetch CSV data from Google Sheets/fallback URL.

## How to use

1. Check hero stats and scan **game cards** for counts.
2. Select a game so the **Code details** table shows that title’s codes.
3. Narrow the table with **search** and the **Code type** / **Status** checkboxes.
4. **Copy** a code and redeem on the official site via **Redeem Codes** (opens [shift.gearboxsoftware.com](https://shift.gearboxsoftware.com) in a new tab).

## Time and status

Expiration logic uses **US Eastern Time** (with DST rules in code) when combining date and optional time fields from the CSV. Sentinel dates in the sheet mark non-expiring and unknown-expiration rows as defined in `getCodeStatus` / `getExpirationTimestamp`.

## External assets

- **Google Fonts** — Inter
- **Font Awesome** (cdnjs) — icons, loaded with **Subresource Integrity** in the HTML
- **Chart.js** (cdnjs) — trend and pie charts

Scripts are **same-origin only** (`copyright-year.js`, `shift-codes-dashboard.js`); there is no third-party JavaScript.

## Security

- CSP is defined inline in `shift-codes-dashboard.html` (allows the Google sources needed for CSV and cdnjs for CSS/Chart.js).
- User-facing data is escaped in `shift-codes-dashboard.js` via `escapeHTML` and `escapeHTMLAttribute`.
- Status/expiration logic uses parsed timestamps and explicit sentinel handling in `getCodeStatus` / `getExpirationTimestamp`.

## Troubleshooting

- **No data / error state** — Network, CORS, or CSP can block `fetch`; use devtools and try **Refresh Data**.
- **Copy fails** — Allow clipboard access when prompted, or copy the visible code text manually.
- **Empty table** — Loosen status filters (e.g. enable **Expired**), widen type filters, clear search, or pick another game.

## Author

**Brian Moler**

- GitHub: [@bmoler68](https://github.com/bmoler68)
- Website: [brianmoler.com](https://www.brianmoler.com)
- Email: bmoler@brianmoler.com

## License

Copyright © 2026 Brian Moler. All rights reserved.

Borderlands and SHiFT are trademarks of Gearbox Software and 2K Games.

## Disclaimer

This is an unofficial fan-made tool. It is not affiliated with, endorsed by, or connected to Gearbox Software, 2K Games, or any official Borderlands entities.
