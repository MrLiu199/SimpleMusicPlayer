package com.musicplayer.llj.simplemusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.musicplayer.llj.simplemusicplayer.fragment.MusicListFragment;
import com.musicplayer.llj.simplemusicplayer.fragment.MusicListFragment.MusicAdapter;
import com.musicplayer.llj.simplemusicplayer.model.Music;
import com.musicplayer.llj.simplemusicplayer.service.PlayService;
import com.musicplayer.llj.simplemusicplayer.util.MusicLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FrameLayout flPlayBar;
    private ControlPanel controlPanel;

    //    private PlayService playService;
    private ServiceConnection serviceConnection;

    private MusicLab mMusicLab;

    private ArrayList<Music> mLocalMusics;
    private MusicAdapter mLocalMusicAdapter;
    private MusicListFragment mMusicListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMusicLab = MusicLab.get(MainActivity.this);
        bindService();

        mLocalMusics = new ArrayList<>();
        mLocalMusicAdapter = new MusicAdapter(MainActivity.this, mLocalMusics);
        mLocalMusicAdapter.setOnItemClickListener(music -> {
            List<Music> musicList = mMusicLab.getMusics();
            if (!musicList.contains(music)) {
                musicList.add(music);
                mMusicLab.addMusic(music);
                Toast.makeText(MainActivity.this, "已加入到播放列表", Toast.LENGTH_SHORT).show();
            }
            PlayService.get().addAndPlay(music);
        });

        initFragment();
        new LoadLocalMusicTask().execute();
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
//        Fragment mMusicListFragment = fm.findFragmentById(R.id.fragment_container);

        if (mMusicListFragment == null) {
            mMusicListFragment = MusicListFragment.newInstance(mLocalMusicAdapter);
            fm.beginTransaction()
                    .add(R.id.fragment_container, mMusicListFragment)
                    .commit();
        }
    }

    private void initView() {
        flPlayBar = findViewById(R.id.fl_play_bar);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        serviceConnection = new PlayServiceConnection();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onServiceBound() {
        initView();
        controlPanel = new ControlPanel(flPlayBar);
        PlayService.get().addOnPlayEventListener(controlPanel);
        mMusicListFragment.notifyServiceBound();
    }

    @Override
    protected void onDestroy() {
        PlayService.get().removeOnPlayEventListener(controlPanel);
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    private class LoadLocalMusicTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mLocalMusics.clear();
            mLocalMusics.addAll(MusicLoader.getAllMusics(MainActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mLocalMusicAdapter.notifyDataSetChanged();
        }
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            playService = ((PlayService.PlayBinder) service).getService();
            onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(getClass().getSimpleName(), "service disconnected");
        }
    }
}
