import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.apache.commons.lang3.StringEscapeUtils;

public class translatesp {

        public static String translate(String title) {
            Translate translate = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyCIeRXutC0n9hRa_PGg64XTLA4HGIL4MXU")
                    .build()
                    .getService();

            Translation translation = translate.translate(title,
                    Translate.TranslateOption.sourceLanguage("es"),
                    Translate.TranslateOption.targetLanguage("en"));

            // System.out.println("Translated text: " + translation.getTranslatedText());
            return StringEscapeUtils.unescapeHtml4(translation.getTranslatedText());
        }





}
