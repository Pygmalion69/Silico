/*
 * This file contains a Java rendition of the BASIC listing by M. Th. A. M.
 * Vijftigshild. It does NOT reflect any good Java or OO practice.
 */
package silico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import silico.util.Util;
import silico.util.TabLineBuilder;

/**
 *
 * @author helfrich
 */
public class SilicoGame {

    TabLineBuilder tlb;

    float[][] data = {{4, 1.8f}, {4, 2.6f}, {3, 1.4f}, {3, 3.4f}, {2, 4.8f}, {1, 0.4f}, {1, 5.8f}};
    double[][] p = new double[10][4];
    double[][] z = new double[11][3];
    String strC[] = new String[5];

    Random rand = new Random(System.currentTimeMillis());

    int w;
    private String strA;
    private String strD;
    int a;
    int b;
    double k;
    double k1; // heading
    private double s; // relative speed
    int f;
    int f1;
    double y;
    double x;
    double y1;
    double x1;
    private double c;
    private double d;
    private int l;
    private int valZ;
    private int h;
    double x2;
    double y2;

    BufferedReader br;
    private boolean consoleGame;
    private Callback callback;

    enum GameState {
        ONGOING, DISABLED, OUT_OF_REACH, BASE_REACHED
    }

    GameState gameState;

    interface Callback {

        void updateGui();

        int getSpeedInput();

        int getHeadingInput();
    }
    
    void setCallback(Callback callback) {
        this.callback = callback;
    }

    private boolean shouldCallBack() {
        return (callback != null && !consoleGame);
    }

    void run(boolean consoleGame) {
        this.consoleGame = consoleGame;
        gameState = GameState.ONGOING;
        if (consoleGame) {
            while (h < 2) {
                output();
                input();
                deadReckoning();
                actualCourse();
                newXamLocation();
            }
            output();
            System.exit(0);
        }
    }

    void startCycle(int heading, int speed) {
        k1 = heading;
        f1 = speed;
        s = f1 / 30d;
        if (h < 2) {
            for (int i = 1; i < p.length; i++) {
                if (p[i][3] == 3) {
                    valZ = i;
                }

            }
            deadReckoning();
            actualCourse();
            newXamLocation();
        }
        output();
    }

    private void instructions() {
        System.out.println("Instructions go here.");
    }

    void init() {

        br = new BufferedReader(new InputStreamReader(System.in));
        tlb = new TabLineBuilder(80);

        // sand storms
        for (int i = 1; i <= 10; i++) {
            z[i][1] = rand.nextInt(360);
            z[i][2] = (rand.nextInt(15) + 1) / 100f;
        }
        //mountain ridge
        for (int i = 1; i <= 7; i++) {
            p[i][1] = data[i - 1][0];
            p[i][2] = data[i - 1][1];
            p[i][3] = 1;  // marker
        }
        // base
        a = rand.nextInt(2) + 6; // y
        b = (rand.nextInt(20) + 45) / 10; // x
        p[8][3] = 2;
        p[8][1] = a;
        p[8][2] = b;
        // survey robot XAM
        y = 0;
        x = (rand.nextInt(15) + 15) / 10;
        y1 = y;
        x1 = x;
        p[9][1] = y;
        p[9][2] = x;
        p[9][3] = 3;
        // initial values
        strA = "0----1----2----3----4----5----6----7";
        strD = "";
        strC[1] = "O";
        strC[2] = "#";
        strC[3] = "*";
        strC[4] = " ";
        // numerics
        w = 1;   // cycle
        c = Math.PI / 180; // deg - rad
        d = 180 / Math.PI;  // rad - deg
        f = 0;
        h = 0;

        gameState = GameState.ONGOING;
    }

    private void output() {
        sortMatP();
        if (shouldCallBack()) {
            callback.updateGui();
            return;
        }
        Util.cls();
        String line = tlb.insert(0, "*** De Zandplaneet Silico ***")
                .insert(35, "< Omloop > " + w)
                .insert(54, "360")
                .toString();
        System.out.println(line);
        tlb.clear();
        line = tlb.insert(3, strA).insert(55, "0").toString();
        System.out.println(line);

        int m = 9;
        for (int i = 10; i >= 1; i--) {
            tlb.clear();
            tlb.insert(0, String.valueOf(i - 1)).insert(3, "!");

            while (m > 0) {
                if (Math.round(p[m][1]) == i - 1) {  // 980

                    //System.out.println(i);
                    int posTab = (int) (p[m][2] * 35 / 7 + 3);
                    int l = (int) p[m][3];
                    if (l == 3) {
                        valZ = m;
                    }
                    if (l > 0 && l < 4) {
                        tlb.insert(posTab, strC[l]);
                    }
                    m--;
                } else {
                    break;
                }
            }
            tlb.insert(38, "!");   /// 1060
            switch (i) {
                case 10:
                    tlb.insert(49, "270   +   90");
                    break;
                case 9:
                    tlb.insert(54, "180");
                    break;
                case 8:
                    tlb.insert(41, String.valueOf(b))
                            .insert(47, "< x-b-y > " + String.valueOf(a));
                    break;
                case 7:
                    tlb.insert(40, "<Berek.>-------<Werk.>");
                    break;
                case 6:
                    tlb.insert(41, String.valueOf(k1))
                            .insert(47, "< Koers > " + k);
                    break;
                case 5:
                    tlb.insert(41, String.valueOf(f1))
                            .insert(47, "< Snelh.> " + f);
                    break;
                case 3:
                    tlb.insert(41, String.valueOf(x1))
                            .insert(47, "< x-cor > " + x);
                    break;
                case 2:
                    tlb.insert(41, String.valueOf(y1))
                            .insert(47, "< y-cor > " + y);
                    break;
                case 1:
                    tlb.insert(41, strD);
                    break;
            }
            System.out.println(tlb.toString());
            tlb.clear();
        }
        line = tlb.insert(3, strA).toString();
        System.out.println(line);
        tlb.clear();

    }

    private void sortMatP() {
        for (int q = 9; q >= 2; q--) {
            for (int r = 1; r <= q; r++) {
                double valP = p[q][1] * 1000 - p[q][2];
                double valQ = p[r][1] * 1000 - p[r][2];
                if (valP > valQ) {
                    continue;
                }
                for (int j = 1; j <= 3; j++) {
                    double m = p[q][j];
                    p[q][j] = p[r][j];
                    p[r][j] = m;
                }
            }
        }
    }

    private void deadReckoning() {
        double p = k1 * c; // heading in rad
        //y1 = ((int) ((s * Math.cos(p) + y) * 10 + 0.5)) / 10;
        //x1 = ((int) ((s * Math.sin(p) + x) * 10 + 0.5)) / 10;
        y1 = Util.round(s * Math.cos(p) + y, 1);
        x1 = Util.round(s * Math.sin(p) + x, 1);
        w++;
    }

    private void actualCourse() {
        s = s / 10;
        double s1;
        double r;
        double valP;
        for (int i = 1; i <= 10; i++) {
            l = (int) y + 1;
            k = z[l][1];
            s1 = z[l][2];
            if (s != 0) {
                r = z[l][1] - k1;   /// stream direction
                s1 = Math.pow(s, 2) + Math.pow(z[l][2], 2) - 2 * s * z[l][2] * Math.cos((180 - r) * c);
                if (Math.abs(s1) <= 0.000001) {
                    s1 = 0;
                    k = -1;
                }
                s1 = Math.sqrt(s1);
                if (s1 == 0) {
                    i = somewhereClose(i);
                }
                valP = Math.sin((180 - r) * c) / s1 * z[l][2];
                valP = Math.atan(valP / Math.sqrt(-valP * valP + 1));  // actual course
                k = (int) (valP * d + k1 + 0.5);
                if (k > 360) {
                    k -= 360;
                }
                if (k < 0) {
                    k += 360;
                }
            }
            valP = k * c;
            f = (int) (s1 * 30 * 10 + 0.5);

            // dead reckoning based on actual course
            y = (s1 * Math.cos(valP) + y);
            x = (s1 * Math.sin(valP) + x);
            // System.out.println(String.format("s1 = %f, x = %f, y = %f", s1, x, y));
            i = somewhereClose(i);

        }
        //System.out.println(String.format("x = %f, y = %f", x, y));
        //y = ((int) (y * 10 + 0.5)) / 10;
        //x = ((int) (x * 10 + 0.5)) / 10;
        y = Util.round(y, 1);
        x = Util.round(x, 1);

        // System.out.println(String.format("x = %f, y = %f", x, y));
    }

    private int somewhereClose(int i) {
        for (int j = 1; j <= 9; j++) {
            y2 = p[j][1] - y;
            x2 = p[j][2] - x;
            if (Math.abs(y2) > 0.15 || Math.abs(x2) > 0.15) {
                continue;
            }
            // Close to what?
            switch ((int) Math.round(p[j][3])) {
                case 1:
                    // against mountain
                    h = 2;
                    l = j;
                    strD = "> XAM uitgeschakeld <";
                    gameState = GameState.DISABLED;
                    break;
                case 2:
                    // arrived at base
                    h = 3;
                    l = j;
                    strD = "> * Basis bereikt * <";
                    gameState = GameState.BASE_REACHED;
                    break;
            }
            if (h < 2) {
                continue;
            }
            j = 9;
            i = 10;
        }
        return i;
    }

    private void newXamLocation() {
        p[valZ][1] = y;
        p[valZ][2] = x;
        float valP;
        if (h >= 2) {
            valP = 0.1f;
            if (x2 < 0) {
                valP = -0.1f;
            }
            p[valZ][2] = p[l][2] + valP;
        }
        if (p[valZ][1] >= 0 && p[valZ][1] <= 9) {
            if (p[valZ][2] >= 0 && p[valZ][2] <= 7) {
                return;
            }
        }
        p[valZ][1] = 0;
        p[valZ][2] = 0;
        p[valZ][3] = 4;
        strD = "> XAM buiten bereik <";
        gameState = GameState.OUT_OF_REACH;
        h = 2;
    }

    private void input() {

        inputHeading();
        inputSpeed();
        s = f1 / 30d;
    }

    private void inputHeading() {
        System.out.print("Koers in graden 0 - 360 : ");
        try {
            try {
                k1 = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Silico.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Geef een geheel getal!");
        }
        if (k1 < 0 || k1 > 360) {
            inputHeading();
        }
    }

    private void inputSpeed() {
        System.out.print("Snelheid in knots 0 - 30 : ");
        try {
            try {
                f1 = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Silico.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Geef een geheel getal!");
        }
        if (f1 < 0 || f1 > 30) {
            inputSpeed();
        }
    }
}
