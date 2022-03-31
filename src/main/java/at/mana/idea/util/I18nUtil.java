package at.mana.idea.util;

import java.util.ResourceBundle;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class I18nUtil {

    public static final String PLUGIN_ID = "plugin.id";
    public static final ResourceBundle LITERALS = ResourceBundle.getBundle("locale");

    public static final String MANA_CLI = "plugin.cli";

    public static String i18n( String key ) {
        return LITERALS.getString( key );
    }

}
