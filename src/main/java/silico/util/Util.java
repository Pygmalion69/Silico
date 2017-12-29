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

package silico.util;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author helfrich
 */
public class Util {

    /*
    public static void cls() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
     */
    public static void cls() {
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException ex) {
        }
    }

    public static String emptyLine() {
        return String.format("%80s", ' ');
    }

    public static String tab(String line, String text, int pos) {
        StringBuilder sb = new StringBuilder(line);
        sb.replace(pos, pos + text.length(), text);
        return sb.toString();
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
    
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static void addButtonEffects(Button button) {
        
        DropShadow shadow = new DropShadow();
        InnerShadow innerShadow = new InnerShadow();
        
         button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
             button.setEffect(shadow);
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            button.setEffect(null);
        });
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            button.setEffect(innerShadow);
        });
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
            button.setEffect(shadow);
        });
    }
}
