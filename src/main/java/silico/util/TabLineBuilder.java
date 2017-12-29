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

/**
 *
 * @author helfrich
 */
public class TabLineBuilder {

    int length;
    private String line;
    StringBuilder sb;

    public TabLineBuilder(int length) {
        this.length = length;
        initialize(); // call non-overridable method
    }

    public TabLineBuilder insert(int pos, String text) {
        sb.replace(pos, pos + text.length(), text);
        line = sb.toString();
        return this;
    }

    public String toString() {
        return line;
    }

    private void initialize() {
        // new line of spaces
        line = String.format("%" + length + "s", ' ');
        sb = new StringBuilder(line);
    }

    public void clear() {
        // Overridable public method
        initialize();
    }
}
