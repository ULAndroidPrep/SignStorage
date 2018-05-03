package edu.rosehulman.photobucket;

/**
 * Created by Matt Boutell on 12/19/2015.
 * Rose-Hulman Institute of Technology.
 * Covered by MIT license.
 */
public class Util {
    public static String randomImageUrl() {
        String[] urls = new String[]{
                "https://upload.wikimedia.org/wikipedia/commons/1/1a/Dszpics1.jpg",
                "https://pixabay.com/static/uploads/photo/2015/09/14/15/52/lightning-939737_960_720.jpg",
                "http://s0.geograph.org.uk/geophotos/01/56/26/1562673_9088a4c9.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/6/61/Hurricane_Hugo_15_September_1989_1105z.png",
                "https://upload.wikimedia.org/wikipedia/commons/d/dc/P5med.jpg"
        };
        return urls[(int) (Math.random() * urls.length)];
    }
}
