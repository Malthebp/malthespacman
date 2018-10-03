package org.example.pacman;

import android.graphics.Bitmap;

/**
 * This class should contain information about a single GoldCoin.
 * such as x and y coordinates (int) and whether or not the goldcoin
 * has been taken (boolean)
 */

public class GoldCoin {

    private boolean taken = false;
    private int x, y = 0;
    private Bitmap image;

    public GoldCoin (Bitmap img) {
        image = img.createScaledBitmap(img, 60, 60, true);
    }

    public GoldCoin (int rndY, int rndX) {
        x = rndX;
        y = rndY;
    }


    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Bitmap getImage() {
        return image;
    }
}
