package backend;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    static backend bk = new backend();

    private static JFrame frame = new JFrame("Face Detection & Editing");

    private static JPanel pathPanel = new JPanel();
    private static JPanel originImagePanel = new JPanel();
    private static JPanel editedImagePanel = new JPanel();
    private static JPanel buttonsPanel = new JPanel();

    private static JLabel imageBrowsingPathLabel = new JLabel("Choose Image");
    private static JTextField imageBrowsingPathField = new JTextField(20);
    private static JButton imageBrowsingPathButton = new JButton("Browse");

    private static JButton faceDetection = new JButton("Face Detection");
    private static JButton blur = new JButton("Blur");
    private static JButton sharpening = new JButton("Sharpening");
    private static JButton lightening = new JButton("Lightening");
    private static JButton gammaCorrection = new JButton("Gamma Correction");
    private static JButton equalization = new JButton("Equalization");
    private static JButton edgeDetection = new JButton("Edge Detection");

    public static void OriginImage(String path) {

        BufferedImage img = null;

        try { img = ImageIO.read(new File(path)); }
        catch (IOException e) { e.printStackTrace(); }

        Image scaledImage = img.getScaledInstance(375, 375, Image.SCALE_SMOOTH);
        originImagePanel.removeAll();
        originImagePanel.add(new JLabel(new ImageIcon(scaledImage)));
        originImagePanel.revalidate();
        originImagePanel.repaint();
        EditedImage(path);
        bk.readImage(path);
    }
    public static void EditedImage(String path) {

        BufferedImage img = null;

        try { img = ImageIO.read(new File(path)); }
        catch (IOException e) { e.printStackTrace(); }

        Image scaledImage = img.getScaledInstance(375, 375, Image.SCALE_SMOOTH);
        editedImagePanel.removeAll();
        editedImagePanel.add(new JLabel(new ImageIcon(scaledImage)));
        editedImagePanel.revalidate();
        editedImagePanel.repaint();
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,500);
        frame.setLocation(450,150);
        frame.setResizable(false);

        OriginImage("..\\Avatar.png");
        EditedImage("..\\Avatar.png");

        pathPanel.add(imageBrowsingPathLabel);
        pathPanel.add(imageBrowsingPathField);
        pathPanel.add(imageBrowsingPathButton);
        imageBrowsingPathField.setEditable(false);

        buttonsPanel.add(faceDetection);
        buttonsPanel.add(blur);
        buttonsPanel.add(sharpening);
        buttonsPanel.add(lightening);
        buttonsPanel.add(gammaCorrection);
        buttonsPanel.add(equalization);
        buttonsPanel.add(edgeDetection);

        imageBrowsingPathButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser imageFile = new JFileChooser();
                imageFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                //file.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","png");
                imageFile.addChoosableFileFilter(filter);
                int result = imageFile.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION) {
                    String imagePath = imageFile.getSelectedFile().getAbsolutePath();
                    imageBrowsingPathField.setText(imagePath);
                    OriginImage(imagePath);
                }
                else if(result == JFileChooser.CANCEL_OPTION)
                    System.out.println("No Image Selected !");
            }
        });

        blur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { EditedImage(bk.blurImage()); }
        });
        sharpening.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditedImage(bk.sharpenImage());
            }
        });
        lightening.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditedImage(bk.lightenImage());
            }
        });
        gammaCorrection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditedImage(bk.gammaCorrection());
            }
        });
        edgeDetection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditedImage(bk.detectEdges());
            }
        });
        equalization.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditedImage(bk.equalizeImage());
            }
        });
        faceDetection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditedImage(bk.detectFaces());
            }
        });

        frame.add(pathPanel , BorderLayout.NORTH);
        frame.add(originImagePanel , BorderLayout.WEST);
        frame.add(editedImagePanel , BorderLayout.EAST);
        frame.add(buttonsPanel , BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
