package backend;

public class Main {
    public static void main(String[] args){
        backend b = new backend();
        b.readImage("C:\\Users\\manar\\Desktop\\q.jpg");

        b.detectEdges(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","a.jbg");

        b.equalizeImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","b.jbg");

        b.sharpenImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","c.jbg");

        b.blurImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","d.jbg");

        b.lightenImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","e.jbg");

        b.gammaCorrection(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","f.jbg");



    }
}
