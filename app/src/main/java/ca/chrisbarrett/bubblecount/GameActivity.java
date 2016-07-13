package ca.chrisbarrett.bubblecount;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;
import ca.chrisbarrett.bubblecount.view.GameView;

/**
 * A helper Activity that starts and stops the {@link ca.chrisbarrett.bubblecount.view.GameView;}
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class GameActivity extends AppCompatActivity implements GameView.OnGameViewListener,
        BackgroundMusicManager.OnBackgroundMusicListener {

    private static final String TAG = "GameActivity";
    private static final BackgroundMusicManager MUSIC_MANAGER = BackgroundMusicManager.getInstance();

    private static boolean isContinueMusic;
    private boolean isMusicOn;
    private GameView gameView;

    //
    // LifeCycles Events Begin Here
    //

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
    }

    @Override
    protected void onResume () {
        super.onResume();
        isContinueMusic = false;
        gameView.onResume();
        MUSIC_MANAGER.initialize(this, R.raw.background);
        isMusicOn = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(getString(R.string.pref_music_is_on_key),
                        getResources().getBoolean(R.bool.pref_music_is_on_default));

    }

    @Override
    protected void onPause () {
        super.onPause();
        gameView.onPause();
        if (!isContinueMusic) {
            Log.d(TAG, "onPause called and releasing MUSIC_MANAGER.");
            MUSIC_MANAGER.musicRelease();
        }
    }

    /**
     * Makes sure triggering the back button does not accidentally shut down the music if playing
     */
    @Override
    public void onBackPressed () {
        super.onBackPressed();
        Log.d(TAG, "Back button pressed. Making sure isContinueMusic continues.");
        isContinueMusic = true;
    }

    //
    // Listeners begin here
    //

    /**
     * Starts the music when the MUSIC_MANAGER advises the music is ready
     */
    @Override
    public void onMusicReady () {
        if (isMusicOn && !MUSIC_MANAGER.isPlaying()) {
            Log.d(TAG, "Calling for music to start.");
            MUSIC_MANAGER.musicStart();
        }
    }

    @Override
    public void onNewLevel (int level) {
        String message = String.format(getString(R.string.notify_level_start), level);
        Log.d(TAG, message);
    }

    @Override
    public void onGameStart () {
        String message = getString(R.string.notify_game_start);
        Log.d(TAG, message);
    }

    @Override
    public void onGameEnd (long time) {
        String message = getString(R.string.notify_game_end);
        Log.d(TAG, message + " : " + time);
        gameView.onPause();
    }

}