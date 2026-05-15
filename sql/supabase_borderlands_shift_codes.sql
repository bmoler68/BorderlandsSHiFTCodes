-- ============================================================================
-- Borderlands SHiFT Codes — Supabase / PostgreSQL DDL
--
-- Modeled after sql/schema.sql (schema isolation, indexes, views, RLS hooks,
-- grants). Reflects appdata/BL_SHIFT_CODES.csv columns with snake_case names
-- and two semantic flags that replace magic expiration dates from the app:
--
--   CSV / legacy app          →  Database
--   ----------------          →  --------
--   EXPIRATION = 1999-12-31   →  is_non_expiring = true, expiration_date NULL
--   EXPIRATION = 2075-12-31   →  is_unknown_expiration = true, expiration_date NULL
--   any other yyyy-MM-dd      →  both flags false, expiration_date set
--
-- CSV header (matches shift_codes_v1 data columns; booleans as true/false; time as HH:MM:SS):
--   code, reward, expiration_date, expiration_time, is_non_expiring, is_unknown_expiration,
--   bl, bl_tps, bl2, bl3, wonderlands, bl4, is_key, is_cosmetic, is_gear
--   Table also has id, ingested_at_utc, updated_at_utc (defaults / trigger; not in CSV).
--   When is_non_expiring or is_unknown_expiration is true, leave expiration_date and
--   expiration_time empty (NULL on load). Use shift_codes_current.expiration_time_12h for display.
--
-- Apply in Supabase SQL editor or ship as a migration.
--
-- REQUIRED for REST / ETL: expose schema borderlands_shift to PostgREST. In the
-- Supabase dashboard: Project Settings → Data API → Exposed schemas → add
-- borderlands_shift (alongside public). Without this, the API returns PGRST106.
-- https://supabase.com/docs/guides/api/using-custom-schemas
--
-- Re-run GRANT blocks after DROP/CREATE on views (privileges reset).
-- ============================================================================

create schema if not exists borderlands_shift;

-- One row per SHiFT code (matches app uniqueness on code string).

create table if not exists borderlands_shift.shift_codes_v1 (
  id uuid primary key default gen_random_uuid(),

  code text not null,
  reward text not null,

  -- Real calendar expiration when the code has a known end date.
  -- Must be NULL when either semantic expiration flag is true.
  expiration_date date null,

  -- Time of day for ET expiration (app semantics). Stored as time; use
  -- expiration_time_12h in shift_codes_current for AM/PM strings (e.g. 08:42:00 PM).
  expiration_time time(0) null,

  -- Replaces legacy 1999-12-31 (non-expiring) and 2075-12-31 (unknown) encodings.
  is_non_expiring boolean not null default false,
  is_unknown_expiration boolean not null default false,

  bl boolean not null default false,
  bl_tps boolean not null default false,
  bl2 boolean not null default false,
  bl3 boolean not null default false,
  wonderlands boolean not null default false,
  bl4 boolean not null default false,

  is_key boolean not null default false,
  is_cosmetic boolean not null default false,
  is_gear boolean not null default false,

  -- Row bookkeeping for Supabase sync / ETL.
  ingested_at_utc timestamptz not null default now(),
  updated_at_utc timestamptz not null default now(),

  constraint shift_codes_v1_code_unique unique (code),

  constraint shift_codes_v1_flags_mutually_exclusive
    check (not (is_non_expiring and is_unknown_expiration)),

  constraint shift_codes_v1_expiration_consistency
    check (
      (is_non_expiring and expiration_date is null and not is_unknown_expiration)
      or (is_unknown_expiration and expiration_date is null and not is_non_expiring)
      or (
        not is_non_expiring
        and not is_unknown_expiration
        and expiration_date is not null
      )
    ),

  constraint shift_codes_v1_at_least_one_game
    check (bl or bl_tps or bl2 or bl3 or wonderlands or bl4)
);

create index if not exists idx_shift_codes_v1_expiration_date
  on borderlands_shift.shift_codes_v1 (expiration_date desc)
  where not is_non_expiring and not is_unknown_expiration;

create index if not exists idx_shift_codes_v1_non_expiring
  on borderlands_shift.shift_codes_v1 (code)
  where is_non_expiring;

create index if not exists idx_shift_codes_v1_unknown_expiration
  on borderlands_shift.shift_codes_v1 (code)
  where is_unknown_expiration;

create index if not exists idx_shift_codes_v1_reward_flags
  on borderlands_shift.shift_codes_v1 (is_key, is_cosmetic, is_gear);

comment on table borderlands_shift.shift_codes_v1 is
  'SHiFT codes aligned with BL_SHIFT_CODES.csv; use is_non_expiring / is_unknown_expiration instead of magic EXPIRATION dates.';

comment on column borderlands_shift.shift_codes_v1.is_non_expiring is
  'True when legacy CSV used EXPIRATION 1999-12-31; expiration_date must be NULL.';

comment on column borderlands_shift.shift_codes_v1.is_unknown_expiration is
  'True when legacy CSV used EXPIRATION 2075-12-31; expiration_date must be NULL.';

comment on column borderlands_shift.shift_codes_v1.expiration_time is
  'Expiration clock in Eastern (ET) per app rules; wall time without date. Import from CSV with ::time (accepts 12h AM/PM, e.g. ''8:42:00 PM''). NULL means unspecified (app default end-of-day).';

-- Keep updated_at_utc fresh on row changes (optional but typical for Supabase).

create or replace function borderlands_shift.touch_shift_codes_v1_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at_utc := now();
  return new;
end;
$$;

drop trigger if exists trg_shift_codes_v1_touch_updated_at
  on borderlands_shift.shift_codes_v1;

create trigger trg_shift_codes_v1_touch_updated_at
  before update on borderlands_shift.shift_codes_v1
  for each row
  execute function borderlands_shift.touch_shift_codes_v1_updated_at();

-- Stable view name for clients (same columns as table v1).

drop view if exists borderlands_shift.shift_codes_current;

create view borderlands_shift.shift_codes_current as
select
  id,
  code,
  reward,
  expiration_date,
  is_non_expiring,
  is_unknown_expiration,
  expiration_time,
  case
    when expiration_time is null then null
    else to_char(expiration_time, 'HH12:MI:SS AM')
  end as expiration_time_12h,
  bl,
  bl_tps,
  bl2,
  bl3,
  wonderlands,
  bl4,
  is_key,
  is_cosmetic,
  is_gear,
  ingested_at_utc,
  updated_at_utc
from borderlands_shift.shift_codes_v1;

-- ---------------------------------------------------------------------------
-- Row level security (RLS)
--
-- Enable RLS; add CREATE POLICY in Supabase so anon/authenticated can SELECT
-- (or service_role can upsert) according to your threat model.
-- ---------------------------------------------------------------------------

alter table borderlands_shift.shift_codes_v1 enable row level security;

-- ---------------------------------------------------------------------------
-- service_role — server-side sync / ETL
-- ---------------------------------------------------------------------------

grant usage on schema borderlands_shift to service_role;

grant select, insert, update, delete on table borderlands_shift.shift_codes_v1 to service_role;
grant select on table borderlands_shift.shift_codes_current to service_role;

-- ---------------------------------------------------------------------------
-- anon / authenticated — read-only public API (adjust after you add policies)
--
-- 1) Add borderlands_shift to Data API → Exposed schemas (required for REST; see file header).
-- 2) Define RLS policies; without them, non-owner roles may still be blocked.
-- 3) Re-run grants if you recreate the view.
-- ---------------------------------------------------------------------------

grant usage on schema borderlands_shift to anon, authenticated;

grant select on table borderlands_shift.shift_codes_v1 to anon, authenticated;
grant select on table borderlands_shift.shift_codes_current to anon, authenticated;

alter view borderlands_shift.shift_codes_current
set (security_invoker = on);
