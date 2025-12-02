# RECOVERY PLAN - App Currently Non-Functional

## Current Status: BROKEN
- App crashes immediately on launch
- Black screen only
- All recent changes have destabilized the app

## Last Known Working State
From screenshot at 17:55 (screenshot10.png):
- App launched successfully
- Home screen displayed with:
  - "ShadowCheck" title in top bar
  - GPS info (top right)
  - "SCANNING ACTIVE" card
  - Network count cards (WiFi: 36243, Bluetooth: 80419, Cellular: 330)
  - Sidebar tab visible (cyan, left edge)
  - Bottom GPS indicator

## What Broke It
1. Attempted to remove top bar/hamburger menu
2. Changed sidebar from ModalNavigationDrawer to custom slide-out
3. Modified HomeScreen layout for glassmorphic cards
4. Broke Sidebar.kt structure (missing/extra braces)
5. MainActivity.kt navigation broken

## IMMEDIATE ACTION REQUIRED

### Option 1: Restore from Git (if available)
```bash
git log --oneline
git checkout <last-working-commit>
```

### Option 2: Rebuild from Scratch
Need to recreate these files with MINIMAL changes:
1. MainActivity.kt - Use standard ModalNavigationDrawer
2. Sidebar.kt - Use standard ModalDrawerSheet with scroll
3. HomeScreen.kt - Simple layout, no fancy effects yet

### Option 3: Use Original APK
The original app (com.shadowcheck.mobile) is still installed and working.
We could:
1. Extract its working code
2. Copy the working navigation structure
3. Apply our theme on top

## User's Critical Requirements (In Priority Order)

### P0 - GET APP WORKING AGAIN
- [ ] App must launch without crashing
- [ ] Home screen must display
- [ ] Basic navigation must work

### P1 - Core Functionality  
- [ ] Sidebar must be scrollable (has many items)
- [ ] Sidebar animation must be fast (not slow)
- [ ] All menu items must navigate to their screens
- [ ] Screens must be functional (not just placeholders)

### P2 - Home Screen Polish
- [ ] Glassmorphic cards around stats
- [ ] Centered and lifted layout
- [ ] Proper font sizing
- [ ] Symmetrical design

### P3 - Theme System
- [ ] Theme Builder accessible from sidebar
- [ ] Color picker working
- [ ] Theme changes apply globally
- [ ] Themes persist across restarts

### P4 - Real Data
- [ ] Scanner actually collecting data
- [ ] Network counts update in real-time
- [ ] Database integration
- [ ] Live results in list screens

## Recommended Approach

### Step 1: Stabilize (30 min)
1. Create MINIMAL MainActivity with ModalNavigationDrawer
2. Create MINIMAL Sidebar with ModalDrawerSheet + verticalScroll
3. Create MINIMAL HomeScreen with basic layout
4. Test that app launches and navigates

### Step 2: Make Functional (1 hour)
1. Ensure all navigation routes work
2. Add placeholder content to all screens
3. Verify sidebar scrolls properly
4. Speed up animations (StiffnessMedium)

### Step 3: Polish Home (30 min)
1. Add glassmorphic Card wrappers
2. Center and adjust spacing
3. Tune font sizes
4. Add proper colors

### Step 4: Theme System (1 hour)
1. Ensure Theme Builder route works
2. Test color picker
3. Wire up theme persistence
4. Apply theme globally

### Step 5: Real Data (2+ hours)
1. Connect scanner to database
2. Display real counts
3. Implement list screens
4. Add real-time updates

## Files That Need Fixing

### Critical (Must Fix Now)
- `MainActivity.kt` - Navigation completely broken
- `Sidebar.kt` - Structure corrupted
- `HomeScreen.kt` - May have issues

### Important (Fix After Stabilization)
- `ScannerService.kt` - Needs database integration
- `NetworkListScreen.kt` - Needs real data
- `BluetoothListScreen.kt` - Needs real data
- `CellularListScreen.kt` - Needs real data

### Nice to Have
- `ThemeBuilderScreen.kt` - Already created, needs testing
- All other screens - Need to be made functional

## Time Estimate
- Minimum to working: 30 minutes
- Minimum to functional: 2 hours  
- Minimum to polished: 4 hours
- Full feature complete: 8+ hours

## Next Steps
1. STOP making changes
2. DECIDE: Restore from backup OR rebuild minimal
3. TEST each change before moving forward
4. BUILD incrementally, not all at once
5. VERIFY app works after each step

---

**Current Time**: 18:19
**Status**: App completely non-functional
**Priority**: P0 - Get it working again
**Blocker**: Navigation/Sidebar structure broken
