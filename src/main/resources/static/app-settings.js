(function () {
    const SETTINGS_KEY = "smartCampusSettings";
    const defaults = {
        theme: "dark",
        fontSize: "medium",
        fontStyle: "dmSans"
    };

    const themeTokens = {
        dark: {
            "--navy": "#0b1120",
            "--navy2": "#111827",
            "--navy3": "#1a2740",
            "--gold": "#c9a84c",
            "--gold2": "#f0c96d",
            "--cream": "#f5f0e8",
            "--muted": "#8899aa",
            "--white": "#ffffff",
            "--border": "rgba(201,168,76,.18)",
            "--glow": "rgba(201,168,76,.12)"
        },
        light: {
            "--navy": "#f4f7fc",
            "--navy2": "#ffffff",
            "--navy3": "#e9eef7",
            "--gold": "#9b6b16",
            "--gold2": "#b8861f",
            "--cream": "#1f2937",
            "--muted": "#4b5563",
            "--white": "#111827",
            "--border": "rgba(17,24,39,.15)",
            "--glow": "rgba(17,24,39,.08)"
        }
    };

    const fontSizeMap = {
        small: "14px",
        medium: "16px",
        large: "18px"
    };

    const fontStyleMap = {
        dmSans: "\"DM Sans\", \"Segoe UI\", Arial, sans-serif",
        inter: "\"Inter\", \"Segoe UI\", Arial, sans-serif",
        serif: "\"Georgia\", \"Times New Roman\", serif",
        system: "system-ui, -apple-system, \"Segoe UI\", sans-serif"
    };

    function loadSettings() {
        try {
            const raw = localStorage.getItem(SETTINGS_KEY);
            if (!raw) return { ...defaults };
            return { ...defaults, ...JSON.parse(raw) };
        } catch (_) {
            return { ...defaults };
        }
    }

    function saveSettings(nextSettings) {
        const normalized = { ...defaults, ...nextSettings };
        localStorage.setItem(SETTINGS_KEY, JSON.stringify(normalized));
        applySettings(normalized);
        return normalized;
    }

    function applySettings(settings) {
        const root = document.documentElement;
        const safeSettings = { ...defaults, ...settings };
        const theme = themeTokens[safeSettings.theme] ? safeSettings.theme : defaults.theme;
        const themeVars = themeTokens[theme];

        Object.keys(themeVars).forEach((token) => {
            root.style.setProperty(token, themeVars[token]);
        });

        root.style.setProperty("--ff-body", fontStyleMap[safeSettings.fontStyle] || fontStyleMap[defaults.fontStyle]);
        root.style.setProperty("--app-font-size", fontSizeMap[safeSettings.fontSize] || fontSizeMap[defaults.fontSize]);
        root.setAttribute("data-theme", theme);
    }

    const initialSettings = loadSettings();
    applySettings(initialSettings);

    window.SmartCampusSettings = {
        defaults,
        load: loadSettings,
        save: saveSettings,
        apply: applySettings,
        key: SETTINGS_KEY
    };
})();
