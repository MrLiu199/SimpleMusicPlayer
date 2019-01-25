package com.musicplayer.llj.simplemusicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import com.musicplayer.llj.simplemusicplayer.MusicLab;
import com.musicplayer.llj.simplemusicplayer.fragment.MusicListFragment;
import com.musicplayer.llj.simplemusicplayer.model.Music;
import com.musicplayer.llj.simplemusicplayer.service.PlayService;

import java.util.List;

public class PlayListActivity extends SingleFragmentActivity {
    private MusicLab mMusicLab;

    private List<Music> mPlayMusics;
    private MusicListFragment.MusicAdapter mPlayMusicAdapter;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, PlayListActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mMusicLab = MusicLab.get(PlayListActivity.this);
        mPlayMusics = mMusicLab.getMusics();
        mPlayMusicAdapter = new MusicListFragment.MusicAdapter(this, mPlayMusics);
        mPlayMusicAdapter.setOnItemClickListener(music -> {
            PlayService.get().addAndPlay(music);
        });
        mPlayMusicAdapter.setOnMoreClickListener(position -> {
            String[] items = new String[]{"移除"};
            Music music = mPlayMusics.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(music.getTitle());
            dialog.setItems(items, (dialog1, which) -> {
                mPlayMusics.remove(position);
                mPlayMusicAdapter.notifyDataSetChanged();
            });
            dialog.show();
        });

        MusicListFragment fragment = MusicListFragment.newInstance(mPlayMusicAdapter);
        fragment.notifyServiceBound();
        return fragment;
    }
}