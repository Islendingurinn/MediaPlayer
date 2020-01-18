package domain;

import database.DB;

import java.util.List;

public class Playlist
{
    private int _ID;
    private String _NAME;
    private List<Video> _VIDEOS;

    /**
     * Constructor
     * @param id ID of the Playlist
     * @param name Name of the Playlist
     * @param videos List of videos in the Playlist
     */
    public Playlist(int id, String name, List<Video> videos)
    {
        this._ID = id;
        this._NAME = name;
        this._VIDEOS = videos;

        if (this._ID == -1) this._ID = setupID();
    }

    /**
     * A method to get the ID of the Playlist
     * @return The int ID of the Playlist
     */
    public int getID()
    {
        return _ID;
    }

    /**
     * A method to get the display of the Playlist
     * @return String display for the Playlist
     */
    @Override
    public String toString()
    {
        return _NAME;
    }

    /**
     * A method to get the Videos in the Playlist
     * @return A List of Video objects
     */
    public List<Video> getVideos()
    {
        return _VIDEOS;
    }

    /**
     * A method to get the expected ID of the Playlist
     * according to the DB
     * @return int count of IDs from DB
     */
    private int setupID()
    {
        DB.selectSQL("SELECT TOP 1 fldPlaylistID FROM tblPlaylist ORDER BY fldPlaylistID DESC");

        int id = 0;
        do
        {
            String resultset = DB.getData();

            if (resultset.equals(DB.NOMOREDATA)) break;
            id = Integer.parseInt(resultset);
        }
        while (true);

        return id+1;
    }

    /**
     * Saves the Playlist data into the DB
     */
    public void save()
    {
        DB.insertSQL("INSERT INTO tblPlaylist (fldPlaylistID, fldName) VALUES (" + _ID + ", '" + _NAME + "')");

        for(Video video : _VIDEOS)
        {
            DB.insertSQL("INSERT INTO tblMapping (fldVideoID, fldPlaylistID) VALUES (" + video.getID() + ", " + _ID + ")");
        }
    }

    /**
     * Removes a Video from the Playlist
     * @param video Video to remove
     */
    public void remove(Video video)
    {
        _VIDEOS.remove(video);
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + _ID + " AND fldVideoID=" + video.getID());
    }

    /**
     * Deletes the Playlist data from the DB
     */
    public void delete()
    {
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + _ID);
        DB.deleteSQL("DELETE FROM tblPlaylist WHERE fldPlaylistID=" + _ID);
    }

    /**
     * Adds a Video to the Playlist
     * @param video Video to add
     */
    public void add(Video video)
    {
        _VIDEOS.add(video);
        DB.insertSQL("INSERT INTO tblMapping (fldPlaylistID, fldVideoID) VALUES (" + _ID + ", " + video.getID() + ")");
    }
}
