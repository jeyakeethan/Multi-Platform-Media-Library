import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.util.PDFTextStripper;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieBox;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class MPML_GUI {
    String[] columnNames = {"First Name", "Last Name"};
    Object[][] data = {{"Kathy", "Smith"},{"John", "Doe"}};
    static User user;
    private JPanel mainPanel;
    private JButton musicButton;
    private JButton videoButton;
    private JButton pdfButton;
    private JButton settingButton;
    private JPanel container;
    private JPanel buttons;
    private JPanel settingPanel;
    private JPanel window;
    private JScrollPane sContainer;
    private JScrollPane vContainer;
    private JScrollPane pContainer;
    private JPanel songPanel;
    private JPanel videoListPanel;
    private JPanel pdfPanel;
    private JPanel videoPanel;
    private JTextField songDir;
    private JTextField videoDir;
    private JTextField pdfDir;
    private JCheckBox uploadToServerCheckBox;
    private JButton saveConfigurationsButton;
    private JTextField username;
    private JTextField password;
    private JButton loginButton;
    private JButton logoutButton;
    private MPML app;
    private CardLayout containerLayout;
    protected ServerConnection dbConnection;
    private ArrayList<Song> songList;
    private ArrayList<Video> videoList;
    private ArrayList<PDF> pdfList;
    private boolean [] sortSongsToggle={false, false,false,false,false};
    private boolean [] sortPDFsToggle={false, false,false,false};
    private boolean [] sortVideosToggle={false, false,false,false};

    private String songDirPath, videoDirPath, pdfDirPath;
    Boolean SyncToServer;
    MPML_GUI(MPML app){
        super();
        dbConnection = new ServerConnection();

        songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.Y_AXIS));
        videoPanel.setLayout(new BoxLayout(videoPanel, BoxLayout.Y_AXIS));
        pdfPanel.setLayout(new BoxLayout(pdfPanel, BoxLayout.Y_AXIS));
        this.app = app;
        containerLayout = (CardLayout) window.getLayout();
        JFrame frame = new JFrame("Media Library");
        frame.setPreferredSize(new Dimension(900,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this.mainPanel);
        int width = window.getWidth();


        songList = new ArrayList<>();
        videoList = new ArrayList<>();
        pdfList = new ArrayList<>();

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

        pdfButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                containerLayout.show(window, "pContainer");
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
                String PDFDirPath = pdfDir.getText();
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
                pdfDir.setText(items[2]);
                uploadToServerCheckBox.setSelected(Boolean.parseBoolean(items[3]));
                this.songDirPath = items[0];
                this.videoDirPath = items[1];
                this.pdfDirPath = items[2];
                this.SyncToServer = Boolean.parseBoolean(items[3]);
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
        getSongPanelReady();
        getVideoPanelReady();
        getPDFPanelReady();

        frame.pack();
        frame.setVisible(true);
    }
    private void getSongPanelReady(){
        songPanel.add(Song.getHeader(this));
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
    }

    private void getVideoPanelReady(){
        this.videoPanel.setLayout(new BorderLayout());
        videoListPanel = new JPanel();
        videoListPanel.setLayout(new FlowLayout());
        videoListPanel.setBackground(new Color(206,184,254));
        videoListPanel.setMaximumSize(new Dimension(200,200));
        videoPanel.add(Video.getHeader(this),BorderLayout.NORTH);
        videoPanel.add(videoListPanel,BorderLayout.CENTER);
        if(this.videoDirPath!=null){
            ArrayList<Video> videos = this.loadVideos(this.videoDirPath);
            videoList.addAll(videos);
            videoList.sort(new Comparator<Video>() {
                @Override
                public int compare(Video o1, Video o2) {
                    Comparable comp1 = o1.getName();
                    Comparable comp2 = o2.getName();
                    if (true) {
                        return comp1.compareTo(comp2);
                    } else {
                        return comp2.compareTo(comp1);
                    }
                }
            });
            for(Video video: videoList){
                videoListPanel.add(video.getGUIVideo());
            }
        }
    }


    private void getPDFPanelReady(){
        pdfPanel.add(PDF.getHeader(this));
        if(this.pdfDirPath!=null){
            ArrayList<PDF> pdfs = this.loadPDFs(this.pdfDirPath);
            pdfList.addAll(pdfs);
            pdfList.sort(new Comparator<PDF>() {
                @Override
                public int compare(PDF o1, PDF o2) {
                    Comparable comp1 = o1.getName();
                    Comparable comp2 = o2.getName();
                    if (true) {
                        return comp1.compareTo(comp2);
                    } else {
                        return comp2.compareTo(comp1);
                    }
                }
            });
            for(PDF pdf: pdfList){
                pdfPanel.add(pdf.getGUIPDF());
            }
        }
    }

    private int login(String username, String password)throws Exception{
        return this.dbConnection.login(username, password);
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
    public void sortSongs(String tag){
        switch(tag){
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

    public void sortPDFs(String tag){
        switch(tag){
            case "name":
                pdfList.sort(new Comparator<PDF>() {
                    @Override
                    public int compare(PDF o1, PDF o2) {
                        Comparable comp1 = o1.getName().toLowerCase();
                        Comparable comp2 = o2.getName().toLowerCase();
                        boolean toggle = sortPDFsToggle[0];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortPDFsToggle[0]=!sortPDFsToggle[0];
                break;
            case "author":
                pdfList.sort(new Comparator<PDF>() {
                    @Override
                    public int compare(PDF o1, PDF o2) {
                        Comparable comp1 = o1.getAuthor();
                        Comparable comp2 = o2.getAuthor();
                        boolean toggle = sortPDFsToggle[1];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortPDFsToggle[1]=!sortPDFsToggle[1];
                break;
            case "date":
                pdfList.sort(new Comparator<PDF>() {
                    @Override
                    public int compare(PDF o1, PDF o2) {
                        Comparable comp1 = o1.getDate();
                        Comparable comp2 = o2.getDate();
                        boolean toggle = sortPDFsToggle[2];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortPDFsToggle[2]=!sortPDFsToggle[2];
                break;
            case "size":
                pdfList.sort(new Comparator<PDF>() {
                    @Override
                    public int compare(PDF o1, PDF o2) {
                        Comparable comp1 = o1.getSize();
                        Comparable comp2 = o2.getSize();
                        boolean toggle = sortPDFsToggle[3];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortPDFsToggle[3]=!sortPDFsToggle[3];
                break;
        }
        this.pdfPanel.removeAll();
        pdfPanel.add(PDF.getHeader(this));
        for(PDF pdf: pdfList){
            this.pdfPanel.add(pdf.getGUIPDF());
        }
        pContainer.revalidate();
        pContainer.repaint();
    }

    public void sortVideos(String tag){
        switch(tag){
            case "name":
                videoList.sort(new Comparator<Video>() {
                    @Override
                    public int compare(Video o1, Video o2) {
                        Comparable comp1 = o1.getName().toLowerCase();
                        Comparable comp2 = o2.getName().toLowerCase();
                        boolean toggle = sortVideosToggle[0];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortVideosToggle[0]=!sortVideosToggle[0];
                break;
            case "date":
                videoList.sort(new Comparator<Video>() {
                    @Override
                    public int compare(Video o1, Video o2) {
                        Comparable comp1 = o1.getDate();
                        Comparable comp2 = o2.getDate();
                        boolean toggle = sortVideosToggle[1];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortVideosToggle[1]=!sortVideosToggle[1];
                break;
            case "duration":
                videoList.sort(new Comparator<Video>() {
                    @Override
                    public int compare(Video o1, Video o2) {
                        Comparable comp1 = o1.getDuration();
                        Comparable comp2 = o2.getDuration();
                        boolean toggle = sortVideosToggle[2];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortVideosToggle[2]=!sortVideosToggle[2];
                break;
            case "size":
                videoList.sort(new Comparator<Video>() {
                    @Override
                    public int compare(Video o1, Video o2) {
                        Comparable comp1 = o1.getSize();
                        Comparable comp2 = o2.getSize();
                        boolean toggle = sortVideosToggle[3];
                        if (toggle) {
                            return comp1.compareTo(comp2);
                        } else {
                            return comp2.compareTo(comp1);
                        }
                    }
                });
                sortVideosToggle[3]=!sortVideosToggle[3];
                break;
        }
        this.videoListPanel.removeAll();
        //videoPanel.add(Video.getHeader(this));
        for(Video video: videoList){
            this.videoListPanel.add(video.getGUIVideo());
        }
        videoPanel.revalidate();
        videoPanel.repaint();
    }


    public void deleteVideo(Video video){
        File videoFile = new File(video.getPath());
        videoFile.delete();
        if(this.user!=null) {
            try {
                this.dbConnection.deleteItem(1, video.getName());
            } catch (Exception x) {
                x.printStackTrace();
            }
            this.videoList.remove(videoList.indexOf(video));
            this.videoPanel.removeAll();
            videoPanel.add(Video.getHeader(this));
            for(Video video1: videoList){
                this.videoPanel.add(video1.getGUIVideo());
            }
            vContainer.revalidate();
            vContainer.repaint();
        }
    }

    public void deletePDF(PDF pdf){
        File pdfFile = new File(pdf.getPath());
        pdfFile.delete();
        if(this.user!=null) {
            try {
                this.dbConnection.deleteItem(2, pdf.getName());
            } catch (Exception x) {
                x.printStackTrace();
            }
            this.pdfList.remove(pdfList.indexOf(pdf));
            this.pdfPanel.removeAll();
            pdfPanel.add(PDF.getHeader(this));
            for(PDF pdf1: pdfList){
                this.pdfPanel.add(pdf1.getGUIPDF());
            }
            pContainer.revalidate();
            pContainer.repaint();
        }
    }

    public ArrayList<Song> loadSongs(String path) {
        ArrayList<Song> songs = new ArrayList<Song>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SongSync.conf"));
            String line =reader.readLine();
            Long recordedLastModified = Long.parseLong(line);
            File dir = new File(this.songDirPath);
            Long realLastModified = dir.lastModified();
            ArrayList<String> songsLastUpdated = new ArrayList<>();
            try {
                BufferedReader readerLastListed = new BufferedReader(new FileReader("LastUpdatedSongs.conf"));

                String line1 = readerLastListed.readLine();

                while (line1.equals("")){
                    songsLastUpdated.add(line1.trim());
                    line1 = readerLastListed.readLine();
                }
            }catch (Exception x){}
            if(!recordedLastModified.equals(realLastModified)){
                System.out.println(realLastModified);
                System.out.println(recordedLastModified);
                FileWriter fileWriter = new FileWriter("SongSync.conf");
                FileWriter lastUpdatedSongsFileWriter = new FileWriter("LastUpdatedSongs.conf");
                fileWriter.write(realLastModified.toString()+"\n");
                FileFilter fileFilter = new WildcardFileFilter("*.MP3", IOCase.INSENSITIVE);
                File folder = new File(path.replace('\\','/'));
                File[] listOfFiles = folder.listFiles(fileFilter);

                for (int i = 0; i < listOfFiles.length; i++) {
                    try {
                        if (listOfFiles[i].isFile()) {
                            MP3File mp3file = new MP3File(listOfFiles[i]);
                            AudioFile audioFile = AudioFileIO.read(listOfFiles[i]);
                            int duration = audioFile.getAudioHeader().getTrackLength();
                            if (mp3file.hasID3v2Tag()) {
                                AbstractID3v2 tag = mp3file.getID3v2Tag();
                                String artist = tag.getLeadArtist();
                                String album = tag.getAlbumTitle();
                                String name = tag.getSongTitle();
                                String songPath = listOfFiles[i].getPath();
                                int size = tag.getSize();
                                if (name.equals("")) name = listOfFiles[i].getName();
                                Song song = new Song(name, album, artist, duration, size, Integer.MAX_VALUE, songPath, this);
                                songs.add(song);
                                fileWriter.write(name+"\n");
                                lastUpdatedSongsFileWriter.write(name+"\n");
                                fileWriter.write(album+"\n");
                                fileWriter.write(artist+"\n");
                                fileWriter.write(songPath+"\n");
                                fileWriter.write(new Integer(duration).toString()+"\n");
                                fileWriter.write(new Integer(size).toString()+"\n");
                            }
                        }
                    } catch (Exception x) {
                        Song song = new Song(listOfFiles[i].getName(), "", "", 0, 0, Integer.MAX_VALUE, listOfFiles[i].getPath(), this);
                        songs.add(song);
                        x.printStackTrace();
                    }
                }
                fileWriter.close();
                lastUpdatedSongsFileWriter.close();
                SyncSong uploadSong = new SyncSong(songs,songsLastUpdated,true, this.SyncToServer);
                uploadSong.start();
            }
            else{
                while(true){
                    try {
                        String name,album,artist,songPath;
                        int duration, size, id;
                        name = reader.readLine();
                        album = reader.readLine();
                        artist = reader.readLine();
                        songPath = reader.readLine();
                        duration = Integer.parseInt(reader.readLine());
                        size = Integer.parseInt(reader.readLine());
                        id = Integer.MAX_VALUE;
                        songs.add(new Song(name,album,artist,duration,size,id,songPath,this));
                    } catch (IOException e) {
                        break;
                    }
                }
                SyncSong uploadSong = new SyncSong(songs,songsLastUpdated,false,this.SyncToServer);
                uploadSong.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songs;
    }

    public ArrayList<Video> loadVideos(String path) {
        ArrayList<Video> videos = new ArrayList<Video>();
        try {
        BufferedReader reader = new BufferedReader(new FileReader("VideoSync.conf"));
        String line = reader.readLine();
        Long recordedLastModified = Long.parseLong(line);
        File dir = new File(this.videoDirPath);
        Long realLastModified = dir.lastModified();
        ArrayList<String> videosLastUpdated = new ArrayList<>();
        try {
        BufferedReader readerLastListed = new BufferedReader(new FileReader("LastUpdatedVideos.conf"));

        String line1 = readerLastListed.readLine();

        while (line1.equals("")){
        videosLastUpdated.add(line1.trim());
        line1 = readerLastListed.readLine();
        }
        }catch (Exception x){}
        if(!recordedLastModified.equals(realLastModified)||recordedLastModified.equals(0)){
        System.out.println(realLastModified);
        System.out.println(recordedLastModified);
        FileWriter fileWriter = new FileWriter("VideoSync.conf");
        FileWriter lastUpdatedVideosFileWriter = new FileWriter("LastUpdatedVideos.conf");
        fileWriter.write(realLastModified.toString()+"\n");
        FileFilter fileFilter = new WildcardFileFilter("*.MP4", IOCase.INSENSITIVE);
        File folder = new File(path.replace('\\','/'));
        File[] listOfFiles = folder.listFiles(fileFilter);

        for (int i = 0; i < listOfFiles.length; i++) {
            try {
                if (listOfFiles[i].isFile()) {
                    File video = listOfFiles[i];
                    FileChannel fc = new FileInputStream(video).getChannel();
                    IsoFile isoFile = new IsoFile(fc);
                    MovieBox moov = isoFile.getMovieBox();
                    System.out.println(moov.getMovieHeaderBox().getDuration());
                    System.out.println(moov.getMovieHeaderBox().getCreationTime());
                    int duration = new Long(moov.getMovieHeaderBox().getDuration()).intValue()/30000;
                    String date = moov.getMovieHeaderBox().getCreationTime().toString();
                    String name = video.getName();
                    String videoPath = video.getPath();
                    int size = new Long(isoFile.getSize()/1024).intValue();
                    if (name.equals("")) name = video.getName();
                    Video videoObj = new Video(name, date, duration, size, Integer.MAX_VALUE, videoPath, this);
                    videos.add(videoObj);
                    fileWriter.write(name+"\n");
                    lastUpdatedVideosFileWriter.write(name+"\n");
                    fileWriter.write(date+"\n");
                    fileWriter.write(videoPath+"\n");
                    fileWriter.write(Integer.toString(duration)+"\n");
                    fileWriter.write(Integer.toString(size)+"\n");
                }
            } catch (Exception x) {
                File video = listOfFiles[i];
                Video videoObj = new Video(video.getName(), "", 0, 0, Integer.MAX_VALUE, video.getPath(), this);
                videos.add(videoObj);
                x.printStackTrace();
            }
        }
        fileWriter.close();
        lastUpdatedVideosFileWriter.close();
        SyncVideo uploadVideo = new SyncVideo(videos,videosLastUpdated,true, this.SyncToServer);
        uploadVideo.start();
        }
        else{
        while(true){
        try {
        String name,album,date,videoPath;
        int duration, size, id;
        name = reader.readLine();
        date = reader.readLine();
        videoPath = reader.readLine();
        duration = Integer.parseInt(reader.readLine());
        size = Integer.parseInt(reader.readLine());
        id = Integer.MAX_VALUE;
        videos.add(new Video(name,date,duration,size,id,videoPath,this));
        } catch (IOException e) {
        break;
        }
        }
        SyncVideo uploadVideo = new SyncVideo(videos,videosLastUpdated,false, SyncToServer);
        uploadVideo.start();
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
        return videos;
    }

    public ArrayList<PDF> loadPDFs(String path) {
        ArrayList<PDF> pdfs = new ArrayList<PDF>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("PDFSync.conf"));
            String line = reader.readLine();
            Long recordedLastModified = Long.parseLong(line);
            File dir = new File(path);
            Long realLastModified = dir.lastModified();
            ArrayList<String> pdfsLastUpdated = new ArrayList<>();
            try {
                BufferedReader readerLastListed = new BufferedReader(new FileReader("LastUpdatedPDFs.conf"));

                String line1 = readerLastListed.readLine();

                while (line1.equals("")){
                    pdfsLastUpdated.add(line1.trim());
                    line1 = readerLastListed.readLine();
                }
            }catch (Exception x){}
            if(!recordedLastModified.equals(realLastModified)||recordedLastModified.equals(0)){
                System.out.println(realLastModified);
                System.out.println(recordedLastModified);
                FileWriter fileWriter = new FileWriter("PDFSync.conf");
                FileWriter lastUpdatedPDFsFileWriter = new FileWriter("LastUpdatedPDFs.conf");
                fileWriter.write(realLastModified.toString()+"\n");
                FileFilter fileFilter = new WildcardFileFilter("*.pdf", IOCase.INSENSITIVE);
                File folder = new File(path.replace('\\','/'));
                File[] listOfFiles = folder.listFiles(fileFilter);

                for (int i = 0; i < listOfFiles.length; i++) {
                    COSDocument cosDoc = null;
                    try {
                        if (listOfFiles[i].isFile()) {
                            File pdf = listOfFiles[i];
                            PDFTextStripper pdfStripper = null;
                            PDDocument pdDoc = null;
                            PDFParser parser = new PDFParser(new FileInputStream(pdf));
                            parser.parse();
                            cosDoc = parser.getDocument();
                            pdfStripper = new PDFTextStripper();
                            pdDoc = new PDDocument(cosDoc);
                            pdfStripper.setStartPage(1);
                            pdfStripper.setEndPage(5);
                            PDDocumentInformation info = pdDoc.getDocumentInformation();
                            PDDocumentCatalog cat = pdDoc.getDocumentCatalog();
                            PDMetadata metadata = cat.getMetadata();
                            String name = info.getTitle();
                            String author = info.getAuthor();
                            String pdfPath = pdf.getPath();
                            DateFormat dateformatter = new SimpleDateFormat("yyyy/MM/dd");
                            String date = dateformatter.format(pdf.lastModified());
                            int size = new Long(pdf.length()).intValue();
                            if (name==null){ name = pdf.getName();}
                            PDF pdfObj = new PDF(name, author,  date,  size, Integer.MAX_VALUE, pdfPath, this);
                            pdfs.add(pdfObj);
                            fileWriter.write(name+"\n");
                            fileWriter.write(author+"\n");
                            lastUpdatedPDFsFileWriter.write(name+"\n");
                            fileWriter.write(date+"\n");
                            fileWriter.write(pdfPath+"\n");
                            fileWriter.write(Integer.toString(size)+"\n");
                            cosDoc.close();
                        }
                    } catch (Exception x) {
                        File pdf = listOfFiles[i];
                        PDF pdfObj = new PDF(pdf.getName(), "","", 0, Integer.MAX_VALUE, pdf.getPath(), this);
                        pdfs.add(pdfObj);
                        x.printStackTrace();
                        if(cosDoc!=null){cosDoc.close();}
                    }
                }
                fileWriter.close();
                lastUpdatedPDFsFileWriter.close();
                SyncPDF uploadPDF = new SyncPDF(pdfs,pdfsLastUpdated,true, this.SyncToServer);
                uploadPDF.start();
            }
            else{
                while(true){
                    try {
                        String name,author,date,pdfPath;
                        int size, id;
                        name = reader.readLine();
                        author = reader.readLine();
                        date = reader.readLine();
                        pdfPath = reader.readLine();
                        size = Integer.parseInt(reader.readLine());
                        id = Integer.MAX_VALUE;
                        pdfs.add(new PDF(name,author, date,size,id,pdfPath,this));
                    } catch (IOException e) {
                        break;
                    }
                }
                SyncPDF uploadPDF = new SyncPDF(pdfs,pdfsLastUpdated,false,this.SyncToServer);
                uploadPDF.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdfs;
    }
}

/*
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
}*/