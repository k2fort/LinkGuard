# LinkGuard

LinkGuard is an advanced Android security application designed to protect users from malicious links and phishing attacks in real-time. By leveraging Notification and Accessibility services, LinkGuard automatically intercepts and analyzes URLs from incoming messages and monitored applications, ensuring safe browsing using industry-standard threat intelligence APIs.

## 🚀 Features

- **Real-Time Notification Scanning**: Automatically extracts and scans URLs from incoming notifications using the Android `NotificationListenerService`.
- **In-App Link Scanning**: Monitors and analyzes links within user-selected apps via an `AccessibilityService`.
- **Threat Intelligence Integration**: Validates URLs against **Google Safe Browsing** and **VirusTotal** APIs.
- **Local Blocklist & Whitelist**: Fast, offline filtering using a local Room database, periodically updated via WorkManager.
- **Background Protection**: A specialized foreground `KeepAliveService` ensures continuous protection even when the app is closed.
- **Scan History Log**: Keeps a detailed record of scanned links and their threat assessments.
- **App Selector & Whitelisting**: Granular control over which apps to monitor and which domains to trust.
- **Onboarding Flow**: Intuitive setup process to guide users through granting necessary system permissions.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM with Coroutines
- **UI**: XML with ViewBinding, Material Components
- **Local Storage**: Room Database
- **Background Tasks**: WorkManager, Foreground Services
- **Networking**: Retrofit, OkHttp, Gson
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35

## ⚙️ Architecture & Core Components

- **`LinkGuardNotificationService`**: Listens to system notifications to extract and scan URLs in the background.
- **`LinkGuardAccessibilityService`**: Monitors user interaction within specific apps to detect and scan links on the screen.
- **`KeepAliveService`**: Ensures the notification and accessibility services remain active and are not killed by the system's battery optimization.
- **`BootReceiver`**: Automatically restarts protection services upon device reboot.

## 🔒 Permissions Required

To function effectively, LinkGuard requires several advanced Android permissions:
- **Notification Access** (`BIND_NOTIFICATION_LISTENER_SERVICE`)
- **Accessibility Service** (`BIND_ACCESSIBILITY_SERVICE`)
- **Display Over Other Apps** (`SYSTEM_ALERT_WINDOW`)
- **Ignore Battery Optimizations** (`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`)
- **Foreground Service** (`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`)

## 🛡️ Privacy & Security

LinkGuard processes URLs locally when possible using the Room database and only sends URL payloads to Google Safe Browsing and VirusTotal for threat analysis. No personal messages or non-URL notification contents are stored or transmitted.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

<div dir="rtl">

# LinkGuard

LinkGuard היא אפליקציית אבטחה מתקדמת לאנדרואיד, שנועדה להגן על משתמשים מפני קישורים זדוניים ומתקפות פישינג (דיוג) בזמן אמת. על ידי שימוש בשירותי התראות ונגישות, LinkGuard מיירטת ומנתחת באופן אוטומטי כתובות אתרים (URLs) מהודעות נכנסות ואפליקציות במעקב, ומבטיחה גלישה בטוחה באמצעות ממשקי API מובילים לזיהוי איומים.

## 🚀 תכונות

- **סריקת התראות בזמן אמת**: חולצת וסורקת באופן אוטומטי קישורים מהתראות נכנסות באמצעות `NotificationListenerService` של אנדרואיד.
- **סריקת קישורים בתוך אפליקציות**: מנטרת ומנתחת קישורים בתוך אפליקציות נבחרות על ידי המשתמש באמצעות `AccessibilityService`.
- **שילוב מודיעין איומים**: מאמתת כתובות אתרים מול ה-APIs של **Google Safe Browsing** ו-**VirusTotal**.
- **רשימת חסימה והיתרים מקומית**: סינון מהיר ולא מקוון באמצעות מסד נתונים מקומי מסוג Room, המתעדכן תקופתית דרך WorkManager.
- **הגנת רגע (Background)**: שירות חזיתי (Foreground Service) ייעודי מסוג `KeepAliveService` מבטיח הגנה רציפה גם כשהאפליקציה סגורה.
- **יומן היסטוריית סריקות**: שומר תיעוד מפורט של קישורים שנסרקו והערכות האיום שלהם.
- **בורר אפליקציות ורשימת היתרים**: שליטה פרטנית על אילו אפליקציות לנטר ולאילו דומיינים לסמוך.
- **תהליך קליטה (Onboarding)**: תהליך הגדרה אינטואיטיבי המנחה את המשתמשים במתן ההרשאות הדרושות למערכת.

## 🛠️ טכנולוגיות

- **שפה**: Kotlin
- **ארכיטקטורה**: MVVM בשילוב Coroutines
- **ממשק משתמש**: XML עם ViewBinding, Material Components
- **אחסון מקומי**: מסד נתונים Room
- **משימות רקע**: WorkManager, Foreground Services
- **תקשורת רשת**: Retrofit, OkHttp, Gson
- **גרסת SDK מינימלית**: 26 (Android 8.0 Oreo)
- **גרסת SDK יעד**: 35

## ⚙️ ארכיטקטורה ורכיבי ליבה

- **`LinkGuardNotificationService`**: מאזין להתראות מערכת כדי לחלץ ולסרוק כתובות אתרים ברקע.
- **`LinkGuardAccessibilityService`**: מנטר אינטראקציית משתמש בתוך אפליקציות ספציפיות כדי לזהות ולסרוק קישורים המופיעים על המסך.
- **`KeepAliveService`**: מוודא ששירותי ההתראות והנגישות נשארים פעילים ולא נסגרים על ידי מנגנוני ייעול הסוללה של המערכת.
- **`BootReceiver`**: מפעיל מחדש באופן אוטומטי את שירותי ההגנה לאחר הפעלה מחדש של המכשיר.

## 🔒 הרשאות דרושות

כדי לתפקד ביעילות, LinkGuard דורשת מספר הרשאות מתקדמות באנדרואיד:
- **גישה להתראות** (`BIND_NOTIFICATION_LISTENER_SERVICE`)
- **שירות נגישות** (`BIND_ACCESSIBILITY_SERVICE`)
- **הצגה על פני אפליקציות אחרות** (`SYSTEM_ALERT_WINDOW`)
- **התעלמות מאופטימיזציית סוללה** (`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`)
- **שירות חזית (Foreground)** (`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`)

## 🛡️ פרטיות ואבטחה

LinkGuard מעבדת קישורים באופן מקומי ככל האפשר באמצעות מסד הנתונים Room, ושולחת אך ורק את כתובת ה-URL עצמה ל-Google Safe Browsing ול-VirusTotal לשם ניתוח איומים. תוכן של הודעות אישיות או התראות שאינן מכילות קישורים אינו נשמר ואינו משודר.

## 📄 רישיון

פרויקט זה מופץ תחת רישיון MIT - ראו את קובץ ה-LICENSE לפרטים נוספים.

</div>

---

## Changelog

### `ebf9717` — fix: accessibility scanning, TLD heuristic, whitelist and threat fallback

- **AccessibilityService — periodic scan fallback**: Scans visible content every 3 seconds for 30 seconds after a monitored app opens. Fixes Google Messages (and other Jetpack NavComponent apps) where fragment navigation doesn't fire `TYPE_WINDOW_STATE_CHANGED`, causing chats to go unscanned.
- **AccessibilityService — null className handling**: NavComponent events with a null class name are no longer discarded; they fall back to `""` so the state machine still detects screen transitions.
- **AccessibilityService — per-URL dedup cache**: The accessibility service now uses its own independent `recentlyScannedByA11y` cache (3-minute window) instead of sharing the notification service's `ScanDeduplicator`. Sharing was silently blocking accessibility scans for any URL the notification service had already seen.
- **ScanOrchestrator — high-risk TLD heuristic**: Added `isHighRiskTld()` covering `.top`, `.tk`, `.ml`, `.ga`, `.cf`, `.gq`, and 20+ other heavily-abused TLDs. The check runs on the original URL first so phishing links that redirect through clean domains are still caught.
- **ScanOrchestrator — TLD safety net**: Secondary check in `scanInternal` using the pre-validated host, ensuring the heuristic fires even if `buildVerdict`'s internal host extraction fails.
- **UrlExtractor — FQDN trailing dot fix**: `extractHost()` now strips the trailing dot that Android ART's `java.net.URL.host` appends in FQDN form (e.g. `"ghostraper.top."` → `"ghostraper.top"`). Without this, `substringAfterLast('.')` returned `""`, silently bypassing both the TLD heuristic and whitelist lookups on device.
- **WhitelistPrefs — normalize trailing dot**: `normalize()` now strips the FQDN trailing dot before removing the `www.` prefix, fixing trusted entries (e.g. `youtube.com`) being ignored for hosts like `www.youtube.com.` returned by ART.
- **ThreatNotifier**: New utility that posts a high-priority system notification for `SUSPICIOUS`/`DANGEROUS` verdicts when overlay permission is not granted.
- **NotificationService — overlay fallback**: Wires `ThreatNotifier` as a fallback so threats are never silently dropped when overlay permission is missing.
- **AndroidManifest**: Set `allowBackup="false"` to prevent ADB backup extraction of scan history.
- **BlocklistUpdateWorker**: Switched to `ExistingPeriodicWorkPolicy.UPDATE` to avoid duplicate worker chains on app restart.
- **ScanOrchestratorTest**: 45 unit tests covering all verdict paths, TLD heuristic edge cases, `looksLikeRedirector`, and `isHighRiskTld`.

---

### `cab322e` — fix: resolve bugs found in QA audit + add ScanOrchestrator unit tests

- Fixed verdict assembly ordering so Google Safe Browsing always takes priority over VirusTotal and heuristics.
- Fixed shortened-URL handling: `isShortened` flag moved to informational-only (`infoReasons`) so safe bit.ly links no longer produce false SUSPICIOUS alerts.
- Added `looksLikeRedirector` heuristic: detects random-subdomain + redirect-path patterns (e.g. `2vz7dk6r84.tracker.com/go/link`) as suspicious.
- Fixed `resolvedUrl` display: shown in overlay only when a redirect actually occurred.
- Fixed `ScanDeduplicator` thread-safety: switched from `mutableMapOf` to `ConcurrentHashMap`.
- Added initial unit test suite for `ScanOrchestrator.buildVerdict` covering DANGEROUS, SUSPICIOUS, and SAFE paths.
