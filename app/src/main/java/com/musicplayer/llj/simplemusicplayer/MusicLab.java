package com.musicplayer.llj.simplemusicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.musicplayer.llj.simplemusicplayer.database.MusicBaseHelper;
import com.musicplayer.llj.simplemusicplayer.database.MusicCursorWrapper;
import com.musicplayer.llj.simplemusicplayer.database.MusicDbSchema.MusicTable;
import com.musicplayer.llj.simplemusicplayer.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicLab {
    private static MusicLab sMusicLab;
    private static List<Music> sMusicList;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private MusicLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MusicBaseHelper(mContext)
                .getWritableDatabase();
    }

    public static MusicLab get(Context context) {
        if (sMusicLab == null) {
            sMusicLab = new MusicLab(context);
        }
        return sMusicLab;
    }

    private static ContentValues getContentValues(Music music) {
        ContentValues values = new ContentValues();
        values.put(MusicTable.Cols.ID, music.getId());
        values.put(MusicTable.Cols.TYPE, music.getType());
        values.put(MusicTable.Cols.SONG_ID, music.getSongId());
        values.put(MusicTable.Cols.TITLE, music.getTitle());
        values.put(MusicTable.Cols.ARTIST, music.getArtist());
        values.put(MusicTable.Cols.ALBUM, music.getAlbum());
        values.put(MusicTable.Cols.ALBUM_ID, music.getAlbumId());
        values.put(MusicTable.Cols.COVER_PATH, music.getCoverPath());
        values.put(MusicTable.Cols.DURATION, music.getDuration());
        values.put(MusicTable.Cols.PATH, music.getPath());
        values.put(MusicTable.Cols.FILE_NAME, music.getFileName());
        values.put(MusicTable.Cols.FILE_SIZE, music.getFileSize());

        return values;
    }

    public void addMusic(Music music) {
        if (getMusicBySongId(music.getSongId()) != null) {
            return;
        }
        ContentValues values = getContentValues(music);

        mDatabase.insert(MusicTable.NAME, null, values);
    }

    public void updateMusic(Music music) {
        String uuidString = music.getId().toString();
        ContentValues values = getContentValues(music);

        mDatabase.update(MusicTable.NAME, values,
                MusicTable.Cols.ID + " = ?",
                new String[]{uuidString});
    }

    private MusicCursorWrapper queryMusics(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MusicTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new MusicCursorWrapper(cursor);
    }

    public List<Music> getMusics() {
        if (sMusicList != null) {
            return sMusicList;
        }

        sMusicList = new ArrayList<>();

        MusicCursorWrapper cursor = queryMusics(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sMusicList.add(cursor.getMusic());
            cursor.moveToNext();
        }
        cursor.close();

        return sMusicList;
    }

    public Music getMusicBySongId(long songId) {
        MusicCursorWrapper cursor = queryMusics(
                MusicTable.Cols.SONG_ID + " = ?",
                new String[]{String.valueOf(songId)}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getMusic();
        } finally {
            cursor.close();
        }
    }
}
