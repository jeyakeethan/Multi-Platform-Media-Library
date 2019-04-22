import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


public class PDF  extends Item{
    private JButton pdfGui;
    private String date;
    private String path;
    private String author;
    private int id;
    private MPML_GUI mpml_gui;

    PDF(String name, String author, String date, int size, int id, String path, MPML_GUI mpml_gui) {
        super.setName(name);
        this.setAuthor(author);
        this.setDate(date);
        super.setSize(size);
        this.setId(id);
        this.setPath(path);
        this.mpml_gui = mpml_gui;
    }

    public JButton getGUIPDF() {
        if (pdfGui == null) {
            this.pdfGui = new JButton();
            JButton deleteButton = new JButton();
            String name = getName();
            if (name.length() > 25)
                name = name.substring(0, 22) + "...";
            JLabel gName = new JLabel(name);
            JLabel gAuthor = new JLabel(getAuthor());
            JLabel gDate = new JLabel(getDate());
            JLabel gSize = new JLabel(Integer.toString(getSize()));
            GridLayout pdfLayout = new GridLayout(0, 7, 10, 10);
            this.pdfGui.setLayout(new BorderLayout());
            JPanel box = new JPanel();
            box.setLayout(pdfLayout);
            box.setOpaque(false);
            this.pdfGui.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            JLabel gCover = new JLabel();
            Border border = gCover.getBorder();
            Border margin = new EmptyBorder(0,0, 0,15);
            gCover.setBorder(new CompoundBorder(border, margin));
            try {
                Image image = ImageIO.read(getClass().getResource("assets/docIcon.png"));
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
                    deletePDF();
                }
            });

            pdfGui.add(BorderLayout.WEST,gCover);
            box.add(gName);
            box.add(gAuthor);
            box.add(gDate);
            box.add(gSize);
            pdfGui.add(BorderLayout.CENTER,box);
            if (getPath() != null) {
                File pdfFile = new File(this.getPath());
                pdfGui.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop dt = Desktop.getDesktop();
                            dt.open(pdfFile);
                        } catch (Exception x) {
                            System.out.println(x.toString());
                            JOptionPane.showMessageDialog(null, x.getCause().toString());
                        }
                    }
                });
            }
            box.add(deleteButton);
        }
        return pdfGui;
    }


    public static JButton getHeader(MPML_GUI mainApp) {
        JButton sortName, sortDate, sortAuthor, sortSize;
        JButton header = new JButton();
        header.setEnabled(false);
        GridLayout boxLayout  = new GridLayout(0, 7, 10, 10);
        JPanel box = new JPanel();
        box.setLayout(boxLayout);
        box.setOpaque(false);
        header.setLayout(new BorderLayout());
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        sortName = new JButton("Name");
        sortDate = new JButton("Date");
        sortAuthor = new JButton("Author");
        sortSize = new JButton("Size");
        JLabel gCover = new JLabel();
        Border border = gCover.getBorder();
        Border margin = new EmptyBorder(0,0, 0,15);
        gCover.setBorder(new CompoundBorder(border, margin));
        try {
            Image image = ImageIO.read(PDF.class.getResource("assets/docIcon.png"));
            ImageIcon imageIcon = new ImageIcon(image);
            gCover.setIcon(imageIcon);
        } catch (Exception x) {
            System.out.print(x.toString());
        }
        header.add(BorderLayout.WEST,gCover);
        header.add(BorderLayout.CENTER,box);
        box.add(sortName);
        box.add(sortAuthor);
        box.add(sortDate);
        box.add(sortSize);
        sortName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortPDFs("name");
            }
        });
        sortDate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortPDFs("date");
            }
        });
        sortAuthor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortPDFs("author");
            }
        });
        sortSize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.sortPDFs("size");
            }
        });
        return header;
    }

    public void deletePDF() {
        try {
            mpml_gui.deletePDF(this);
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
