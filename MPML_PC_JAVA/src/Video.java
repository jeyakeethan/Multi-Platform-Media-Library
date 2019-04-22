import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


public class Video extends Item{
    private JButton videoGui;
    private String date;
    private String path;
    private int duration;
    private int id;
    private MPML_GUI mpml_gui;

    Video(String name, String date, int duration, int size, int id, String path, MPML_GUI mpml_gui) {
        super.setName(name);
        this.setDate(date);
        this.setDuration(duration);
        super.setSize(size);
        this.setId(id);
        this.setPath(path);
        this.mpml_gui = mpml_gui;
    }

    public JButton getGUIVideo() {
        if (videoGui == null) {
            this.videoGui = new JButton();
            int h = this.getDuration() / 3600;
            int m = (this.getDuration() % 3600) / 60;
            int s = (this.getDuration() % 3600) % 60;
            String strDur = String.format("%s%s:%s", h != 0 ? Integer.toString(h) + ":" : "", m, s);
            JButton deleteButton = new JButton();
            JLabel gName = new JLabel(getName());
            JLabel gDate = new JLabel(getDate());
            JLabel gDuration = new JLabel(strDur);
            JLabel gSize = new JLabel(Integer.toString(getSize()));

            try {
                Image image = ImageIO.read(getClass().getResource("assets/video.png"));
                Image binImage = ImageIO.read(getClass().getResource("assets/bin.png"));
                ImageIcon imageIcon = new ImageIcon(image);
                ImageIcon binImageIcon = new ImageIcon(binImage);
                videoGui.setIcon(imageIcon);
                deleteButton.setIcon(binImageIcon);
            } catch (Exception x) {
                System.out.print(x.toString());
            }
            deleteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    deleteVideo();
                }
            });
            Box box = Box.createVerticalBox();
            this.videoGui.setPreferredSize(new Dimension(200, 200));
            videoGui.setMargin(new Insets(7, 7, 7, 7));
            deleteButton.setOpaque(false);
            deleteButton.setContentAreaFilled(false);
            BorderLayout videoLayout = new BorderLayout();
            this.videoGui.setLayout(videoLayout);
            this.videoGui.add(box, BorderLayout.CENTER);
            this.videoGui.add(deleteButton, BorderLayout.SOUTH);

            box.add(gName);
            box.add(gDate);
            box.add(gDuration);
            box.add(gSize);
            if (getPath() != null) {
                File videoFile = new File(this.getPath());
                videoGui.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop dt = Desktop.getDesktop();
                            dt.open(videoFile);
                        } catch (Exception x) {
                            System.out.println(x.toString());
                            JOptionPane.showMessageDialog(null, x.getCause().toString());
                        }
                    }
                });
            }
        }
        return videoGui;
    }


    public static JButton getHeader(MPML_GUI mainApp) {
        JButton sortName, sortDate, sortArtist, sortDuration, sortSize;
        JButton header = new JButton();
        header.setEnabled(false);
        header.setLayout(new GridLayout(0, 7, 10, 10));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        sortName = new JButton("Name");
        sortDate = new JButton("Date");
        sortDuration = new JButton("Duration");
        sortSize = new JButton("Size");
        JLabel gCover = new JLabel();
        try {
            Image image = ImageIO.read(Video.class.getResource("assets/video.png"));
            ImageIcon imageIcon = new ImageIcon(image);
            gCover.setIcon(imageIcon);
        } catch (Exception x) {
            System.out.print(x.toString());
        }

        header.add(gCover);
        header.add(sortName);
        header.add(sortDate);
        header.add(sortDuration);
        header.add(sortSize);
        sortName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortVideos("name");
            }
        });
        sortDate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortVideos("date");
            }
        });
        sortDuration.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortVideos("duration");
            }
        });
        sortSize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortVideos("size");
            }
        });
        return header;
    }

    public void deleteVideo() {
        try {
            mpml_gui.deleteVideo(this);
        } catch (Exception x) {
        }
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
