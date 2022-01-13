package at.mana.idea.util;

import com.intellij.ui.JBColor;

import java.awt.*;

public class ColorUtil {

    public static final JBColor NOTIFICATION_COLOR = new JBColor(
            new Color(237, 180, 180),
            new Color(237, 180, 180));

    public static final JBColor LINE_MARKER_DATA_AVAILABLE = new JBColor(
            JBColor.decode("0xD8F0E8"),
            JBColor.decode("0x073c0a"));

    public static final JBColor INLINE_TEXT = new JBColor( JBColor.decode("0x999999"), JBColor.decode("0x999999"));

    public static final JBColor INLINE_TEXT_INCREASE = new JBColor(new Color(255, 143, 143), new Color(255, 143, 143));
    public static final JBColor INLINE_TEXT_DECREASE = new JBColor(new Color(154,197,166), new Color(154,197,166));


    public static final Color[] HEAT_MAP_COLORS_DEFAULT = new Color[] {
            new JBColor(new Color(138,153,212), new Color(138,153,212)),
            new JBColor(new Color(120,184,174), new Color(120,184,174)),
            new JBColor(new Color(154,197,166), new Color(154,197,166)),
            new JBColor(new Color(255,173,138), new Color(255,173,138)),
            new JBColor(new Color(255, 143, 143), new Color(255, 143, 143)),
            new JBColor(new Color(255, 143, 143), new Color(255, 143, 143))
    };

    public static final Color[] HEATMAP_COLORS_YLGNBU = new Color[] {
            new JBColor(new Color(255,255,217), new Color(255,255,217)),
            new JBColor(new Color(237,248,177), new Color(237,248,177)),
            new JBColor(new Color(199,233,180), new Color(199,233,180)),
            new JBColor(new Color(127,205,187), new Color(127,205,187)),
            new JBColor(new Color(65, 182, 196), new Color(65, 182, 196)),
            new JBColor(new Color(29, 145, 192), new Color(29, 145, 192)),
            new JBColor(new Color(34, 94, 168), new Color(34, 94, 168)),
            new JBColor(new Color(37, 52, 148), new Color(37, 52, 148)),
    };


}
