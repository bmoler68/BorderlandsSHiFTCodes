// Borderlands SHiFT Codes Dashboard — reads borderlands_shift.shift_codes_current via Supabase REST.
// Expects window.__SHIFT_DASHBOARD_CONFIG__ { supabaseUrl, supabaseAnonKey } from config.js (generated in CI).

const REST_SCHEMA = 'borderlands_shift';
const REST_TABLE = 'shift_codes_current';

const GAME_CONFIG = [
    { id: 'BL4', name: 'Borderlands 4', icon: 'fa-gamepad' },
    { id: 'BL3', name: 'Borderlands 3', icon: 'fa-gamepad' },
    { id: 'BL2', name: 'Borderlands 2', icon: 'fa-gamepad' },
    { id: 'BL:TPS', name: 'Borderlands: TPS', icon: 'fa-gamepad' },
    { id: 'BL', name: 'Borderlands', icon: 'fa-gamepad' },
    { id: 'Wonderlands', name: 'Wonderlands', icon: 'fa-hat-wizard' }
];

let allCodes = [];
let selectedGame = 'BL4';
let newCodesTrendChart = null;
let statusBreakdownChart = null;
let typeBreakdownChart = null;
let chartViewportListenersBound = false;

const logger = {
    log: (message, ...args) => console.log(message, ...args),
    warn: (message, ...args) => console.warn(message, ...args),
    error: (message, ...args) => console.error(message, ...args)
};

function escapeHTML(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/** For double-quoted HTML attributes (e.g. data-code). escapeHTML() does not encode `"`. */
function escapeHTMLAttribute(text) {
    if (text == null || text === '') return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

function formatPostgresTimeAs12h(t) {
    if (!t || typeof t !== 'string') return '';
    const m = t.trim().match(/^(\d{1,2}):(\d{2})(?::(\d{2}))?/);
    if (!m) return '';
    let h = parseInt(m[1], 10);
    const mm = m[2];
    const ss = m[3] ? m[3].padStart(2, '0') : '00';
    if (Number.isNaN(h) || h < 0 || h > 23) return '';
    const ampm = h >= 12 ? 'PM' : 'AM';
    const h12 = h % 12 || 12;
    return `${h12}:${mm}:${ss} ${ampm}`;
}

function ynFromApi(b) {
    return b === true || b === 't' ? 'Y' : 'N';
}

function normalizeFromSupabase(row) {
    const non = row.is_non_expiring === true;
    const unk = row.is_unknown_expiration === true;
    const exp = row.expiration_date || '';
    let pseudoExp = exp;
    if (non) pseudoExp = '1999-12-31';
    else if (unk) pseudoExp = '2075-12-31';

    const time12 = row.expiration_time_12h || formatPostgresTimeAs12h(row.expiration_time);

    const code = {
        CODE: row.code,
        REWARD: row.reward,
        EXPIRATION: pseudoExp,
        'EXPIRATION TIME': time12 || '',
        IS_KEY: ynFromApi(row.is_key),
        IS_COSMETIC: ynFromApi(row.is_cosmetic),
        IS_GEAR: ynFromApi(row.is_gear),
        BL: ynFromApi(row.bl),
        'BL:TPS': ynFromApi(row.bl_tps),
        BL2: ynFromApi(row.bl2),
        BL3: ynFromApi(row.bl3),
        Wonderlands: ynFromApi(row.wonderlands),
        BL4: ynFromApi(row.bl4)
    };

    if (exp && !non && !unk) {
        code.expirationDate = new Date(`${exp}T12:00:00Z`);
    } else if (pseudoExp) {
        code.expirationDate = new Date(`${pseudoExp}T12:00:00Z`);
    } else {
        code.expirationDate = null;
    }

    code.timestampDate = row.ingested_at_utc ? new Date(row.ingested_at_utc) : null;
    return code;
}

async function fetchShiftCodesFromSupabase() {
    const cfg = typeof window !== 'undefined' ? window.__SHIFT_DASHBOARD_CONFIG__ : null;
    if (!cfg || !cfg.supabaseUrl || !cfg.supabaseAnonKey) {
        throw new Error(
            'Missing dashboard config. Copy config.example.js to config.js and set supabaseUrl and supabaseAnonKey, or open the GitHub Pages build that generates config.js in CI.'
        );
    }

    const base = String(cfg.supabaseUrl).replace(/\/$/, '');
    const key = cfg.supabaseAnonKey;
    const headers = {
        apikey: key,
        Authorization: `Bearer ${key}`,
        Accept: 'application/json',
        'Accept-Profile': REST_SCHEMA
    };

    const pageSize = 500;
    const all = [];
    let offset = 0;

    for (;;) {
        const url = `${base}/rest/v1/${REST_TABLE}?select=*&order=code.asc&limit=${pageSize}&offset=${offset}`;
        const response = await fetch(url, { headers });
        if (!response.ok) {
            const errBody = await response.text();
            throw new Error(`Supabase REST ${response.status}: ${errBody}`);
        }
        const chunk = await response.json();
        if (!Array.isArray(chunk) || chunk.length === 0) break;
        all.push(...chunk);
        if (chunk.length < pageSize) break;
        offset += pageSize;
    }

    logger.log(`Loaded ${all.length} row(s) from ${REST_SCHEMA}.${REST_TABLE}`);
    return all.map(normalizeFromSupabase);
}

function getSecondSunday(year, month) {
    const firstDay = new Date(year, month, 1);
    const firstDayOfWeek = firstDay.getDay();
    const daysUntilFirstSunday = (7 - firstDayOfWeek) % 7;
    return daysUntilFirstSunday + 8;
}

function getFirstSunday(year, month) {
    const firstDay = new Date(year, month, 1);
    const firstDayOfWeek = firstDay.getDay();
    const daysUntilFirstSunday = (7 - firstDayOfWeek) % 7;
    return daysUntilFirstSunday === 0 ? 1 : daysUntilFirstSunday + 1;
}

function getEasternTimeOffsetHours(date) {
    const year = date.getUTCFullYear();
    const month = date.getUTCMonth();
    const day = date.getUTCDate();

    if (month > 2 && month < 10) return -4;
    if (month === 2) {
        const secondSunday = getSecondSunday(year, 2);
        if (day >= secondSunday) return -4;
    }
    if (month === 10) {
        const firstSunday = getFirstSunday(year, 10);
        if (day < firstSunday) return -4;
    }
    return -5;
}

function getExpirationTimestamp(code) {
    if (!code.expirationDate || !code.EXPIRATION) return null;
    if (code.EXPIRATION === '1999-12-31' || code.EXPIRATION === '2075-12-31') return null;

    const expirationDate = new Date(code.expirationDate);
    const year = expirationDate.getUTCFullYear();
    const month = expirationDate.getUTCMonth();
    const day = expirationDate.getUTCDate();

    let localHour = 0;
    let localMinute = 0;
    let localSecond = 0;
    const timeStr = code['EXPIRATION TIME'] || code.EXPIRATION_TIME || '';
    if (typeof timeStr === 'string' && timeStr.trim()) {
        const m = timeStr.trim().match(/^(\d{1,2}):(\d{2})(?::(\d{2}))?\s*(AM|PM)?$/i);
        if (m) {
            let h = parseInt(m[1], 10);
            const mm = parseInt(m[2], 10);
            const ss = m[3] ? parseInt(m[3], 10) : 0;
            const ampm = m[4] ? m[4].toUpperCase() : '';
            if (ampm) {
                if (ampm === 'AM' && h === 12) h = 0;
                if (ampm === 'PM' && h !== 12) h += 12;
            } else {
                if (h < 0 || h > 23) h = 0;
            }
            if (!Number.isNaN(h) && h >= 0 && h <= 23) localHour = h;
            if (!Number.isNaN(mm) && mm >= 0 && mm <= 59) localMinute = mm;
            if (!Number.isNaN(ss) && ss >= 0 && ss <= 59) localSecond = ss;
        }
    }

    const dstCheckDate = new Date(Date.UTC(year, month, day, 12, 0, 0));
    const easternOffsetHours = getEasternTimeOffsetHours(dstCheckDate);
    const utcHour = localHour - easternOffsetHours;
    return new Date(Date.UTC(year, month, day, utcHour, localMinute, localSecond, 0));
}

function getCodeStatus(code) {
    if (!code.expirationDate || !code.EXPIRATION) return 'expired';
    if (code.EXPIRATION === '1999-12-31') return 'non-expiring';
    if (code.EXPIRATION === '2075-12-31') return 'unknown-expiration';

    const expirationWithTime = getExpirationTimestamp(code);
    if (!expirationWithTime) return 'expired';
    return expirationWithTime > new Date() ? 'active' : 'expired';
}

function isCodeActive(code) {
    const status = getCodeStatus(code);
    return status === 'active' || status === 'non-expiring' || status === 'unknown-expiration';
}

function filterCodesByGame(codes, game) {
    return codes.filter(code => code[game] === 'Y');
}

function filterCodesBySearch(codes, query) {
    if (!query) return codes;
    const lowerQuery = query.toLowerCase();
    return codes.filter(code => {
        const codeValue = (code.CODE || '').toLowerCase();
        const rewardValue = (code.REWARD || '').toLowerCase();
        return codeValue.includes(lowerQuery) || rewardValue.includes(lowerQuery);
    });
}

function getSelectedTypeFilters() {
    const key = document.getElementById('filterKey');
    const cosmetic = document.getElementById('filterCosmetic');
    const gear = document.getElementById('filterGear');
    return {
        key: !!(key && key.checked),
        cosmetic: !!(cosmetic && cosmetic.checked),
        gear: !!(gear && gear.checked)
    };
}

function filterCodesByType(codes, selected) {
    if (!selected.key && !selected.cosmetic && !selected.gear) return [];
    if (selected.key && selected.cosmetic && selected.gear) return codes;

    return codes.filter(code => {
        const isKey = code.IS_KEY === 'Y';
        const isCosmetic = code.IS_COSMETIC === 'Y';
        const isGear = code.IS_GEAR === 'Y';
        return (selected.key && isKey) || (selected.cosmetic && isCosmetic) || (selected.gear && isGear);
    });
}

function getSelectedStatusFilters() {
    const active = document.getElementById('statusActive');
    const unknown = document.getElementById('statusUnknown');
    const nonExpiring = document.getElementById('statusNonExpiring');
    const expired = document.getElementById('statusExpired');
    return {
        active: !!(active && active.checked),
        'unknown-expiration': !!(unknown && unknown.checked),
        'non-expiring': !!(nonExpiring && nonExpiring.checked),
        expired: !!(expired && expired.checked)
    };
}

function filterCodesByStatus(codes, selectedStatuses) {
    const anySelected = Object.values(selectedStatuses).some(Boolean);
    if (!anySelected) return [];
    return codes.filter(code => !!selectedStatuses[getCodeStatus(code)]);
}

function formatRelativeTime(date) {
    if (!date) return 'Unknown';
    const now = new Date();
    const diffMs = date - now;
    if (diffMs <= 0) return 'Now';

    const minuteMs = 60 * 1000;
    const totalMinutes = Math.ceil(diffMs / minuteMs);
    const days = Math.floor(totalMinutes / (24 * 60));
    const remMinutesAfterDays = totalMinutes - days * 24 * 60;
    const hours = Math.floor(remMinutesAfterDays / 60);
    const minutes = remMinutesAfterDays % 60;

    if (days > 0) return hours > 0 ? `${days}d ${hours}h` : `${days}d`;
    if (hours > 0) return minutes > 0 ? `${hours}h ${minutes}m` : `${hours}h`;
    return `${minutes}m`;
}

function formatDateTimeEastern(date) {
    if (!date) return 'Unknown';
    const formatter = new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true,
        timeZone: 'America/New_York'
    });
    return `${formatter.format(date)} ET`;
}

function formatDateString(dateStr) {
    const parts = dateStr.split('-');
    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1;
    const day = parseInt(parts[2], 10);
    const date = new Date(year, month, day);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function getGameBadges(code) {
    const games = [];
    const gameMap = {
        BL: { label: 'BL1', class: 'bl1' },
        'BL:TPS': { label: 'TPS', class: 'tps' },
        BL2: { label: 'BL2', class: 'bl2' },
        BL3: { label: 'BL3', class: 'bl3' },
        Wonderlands: { label: 'WL', class: 'wl' },
        BL4: { label: 'BL4', class: 'bl4' }
    };

    Object.entries(gameMap).forEach(([key, game]) => {
        if (code[key] === 'Y') games.push(game);
    });
    return games;
}

function getGameBadgeForGame(gameId) {
    const gameMap = {
        BL: { label: 'BL1', class: 'bl1' },
        'BL:TPS': { label: 'TPS', class: 'tps' },
        BL2: { label: 'BL2', class: 'bl2' },
        BL3: { label: 'BL3', class: 'bl3' },
        Wonderlands: { label: 'WL', class: 'wl' },
        BL4: { label: 'BL4', class: 'bl4' }
    };
    return gameMap[gameId] || null;
}

function getStatusDisplayInfo(status) {
    const statusInfo = {
        active: { text: 'Active', class: 'status-active' },
        expired: { text: 'Expired', class: 'status-expired' },
        'non-expiring': { text: 'Non-Expiring', class: 'status-non-expiring' },
        'unknown-expiration': { text: 'Unknown Expiration', class: 'status-unknown' }
    };
    return statusInfo[status] || statusInfo.expired;
}

function getStatusCounts(codes) {
    const counts = { active: 0, expired: 0, 'non-expiring': 0, 'unknown-expiration': 0 };
    codes.forEach(code => {
        const status = getCodeStatus(code);
        counts[status]++;
    });
    return counts;
}

function getExpirationDisplay(code) {
    const status = getCodeStatus(code);
    if (status === 'non-expiring') return 'Never Expires';
    if (status === 'unknown-expiration') return 'Unknown Expiration';
    if (status === 'active') {
        const expirationTimestamp = getExpirationTimestamp(code);
        return `${formatDateTimeEastern(expirationTimestamp)} (${formatRelativeTime(expirationTimestamp)})`;
    }

    const expirationTimestamp = getExpirationTimestamp(code);
    if (expirationTimestamp) return `Expired: ${formatDateTimeEastern(expirationTimestamp)}`;
    if (code.EXPIRATION) return `Expired: ${formatDateString(code.EXPIRATION)}`;
    return 'Expired';
}

function getGameNameById(gameId) {
    const game = GAME_CONFIG.find(item => item.id === gameId);
    return game ? game.name : gameId;
}

function getChartViewportTier() {
    if (typeof window === 'undefined') return 'desktop';
    if (window.matchMedia('(max-width: 480px)').matches) return 'narrow';
    if (window.matchMedia('(max-width: 768px)').matches) return 'compact';
    return 'desktop';
}

function getTrendChartTypography() {
    const tier = getChartViewportTier();
    if (tier === 'narrow') {
        return { legend: 10, tick: 10, tooltipTitle: 11, tooltipBody: 10, barRadius: 4 };
    }
    if (tier === 'compact') {
        return { legend: 11, tick: 11, tooltipTitle: 12, tooltipBody: 11, barRadius: 5 };
    }
    return { legend: 12, tick: 12, tooltipTitle: 13, tooltipBody: 12, barRadius: 6 };
}

function getPieChartTypography() {
    const tier = getChartViewportTier();
    if (tier === 'narrow') {
        return { legend: 10, tooltipTitle: 11, tooltipBody: 10 };
    }
    if (tier === 'compact') {
        return { legend: 11, tooltipTitle: 12, tooltipBody: 11 };
    }
    return { legend: 12, tooltipTitle: 13, tooltipBody: 12 };
}

function aggregateNewCodesByMonthForGame(codes, gameId) {
    const byMonth = new Map();

    codes.forEach(code => {
        if (code[gameId] !== 'Y') return;
        const d = code.timestampDate;
        if (!(d instanceof Date) || Number.isNaN(d.getTime())) return;

        const monthKey = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
        byMonth.set(monthKey, (byMonth.get(monthKey) || 0) + 1);
    });

    const keys = Array.from(byMonth.keys()).sort();
    const labels = keys.map(key => {
        const [yearStr, monthStr] = key.split('-');
        const year = parseInt(yearStr, 10);
        const month = parseInt(monthStr, 10) - 1;
        return new Date(year, month, 1).toLocaleDateString('en-US', {
            month: 'short',
            year: 'numeric'
        });
    });

    const values = keys.map(k => byMonth.get(k) || 0);
    return { labels, values };
}

function destroyTrendChart() {
    if (newCodesTrendChart) {
        newCodesTrendChart.destroy();
        newCodesTrendChart = null;
    }
}

function destroyPieCharts() {
    if (statusBreakdownChart) {
        statusBreakdownChart.destroy();
        statusBreakdownChart = null;
    }
    if (typeBreakdownChart) {
        typeBreakdownChart.destroy();
        typeBreakdownChart = null;
    }
}

function applyTrendChartResponsiveTypography() {
    if (!newCodesTrendChart) return;
    const t = getTrendChartTypography();
    const opts = newCodesTrendChart.options;
    opts.plugins.legend.labels.font = { size: t.legend };
    opts.plugins.tooltip.titleFont = { size: t.tooltipTitle };
    opts.plugins.tooltip.bodyFont = { size: t.tooltipBody };
    opts.scales.x.ticks.font = { size: t.tick };
    opts.scales.y.ticks.font = { size: t.tick };
    newCodesTrendChart.data.datasets[0].borderRadius = t.barRadius;
    newCodesTrendChart.update();
}

function applyPieChartsResponsiveTypography() {
    const t = getPieChartTypography();
    [statusBreakdownChart, typeBreakdownChart].forEach(chart => {
        if (!chart) return;
        chart.options.plugins.legend.labels.font = { size: t.legend };
        chart.options.plugins.tooltip.titleFont = { size: t.tooltipTitle };
        chart.options.plugins.tooltip.bodyFont = { size: t.tooltipBody };
        chart.update();
    });
}

function setupTrendChartViewportListeners() {
    if (chartViewportListenersBound || typeof window === 'undefined') return;
    chartViewportListenersBound = true;
    const onChange = () => {
        applyTrendChartResponsiveTypography();
        applyPieChartsResponsiveTypography();
    };
    window.matchMedia('(max-width: 768px)').addEventListener('change', onChange);
    window.matchMedia('(max-width: 480px)').addEventListener('change', onChange);
}

function renderNewCodesTrendChart() {
    const subtitle = document.getElementById('trendSubtitle');
    if (subtitle) {
        subtitle.textContent = `Selected game: ${getGameNameById(selectedGame)}`;
    }

    const canvas = document.getElementById('newCodesTrendChart');
    if (!canvas) return;
    destroyTrendChart();

    if (typeof Chart === 'undefined') {
        logger.warn('Chart.js not loaded; skipping trend chart');
        return;
    }

    const { labels, values } = aggregateNewCodesByMonthForGame(allCodes, selectedGame);
    const t = getTrendChartTypography();
    const gameName = getGameNameById(selectedGame);
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    newCodesTrendChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels.length ? labels : ['No ingest data'],
            datasets: [
                {
                    label: `${gameName} new codes`,
                    data: values.length ? values : [0],
                    backgroundColor: 'rgba(61, 220, 132, 0.2)',
                    borderColor: '#3ddc84',
                    borderWidth: 1,
                    borderRadius: t.barRadius,
                    maxBarThickness: 46
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            aspectRatio: 2.6,
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        font: { size: t.legend },
                        boxWidth: 12,
                        padding: 10
                    }
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    titleFont: { size: t.tooltipTitle },
                    bodyFont: { size: t.tooltipBody }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { font: { size: t.tick }, maxRotation: 45, minRotation: 0 }
                },
                y: {
                    beginAtZero: true,
                    ticks: { precision: 0, font: { size: t.tick } }
                }
            }
        }
    });
}

function renderStatusBreakdownChart() {
    const subtitle = document.getElementById('statusBreakdownSubtitle');
    if (subtitle) {
        subtitle.textContent = `Selected game: ${getGameNameById(selectedGame)}`;
    }

    const canvas = document.getElementById('statusBreakdownChart');
    if (!canvas || typeof Chart === 'undefined') return;
    if (statusBreakdownChart) {
        statusBreakdownChart.destroy();
        statusBreakdownChart = null;
    }

    const gameCodes = filterCodesByGame(allCodes, selectedGame);
    const counts = getStatusCounts(gameCodes);
    const t = getPieChartTypography();
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    statusBreakdownChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Active', 'Unknown expiration', 'Non-expiring', 'Expired'],
            datasets: [
                {
                    data: [counts.active, counts['unknown-expiration'], counts['non-expiring'], counts.expired],
                    backgroundColor: ['#28a745', '#ffc107', '#17a2b8', '#dc3545']
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { font: { size: t.legend }, boxWidth: 12, padding: 10 }
                },
                tooltip: {
                    titleFont: { size: t.tooltipTitle },
                    bodyFont: { size: t.tooltipBody }
                }
            }
        }
    });
}

function renderTypeBreakdownChart() {
    const subtitle = document.getElementById('typeBreakdownSubtitle');
    if (subtitle) {
        subtitle.textContent = `Selected game: ${getGameNameById(selectedGame)}`;
    }

    const canvas = document.getElementById('typeBreakdownChart');
    if (!canvas || typeof Chart === 'undefined') return;
    if (typeBreakdownChart) {
        typeBreakdownChart.destroy();
        typeBreakdownChart = null;
    }

    const gameCodes = filterCodesByGame(allCodes, selectedGame);
    const typeCounts = gameCodes.reduce((acc, code) => {
        if (code.IS_KEY === 'Y') acc.key += 1;
        if (code.IS_COSMETIC === 'Y') acc.cosmetic += 1;
        if (code.IS_GEAR === 'Y') acc.gear += 1;
        return acc;
    }, { key: 0, cosmetic: 0, gear: 0 });
    const t = getPieChartTypography();
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    typeBreakdownChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Key', 'Cosmetic', 'Gear'],
            datasets: [
                {
                    data: [typeCounts.key, typeCounts.cosmetic, typeCounts.gear],
                    backgroundColor: ['#2563eb', '#8b5cf6', '#f97316']
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { font: { size: t.legend }, boxWidth: 12, padding: 10 }
                },
                tooltip: {
                    titleFont: { size: t.tooltipTitle },
                    bodyFont: { size: t.tooltipBody }
                }
            }
        }
    });
}

function renderGameCards() {
    const container = document.getElementById('gameCards');
    if (!container) return;

    const cardsHTML = GAME_CONFIG.map(game => {
        const gameCodes = filterCodesByGame(allCodes, game.id);
        const counts = getStatusCounts(gameCodes);
        const isSelected = selectedGame === game.id;

        return `
            <article class="game-card ${isSelected ? 'selected' : ''}" data-game-card="${game.id}">
                <h3 class="game-card-title"><i class="fas ${game.icon}" aria-hidden="true"></i> ${escapeHTML(game.name)}</h3>
                <div class="game-card-stats">
                    <div><span>Active</span><strong>${counts.active}</strong></div>
                    <div><span>Unknown Exp.</span><strong>${counts['unknown-expiration']}</strong></div>
                    <div><span>Non-Expiring</span><strong>${counts['non-expiring']}</strong></div>
                    <div><span>Expired</span><strong>${counts.expired}</strong></div>
                </div>
                <button class="game-select-button" data-game-select="${game.id}" aria-label="Select ${escapeHTML(game.name)} for the detail grid">
                    Select Game
                </button>
            </article>
        `;
    }).join('');

    container.innerHTML = cardsHTML;
}

function getGridCodes() {
    const searchQuery = (document.getElementById('searchInput')?.value || '').trim();
    const selectedTypes = getSelectedTypeFilters();
    const selectedStatuses = getSelectedStatusFilters();
    let filtered = filterCodesBySearch(allCodes, searchQuery);
    filtered = filterCodesByType(filtered, selectedTypes);
    filtered = filterCodesByStatus(filtered, selectedStatuses);
    return filterCodesByGame(filtered, selectedGame);
}

function renderDetailGrid() {
    const container = document.getElementById('detailGridContainer');
    const selectedGameLabel = document.getElementById('selectedGameLabel');
    if (!container) return;

    if (selectedGameLabel) {
        selectedGameLabel.textContent = `Selected game: ${getGameNameById(selectedGame)}`;
    }

    const codes = getGridCodes();
    if (codes.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-inbox"></i>
                <p>No codes match the current filters for ${escapeHTML(getGameNameById(selectedGame))}.</p>
            </div>
        `;
        return;
    }

    const rows = codes.map(code => {
        const rawCode = code.CODE || '';
        const safeCode = escapeHTML(rawCode);
        const attrCode = escapeHTMLAttribute(rawCode);
        const safeReward = escapeHTML(code.REWARD || '');
        const statusInfo = getStatusDisplayInfo(getCodeStatus(code));
        const expirationDisplay = escapeHTML(getExpirationDisplay(code));
        const selectedGameBadge = getGameBadgeForGame(selectedGame);

        return `
            <tr>
                <td class="code-cell">
                    <div class="code-value-container">
                        <span class="code-value">${safeCode}</span>
                        <button type="button" class="copy-button" data-code="${attrCode}" aria-label="Copy code">
                            <i class="fas fa-copy"></i>
                        </button>
                    </div>
                </td>
                <td>${safeReward}</td>
                <td>
                    <span class="code-status ${statusInfo.class}">
                        <span class="status-icon"></span>${statusInfo.text}
                    </span>
                </td>
                <td>${expirationDisplay}</td>
                <td>
                    <div class="code-games">
                        ${selectedGameBadge ? `<span class="game-badge ${selectedGameBadge.class}">${selectedGameBadge.label}</span>` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join('');

    container.innerHTML = `
        <div class="table-scroll">
            <table class="codes-table">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Reward</th>
                        <th>Status</th>
                        <th>Expiration</th>
                        <th>Games</th>
                    </tr>
                </thead>
                <tbody>
                    ${rows}
                </tbody>
            </table>
        </div>
    `;
}

function updateDashboard() {
    renderGameCards();
    renderNewCodesTrendChart();
    renderStatusBreakdownChart();
    renderTypeBreakdownChart();
    renderDetailGrid();
}

/** Latest `ingested_at_utc` across the full dataset (not filtered by game). */
function getLatestTimestampFromDataset() {
    let maxMs = 0;
    allCodes.forEach(code => {
        const d = code.timestampDate;
        if (d instanceof Date && !Number.isNaN(d.getTime())) {
            const t = d.getTime();
            if (t > maxMs) maxMs = t;
        }
    });
    return maxMs > 0 ? new Date(maxMs) : null;
}

function updateHeroStats() {
    const stats = document.querySelectorAll('.stat-number-dashboard');
    if (stats.length < 3) return;

    const activeCodes = allCodes.filter(code => getCodeStatus(code) === 'active');
    stats[0].textContent = allCodes.length;
    stats[1].textContent = activeCodes.length;

    const latest = getLatestTimestampFromDataset();
    if (!latest) {
        stats[2].textContent = 'No ingest timestamps';
        return;
    }

    stats[2].textContent = formatDateTimeEastern(latest);
}

async function loadData() {
    try {
        destroyTrendChart();
        destroyPieCharts();
        const cards = document.getElementById('gameCards');
        const detail = document.getElementById('detailGridContainer');
        if (cards) {
            cards.innerHTML = `
                <div class="loading-spinner">
                    <i class="fas fa-spinner fa-spin"></i> Loading game summaries...
                </div>
            `;
        }
        if (detail) {
            detail.innerHTML = `
                <div class="loading-spinner">
                    <i class="fas fa-spinner fa-spin"></i> Loading codes...
                </div>
            `;
        }

        const rows = await fetchShiftCodesFromSupabase();
        allCodes = rows;
        allCodes.sort((a, b) => {
            const aTime = a.expirationDate instanceof Date ? a.expirationDate.getTime() : 0;
            const bTime = b.expirationDate instanceof Date ? b.expirationDate.getTime() : 0;
            return bTime - aTime;
        });

        updateDashboard();
        updateHeroStats();
        logger.log('Dashboard updated successfully');
    } catch (error) {
        logger.error('Failed to load data:', error);
        const cards = document.getElementById('gameCards');
        const detail = document.getElementById('detailGridContainer');
        const errorHTML = `
            <div class="error-state">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Failed to load codes</p>
                <p style="font-size: 0.9rem; margin-top: 0.5rem;">Please try again later</p>
            </div>
        `;
        if (cards) cards.innerHTML = errorHTML;
        if (detail) detail.innerHTML = errorHTML;
    }
}

function initDashboard() {
    const cy = document.getElementById('copyright-year');
    if (cy) {
        cy.textContent = `© ${new Date().getFullYear()}`;
    }

    setupTrendChartViewportListeners();

    const hamburger = document.querySelector('.hamburger');
    const navMenu = document.querySelector('.nav-menu');
    if (hamburger && navMenu) {
        hamburger.addEventListener('click', () => {
            const isExpanded = hamburger.getAttribute('aria-expanded') === 'true';
            hamburger.setAttribute('aria-expanded', !isExpanded);
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
        });
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', () => renderDetailGrid());
    }

    ['filterKey', 'filterCosmetic', 'filterGear'].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('change', () => renderDetailGrid());
        }
    });

    ['statusActive', 'statusUnknown', 'statusNonExpiring', 'statusExpired'].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('change', () => renderDetailGrid());
        }
    });

    const cardsContainer = document.getElementById('gameCards');
    if (cardsContainer) {
        cardsContainer.addEventListener('click', event => {
            const button = event.target.closest('button[data-game-select]');
            if (!button) return;
            const nextGame = button.getAttribute('data-game-select');
            if (!nextGame || nextGame === selectedGame) return;
            selectedGame = nextGame;
            updateDashboard();
        });
    }

    const refreshButton = document.getElementById('refreshButton');
    if (refreshButton) {
        refreshButton.addEventListener('click', async () => {
            refreshButton.disabled = true;
            const icon = refreshButton.querySelector('i');
            if (icon) icon.classList.add('fa-spin');
            await loadData();
            setTimeout(() => {
                refreshButton.disabled = false;
                if (icon) icon.classList.remove('fa-spin');
            }, 1000);
        });
    }

    const detailsSection = document.querySelector('.details-section');
    if (detailsSection) {
        detailsSection.addEventListener('click', event => {
            const button = event.target.closest('.copy-button');
            if (!button || !detailsSection.contains(button)) return;
            const code = button.getAttribute('data-code');
            if (code === null) return;
            void copyToClipboard(code, button);
        });
    }

    loadData();
}

document.addEventListener('DOMContentLoaded', initDashboard);

async function copyToClipboard(text, button) {
    try {
        await navigator.clipboard.writeText(text);
        const originalHTML = button.innerHTML;
        button.innerHTML = '<i class="fas fa-check"></i>';
        button.classList.add('copied');
        setTimeout(() => {
            button.innerHTML = originalHTML;
            button.classList.remove('copied');
        }, 2000);
    } catch (error) {
        logger.error('Failed to copy to clipboard:', error);
        const textArea = document.createElement('textarea');
        textArea.value = text;
        textArea.style.position = 'fixed';
        textArea.style.left = '-999999px';
        document.body.appendChild(textArea);
        textArea.select();
        try {
            document.execCommand('copy');
            const originalHTML = button.innerHTML;
            button.innerHTML = '<i class="fas fa-check"></i>';
            button.classList.add('copied');
            setTimeout(() => {
                button.innerHTML = originalHTML;
                button.classList.remove('copied');
            }, 2000);
        } catch (fallbackError) {
            logger.error('Fallback copy failed:', fallbackError);
        }
        document.body.removeChild(textArea);
    }
}
