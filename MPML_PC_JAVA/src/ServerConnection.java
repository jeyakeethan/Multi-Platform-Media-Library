import java.sql.*;
import java.util.ArrayList;

class ServerConnection {
    String host = "jdbc:mysql://37.59.55.185:3306/";//"jdbc:mysql://localhost/";
    String user = "qsrsEnzGc4";//"root";
    String pass = "eMLeNut1SQ";
    String dbName = "qsrsEnzGc4?characterEncoding=latin1";

    ServerConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean deleteItem(int mode, int id) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        Statement stmt = connection.createStatement();
        boolean a = false;
        switch (mode) {
            case 0:
                a = stmt.executeQuery("DELETE from songs where song_id=" + id + "Limit 1;").getBoolean("match_found");
                break;
            case 1:
                a = stmt.executeQuery("DELETE from movies where movie_id=" + id + "Limit 1;").getBoolean("match_found");
                break;
            case 2:
                a = stmt.executeQuery("DELETE from PDFs where PDF_id=" + id + "Limit 1;").getBoolean("match_found");
                break;
        }
        return a;
    }

    public boolean deleteItem(int mode, String name) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        Statement stmt = connection.createStatement();
        PreparedStatement rs = null;
        switch (mode) {
            case 0:
                rs = (PreparedStatement) connection.prepareStatement("DELETE from songs WHERE song_id IN (SELECT * FROM (SELECT song_id from songs NATURAL JOIN user_song NATURAL JOIN users where user_id=" + MPML_GUI.user.getId() + " AND songs.name='" + name + "') AS p);");
                break;
            case 1:
                rs = (PreparedStatement) connection.prepareStatement("DELETE from movies WHERE movie_id IN (SELECT * FROM (SELECT movie_id from movies NATURAL JOIN user_movie NATURAL JOIN users where user_id=" + MPML_GUI.user.getId() + " AND movies.name='" + name + "') AS p);");
                break;
            case 2:
                rs = (PreparedStatement) connection.prepareStatement("DELETE from PDFs WHERE PDF_id IN (SELECT * FROM (SELECT PDF_id from PDFs NATURAL JOIN user_PDF NATURAL JOIN users where user_id=" + MPML_GUI.user.getId() + " AND PDFs.name='" + name + "') AS p);");
                break;
        }
        System.out.println(rs.toString());
        int result = rs.executeUpdate();
        return result == 1;
    }

    public int login(String username, String password) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        PreparedStatement rs = (PreparedStatement) connection.prepareStatement("SELECT * FROM users where username=?  and password=MD5(?);");
        rs.setString(1, username);
        rs.setString(2, password);
        ResultSet pst = rs.executeQuery();
        if (pst.next()) {
            return pst.getInt(1);
        } else {
            return 0;
        }
    }

    public int uploadSong(Song song) throws Exception {
        int id = 0;
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        PreparedStatement rs = (PreparedStatement) connection.prepareStatement("INSERT INTO songs(name,album,artist, duration,size ) VALUES (?,?,?,?,?);");

        rs.setString(1, song.getName());
        rs.setString(2, song.getAlbum());
        rs.setString(3, song.getArtist());
        rs.setInt(4, song.getDuration());
        rs.setInt(5, song.getSize());
        int pst = rs.executeUpdate();
        PreparedStatement rsLastInsertID = (PreparedStatement) connection.prepareStatement("SELECT LAST_INSERT_ID();");
        ResultSet idRs = rsLastInsertID.executeQuery();
        idRs.next();
        id = idRs.getInt(1);
        PreparedStatement user_item = (PreparedStatement) connection.prepareStatement("INSERT INTO user_song(user_id,song_id) VALUES (?,?);");
        user_item.setInt(1, MPML_GUI.user.getId());
        user_item.setInt(2, id);
        int user_item_result = user_item.executeUpdate();
        return id;
    }

    public int uploadVideo(Video video) throws Exception {
        int id = 0;
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        PreparedStatement rs = (PreparedStatement) connection.prepareStatement("INSERT INTO videos(name,date,duration,size ) VALUES (?,?,?,?);");

        rs.setString(1, video.getName());
        rs.setString(2, video.getDate());
        rs.setInt(3, video.getDuration());
        rs.setInt(4, video.getSize());
        int pst = rs.executeUpdate();
        PreparedStatement rsLastInsertID = (PreparedStatement) connection.prepareStatement("SELECT LAST_INSERT_ID();");
        ResultSet idRs = rsLastInsertID.executeQuery();
        idRs.next();
        id = idRs.getInt(1);
        PreparedStatement user_item = (PreparedStatement) connection.prepareStatement("INSERT INTO user_video(user_id,video_id) VALUES (?,?);");
        user_item.setInt(1, MPML_GUI.user.getId());
        user_item.setInt(2, id);
        int user_item_result = user_item.executeUpdate();
        return id;
    }


    public int uploadPDF(PDF pdf) throws Exception {
        int id = 0;
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        PreparedStatement rs = (PreparedStatement) connection.prepareStatement("INSERT INTO pdfs(name,author,date, size ) VALUES (?,?,?,?);");
        rs.setString(1, pdf.getName());
        rs.setString(2, pdf.getAuthor());
        rs.setString(3, pdf.getDate());
        rs.setInt(4, pdf.getSize());
        int pst = rs.executeUpdate();
        PreparedStatement rsLastInsertID = (PreparedStatement) connection.prepareStatement("SELECT LAST_INSERT_ID();");
        ResultSet idRs = rsLastInsertID.executeQuery();
        idRs.next();
        id = idRs.getInt(1);
        PreparedStatement user_item = (PreparedStatement) connection.prepareStatement("INSERT INTO user_pdf(user_id,pdf_id) VALUES (?,?);");
        user_item.setInt(1, MPML_GUI.user.getId());
        user_item.setInt(2, id);
        int user_item_result = user_item.executeUpdate();
        return id;
    }

    public ArrayList<String> getItemNameInServer(int mode) throws Exception {
        ArrayList<String> songs = new ArrayList<>();
        Class.forName("com.mysql.jdbc.Driver");
        String table = "";
        switch (mode) {
            case 0:
                table = "songs";
                break;
            case 1:
                table = "movies";
                break;
            case 2:
                table = "PDFs";
                break;
        }
        Connection connection = DriverManager.getConnection(this.host + this.dbName, this.user, this.pass);
        PreparedStatement rs = (PreparedStatement) connection.prepareStatement("SELECT name FROM " + table + " NATURAL JOIN user_" + table.substring(0, table.length() - 1) + " NATURAL JOIN users WHERE user_id=" + MPML_GUI.user.getId() + ";");
        ResultSet pst = rs.executeQuery();
        while (pst.next()) {
            songs.add(pst.getString(1));
        }
        return songs;
    }

}