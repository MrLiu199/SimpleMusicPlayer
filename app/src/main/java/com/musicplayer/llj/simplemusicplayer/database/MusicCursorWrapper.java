package com.musicplayer.llj.simplemusicplayer.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.musicplayer.llj.simplemusicplayer.database.MusicDbSchema.MusicTable;
import com.musicplayer.llj.simplemusicplayer.model.Music;

public class MusicCursorWrapper extends CursorWrapper {
    public MusicCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Music getMusic() {
        long id = getLong(getColumnIndex(MusicTable.Cols.ID));
        int type = getInt(getColumnIndex(MusicTable.Cols.TYPE));
        long songId = getLong(getColumnIndex(MusicTable.Cols.SONG_ID));
        String title = getString(getColumnIndex(MusicTable.Cols.TITLE));
        String artist = getString(getColumnIndex(MusicTable.Cols.ARTIST));
        String album = getString(getColumnIndex(MusicTable.Cols.ALBUM));
        long albumId = getLong(getColumnIndex(MusicTable.Cols.ALBUM_ID));
        String coverPath = getString(getColumnIndex(MusicTable.Cols.COVER_PATH));
        long duration = getLong(getColumnIndex(MusicTable.Cols.DURATION));
        String path = getString(getColumnIndex(MusicTable.Cols.PATH));
        String fileName = getString(getColumnIndex(MusicTable.Cols.FILE_NAME));
        long fileSize = getLong(getColumnIndex(MusicTable.Cols.FILE_SIZE));

        Music music = new Music(id, type, songId, title, artist,
                album, albumId, coverPath, duration,
                path, fileName, fileSize);

        return music;
    }
}