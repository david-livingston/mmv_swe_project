import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 19, 2011
 * Time: 1:26:29 PM
 * To change this template use File | Settings | File Templates.
 *
 * TODO: make the rest of the program actually use the statusbar.
 */
class StatusBar extends JLabel {

    final private static String initialMessage = "Ready.";
    private String currentMessage;

    /**
     * Create a JLabel representing the statusbar and set an initial message.
     */
    public StatusBar() {
        super();
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setMessage(initialMessage);
    }

    /**
     * Sets the text displayed in the status bar; offset slightly for
     * readability.
     *
     * @param message text to be displayed
     */
    void setMessage(final String message) {
        setText(currentMessage = " " + message);
    }

    /**
     * @return text currently being displayed in status bar
     */
    public String getCurrentMessage() { return currentMessage; }
}
