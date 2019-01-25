package com.musicplayer.llj.simplemusicplayer;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.musicplayer.llj.simplemusicplayer.activity.PlayListActivity;
import com.musicplayer.llj.simplemusicplayer.model.Music;
import com.musicplayer.llj.simplemusicplayer.service.OnPlayerEventListener;
import com.musicplayer.llj.simplemusicplayer.service.PlayService;

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
public class ControlPanel implements View.OnClickListener, OnPlayerEventListener {
    private ProgressBar mProgressBar;
    private ImageView ivPlayBarCover;
    private TextView tvPlayBarTitle;
    private TextView tvPlayBarArtist;
    private ImageView ivPlayBarPlay;
    private ImageView ivPlayBarNext;
    private ImageView ivPlayBarPlaylist;

    public ControlPanel(View view) {
        initView(view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        ivPlayBarPlaylist.setOnClickListener(this);
        onChange(PlayService.get().getPlayMusic());
    }

    private void initView(View view) {
        mProgressBar = view.findViewById(R.id.pb_play_bar);
        ivPlayBarCover = view.findViewById(R.id.iv_play_bar_cover);
        tvPlayBarTitle = view.findViewById(R.id.tv_play_bar_title);
        tvPlayBarArtist = view.findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlay = view.findViewById(R.id.iv_play_bar_play);
        ivPlayBarNext = view.findViewById(R.id.iv_play_bar_next);
        ivPlayBarPlaylist = view.findViewById(R.id.iv_play_bar_playlist);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bar_play:
                PlayService.get().playPause();
                break;
            case R.id.iv_play_bar_next:
                PlayService.get().next();
                break;
            case R.id.iv_play_bar_playlist:
                Context context = ivPlayBarPlaylist.getContext();
                context.startActivity(PlayListActivity.newIntent(context));
                break;
            default:
        }
    }

    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
//        Bitmap cover = CoverLoader.get().loadThumb(music);
//        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(PlayService.get().isPlaying() || PlayService.get().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress(PlayService.get().getAudioPosition());
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }
}
