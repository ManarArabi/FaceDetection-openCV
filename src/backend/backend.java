package backend;

import org.opencv.core.*;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.dnn.Dnn;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;


public class backend {

    private Mat originalImg;
    private Mat processedImg;
    private String originalImg_path;
    private String processedImg_path;
    private double brightness_controller ;
    private double contrast_controller ;
    private int edge_detection_thershold_controller;
    private double gamma_controller; //neutral >>1 , smaller >> brighter , larger >>darker
    private String filterPath;
    private String processedImg_name = "temp.jpg";
    private CascadeClassifier faceDetector;
    private MatOfRect facesLocations;
    /*be carefullll!! with gamma*/
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    
    public backend(){
        // different for each laptop so you must change this according to your folders
        // you must use one in source\\data
        this.filterPath =
                "C:\\OpenCV3.4.1\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface_improved.xml";
        faceDetector = new CascadeClassifier();
        this.brightness_controller = 1 ;
        this.contrast_controller = 50;
        this.edge_detection_thershold_controller = 60;
        this.gamma_controller = 0.8;
        this.facesLocations = new MatOfRect();
        // change to your desired path for output
        this.processedImg_path = "E:\\Projects\\Java\\GP\\test\\";
    }


    public String getOriginalImg_path() {
        return originalImg_path;
    }

    public String getProcessedImg_path() {
        return processedImg_path;
    }

    public Mat getOriginalImg() {
        return originalImg;
    }

    public int getEdge_detection_thershold_controller() {
        return edge_detection_thershold_controller;
    }

    public void setEdge_detection_thershold_controller(int edge_detection_thershold_controller) {
        this.edge_detection_thershold_controller = edge_detection_thershold_controller;
    }

    public double getGamma_controller() {
        return gamma_controller;
    }

    public void setGamma_controller(double gamma_controller) {
        this.gamma_controller = gamma_controller;
    }

    public double getContrast_controller() {
        return contrast_controller;
    }

    public void setContrast_controller(double contrast_controller) {
        this.contrast_controller = contrast_controller;
    }

    public double getBrightness_controller() {
        return brightness_controller;
    }

    public void setBrightness_controller(double brightness_controller) {
        this.brightness_controller = brightness_controller;
    }

    public void readImage(String path){
        //this method read any image and make it the original image
        this.originalImg = Imgcodecs.imread(path);
        this.processedImg= new Mat(this.originalImg.rows(),this.originalImg.cols(),this.originalImg.type());
        this.originalImg_path = path;
    }

    public void saveImage(String path, String name) {
        //this method only save the output image
        try {
            path += "\\" + name;
            Imgcodecs.imwrite(path, this.processedImg);
        }
        catch(RuntimeException e){
            System.out.println("You are trying to save null.\nThere is no image to save!");
        }
    }

    //in each method the return is output image path to gui to display it

    //detect and select faces in a image

    public String detectFaces(){

        //load filter
        if (!faceDetector.load(filterPath)) {
            System.err.println("--(!)Error loading face cascade: " + filterPath);
            System.exit(0);
        }
        System.out.println("The filter is loaded.");

        Mat img = this.originalImg.clone();

        img = detect(img);

        img = selectFaces(img);

        this.processedImg = img ;

        this.saveImage(this.processedImg_path,this.processedImg_name);

        //return output image path to gui to display it
        return this.processedImg_path+this.processedImg_name;
    }

    private Mat detect(Mat img){
        System.out.println("detecting...");

        //detect faces from "img" and save their locations in this image in "faceslocations"
        faceDetector.detectMultiScale(img, facesLocations);
        return img;
    }

    private Mat selectFaces(Mat img){
        // drawing a rectangle on the detected faces
        for(Rect rect: facesLocations.toArray()) {
            Imgproc.rectangle(
                    img,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255,0) // color green
            );
        }
        return img;
    }

    /*******************************************/

    public String detectEdges(){

        Mat temp = this.originalImg.clone() ;

        //canny edge detection
        Mat detectedEdges = new Mat(temp.rows(),temp.cols(),temp.type());

        int lowThresh = this.edge_detection_thershold_controller;
        int RATIO = 3;
        int KERNEL_SIZE = 3;
        //first blur to ignore small and trivial details
        Imgproc.blur(temp, this.processedImg, new Size(3,3));
        //detect
        Imgproc.Canny(temp, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);

        this.processedImg = new Mat(temp.size(), CvType.CV_8UC3, Scalar.all(0));

        temp.copyTo(this.processedImg, detectedEdges);

        this.saveImage(this.processedImg_path,this.processedImg_name);

        return this.processedImg_path+this.processedImg_name;
    }

    /*******************************************/

    public String blurImage(){
        //gaussian can be replaced by median
        Imgproc.GaussianBlur(this.originalImg, this.processedImg,new Size(45,45), 0);
        this.saveImage(this.processedImg_path,this.processedImg_name);
        return this.processedImg_path+this.processedImg_name;
    }

    /*******************************************/

    public String sharpenImage(){

        Mat temp = this.originalImg.clone() ;

        int kernelSize = 3;
        //filter pattern used to sharpen the image (2nd derivation)
        Mat kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F) {
            {
                put(0,0,-1);
                put(0,1,-1);
                put(0,2,-1);

                put(1,0,-1);
                put(1,1, 9);
                put(1,2,-1);

                put(2,0,-1);
                put(2,1,-1);
                put(2,2,-1);
            }
        };
        //apply the filter
        Imgproc.filter2D(temp, this.processedImg, -1, kernel);
        this.saveImage(this.processedImg_path,this.processedImg_name);
        return this.processedImg_path+this.processedImg_name;
    }

    /*******************************************/

    public String equalizeImage(){
        //this method divie the image into the basic 3 channels to equlize each channel the merge them again
        Mat temp = this.originalImg.clone() ;
        Mat equalized = new Mat(this.originalImg.rows(),this.originalImg.cols(),this.originalImg.type()) ;
        // blue , green , red (BGR)
        // the channels order is reversed
        List<Mat> channels = new ArrayList<Mat>(3);

        Core.split(temp,channels);

        //equalization
        Imgproc.equalizeHist( channels.get(0), channels.get(0) );
        Imgproc.equalizeHist( channels.get(1), channels.get(1) );
        Imgproc.equalizeHist( channels.get(2), channels.get(2) );

        //merge
        Core.merge(channels,equalized);

        this.processedImg = equalized.clone();
        this.saveImage(this.processedImg_path,this.processedImg_name);
        return this.processedImg_path+this.processedImg_name;

    }

    /*******************************************/

    public String lightenImage() {

        double alpha = this.brightness_controller , beta = this.contrast_controller ;
        Mat temp = this.originalImg.clone() ;

        temp.convertTo(this.processedImg, -1, alpha, beta);

        this.saveImage(this.processedImg_path,this.processedImg_name);
        return this.processedImg_path+this.processedImg_name;
    }

    /*******************************************/

    public String gammaCorrection() {

        Mat temp = this.originalImg.clone();
        Mat out =  temp.clone();
        double inverse_gamma = 1/this.gamma_controller;

        out = temp.clone();//get address
        temp.convertTo(temp, CvType.CV_64FC3);

        int size = (int) (temp.total() * temp.channels());

        double[] t = new double[size];
        temp.get(0, 0, t);

        for (int i=0 ; i<size ;i++){
            t[i] =  (pow(t[i], inverse_gamma));
        }

        out.put(0,0,t);
        this.processedImg = out;
        this.saveImage(this.processedImg_path,this.processedImg_name);
        return this.processedImg_path+this.processedImg_name;
    }

}
