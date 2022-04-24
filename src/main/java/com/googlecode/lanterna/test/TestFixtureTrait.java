package com.googlecode.lanterna.test;

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.gui2.ImageComponent;

import static com.googlecode.lanterna.gui2.Attributed.attrs;

public interface TestFixtureTrait {
    String[] IMAGE = new String[]{
        "-====================================================-",
        "xx                                                  xx",
        "xx  X                                            X  xx",
        "xx                                                  xx",
        "xx    .d8b.  d8888b.  .o88b.                        xx",
        "xx   d8' `8b 88  `8D d8P  Y8                        xx",
        "xx   88ooo88 88oooY' 8P            asdfasdf         xx",
        "xx   88~~~88 88~~~b. 8b                             xx",
        "xx   88   88 88   8D Y8b  d8              1234      xx",
        "xx   YP   YP Y8888P'  `Y88P'                        xx",
        "xx                                 asdfasdf         xx",
        "xx                                                  xx",
        "xx   db    db db    db d88888D                      xx",
        "xx   `8b  d8' `8b  d8' YP  d8'                      xx",
        "xx    `8bd8'   `8bd8'     d8'          xxxxxxx      xx",
        "xx    .dPYb.     88      d8'           x     x      xx",
        "xx   .8P  Y8.    88     d8' db         x     x      xx",
        "xx   YP    YP    YP    d88888P         x     x      xx",
        "xx                                     xxxxxxx      xx",
        "xx  X                                            X  xx",
        "xx                                                  xx",
        "-====================================================-"
    };
    String[] IMAGE_BLANK = new String[]{
        "-=================================-",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "x                                 x",
        "-=================================-"
    };
    String[] IMAGE_X = new String[]{
        "-=================================-",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "xx     XXXXXXX       XXXXXXX     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     X::::::X     X::::::X     xx",
        "xx     XXX:::::X   X:::::XXX     xx",
        "xx        X:::::X X:::::X        xx",
        "xx         X:::::X:::::X         xx",
        "xx          X:::::::::X          xx",
        "xx          X:::::::::X          xx",
        "xx         X:::::X:::::X         xx",
        "xx        X:::::X X:::::X        xx",
        "xx     XXX:::::X   X:::::XXX     xx",
        "xx     X::::::X     X::::::X     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     X:::::X       X:::::X     xx",
        "xx     XXXXXXX       XXXXXXX     xx",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "-=================================-"
    };
    String[] IMAGE_Y = new String[]{
        "-=================================-",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "xx     YYYYYYY       YYYYYYY     xx",
        "xx     Y:::::Y       Y:::::Y     xx",
        "xx     Y:::::Y       Y:::::Y     xx",
        "xx     Y::::::Y     Y::::::Y     xx",
        "xx     YYY:::::Y   Y:::::YYY     xx",
        "xx        Y:::::Y Y:::::Y        xx",
        "xx         Y:::::Y:::::Y         xx",
        "xx          Y:::::::::Y          xx",
        "xx           Y:::::::Y           xx",
        "xx            Y:::::Y            xx",
        "xx            Y:::::Y            xx",
        "xx            Y:::::Y            xx",
        "xx            Y:::::Y            xx",
        "xx         YYYY:::::YYYY         xx",
        "xx         Y:::::::::::Y         xx",
        "xx         YYYYYYYYYYYYY         xx",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "-=================================-"
    };
    String[] IMAGE_Z = new String[]{
        "-=================================-",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "xx     ZZZZZZZZZZZZZZZZZZZ       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     Z:::ZZZZZZZZ:::::Z        xx",
        "xx     ZZZZZ     Z:::::Z         xx",
        "xx             Z:::::Z           xx",
        "xx            Z:::::Z            xx",
        "xx           Z:::::Z             xx",
        "xx          Z:::::Z              xx",
        "xx         Z:::::Z               xx",
        "xx        Z:::::Z                xx",
        "xx     ZZZ:::::Z     ZZZZZ       xx",
        "xx     Z::::::ZZZZZZZZ:::Z       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     Z:::::::::::::::::Z       xx",
        "xx     ZZZZZZZZZZZZZZZZZZZ       xx",
        "xx                               xx",
        "xx  X                         X  xx",
        "xx                               xx",
        "-=================================-"
    };

    default ImageComponent createImageComponent(final String id, String[] image) {
        return new ImageComponent(attrs(id)).setTextImage(createTextImage(image));
    }

    default TextImage createTextImage(String[] image) {
        return createTextImage(image, new Dimension(image[0].length(), image.length));
    }

    default TextImage createTextImage(String[] image, Dimension dimension) {
        TextImage textImage = new BasicTextImage(dimension);
        for (int row = 0; row < image.length; row++) {
            for (int x = 0; x < image[row].length(); x++) {
                char c = image[row].charAt(x);
                TextCharacter textCharacter = new TextCharacter(c);
                textImage.setCharacterAt(x, row, textCharacter);
            }
        }
        return textImage;
    }
}
