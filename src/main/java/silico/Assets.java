/*
 * Copyright (C) 2017 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package silico;

import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

/**
 *
 * @author helfrich
 */
public class Assets {

    private final Image surface1;
    private final Image surface2;
    private final Image mountain;
    private final Image base;
    private final Image xam;

    private final int tileWidth = 32;
    private final int tileHeight = tileWidth;

    Random rand = new Random(System.currentTimeMillis());
    
    private final Font zorqueFontSmall;
        private final Font zorqueFontNormal;


    public Assets() {
        surface1 = loadImage("surface1.png");
        surface2 = loadImage("surface2.png");
        mountain = loadImage("mountain.png", 1.3f);
        base = loadImage("base.png");
        xam = loadImage("xam.png");
        
        zorqueFontSmall = Font.loadFont(getClass().getClassLoader().getResourceAsStream("assets/zorque.ttf"), 12);
                zorqueFontNormal = Font.loadFont(getClass().getClassLoader().getResourceAsStream("assets/zorque.ttf"), 18);



    }

    private Image loadImage(String filename, float scaleFactor) {
        return new Image(getClass().getClassLoader().getResourceAsStream("assets/" + filename), tileWidth * scaleFactor, tileHeight * scaleFactor, true, false);
    }
    
    private Image loadImage(String filename) {
        return loadImage(filename, 1);
    }

    public Image getSurface1() {
        return surface1;
    }

    public Image getSurface2() {
        return surface2;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    Image getRandomSurface() {
        return (rand.nextInt(2) == 0 ? surface1 : surface2);
    }

    public Image getMountain() {
        return mountain;
    }

    public Image getBase() {
        return base;
    }

    public Image getXam() {
        return xam;
    }

    public Font getZorqueFontSmall() {
        return zorqueFontSmall;
    }
    
    public Font getZorqueFontNormal() {
        return zorqueFontNormal;
    }

}
