/**
 *
 */
package application.util.localisation;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LangResourceManager{
	final static Logger LOG = LogManager.getLogger();
	final private String LANG_BUNDLE_BASE_NAME = "lang";
	ResourceBundle langResourceBundle = null;

	/**
	 *
	 */
	public LangResourceManager(final Locale locale){
		try{
			this.langResourceBundle = PropertyResourceBundle.getBundle(this.LANG_BUNDLE_BASE_NAME, locale);
		} catch (final MissingResourceException missingResourceException){
			LOG.catching(missingResourceException);
		}
		if(this.langResourceBundle == null){
			LOG.error("Could not get a langResourceBundle");
		}
	}

	public void changeLocale(final Locale locale){
		try{
			this.langResourceBundle = PropertyResourceBundle.getBundle(this.LANG_BUNDLE_BASE_NAME, locale);
		} catch (final MissingResourceException missingResourceException){
			LOG.catching(missingResourceException);
		}
		if(this.langResourceBundle == null){
			LOG.error("Could not get a langResourceBundle");
		}

	}

	public String getLocaleString(final LangResourceKeys langResourceKey){
		return this.langResourceBundle.getString(langResourceKey.name());
	}

}
