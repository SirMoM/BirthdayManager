/**
 * 
 */
package application.util.localisation;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LangResourceManager{
	final private String LANG_BUNDLE_BASE_NAME = "lang";
	ResourceBundle langResourceBundle = null;

	/**
	 * 
	 */
	public LangResourceManager(Locale locale){
		try{
			this.langResourceBundle = PropertyResourceBundle.getBundle(this.LANG_BUNDLE_BASE_NAME, locale);
		} catch (MissingResourceException missingResourceException){
			missingResourceException.printStackTrace();
		}
		if(this.langResourceBundle == null){
			System.err.println("Could not get a langResourceBundle");
		}
	}

	public void changeLocale(Locale locale){
		try{
			this.langResourceBundle = PropertyResourceBundle.getBundle(this.LANG_BUNDLE_BASE_NAME, locale);
		} catch (MissingResourceException missingResourceException){
			missingResourceException.printStackTrace();
		}
		if(this.langResourceBundle == null){
			System.err.println("Could not get a langResourceBundle");
		}

	}

	public String getLocaleString(LangResourceKeys langResourceKey){
		return this.langResourceBundle.getString(langResourceKey.name());
	}

}
