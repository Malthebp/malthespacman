package org.example.pacman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    //reference to the main view
    GameView gameView;
    //reference to the game class.
    Game game;
    private Timer myTimer;
    private int counter = 0;
    private Timer gameTimer;
    private TextView timerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //saying we want the game to run in one mode only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        gameView =  findViewById(R.id.gameView);
        TextView pointsView = findViewById(R.id.points);
        TextView levelView = findViewById(R.id.level);
        timerView = findViewById(R.id.timer);

        game = new Game(this, pointsView, levelView);
        game.setGameView(gameView);
        gameView.setGame(game);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.welcome);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                game.newGame();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();



        //make a new timer
        myTimer = new Timer();
        //We will call the timer 5 times each second
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 50); //0 indicates we start now, 200

        gameTimer = new Timer();
        //We will call the timer 5 times each second
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                GameTimer();
            }

        }, 0, 1000); //0 indicates we start now, 200

        Button buttonRight = findViewById(R.id.moveRight);
        //listener of our pacman, when somebody clicks it
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.movePacmanRight();
            }
        });

        Button buttonLeft = findViewById(R.id.moveLeft);
        //listener of our pacman, when somebody clicks it
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.movePacmanLeft();
            }
        });

        Button buttonUp = findViewById(R.id.moveUp);
        //listener of our pacman, when somebody clicks it
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.movePacmanUp();
            }
        });

        Button buttonDown = findViewById(R.id.moveDown);
        //listener of our pacman, when somebody clicks it
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.movePacmanDown();
            }
        });


    }

    private void GameTimer() {
        this.runOnUiThread(Game_Timer_Tick);
    }

    private Runnable Game_Timer_Tick = new Runnable() {
        public void run() {
            if (game.isRunning())
            {
                game.decreaseTimeRemaining();
                timerView.setText(getResources().getString(R.string.time)+" " + game.timeRemaining);
                for (Enemy e : game.getEnemies()) {
                    e.randomizeDirection();
                }
            }
        }
    };

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.
            // so we can draw
            if (game.isRunning())
            {
                counter++;
                //update the counter - notice this is NOT seconds in this example
                //you need TWO counters - one for the time and one for the pacman
                game.move(); //move the pacman - you

                for (Enemy e : game.getEnemies()) {
                    e.move();
                }

                //should call a method on your game class to move
                //the pacman instead of this
            }

        }
    };

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this,"settings clicked",Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_newGame) {
            final MenuItem fItem = item;
            game.setRunning(!game.isRunning());
            fItem.setTitle("Paused!");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.paused_game);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.resume_game, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    game.setRunning(true);
                    fItem.setTitle("Pause");
                }
            });
            builder.setNegativeButton(R.string.new_game, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    game.newGame();
                    fItem.setTitle("Pause");
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
