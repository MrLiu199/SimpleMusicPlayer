package com.musicplayer.llj.simplemusicplayer.database;

public class MusicDbSchema {
    public static final class MusicTable {
        public static final String NAME = "PlayMusics";

        public static final class Cols {
            public static final String ID = "id";
            public static final String TYPE = "type";
            public static final String SONG_ID = "songId";
            public static final String TITLE = "title";
            public static final String ARTIST = "artist";
            public static final String ALBUM = "album";
            public static final String ALBUM_ID = "albumId";
            public static final String COVER_PATH = "coverPath";
            public static final String DURATION = "duration";
            public static final String PATH = "path";
            public static final String FILE_NAME = "fileName";
            public static final String FILE_SIZE = "fileSize";
        }
    }
}