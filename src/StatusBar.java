import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 19, 2011
 * Time: 1:26:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusBar extends JLabel {

    public StatusBar() {
        super();
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setMessage("Ready");
    }

    public void setMessage(String message) {
        setText(" " + message);
    }
}
