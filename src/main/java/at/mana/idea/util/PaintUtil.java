package at.mana.idea.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class PaintUtil {

    public static Rectangle getStringBounds(Graphics2D g2, String str, float x, float y)
    {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, x, y);
    }
}
