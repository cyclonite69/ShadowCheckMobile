# ShadowCheck Mobile - Implementation Roadmap

## Project Status: SIGINT Counter-Surveillance Platform
**Not a wardriving app - this is a signals intelligence and counter-surveillance detection platform**

## Current State
- ✅ 57 Kotlin files (5,500+ lines clean code)
- ✅ 13 Room entities, 11 DAOs
- ✅ 29 UI screens (rewritten from decompiled code)
- ✅ Navigation structure (14 routes)
- ✅ Threat detection logic (7 systems)
- ✅ Database schema complete
- ✅ 255+ days of operational data
- ✅ 328,877 observations tracked

## Phase 1: Core Navigation & Structure ✅
- [x] MainActivity with complete navigation
- [x] HomeScreen dashboard
- [x] Sidebar with all 14 menu items
- [x] Theme system (dark + cyan accent)
- [x] Navigation routes wired up

## Phase 2: Fix Broken Screens 🔧
### Playback Screen
- [ ] Historical data playback UI
- [ ] Timeline scrubber
- [ ] Speed controls (1x, 2x, 5x, 10x)
- [ ] Date/time range selector
- [ ] Replay network discoveries

### Network Finder Screen
- [ ] Fix search query (not returning results)
- [ ] Network selection from search
- [ ] Compass pointing to selected network
- [ ] Distance and bearing calculation
- [ ] AR camera overlay integration
- [ ] Real-time signal strength indicator

### Heatmap Screen
- [ ] Fix heatmap generation
- [ ] Google Maps integration
- [ ] Gradient overlay (WiFi/Bluetooth/Cellular)
- [ ] Intensity calculations
- [ ] Filter by network type
- [ ] Export heatmap as image

## Phase 3: Complete Statistics Implementation 📊
### StatsScreen Enhancements
- [ ] Real-time data calculations
- [ ] Overview section with live counts
- [ ] WiFi bands breakdown
- [ ] WiFi standards distribution
- [ ] Security analysis with color coding
- [ ] Bluetooth breakdown (Dual/BLE/Classic)
- [ ] Cellular network types
- [ ] Motion & environment tracking
- [ ] Advanced WiFi features
- [ ] Top networks section
- [ ] Temporal & coverage metrics
- [ ] Scan duration display
- [ ] Avg sightings/min calculation

## Phase 4: WiGLE Integration 🌐
### WiGLE Dashboard
- [ ] User profile display (Cyclonite01)
- [ ] Global rank (#7321)
- [ ] Month rank (#2933)
- [ ] Discoveries section:
  - WiFi (GPS) vs WiFi (Total)
  - Bluetooth (GPS) vs Bluetooth (Total)
  - Cellular (GPS) vs Cellular (Total)
- [ ] Statistics section:
  - Total observations
  - This month / Last month
  - First transaction
  - Latest transaction
  - Last upload timestamp
- [ ] Upload history (9,649 entries)
  - Transaction ID
  - Status (D-100%)
  - WiFi+GPS count
  - BT+GPS count
  - Discovered count
  - Byte size
- [ ] Upload button
- [ ] Load upload history button

## Phase 5: Network Details Enhancements 📡
### Sightings History
- [ ] Create sightings table in database
- [ ] Recent sightings list (scrollable)
- [ ] Each sighting shows:
  - Timestamp
  - Channel + Frequency
  - Signal strength (color-coded)
  - GPS coordinates
  - Upload indicator
- [ ] Signal strength over time graph
- [ ] Min/max signal with timestamps
- [ ] Notes & tags editing
- [ ] Action buttons (Play, Navigate, Map)

## Phase 6: Live Scanning Service 🔄
### ScannerService Implementation
- [ ] Foreground service with notification
- [ ] WiFi scanning loop
- [ ] Bluetooth scanning loop
- [ ] Cellular tower detection
- [ ] GPS location tracking
- [ ] Motion detection (stationary/walking/running/driving)
- [ ] Environment detection (indoor/outdoor)
- [ ] Database insertion
- [ ] Live count updates via Flow
- [ ] Broadcast updates to UI
- [ ] "Stop Scanning" button functionality

### HomeScreen Live Updates
- [ ] Subscribe to scanning service
- [ ] Real-time count updates
- [ ] GPS accuracy display (top right)
- [ ] Satellite count display
- [ ] Scanning status banner
- [ ] Animation for active scanning

## Phase 7: Threat Detection Algorithms 🛡️
### IMSI Catcher Detection
- [ ] Detect fake cell towers
- [ ] Monitor LAC/CID changes
- [ ] Track signal strength anomalies
- [ ] Alert on suspicious towers

### Evil Twin Detection
- [ ] Detect duplicate SSIDs
- [ ] Compare MAC addresses
- [ ] Monitor encryption changes
- [ ] Alert on rogue APs

### Tracking Beacon Detection
- [ ] Identify AirTags, Tiles
- [ ] Monitor BLE advertisement patterns
- [ ] Detect persistent followers
- [ ] Alert on tracking devices

### WiFi Pineapple Detection
- [ ] Detect MITM devices
- [ ] Monitor for deauth attacks
- [ ] Identify suspicious SSIDs
- [ ] Alert on attack devices

### Hidden Camera Detection
- [ ] Scan for WiFi cameras
- [ ] Identify common camera SSIDs
- [ ] Monitor for streaming patterns
- [ ] Alert on surveillance devices

### GPS Jamming Detection
- [ ] Monitor GPS signal quality
- [ ] Detect signal interference
- [ ] Track satellite count drops
- [ ] Alert on jamming attempts

### ML Anomaly Detection
- [ ] Train on normal patterns
- [ ] Detect unusual network behavior
- [ ] Identify statistical outliers
- [ ] Alert on anomalies

## Phase 8: Map Enhancements 🗺️
### Map Features
- [ ] Tab switcher (WiFi/Bluetooth)
- [ ] Filter "Active" badge
- [ ] Floating action buttons:
  - List view toggle
  - Layer selector
  - Current location
- [ ] Network markers with clustering
- [ ] Marker colors by signal strength
- [ ] Info window on marker tap
- [ ] Navigate to network details

## Phase 9: Settings & Configuration ⚙️
### Settings Screen
- [ ] Scanning preferences
- [ ] Notification settings
- [ ] GPS accuracy threshold
- [ ] Database management
- [ ] Export options
- [ ] API key management
- [ ] Theme selection
- [ ] About section

## Phase 10: Polish & Optimization ✨
### Performance
- [ ] Database query optimization
- [ ] Lazy loading for large lists
- [ ] Image caching
- [ ] Background task optimization
- [ ] Memory leak fixes

### UI/UX
- [ ] Loading states
- [ ] Error handling
- [ ] Empty states
- [ ] Pull-to-refresh
- [ ] Swipe actions
- [ ] Haptic feedback
- [ ] Animations

### Testing
- [ ] Unit tests for detection algorithms
- [ ] Integration tests for database
- [ ] UI tests for critical flows
- [ ] Performance testing
- [ ] Battery usage optimization

## Priority Order
1. **CRITICAL:** Fix Network Finder search (broken)
2. **CRITICAL:** Fix Heatmap generation (broken)
3. **HIGH:** Implement live scanning service
4. **HIGH:** Complete Statistics screen
5. **HIGH:** WiGLE dashboard
6. **MEDIUM:** Sightings history
7. **MEDIUM:** Playback screen
8. **MEDIUM:** Map enhancements
9. **LOW:** Settings screen
10. **LOW:** Polish & optimization

## Success Metrics
- All 14 screens functional
- Live scanning with real-time updates
- All 7 threat detection systems operational
- 100% GPS coverage maintained
- Sub-second UI response times
- Zero data loss
- Professional-grade SIGINT platform
