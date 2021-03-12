/** */
package application.util.localisation;

import application.util.PropertyFields;
import application.util.PropertyManager;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LangResourceManager {
  static final Logger LOG = LogManager.getLogger();
  private static final String LANG_BUNDLE_BASE_NAME = "lang";
  ResourceBundle langResourceBundle = null;

  /** The Basic Constructor */
  public LangResourceManager() {
    final Locale locale = new Locale(PropertyManager.getProperty(PropertyFields.SAVED_LOCALE));
    try {
      this.langResourceBundle = ResourceBundle.getBundle(LANG_BUNDLE_BASE_NAME, locale);
    } catch (final MissingResourceException missingResourceException) {
      LOG.catching(missingResourceException);
    }
    if (this.langResourceBundle == null) {
      LOG.error("Could not get a langResourceBundle");
    }
  }

  /** @param locale the {@link Locale} which determinate the application language */
  public void changeLocale(final Locale locale) {
    try {
      this.langResourceBundle = ResourceBundle.getBundle(LANG_BUNDLE_BASE_NAME, locale);
    } catch (final MissingResourceException missingResourceException) {
      LOG.catching(missingResourceException);
    }
    if (this.langResourceBundle == null) {
      LOG.error("Could not get a langResourceBundle");
    }
  }

  /**
   * @param LangResourceKeys A key from {@link LangResourceKeys}
   * @return the component string in the correct language
   */
  public String getLocaleString(final LangResourceKeys LangResourceKeys) {
    return this.langResourceBundle.getString(LangResourceKeys.name());
  }
}
