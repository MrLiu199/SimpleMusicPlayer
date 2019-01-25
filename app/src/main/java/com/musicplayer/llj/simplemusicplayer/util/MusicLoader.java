package com.musicplayer.llj.simplemusicplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.musicplayer.llj.simplemusicplayer.model.Music;

import java.util.ArrayList;

public class MusicLoader {
    public static ArrayList<Music> getAllMusics(Context context) {
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = makeMusicCursor(context, null, null, sortOrder);
        return getMusicsForCursor(cursor);
    }

    public static ArrayList<Music> getMusicsForCursor(Cursor cursor) {
        ArrayList<Music> arrayList = new ArrayList<>();
        if ((cursor != null) && (cursor.moveToFirst())) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                int type = Music.Type.LOCAL;

                long songId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
//                String coverPath = cursor.getString(cursor.getColumnIndex(MusicTable.Cols.COVER_PATH));
                String coverPath = null;
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Music music = new Music(id, type, songId, title, artist,
                        album, albumId, coverPath, duration,
                        path, fileName, fileSize);
                arrayList.add(music);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    private static Cursor makeMusicCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "title", "artist", "album", "album_id", "duration", "_data", "_display_name", "_size", "track", "artist_id"},
                selectionStatement, paramArrayOfString, sortOrder);
    }
}
