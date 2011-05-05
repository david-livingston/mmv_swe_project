import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 5/4/11
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringFormats {

    /**
     * @param d
     * @return
     */
    public static String strFromDouble(double d){
        DecimalFormat f = new DecimalFormat("0.0##########");
        return f.format(d);
    }

    public static String strFromByteCount(long size){
        return strFromInt((int)(size/(1024 * 1024))) + " MB";
    }

    public static String strFromRatio(final double dividend, final double divisor){
        DecimalFormat f = new DecimalFormat("###,##0.0");
        return "(" + f.format(100 * dividend/divisor) + " %)";
    }

    public static String strFromInt(final int i){
        DecimalFormat f = new DecimalFormat();
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator(',');
        f.setDecimalFormatSymbols(s);
        return f.format(i);
    }
}
