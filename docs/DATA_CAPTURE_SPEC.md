# ShadowCheck Data Capture Specification

## WiFi Networks - 33 Fields Captured

### Identity
1. **BSSID** - MAC address of access point
2. **SSID** - Network name
3. **vendorOui** - First 3 bytes of MAC (manufacturer ID)
4. **vendorName** - Manufacturer name from OUI lookup

### Signal & Frequency
5. **signalLevel** - Signal strength in dBm
6. **frequency** - Frequency in MHz (2.4GHz or 5GHz)
7. **channel** - WiFi channel number
8. **channelWidth** - 20/40/80/160 MHz
9. **centerFreq0** - Center frequency 0
10. **centerFreq1** - Center frequency 1 (for 80+80)

### Capabilities & Standards
11. **capabilities** - Security/encryption (WPA2, WPA3, etc.)
12. **standard** - 802.11 standard (a/b/g/n/ac/ax)
13. **maxDataRate** - Maximum data rate in Mbps
14. **is80211mc** - Fine Timing Measurement support
15. **isPasspoint** - Hotspot 2.0 support

### Venue Information
16. **operatorFriendlyName** - Operator name for Passpoint
17. **venueName** - Venue name for Passpoint
18. **rcoi** - Roaming Consortium OI

### Location Data
19. **latitude** - GPS latitude
20. **longitude** - GPS longitude
21. **altitude** - GPS altitude (meters)
22. **altitudeBarometric** - Barometric altitude
23. **accuracy** - GPS accuracy (meters)
24. **speed** - Device speed (m/s)
25. **bearing** - Device bearing (degrees)

### Temporal Data
26. **timestamp** - Current observation time
27. **firstSeen** - First time this BSSID was seen
28. **lastSeen** - Last time this BSSID was seen

### Metadata
29. **source** - Data source (scanner/import/wigle)

## Bluetooth/BLE Devices - 17 Fields Captured

### Identity
1. **address** - MAC address
2. **name** - Device name (if available)
3. **deviceClass** - Bluetooth device class
4. **bondState** - Pairing state
5. **deviceType** - Classic/BLE/Dual

### Signal & Power
6. **rssi** - Received Signal Strength Indicator
7. **txPower** - Transmit power level

### BLE Specific
8. **isConnectable** - Can be connected to
9. **serviceUuids** - Advertised service UUIDs
10. **manufacturerData** - Manufacturer-specific data

### Location Data
11. **latitude** - GPS latitude
12. **longitude** - GPS longitude
13. **altitude** - GPS altitude
14. **accuracy** - GPS accuracy

### Temporal Data
15. **timestamp** - Current observation time
16. **firstSeen** - First detection time
17. **lastSeen** - Last detection time

## Cellular Towers - 18 Fields Captured

### Identity
1. **cellId** - Cell ID (CID)
2. **lac** - Location Area Code (GSM) or TAC (LTE)
3. **mcc** - Mobile Country Code
4. **mnc** - Mobile Network Code
5. **psc** - Primary Scrambling Code (UMTS) or PCI (LTE)

### Signal Quality
6. **signalStrength** - Signal strength level (0-4)
7. **signalQuality** - Signal quality indicator

### Network Info
8. **networkType** - GSM/UMTS/LTE/NR
9. **operatorName** - Network operator name

### Location Data
10. **latitude** - GPS latitude
11. **longitude** - GPS longitude
12. **altitude** - GPS altitude
13. **accuracy** - GPS accuracy

### Temporal Data
14. **timestamp** - Current observation time
15. **firstSeen** - First detection time
16. **lastSeen** - Last detection time

### Metadata
17. **source** - Data source

## GPS Validation Rules

- **Minimum Accuracy**: < 100 meters
- **Valid Coordinates**: latitude != 0.0 AND longitude != 0.0
- **Records Rejected**: Any scan without valid GPS is NOT saved

## Deduplication Rules

### 30-Second Window
- **Same BSSID/Address/CellID**: Only ONE observation per 30 seconds
- **Implementation**: Check lastSeen timestamp before inserting
- **Max Rate**: 2 observations per minute per unique beacon
- **Purpose**: Reduce database bloat while maintaining temporal accuracy

### Scan Rate Adjustment
- **New Networks**: Scan every 3 seconds
- **Known Networks**: Scan every 30 seconds
- **Moving (speed > 5 m/s)**: Scan every 3 seconds (driving)
- **Stationary (speed < 1 m/s)**: Scan every 30 seconds (walking/stopped)

## Total Fields Per Scan
- **WiFi**: 33 fields × N networks
- **Bluetooth**: 17 fields × N devices  
- **Cellular**: 18 fields × N towers
- **Total**: 68 unique data points per beacon type
