import com.meterware.httpunit.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * This class demonstrates using httpunit to use the functionality of a web set from a command
 * line. To use it, specify a single word with one or more characters replaced by '?'. The
 * program will use the Merriam-Webster web site to find all words which match the pattern.
 *
 * Note: this program is not robust, but should work is used properly.
 **/
public class NearWords {


    public static void main( String[] params ) {
        try {
            if (params.length < 1) {
                System.out.println( "Usage: java NearWords [pattern]" );
                System.out.println( "where [pattern] may contain '?' to match any character" ); 
            }
            WordSeeker seeker = new WordSeeker();
            
            PrintStream err = new PrintStream( new FileOutputStream( "null.txt" ) );
            System.setErr( err );

            String[] words = seeker.getWordsMatching( params[0] );
            for (int i=0; i < words.length; i++) {
                System.out.println( (i+1) + ". " + words[i] ); 
            }
        } catch (Exception e) {
            System.err.println( "Exception: " + e );
        }
    }

}


class WordSeeker {

    public WordSeeker() {
        try {
            WebRequest request = new GetMethodWebRequest( "http://www.m-w.com/" );
            response = conversation.getResponse( request );
        } catch (Exception e) {
            throw new RuntimeException( "Error retrieving form: " + e );
        }
    }


    public String[] getWordsMatching( String pattern ) throws SAXException, IOException, java.net.MalformedURLException {
        WebForm lookupForm = getFormWithName( "dict" );
        WebRequest request = lookupForm.getRequest();
        request.setParameter( "va", pattern );
        request.setParameter( "book", "Dictionary" );
        response = conversation.getResponse( request );

        return getOptionsFromResponse();
    }


    private WebConversation conversation = new WebConversation();

    private WebResponse     response;

    private WebForm getFormWithName( String name ) throws SAXException {
        WebForm[] forms = response.getForms();
        for (int i=0; i < forms.length; i++) {
            Node formNode = forms[i].getDOMSubtree();
            NamedNodeMap nnm = formNode.getAttributes();
            Node nameNode = nnm.getNamedItem( "name" );
            if (nameNode == null) continue;
            if (nameNode.getNodeValue().equalsIgnoreCase( name )) {
                return forms[i];
            }
        }
        return null;
    }


    private String[] getOptionsFromResponse() throws SAXException {
        String[] words;
        WebForm[] forms = response.getForms();
        for (int i=0; i < forms.length; i++) {
            Element form = (Element) forms[i].getDOMSubtree();
            NodeList nl = form.getElementsByTagName( "option" );
            if (nl.getLength() == 0) continue;

            words = new String[ nl.getLength() ];
            for (int j = 0; j < nl.getLength(); j++) {
                words[j] = nl.item(j).getFirstChild().getNodeValue(); 
            }
            return words;
        }
        return new String[0];
    }

}

