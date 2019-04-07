import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MPML_GUI {
    String[] columnNames = {"First Name", "Last Name"};
    Object[][] data = {{"Kathy", "Smith"},{"John", "Doe"}};
    static User user;
    private JPanel mainPanel;
    private JButton musicButton;
    private JButton videoButton;
    private JButton PDFButton;
    private JButton settingButton;
    private JPanel container;
    private JPanel buttons;
    private JPanel settingPanel;
    private JPanel window;
    private JScrollPane sContainer;
    private JScrollPane vContainer;
    private JScrollPane PContainer;
    private JPanel songPanel;
    private JPanel PDFPanel;
    private JPanel videoPanel;
    private JTextField songDir;
    private JTextField videoDir;
    private JTextField PDFDir;
    private JCheckBox uploadToServerCheckBox;
    private JButton saveConfigurationsButton;
    private JTextField username;
    private JTextField password;
    private JButton loginButton;
    private JButton logoutButton;
    private MPML app;
    private CardLayout containerLayout;
    protected DBConnection dbConnection;
    private ArrayList<Song> songList;
    private boolean [] sortSongsToggle={false, false,false,false,false};

    private String songDirPath, videoDirPath, PDFDirPath;
    Boolean uploadToServer;
    MPML_GUI(MPML app){
        super();
        dbConnection = new DBConnection();
        songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.Y_AXIS));
        videoPanel.setLayout(new BoxLayout(videoPanel, BoxLayout.Y_AXIS));
        PDFPanel.setLayout(new BoxLayout(PDFPanel, BoxLayout.Y_AXIS));
        this.app = app;
        containerLayout = (CardLayout) window.getLayout();
        JFrame frame = new JFrame("Media Library");
        frame.setPreferredSize(new Dimension(900,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this.mainPanel);
        int width = window.getWidth();
        songPanel.add(Song.getHeader(this));
        songList = new ArrayList<>();
        /*try{
           ResultSet rs = dbConnection.getSongs();
           while (rs.next()){
               Song song = new Song(rs.getString(2), rs.getString(3),rs.getString(4),rs.getInt(5),rs.getInt(6),rs.getInt(1));
               songList.add(song);
           }
        }catch (Exception x){System.out.println(x.toString());}*/
        musicButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                containerLayout.show(window, "sContainer");

            }
        });

        videoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                containerLayout.show(window, "vContainer");

            }
        });

        PDFButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                containerLayout.show(window, "PContainer");

            }
        });

        settingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                containerLayout.show(window,"settingPanel");
            }
        });

        saveConfigurationsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String songDirPath = songDir.getText();
                String videoDirPath = videoDir.getText();
                String PDFDirPath = PDFDir.getText();
                boolean uploadServer = uploadToServerCheckBox.isSelected();
                try{
                FileWriter fileWriter = new FileWriter("config.txt");
                fileWriter.write(songDirPath+","+videoDirPath+","+PDFDirPath+","+Boolean.toString(uploadServer));
                fileWriter.close();
                }catch(Exception x){}
            }
        });
        loginButton.addMouseListener(new MouseAdapter() {
            User user = this.user;
            @Override
            public void mouseClicked(MouseEvent e) {
                String usernameText = username.getText().trim();
                String passwordText = password.getText().trim();
                if(!usernameText.equals("") && !passwordText.equals(""))
                {
                    int success = 0;
                    try {
                        success = login(usernameText, passwordText);
                    }catch (Exception x){}
                    if(success!=0) {
                        user = new User(usernameText, passwordText,success);
                        try {
                            FileWriter fileWriter = new FileWriter("configLogin.txt");
                            fileWriter.write(usernameText + "," + passwordText+","+success);
                            fileWriter.close();
                        } catch (Exception x) {
                        }
                        logoutButton.setVisible(true);
                        loginButton.setVisible(false);
                        JOptionPane.showMessageDialog(null,"Login Success!","Success!",1);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Username or Password missmatch!","Warning!",1);
                    }
                }
            }
        });
        try{
            BufferedReader fileReader = new BufferedReader(new FileReader("config.txt"));
            String line = fileReader.readLine().trim();
            if(line!=""){
                String[] items= line.split(",");
                songDir.setText(items[0]);
                videoDir.setText(items[1]);
                PDFDir.setText(items[2]);
                uploadToServerCheckBox.setSelected(Boolean.parseBoolean(items[3]));
                this.songDirPath = items[0];
                this.videoDirPath = items[1];
                this.PDFDirPath = items[2];
                this.uploadToServer = Boolean.parseBoolean(items[3]);
            }
            fileReader.close();
        }catch(Exception x){}
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("configLogin.txt"));
            String line = fileReader.readLine();
            if (line!=null) {
                String[] items = line.split(",");
                username.setText(items[0]);
                password.setText(items[1]);
                this.user = new User(items[0], items[1],Integer.parseInt(items[2]));
                fileReader.close();
                loginButton.setVisible(false);
                logoutButton.setVisible(true);
            }
            else {
                loginButton.setVisible(true);
                logoutButton.setVisible(false);
            }
        }catch (Exception x){}
        logoutButton.addMouseListener(new MouseAdapter() {
            User user = this.user;
            @Override
            public void mouseClicked(MouseEvent e) {
                user = null;
                username.setText("");
                password.setText("");
                try{
                    FileWriter fileWriter = new FileWriter("configLogin.txt");
                    fileWriter.write("");
                    fileWriter.close();
                }catch(Exception x){}
                loginButton.setVisible(true);
                logoutButton.setVisible(false);
            }
        });
        if(this.songDirPath!=null){
            ArrayList<Song> songs = this.loadSongs(this.songDirPath);
            songList.addAll(songs);
            songList.sort(new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    Comparable comp1 = o1.getName();
                    Comparable comp2 = o2.getName();
                    if (true) {
                        return comp1.compareTo(comp2);
                    } else {
                        return comp2.compareTo(comp1);
                    }
                }
            });
            for(Song song: songList){
                songPanel.add(song.getGUISong());
            }
        }
        frame.pack();
        frame.setVisible(true);
        UploadSong uploadSong = new UploadSong(songList,this.uploadToServer);
        uploadSong.start();
    }
    private int login(String username, String password)throws Exception{
        return this.dbConnection.login(username, password);
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
    public void sort(String mode){
        switch(mode){
            case "name":
                songList.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        Comparable comp1 = o1.getName().toLowerCase();
                        Comparable comp2 = o2.getName().toLowerCase();
                        boolean toggle = sortSongsToggle[0];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortSongsToggle[0]=!sortSongsToggle[0];
            break;
            case "album":
                songList.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        Comparable comp1 = o1.getAlbum();
                        Comparable comp2 = o2.getAlbum();
                        boolean toggle = sortSongsToggle[1];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortSongsToggle[1]=!sortSongsToggle[1];
             break;
            case "artist":
                songList.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        Comparable comp1 = o1.getArtist();
                        Comparable comp2 = o2.getArtist();
                        boolean toggle = sortSongsToggle[2];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortSongsToggle[2]=!sortSongsToggle[2];
            break;
            case "duration":
                songList.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        Comparable comp1 = o1.getDuration();
                        Comparable comp2 = o2.getDuration();
                        boolean toggle = sortSongsToggle[3];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortSongsToggle[3]=!sortSongsToggle[3];
            break;
            case "size":
                songList.sort(new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        Comparable comp1 = o1.getSize();
                        Comparable comp2 = o2.getSize();
                        boolean toggle = sortSongsToggle[4];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortSongsToggle[4]=!sortSongsToggle[4];
            break;
        }
        this.songPanel.removeAll();
        songPanel.add(Song.getHeader(this));
        for(Song song: songList){
            this.songPanel.add(song.getGUISong());
        }
        sContainer.revalidate();
        sContainer.repaint();
    }
    public void deleteSong(Song song){
            File songFile = new File(song.getPath());
            songFile.delete();
            if(this.user!=null) {
            try {
                this.dbConnection.deleteItem(0, song.getName());
            } catch (Exception x) {
                x.printStackTrace();
            }
            this.songList.remove(songList.indexOf((song)));
            this.songPanel.removeAll();
            songPanel.add(Song.getHeader(this));
            for(Song song1: songList){
                this.songPanel.add(song1.getGUISong());
            }
            sContainer.revalidate();
            sContainer.repaint();
        }
    }
    public ArrayList<Song> loadSongs(String path) {
        FileFilter fileFilter = new WildcardFileFilter("*.MP3", IOCase.INSENSITIVE);
        File folder = new File(path.replace('\\','/'));
        File[] listOfFiles = folder.listFiles(fileFilter);
        ArrayList<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < listOfFiles.length; i++) {
            try {
                if (listOfFiles[i].isFile()) {
                    MP3File mp3file = new MP3File(listOfFiles[i]);
                    AudioFile audioFile= AudioFileIO.read(listOfFiles[i]);
                    int duration = audioFile.getAudioHeader().getTrackLength();
                    if (mp3file.hasID3v2Tag()) {
                        AbstractID3v2 tag = mp3file.getID3v2Tag();
                        String artist = tag.getLeadArtist();
                        String album = tag.getAlbumTitle();
                        String name = tag.getSongTitle();
                        int size = tag.getSize();
                        if (name.equals(""))name = listOfFiles[i].getName();
                        Song song = new Song(name,album,artist,duration,size,Integer.MAX_VALUE,listOfFiles[i].getPath(),this);
                        songs.add(song);
                    }
                }
            }catch (Exception x){
                Song song = new Song(listOfFiles[i].getName(),"","",0,0,Integer.MAX_VALUE,listOfFiles[i].getPath(),this);
                songs.add(song);
                x.printStackTrace();
            }
        }
        return songs;
    }
}


class TableModel extends AbstractTableModel {
    private String[] columnNames = {"First Name", "Last Name"};
    private Object[][] data = {{"Kathy", "Smith"},{"John", "Doe"}};

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
}