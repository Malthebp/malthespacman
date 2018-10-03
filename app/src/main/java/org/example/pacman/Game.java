package org.example.pacman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;

/**
 *
 * This class should contain all your game logic
 */

public class Game {
    //context is a reference to the activity
    private Context context;
    private int points = 0; //how points do we have

    //bitmap of the pacman
    private Bitmap pacBitmap;
    //textview reference to points
    private TextView pointsView;
    private TextView levelView;
    private int pacx, pacy;
    private int currentDeg = 0;
    private int pixels = 18;
    //the list of goldcoins - initially empty
    private ArrayList<GoldCoin> coins = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();

    //a reference to the gameview
    private GameView gameView;
    private int h,w; //height and width of screen

    public int gameLevel = 1;
    public int levelPoints = 10;
    private double levelDifficulty = 1.1;

    private boolean isRunning = false;
    private Directions direction = Directions.RIGHT;
    public int timeRemaining;

    public Game(Context context, TextView pView, TextView lView)
    {
        this.context = context;
        this.pointsView = pView;
        this.levelView = lView;
        resetPacmanBitmap();

    }

    public void setGameView(GameView view)
    {
        this.gameView = view;
    }

    //TODO initialize goldcoins also here
    public void newGame()
    {
        pacx = 50;
        pacy = 400; //just some starting coordinates
        //reset the points
        points = 0;
        coins = new ArrayList<>();
        direction = Directions.RIGHT;
        pointsView.setText(context.getResources().getString(R.string.points)+" "+points);
        levelView.setText(context.getResources().getString(R.string.level)+" "+gameLevel);
        isRunning = true;
        levelPoints = 10;
        gameLevel = 1;
        timeRemaining = 60;
        resetPacmanBitmap();

        createCoins(levelPoints);
        createEnemies(gameLevel);

        gameView.invalidate(); //redraw screen
    }

    private void createEnemies (int count) {
        enemies = new ArrayList<Enemy>();
        for(int i = 0; i < count; i++) {
            enemies.add(new Enemy(context, h, w, gameView));
        }
    }

    private void createCoins (int count) {
        Bitmap coin = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
        coins = new ArrayList<GoldCoin>();
        for(int i = 0; i < count; i++) {
            coins.add(new GoldCoin(coin));
        }
    }

    public void stopEnemies () {
        for (Enemy e : enemies) {
            e.setAlive(false);
        }
    }

    public void nextLevel () {
        isRunning = false;
        resetPacmanBitmap();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You have completed level " + gameLevel);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.next_level, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gameLevel++;
                int lvlNext = (int) Math.round(levelPoints * levelDifficulty);
                levelPoints += lvlNext;
                levelView.setText(context.getResources().getString(R.string.level)+" "+gameLevel);
                createCoins(lvlNext);
                createEnemies( gameLevel);
                pixels =  18;
                isRunning = true;
                timeRemaining = 60;
                direction = Directions.RIGHT;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void endGame () {
        isRunning = false;
        stopEnemies();
        if (points != levelPoints) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.game_over);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.new_game, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newGame();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void AddPoints () {
        points++;
        pointsView.setText(context.getResources().getString(R.string.points)+" " + points);
        if (points >= levelPoints) {
            nextLevel();
        }
    }

    public void decreaseTimeRemaining () {
        timeRemaining--;
        if (timeRemaining <= 0) {
            endGame();
        }
    }

    public void setSize(int h, int w)
    {
        this.h = h;
        this.w = w;
    }

    private void resetPacmanBitmap () {
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman);
    }

    private void rotatePacman () {
        Matrix matrix = new Matrix();
        resetPacmanBitmap();
        switch (direction) {
            case LEFT:
                matrix.preScale(-1, 1);
                pacBitmap = Bitmap.createBitmap(pacBitmap, 0, 0, pacBitmap.getWidth(), pacBitmap.getHeight(), matrix, true);
                break;
            case DOWN:
                matrix.postRotate(90);
                pacBitmap = Bitmap.createBitmap(pacBitmap, 0, 0, pacBitmap.getWidth(), pacBitmap.getHeight(), matrix, true);
                break;
            case UP:
                matrix.postRotate( 270);
                pacBitmap = Bitmap.createBitmap(pacBitmap, 0, 0, pacBitmap.getWidth(), pacBitmap.getHeight(), matrix, true);
                break;
            default:
                matrix.postRotate(0);
                pacBitmap = Bitmap.createBitmap(pacBitmap, 0, 0, pacBitmap.getWidth(), pacBitmap.getHeight(), matrix, true);
                break;
        }
    }

    public void movePacmanRight()
    {
        direction = Directions.RIGHT;
        rotatePacman();
    }

    public void movePacmanLeft()
    {
        direction = Directions.LEFT;
        rotatePacman();
    }

    public void movePacmanDown()
    {
        direction = Directions.DOWN;
        rotatePacman();
    }

    public void movePacmanUp()
    {
        direction = Directions.UP;
        rotatePacman();
    }

    public void move () {
        if (!isRunning) return;
        doCollisionCheck();
        switch (direction) {
            case LEFT:
                if (pacx+pixels+pacBitmap.getWidth()> pacBitmap.getWidth()) {
                    pacx = pacx - pixels;
                    gameView.invalidate();
                }
                break;
            case UP:
                //still within our boundaries?
                if (pacy+pixels+pacBitmap.getHeight() > pacBitmap.getHeight()) {
                    pacy = pacy - pixels;
                    gameView.invalidate();
                }
                break;
            case DOWN:

                //still within our boundaries?
                if (pacy+pixels+pacBitmap.getHeight()<h) {
                    pacy = pacy + pixels;
                    gameView.invalidate();
                }
                break;

            default:
                //still within our boundaries?
                if (pacx+pixels+pacBitmap.getWidth() <= w) {
                    pacx = pacx + pixels;
                    gameView.invalidate();
                }
                break;
        }
    }

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman
    public void doCollisionCheck()
    {
        for (GoldCoin coin : coins) {
            int disX = (int) Math.pow(((pacBitmap.getHeight() / 2) + pacx) - ((coin.getImage().getHeight() / 2) + coin.getX()), 2);
            int disY = ((int) Math.pow(((pacBitmap.getWidth() / 2) + pacy) - ((coin.getImage().getWidth() / 2) + coin.getY()), 2));
            double dist =  Math.sqrt(disX + disY);
            if (!coin.isTaken() && dist <= ((pacBitmap.getHeight() / 2) + (coin.getImage().getWidth() / 2))) {
                coin.setTaken(true);
                AddPoints();
            }
        }

        for (Enemy enemy : enemies) {
            int disX = (int) Math.pow(((pacBitmap.getHeight() / 2) + pacx) - ((enemy.getEnemyBitMap().getHeight() / 2) + enemy.getEnemyx()), 2);
            int disY = ((int) Math.pow(((pacBitmap.getWidth() / 2) + pacy) - ((enemy.getEnemyBitMap().getWidth() / 2) + enemy.getEnemyy()), 2));
            double dist =  Math.sqrt(disX + disY);
            if (enemy.isAlive() && dist < ((pacBitmap.getHeight() / 2) + (enemy.getEnemyBitMap().getWidth() / 2))) {
                enemy.setAlive(false);
                endGame();
            }
        }
    }

    public int getPacx()
    {
        return pacx;
    }

    public int getPacy()
    {
        return pacy;
    }

    public int getPoints()
    {
        return points;
    }

    public ArrayList<GoldCoin> getCoins()
    {
        return coins;
    }

    public Bitmap getPacBitmap()
    {
        return pacBitmap;
    }


    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public ArrayList<Enemy> getEnemies (){
        return enemies;
    }
}
