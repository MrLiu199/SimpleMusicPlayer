package com.musicplayer.llj.simplemusicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.musicplayer.llj.simplemusicplayer.R;
import com.musicplayer.llj.simplemusicplayer.model.Music;
import com.musicplayer.llj.simplemusicplayer.service.BaseOnPlayerEventListener;
import com.musicplayer.llj.simplemusicplayer.service.OnPlayerEventListener;
import com.musicplayer.llj.simplemusicplayer.service.PlayService;

import java.util.List;

public class MusicListFragment extends Fragment {
    private RecyclerView mMusicRecyclerView;
    private MusicAdapter mAdapter;
    private OnPlayerEventListener mOnPlayerEventListener = new BaseOnPlayerEventListener() {
        @Override
        public void onChange(Music music) {
            mAdapter.notifyDataSetChanged();
        }
    };

    public static MusicListFragment newInstance(MusicAdapter musicAdapter) {
        MusicListFragment fragment = new MusicListFragment();
        fragment.mAdapter = musicAdapter;
//        fragment.mOnPlayerEventListener = new BaseOnPlayerEventListener() {
//            @Override
//            public void onChange(Music music) {
//                fragment.mAdapter.notifyDataSetChanged();
//            }
//        };
        return fragment;
    }

    public void notifyServiceBound() {
        PlayService.get().addOnPlayEventListener(mOnPlayerEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);

        mMusicRecyclerView = view
                .findViewById(R.id.music_recycler_view);
        mMusicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMusicRecyclerView.setAdapter(mAdapter);

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        updateUI();
//    }
//
//    private void updateUI() {
//        MusicLab musicLab = MusicLab.get(getActivity());
//        List<Music> musics = musicLab.getMusics();
//
//        if (mAdapter == null) {
//            mAdapter = new MusicAdapter(getActivity(), musics);
//            mMusicRecyclerView.setAdapter(mAdapter);
//        } else {
//            mAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayService.get().removeOnPlayEventListener(mOnPlayerEventListener);
    }

    private static class MusicHolder extends RecyclerView.ViewHolder {
        private View vPlaying;
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvArtist;
        private ImageView ivMore;
        private View vDivider;

        private Music mMusic;

        public MusicHolder(View itemView) {
            super(itemView);

            vPlaying = itemView.findViewById(R.id.v_playing);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            ivMore = itemView.findViewById(R.id.iv_more);
            vDivider = itemView.findViewById(R.id.v_divider);
        }

        public void bindMusic(Music music) {
            mMusic = music;
            tvTitle.setText(mMusic.getTitle());
            tvArtist.setText(mMusic.getArtist());
        }
    }

    public static class MusicAdapter extends RecyclerView.Adapter<MusicHolder> {
        private Context mContext;
        private List<Music> mMusics;
        private OnItemClickListener mItemClickListener;
        private OnMoreClickListener mMoreClickListener;

        public MusicAdapter(Context context, List<Music> musics) {
            mContext = context;
            mMusics = musics;
        }

        @Override
        public MusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.view_holder_music, parent, false);
            return new MusicHolder(view);
        }

        @Override
        public void onBindViewHolder(MusicHolder holder, int position) {
            final Music music = mMusics.get(position);
            holder.bindMusic(music);
            holder.vPlaying.setVisibility(music.equals(PlayService.get().getPlayMusic()) ? View.VISIBLE : View.INVISIBLE);
            holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(music);
                }
            });
            holder.ivMore.setOnClickListener(v -> {
                if (mMoreClickListener != null) {
                    mMoreClickListener.onMoreClick(position);
                }
            });
        }

        private boolean isShowDivider(int position) {
            return position != mMusics.size() - 1;
        }

        @Override
        public int getItemCount() {
            return mMusics.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mItemClickListener = onItemClickListener;
        }

        public void setOnMoreClickListener(OnMoreClickListener listener) {
            this.mMoreClickListener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(Music music);
        }

        public interface OnMoreClickListener {
            void onMoreClick(int position);
        }
    }
}
