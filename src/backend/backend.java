package backend;


import org.opencv.core.*;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.opencv.dnn.Dnn;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;


public class backend {

    private Mat originalImg;
    private Mat processedImg;
    private double brightness_controller ;
    private double contrast_controller ;
    private int edge_detection_thershold_controller;
    private double gamma_controller; //neutral >>1 , smaller >> brighter , larger >>darker
    /*be carefullll!! with gamma*/
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public backend(){
        this.brightness_controller = 1 ;
        this.contrast_controller = 50;
        this.edge_detection_thershold_controller = 60;
        this.gamma_controller = 0.8;
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
        this.originalImg = Imgcodecs.imread(path);
        this.processedImg= new Mat(this.originalImg.rows(),this.originalImg.cols(),this.originalImg.type());
    }

    public void saveImage(String path, String name) {
        try {
            path += "\\" + name;
            Imgcodecs.imwrite(path, this.processedImg);
        }
        catch(RuntimeException e){
            System.out.println("You are trying to save null.\nThere is no image to save!");
        }
    }

    public Mat detectFaces(Boolean isOrignal){
        String prototxtPath = "/home/aim/opencv-3.4.1/samples/dnn/face_detector/deploy.prototxt";
        String modelPath = "/home/aim/opencv-3.4.1/samples/dnn/face_detector/res10_300x300_ssd_iter_140000.caffemodel";
        Net net = Dnn.readNetFromCaffe(prototxtPath, modelPath);
        Mat img;
        if(isOrignal){
            img = originalImg.clone();
        }else{
            img = processedImg.clone();
        }
        Size h = img.size();
        Mat resizeimage = new Mat();
        Size sz = new Size(300,300);
        Imgproc.resize( img, resizeimage, sz );
        Mat blob = Dnn.blobFromImage(
                resizeimage,
                1.0,
                new Size(300, 300),
                new Scalar(104.0, 177.0, 123.0),
                false,
                false
        );
        net.setInput(blob);
        Mat detections = net.forward();



        throw new NotImplementedException();
    }

    public Mat detectEdges(Boolean isOriginal){

        Mat temp ;
        if (isOriginal) {
            temp = this.originalImg ;
        }else{
            temp = this.processedImg;
        }
        //canny edge detection

        Mat detectedEdges = new Mat(temp.rows(),temp.cols(),temp.type());
        int lowThresh = this.edge_detection_thershold_controller;
        int RATIO = 3;
        int KERNEL_SIZE = 3;

        Imgproc.blur(temp, this.processedImg, new Size(3,3));

        Imgproc.Canny(temp, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);

        this.processedImg = new Mat(temp.size(), CvType.CV_8UC3, Scalar.all(0));
        temp.copyTo(this.processedImg, detectedEdges);

        return this.processedImg;

    }

    public Mat blurImage(Boolean isOriginal){
        Imgproc.GaussianBlur(this.originalImg, this.processedImg,new Size(45,45), 0);
        return this.processedImg;
    }

    public Mat sharpenImage(Boolean isOriginal){
        Mat temp ;
        if (isOriginal) {
            temp = this.originalImg ;
        }else{
            temp = this.processedImg;
        }
        int kernelSize = 3;
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
        Imgproc.filter2D(temp, this.processedImg, -1, kernel);
        return this.processedImg;
    }

    public Mat equalizeImage(Boolean isOriginal){
        Mat temp ;

        if (isOriginal) {
            temp = this.originalImg ;
        }else{
            temp = this.processedImg;
        }

        // GBR
        List<Mat> channels = new ArrayList<Mat>(3);
        Core.split(temp,channels);

        //equalization
        Imgproc.equalizeHist( channels.get(0), channels.get(0) );
        Imgproc.equalizeHist( channels.get(1), channels.get(1) );
        Imgproc.equalizeHist( channels.get(2), channels.get(2) );

        //merge
        Mat equalized = new Mat(this.originalImg.rows(),this.originalImg.cols(),this.originalImg.type()) ;
        Core.merge(channels,equalized);

        this.processedImg = equalized;
        return  equalized;

    }

    public Mat lightenImage(Boolean isOriginal) {

        double alpha = this.brightness_controller , beta = this.contrast_controller ;
        Mat temp ;
        if (isOriginal) {
            temp = this.originalImg ;
        }else{
            temp = this.processedImg;
        }
        temp.convertTo(this.processedImg, -1, alpha, beta);
        return this.processedImg ;
    }

    public Mat gammaCorrection(Boolean isOriginal) {
        Mat temp, out;
        double inverse_gamma = 1/this.gamma_controller;
        double r,g,b;

        if (isOriginal) {
            temp = this.originalImg ;
        }else{
            temp = this.processedImg;
        }

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
        return this.processedImg;
    }

}
