package backend;

public class Main {
    public static void main(String[] args){
        backend b = new backend();
        b.readImage("C:\\Users\\manar\\Desktop\\q.jpg");

        b.detectEdges(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","a.jpg");

        b.equalizeImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","b.jpg");

        b.sharpenImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","c.jpg");

        b.blurImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","d.jpg");

        b.lightenImage(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","e.jpg");

        b.gammaCorrection(true);
        b.saveImage("C:\\Users\\manar\\Desktop\\","f.jpg");



    }
}
