package code.playlist;

import database.DB;

import java.util.List;

public class PlaylistManager {

    public static void createPlaylist(String name, List<Integer> videos){

        DB.selectSQL("SELECT count(fldPlaylistID) FROM tblPlaylist");
        int ID = Integer.parseInt(DB.getData());

        DB.insertSQL("INSERT INTO tblPlaylist (fldName) VALUES (" + name + ")");

        for(int videoID : videos){
            DB.insertSQL("INSERT INTO tblMapping (fldPlaylistID, fldVideoID) VALUES (" + ID + ", " + videoID + ")");
        }
    }

    public static void deletePlaylist(int ID){

        DB.deleteSQL("DELETE FROM tblPlaylist WHERE fldPlaylistID=" + ID);
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + ID);

    }

    public static void addVideoToPlaylist(int playlistID, int videoID){

        DB.insertSQL("INSERT INTO tblMapping (fldPlaylistID, fldVideoID) VALUES (" + playlistID + ", " + videoID + ")");

    }

    public static void removeVideoFromPlaylist(int playlistID, int videoID){

        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + playlistID + " AND fldVideoID=" + videoID);

    }

}
