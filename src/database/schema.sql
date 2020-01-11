CREATE DATABASE MediaPlayer;
GO
use MediaPlayer;

CREATE TABLE tblVideo (
	fldVideoID int NOT NULL IDENTITY(1,1) PRIMARY KEY,
	fldName NCHAR(255) NOT NULL,
	fldPath NCHAR(255) NOT NULL,
	fldCategory int NOT NULL,
)

CREATE TABLE tblPlaylist (
	fldPlayListID int NOT NULL IDENTITY(1,1) PRIMARY KEY,
	fldName NCHAR(255) NOT NULL,
)

CREATE TABLE tblMapping (
	fldMapID int NOT NULL IDENTITY(1,1) PRIMARY KEY,
	fldVideoID int NOT NULL REFERENCES tblVideo(fldVideoID),
	fldPlayListID int NOT NULL REFERENCES tblPlaylist(fldPlayListID),
)
