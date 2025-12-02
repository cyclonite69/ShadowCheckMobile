# ShadowCheck Theme Builder Guide

## Overview
The Theme Builder is a visual UI customization tool that allows you to modify the app's appearance in real-time.

## Features

### 1. **Color Customization**
- **Background** - Main app background color
- **Surface** - Card and panel backgrounds
- **Primary** - Main accent color (buttons, icons)
- **Secondary** - Secondary accent color
- **Accent** - Highlight color (success states)
- **Error** - Error and warning states
- **Text Primary** - Main text color
- **Text Secondary** - Secondary/dimmed text

### 2. **Color Picker**
- **HSV Sliders**:
  - Hue (0-360°) - Color selection
  - Saturation (0-100%) - Color intensity
  - Brightness (0-100%) - Light/dark
  - Opacity (0-100%) - Transparency
- **Live Preview** - See changes immediately
- **Hex Display** - Shows color code

### 3. **Typography**
- **Title Size** (10-40sp) - Headers and titles
- **Body Size** (10-40sp) - Main content text
- **Caption Size** (10-40sp) - Small labels

## How to Use

### Accessing Theme Builder
1. Open the sidebar (tap the cyan tab on the left)
2. Scroll down and tap "Theme Builder"

### Customizing Colors
1. Tap any color property in the left panel
2. Use the sliders in the right panel to adjust:
   - Move Hue slider to change color
   - Adjust Saturation for intensity
   - Modify Brightness for light/dark
   - Change Opacity for transparency
3. Watch the preview box update in real-time

### Adjusting Typography
1. Scroll down in the left panel to "Typography"
2. Use sliders to adjust font sizes
3. Changes apply to all text of that type

### Saving Your Theme
1. Tap the Save icon (💾) in the top right
2. Theme will be applied immediately
3. Settings are persisted across app restarts

## Current Implementation

### Fixed Issues
✅ Removed hamburger menu from home screen
✅ Added slide-out sidebar with cyan tab
✅ Fixed layout to match original app
✅ Proper color theming throughout
✅ Created visual theme builder

### Sidebar Features
- **Slide Animation** - Smooth 280dp slide from left
- **Cyan Tab** - Always visible, 40x120dp
- **Dim Overlay** - Semi-transparent background when open
- **Touch to Close** - Tap outside to dismiss

## Technical Details

### Theme Configuration
```kotlin
data class ThemeConfig(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val titleSize: Int,
    val bodySize: Int,
    val captionSize: Int
)
```

### Color Conversion
- Uses HSV (Hue, Saturation, Value) color space
- Converts to/from Android Color objects
- Displays as hex codes for easy copying

## Next Steps

### Planned Enhancements
1. **Preset Themes** - Light, Dark, High Contrast, etc.
2. **Import/Export** - Share themes as JSON files
3. **Color Palettes** - Pre-defined color schemes
4. **Font Selection** - Choose from system fonts
5. **Preview Mode** - See theme on different screens
6. **Reset to Default** - Restore original colors

### Persistence
Currently themes are saved in memory. Next update will add:
- SharedPreferences storage
- Theme profiles (multiple saved themes)
- Quick theme switching

## Color Psychology Tips

### Professional Themes
- **Dark Blue** backgrounds reduce eye strain
- **Cyan/Teal** accents are modern and tech-focused
- **High contrast** improves readability

### Accessibility
- Maintain 4.5:1 contrast ratio for text
- Use distinct colors for different states
- Test with colorblind simulators

## Troubleshooting

### Colors Not Saving
- Ensure you tap the Save icon
- Check app permissions
- Restart app if needed

### Sidebar Not Opening
- Tap the cyan tab on the left edge
- Swipe from left edge
- Check for conflicting gestures

### Performance Issues
- Reduce transparency (increase opacity)
- Use solid colors instead of gradients
- Restart app to clear cache

---

**Version**: 1.0  
**Last Updated**: 2025-12-01  
**Status**: Beta - Fully functional, enhancements planned
