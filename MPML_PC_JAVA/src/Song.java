import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import org.farng.mp3.*;
import org.farng.mp3.id3.*;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;


public class Song extends Item{
    private JButton songGui;
    private String album;
    private String artist;
    private String path;
    private int duration;
    private int id;
    private MPML_GUI mpml_gui;

    Song(String name, String album, String artist, int duration, int size, int id, String path, MPML_GUI mpml_gui) {
        super.setName(name);
        this.setAlbum(album);
        this.setArtist(artist);
        this.setDuration(duration);
        super.setSize(size);
        this.setId(id);
        this.setPath(path);
        this.mpml_gui = mpml_gui;
    }

    public JButton getGUISong() {
        if (songGui == null) {
            this.songGui = new JButton();
            JButton deleteButton = new JButton();
            JLabel gName = new JLabel(getName());
            JLabel gAlbum = new JLabel(getAlbum());
            JLabel gArtist = new JLabel(getArtist());
            int h = this.getDuration() / 3600;
            int m = (this.getDuration() % 3600) / 60;
            int s = (this.getDuration() % 3600) % 60;
            String strDur = String.format("%s%s:%s", h != 0 ? Integer.toString(h) + ":" : "", m, s);
            JLabel gDuration = new JLabel(strDur);
            JLabel gSize = new JLabel(Integer.toString(getSize()));
            GridLayout songLayout = new GridLayout(0, 7, 10, 10);
            JPanel box = new JPanel();
            box.setLayout(songLayout);
            box.setOpaque(false);
            this.songGui.setLayout(new BorderLayout());
            this.songGui.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            JLabel gCover = new JLabel();
            Border border = gCover.getBorder();
            Border margin = new EmptyBorder(0,0, 0,15);
            gCover.setBorder(new CompoundBorder(border, margin));
            try {
                Image image = ImageIO.read(getClass().getResource("assets/music.png"));
                Image binImage = ImageIO.read(getClass().getResource("assets/bin.png"));
                ImageIcon imageIcon = new ImageIcon(image);
                ImageIcon binImageIcon = new ImageIcon(binImage);
                gCover.setIcon(imageIcon);
                deleteButton.setIcon(binImageIcon);
            } catch (Exception x) {
                System.out.print(x.toString());
            }
            deleteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    deleteSong();
                }
            });

            this.songGui.add(BorderLayout.WEST,gCover);
            this.songGui.add(BorderLayout.CENTER,box);
           box.add(gName);
            box.add(gAlbum);
            box.add(gArtist);
            box.add(gDuration);
            box.add(gSize);
            if (getPath() != null) {
                File songFile = new File(this.getPath());
                songGui.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop dt = Desktop.getDesktop();
                            dt.open(songFile);
                        } catch (Exception x) {
                            System.out.println(x.toString());
                            JOptionPane.showMessageDialog(null, x.getCause().toString());
                        }
                    }
                });
            }
            box.add(deleteButton);
        }
        return songGui;
    }


    public static JButton getHeader(MPML_GUI mainApp) {
        JButton sortName, sortAlbum, sortArtist, sortDuration, sortSize;
        JButton header = new JButton();
        header.setEnabled(false);
        header.setLayout(new BorderLayout());
        GridLayout boxLayout  = new GridLayout(0, 7, 10, 10);
        JPanel box = new JPanel();
        box.setLayout(boxLayout);
        box.setOpaque(false);
        header.setLayout(new BorderLayout());
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        sortName = new JButton("Name");
        sortAlbum = new JButton("Album");
        sortArtist = new JButton("Artist");
        sortDuration = new JButton("Duration");
        sortSize = new JButton("Size");
        JLabel gCover = new JLabel();
        Border border = gCover.getBorder();
        Border margin = new EmptyBorder(0,0, 0,15);
        gCover.setBorder(new CompoundBorder(border, margin));
        try {
            Image image = ImageIO.read(Song.class.getResource("assets/music.png"));
            ImageIcon imageIcon = new ImageIcon(image);
            gCover.setIcon(imageIcon);
        } catch (Exception x) {
            System.out.print(x.toString());
        }

        header.add(BorderLayout.WEST,gCover);
        header.add(BorderLayout.CENTER,box);
        box.add(sortName);
        box.add(sortAlbum);
        box.add(sortArtist);
        box.add(sortDuration);
        box.add(sortSize);
        sortName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortSongs("name");
            }
        });
        sortAlbum.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortSongs("album");
            }
        });
        sortArtist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortSongs("artist");
            }
        });
        sortDuration.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortSongs("duration");
            }
        });
        sortSize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortSongs("size");
            }
        });
        return header;
    }

    public void deleteSong() {
        try {
            mpml_gui.deleteSong(this);
        } catch (Exception x) {
        }
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
