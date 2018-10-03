package org.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class Enemy {

    private boolean isAlive = true;
    private Context context;

    public Bitmap getEnemyBitMap() {
        return enemyBitMap;
    }

    private Bitmap enemyBitMap;

    public int getEnemyx() {
        return enemyx;
    }

    public int getEnemyy() {
        return enemyy;
    }

    public void setEnemyx(int enemyx) {
        this.enemyx = enemyx;
    }

    public void setEnemyy(int enemyy) {
        this.enemyy = enemyy;
    }

    private int enemyx, enemyy = 0;

    //a reference to the gameview
    private GameView gameView;
    private int h,w; //height and width of screen
    private Directions direction = Directions.DOWN;

    public Enemy(Context context, int h, int w, GameView view)
    {

        this.context = context;
        this.w = w;
        this.h = h;
        this.gameView = view;
        this.enemyBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        this.enemyBitMap = enemyBitMap.createScaledBitmap(enemyBitMap, 120, 120, true);
        Random rnd = new Random();

        this.enemyx = 0;
        this.enemyy = 0;
    }

    public void randomizeDirection () {
        Random rnd = new Random();
        direction = Directions.values()[rnd.nextInt(Directions.values().length)] == direction ? Directions.values()[rnd.nextInt(Directions.values().length)] : Directions.values()[rnd.nextInt(Directions.values().length)];
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        enemyBitMap = Bitmap.createBitmap(enemyBitMap, 0, 0, enemyBitMap.getWidth(), enemyBitMap.getHeight(), matrix, true);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void move () {
        int pixels = 8;
        switch (direction) {
            case LEFT:
                if (enemyx+pixels+enemyBitMap.getWidth()> enemyBitMap.getWidth()) {
                    enemyx = enemyx - pixels;
                    gameView.invalidate();
                }
                break;
            case UP:
                //still within our boundaries?
                if (enemyy+pixels+enemyBitMap.getHeight() > enemyBitMap.getHeight()) {
                    enemyy = enemyy - pixels;
                    gameView.invalidate();
                }
                break;
            case DOWN:
                //still within our boundaries?
                if (enemyy+pixels+enemyBitMap.getHeight()<h) {
                    enemyy = enemyy + pixels;
                    gameView.invalidate();
                }
                break;

            default:
                //still within our boundaries?
                if (enemyx+pixels+enemyBitMap.getWidth() <= w) {
                    enemyx = enemyx + pixels;
                    gameView.invalidate();
                }
                break;
        }
    }
}
