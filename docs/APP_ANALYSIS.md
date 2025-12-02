# ShadowCheck Mobile - Complete App Analysis

## App Overview
Network security & surveillance detection app for wardriving and threat monitoring.

## Design System
- **Theme:** Dark with cyan accent (#00BCD4)
- **Primary Color:** Cyan
- **Background:** Dark (#121212)
- **Text:** White primary, gray secondary
- **Icons:** Custom colored icons per feature

## Navigation Structure (Complete Sidebar)
1. **Home** - Dashboard with live counts
2. **WiFi** (36,243) - WiFi network list
3. **Bluetooth** (80,419) - Bluetooth device list
4. **Cellular** (330) - Cell tower list
5. **Map** - Mapbox map view with network markers
6. **Playback** - Historical playback (BROKEN)
7. **Network Finder** - Compass/AR to locate networks (BROKEN)
8. **Heatmap** - Density visualization (BROKEN)
9. **Threats** - Surveillance detection
10. **Geofence** - Location-based alerts
11. **Channels** - WiFi channel analysis (NEEDS SCREENSHOT)
12. **Stats** - Statistics dashboard (NEEDS SCREENSHOT)
13. **WiGLE** - WiGLE.net integration
14. **Settings** - App configuration

## Screen Details

### Home/Dashboard
- "SCANNING ACTIVE" status banner (green)
- "Collecting network data..." subtitle
- Live counts with icons: WiFi, Bluetooth, Cellular
- GPS accuracy + satellite count (top right)
- Hamburger menu (left)

### WiFi Networks List
- Header: "WiFi Networks (36234 distinct)"
- Collapsible Filters panel
- Each item shows:
  - SSID (cyan)
  - MAC address (gray)
  - Frequency + sighting count
  - Signal strength (color-coded: green/orange/red)
  - Upload indicator (red arrow)

### WiFi Filters Panel
- Search: "Search SSID/BSSID" text input
- Signal Strength: Range slider (-100 to -10 dBm)
- Security: Toggle buttons (Open, WEP, WPA, WPA2, WPA3)
- Frequency Band: Toggle buttons (2.4 GHz, 5 GHz, 6 GHz)
- WiFi Standard: Toggle buttons (WiFi 4, 5, 6, 6E, 7)
- Checkbox: "Only networks with GPS location"

### Network Details
- Header: Network name + MAC
- Action buttons: Play (red), Navigate (yellow), Map (white)
- **Network Summary:**
  - Total Sightings count
  - Average Signal strength
- **Notes & Tags:** Editable text field
- **Signal Strength Over Time:** Line graph showing signal history with min/max
- **Network Information:**
  - Frequency (MHz)
  - Channel (Width)
  - Security protocols
  - First Seen timestamp
  - Last Seen timestamp
- **Recent Sightings (scrollable list):**
  - Each sighting: timestamp, channel, frequency, signal, GPS coords, upload indicator

### Map Screen
- Top tabs: WiFi (36243) / Bluetooth (0)
- Filter bar with "Active" badge
- Mapbox map (light theme)
- Floating action buttons (right side):
  - List view toggle
  - Additional actions

### Surveillance Detection (Threats)
- Shield icon header
- **Threat Status:**
  - Large count (0)
  - Status text: "CLEAR - No Threats Detected"
  - Breakdown: Critical (0), High (0), Medium (0), Low (0)
- **Active Detection Systems (all with checkmarks):**
  1. IMSI Catcher / Stingray - Fake cell towers
  2. Evil Twin / Rogue AP - Fake WiFi networks
  3. Tracking Beacons - AirTags, Tiles, etc.
  4. WiFi Pineapple - MITM attack devices
  5. Hidden Cameras - WiFi surveillance
  6. GPS Jamming - Signal interference
  7. ML Anomaly Detection - Unusual patterns
- **Bottom status:** Green checkmark + "All Clear" + subtitle

### Geofence Alerts
- Add button (+) in header
- **Add Geofence Dialog:**
  - Location: Current GPS coordinates
  - Name: Text input
  - Radius (meters): Number input (default 100)
  - Action: Radio buttons (Alert, Pause Scan, Silent Log)
  - Buttons: Cancel, Add

### Network Finder (BROKEN)
- "Select a network to track" header
- Search box: "Search networks"
- Camera FAB button (bottom right) for AR mode
- Issue: Search not returning results

### WiGLE Dashboard
- User: Cyclonite01
- Rankings: Global #7321, Month #2933
- **Discoveries:**
  - WiFi (GPS): 20,894 | WiFi (Total): 46,722
  - Bluetooth (GPS): 107,407 | Bluetooth (Total): 111,219
  - Cellular (GPS): 154 | Cellular (Total): 433
- **Statistics:**
  - Total Observations: 328,877
  - This Month: 82
  - Last Month: 2,502
  - First Transaction: 20241007-01413
  - Latest Transaction: Unknown
  - Last Upload: 20251122-00344
- **Upload History (9649 total):**
  - Each entry: timestamp, status (D-100%), WiFi+GPS count, BT+GPS count, discovered count, byte size

## Features Present in Code
✓ WiFi/Bluetooth/Cellular list screens
✓ Network details with signal graph
✓ Filters panel (comprehensive)
✓ Map with Mapbox
✓ Surveillance detection (7 systems)
✓ Geofence alerts
✓ WiGLE API integration
✓ Database (13 entities, 11 DAOs)
✓ Export utils (CSV, KML, GeoJSON)
✓ Secure API key storage
✓ Deduplication logic
✓ Threat detection logic

## Features Broken/Missing
✗ Playback - Not loading
✗ Network Finder - Search not working, AR camera present
✗ Heatmap - Not loading
✗ Channels - Need to see implementation
✗ Stats - Need to see implementation
✗ Settings - Need to see implementation
✗ Sightings history table/display
✗ Recent sightings in network details
✗ Tab switcher (WiFi/Bluetooth) on map
✗ Active scanning service integration
✗ Live count updates

## Database Schema (13 Tables)
1. wifi_networks
2. bluetooth_devices
3. ble_devices
4. cellular_towers
5. sensor_readings
6. hardware_metadata
7. radio_manufacturers
8. geofences
9. network_notes (with tags field)
10. device_tags
11. api_tokens
12. api_usage
13. media_attachments

## Technical Stack
- Kotlin + Jetpack Compose
- Room Database
- Retrofit + OkHttp
- Mapbox SDK
- Google Maps SDK
- Material 3
- Coroutines + Flow
- AndroidX Security
- Coil (image loading)

## Statistics Screen - Complete Intelligence Data

### Overview
- Total Sightings: 268,869 WiFi, 146,338 BT, 18,897 Cell
- Distinct Networks: 36,243 WiFi, 80,419 BT, 331 Cell
- GPS Coverage: 100% with valid coordinates

### WiFi Bands (Distinct Networks)
- 2.4 GHz: 19,082 networks
- 5 GHz: 17,013 networks
- 6 GHz (WiFi 6E): 133 networks
- Total: 36,228 / 36,243

### WiFi Standards
- 802.11ax (WiFi 6): 267
- 802.11ac (WiFi 5): 173
- 802.11n (WiFi 4): 191
- Avg Signal: -80 dBm

### Security Analysis (CRITICAL SIGINT)
- WPA3 (SAE/OWE): 4,627 (secure)
- WPA2: 27,691 (secure)
- WPA: 60 (weak - orange flag)
- WEP: 125 (vulnerable - red flag)
- Open: 3,740 (no security - red flag)
- **Total Vulnerable: 3,925 networks (10.8%)**

### Bluetooth Analysis
- Dual Mode: 415
- BLE Only: 128,591 (tracking beacons, IoT, wearables)
- Classic Only: 17,332
- Total: 146,338 sightings / 80,419 distinct
- Avg Signal: -62 dBm

### Cellular Network Analysis
- 5G NR: 8,128 towers
- LTE (4G): 9,047 towers
- UMTS/HSPA (3G): 0 (networks shut down)
- GSM/EDGE (2G): 267 towers (potential IMSI catcher targets)
- CDMA: 0
- Other: 1,455
- Avg Signal: -89 dBm

### Motion & Environment (Operational Intelligence)
- Stationary: 6,633 readings (warsitting/warstanding)
- Walking: 768 readings (warwalking)
- Running: 84 readings (warrunning)
- Driving: 10 readings (wardriving)
- Indoor: 4,905 readings
- Outdoor: 452 readings
- Total Readings: 7,495

### Advanced WiFi Features
- Dual-Band APs: 3,995
- Passpoint/Hotspot 2.0: 104 (carrier WiFi)
- WiFi 6E (6 GHz): 1,062 networks

### Top Networks
- Strongest Signal: 127 dBm (extremely strong)
- Most Frequently Seen: BLRS_EXT (2,097 sightings)

### Temporal & Coverage
- Scan Duration: 6,127 hours (255+ days continuous)
- Avg Sightings/Min: 0.7
- **This is a long-term counter-surveillance operation**

## Channel Analysis Details
### 2.4 GHz Band
- Channels 1, 6, 11 are non-overlapping (recommended)
- Channel 1: 6,841 networks (HIGH congestion - red)
- Channel 6: 6,841 networks (HIGH congestion - red)
- Channel 11: 4,104 networks (MEDIUM congestion - orange)
- Other channels: Low congestion (green)

### 5 GHz Band
- More channels available, less congestion
- Multiple channels with varying congestion levels
- Most channels: Low (green)

### 6 GHz Band (WiFi 6E)
- Latest standard with maximum bandwidth
- Nearly empty (very few networks)
- Channels 1-226 available
- Ideal for interference-free operation

### Summary
- 2.4 GHz: 19,155 networks
- 5 GHz: 17,244 networks
- 6 GHz: 9 networks

## Key Intelligence Findings

### Security Posture
- 10.8% of networks are vulnerable (WEP/Open)
- 3,740 open networks (no encryption)
- 125 WEP networks (easily crackable)
- 32,318 networks use modern security (WPA2/WPA3)

### Threat Landscape
- 267 legacy 2G towers (IMSI catcher risk)
- 128,591 BLE devices (tracking beacon risk)
- 0 detected threats currently
- All 7 detection systems active

### Technology Adoption
- WiFi 6E still rare (133 networks)
- 5G deployment strong (8,128 towers)
- 3G completely phased out (0 towers)
- Dual-band APs common (3,995)

### Operational Profile
- 255+ days of continuous monitoring
- Primarily stationary/indoor scanning (home base)
- Tracking specific network (BLRS_EXT) with 2,097 sightings
- Comprehensive coverage across all motion types
- 100% GPS coverage (all data geotagged)

## Next Steps
1. ✅ Create complete MainActivity with all navigation
2. ✅ Create HomeScreen with dashboard
3. ✅ Document complete statistics and intelligence
4. Fix broken screens (Playback, Network Finder, Heatmap)
5. Implement WiGLE dashboard with user stats
6. Add sightings history table and display
7. Wire up scanning service with live updates
8. Implement threat detection algorithms
9. Add motion detection integration
10. Build comprehensive statistics calculations
