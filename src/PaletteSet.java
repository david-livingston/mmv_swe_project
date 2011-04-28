import java.awt.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/27/11
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaletteSet {

    final HashMap<String, Palette> palettes = new HashMap<String, Palette>();
    final String DEFAULT_NAME = "Foilage";

    public PaletteSet(){
        palettes.put(DEFAULT_NAME,
            new Palette(DEFAULT_NAME) {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(counter * counter, counter * counter + counter, counter + counter);
                }
            }
        );
        palettes.put("Sorority",
            new Palette("Sorority") {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(counter * counter + counter, counter + counter, counter * counter);
                }
            }
        );
        palettes.put("Black & White",
            new Palette("Black & White") {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(counter * counter + counter, counter * counter + counter, counter * counter + counter);
                }
            }
        );
        palettes.put("Moss",
            new Palette("Moss") {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(255.0 - (counter * counter), 255.0 - (counter * counter), counter + counter);
                }
            }
        );
        palettes.put("Frost",
            new Palette("Frost") {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(255.0 - (counter + counter), 255.0 - (counter + counter), 255.0);
                }
            }
        );
        palettes.put("Light Frost",
            new Palette("Light Frost") {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(255.0 - (counter), 255.0 - (counter), 255.0);
                }
            }
        );
        palettes.put("Lava Lamp",
            new Palette("Lava Lamp") {
                @Override
                public Color getColorDetail(double counter) {
                    return safeColor(255.0, 100.0 - (counter * counter * counter), 100.0 - (counter * counter));
                }
            }
        );
    }

    public Set<String> getNames(){
        return palettes.keySet();
    }

    public Palette getPalette(String name){
        return palettes.get(name);
    }

    public Palette getDefault(){
        return palettes.get(DEFAULT_NAME);
    }
}
