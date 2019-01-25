package com.musicplayer.llj.simplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.musicplayer.llj.simplemusicplayer.MusicLab;
import com.musicplayer.llj.simplemusicplayer.model.Music;
import com.musicplayer.llj.simplemusicplayer.util.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayService extends Service {
    private static final String TAG = "PlayService";
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private static final long TIME_UPDATE = 300L;

    private static PlayService sPlayService;
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();
    private int state = STATE_IDLE;
    private List<Music> musicList;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    //更新播放进度
    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    public PlayService() {
    }

    public static PlayService get() {
        return sPlayService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
        sPlayService = this;

        musicList = MusicLab.get(this).getMusics();
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());

        mediaPlayer.setOnCompletionListener(mp -> next());
        mediaPlayer.setOnPreparedListener(mp -> {
            if (isPreparing()) {
                startPlayer();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            for (OnPlayerEventListener listener : listeners) {
                listener.onBufferingUpdate(percent);
            }
        });
    }

    public void stopService() {
        stopPlayer();
        stopSelf();
    }

    public void addAndPlay(Music music) {
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            MusicLab.get(this).addMusic(music);
            Toast.makeText(PlayService.this, "已加入到播放列表", Toast.LENGTH_SHORT).show();
            position = musicList.size() - 1;
        }
        play(position);
    }

    public void play(int position) {
        if (musicList.isEmpty()) {
            return;
        }

        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }

        setPlayPosition(position);
        Music music = getPlayMusic();

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepareAsync();
            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                listener.onChange(music);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PlayService.this, "当前歌曲无法播放", Toast.LENGTH_SHORT).show();
        }
    }

    public void startPlayer() {
        mediaPlayer.start();
        state = STATE_PLAYING;
        handler.post(mPublishRunnable);
        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerStart();
        }
    }

    public void pausePlayer() {
        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerPause();
        }
    }

    public void stopPlayer() {
        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getPlayPosition());
        }
    }

    public void next() {
        if (musicList.isEmpty()) {
            return;
        }
        play(getPlayPosition() + 1);
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
//        return mediaPlayer.isPlaying();
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public void addOnPlayEventListener(OnPlayerEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnPlayEventListener(OnPlayerEventListener listener) {
        listeners.remove(listener);
    }

    public int getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public Music getPlayMusic() {
        if (musicList.isEmpty()) {
            return null;
        }
        return musicList.get(getPlayPosition());
    }

    public int getPlayPosition() {
        int position = Preferences.getPlayPosition();
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            Preferences.savePlayPosition(position);
        }
        return position;
    }

    private void setPlayPosition(int position) {
        Preferences.savePlayPosition(position);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
