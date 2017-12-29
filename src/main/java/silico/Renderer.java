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

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author helfrich
 */
public class Renderer {

    private final SilicoGame game;
    private GraphicsContext gc;
    private Assets assets;

    int playFieldMargin = 32;
    private int fieldHeight;
    private final Silico silico;

    public Renderer(Silico silico, SilicoGame game, GraphicsContext gc, Assets assets) {
        this.silico = silico;
        this.game = game;
        this.gc = gc;
        this.assets = assets;
    }

    void render() {

        int fieldWidth = assets.getTileWidth() * 14;
        fieldHeight = assets.getTileHeight() * 10;
        for (int x = 0; x < fieldWidth; x += assets.getTileWidth()) {
            for (int y = 0; y < fieldHeight; y += assets.getTileHeight()) {
                gc.drawImage(assets.getRandomSurface(), playFieldMargin + x, playFieldMargin + y);
            }
        }
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(assets.getZorqueFontSmall());
        gc.setFill(Color.valueOf("#4b4456"));
        for (int xCoor = 0; xCoor <= 7; xCoor++) {
            gc.fillText(String.valueOf(xCoor), playFieldMargin + xCoor * assets.getTileWidth() * 2, playFieldMargin + fieldHeight + 16);
        }
        for (int yCoor = 0; yCoor <= 9; yCoor++) {
            gc.fillText(String.valueOf(yCoor), playFieldMargin - 8, fieldHeight + playFieldMargin - yCoor * assets.getTileHeight() - assets.getTileHeight() / 2);
        }

        // Game objects
        /*
        for (int i = 1; i < 10; i++) {
            double x = toScreenX(game.p[i][2]);
            double y = toScreenY(game.p[i][1]);
            switch ((int) Math.round(game.p[i][3])) {
                case 1:
                    gc.drawImage(assets.getMountain(), x, y);
                    break;
                case 2:
                    gc.drawImage(assets.getBase(), x, y);
                    break;
                case 3:
                    gc.drawImage(assets.getXam(), x, y);
                    break;
            }
        }
         */
        for (int i = 1; i < 10; i++) {
            if (Math.round(game.p[i][3]) == 1) {
                double x = toScreenX(game.p[i][2]);
                double y = toScreenY(game.p[i][1]);
                gc.drawImage(assets.getMountain(), x, y);
            }
        }
        
        for (int i = 1; i < 10; i++) {
            if (Math.round(game.p[i][3]) == 3) {
                double x = toScreenX(game.p[i][2]);
                double y = toScreenY(game.p[i][1]);
                gc.drawImage(assets.getXam(), x, y);
            }
        }
        
        for (int i = 1; i < 10; i++) {
            if (Math.round(game.p[i][3]) == 2) {
                double x = toScreenX(game.p[i][2]);
                double y = toScreenY(game.p[i][1]);
                gc.drawImage(assets.getBase(), x, y);
            }
        }
        
        updateScreenValues();
    }

    private void updateScreenValues() {
        silico.cycleNumberText.setText(String.valueOf(game.w));

        silico.baseXLabel.setText(String.valueOf(game.b));
        silico.baseYLabel.setText(String.valueOf(game.a));

        silico.courseCalcLabel.setText(String.valueOf(game.k1));
        silico.courseRealLabel.setText(String.valueOf(game.k));

        silico.speedCalcLabel.setText(String.valueOf(game.f1));
        silico.speedRealLabel.setText(String.valueOf(game.f));

        silico.xCalcLabel.setText(String.valueOf(game.x1));
        silico.xRealLabel.setText(String.valueOf(game.x));

        silico.yCalcLabel.setText(String.valueOf(game.y1));
        silico.yRealLabel.setText(String.valueOf(game.y));

    }

    private double toScreenX(double pX) {
        return playFieldMargin + (pX * assets.getTileWidth() * 2) - (assets.getTileWidth() / 2);
    }

    private double toScreenY(double pY) {
        return playFieldMargin + fieldHeight - (pY * assets.getTileHeight()) - assets.getTileHeight();
    }
}
