package backend;


import org.opencv.core.*;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.opencv.dnn.Dnn;

public class backend {
    public Mat originalImg;
    public Mat processedImg;
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public void readImage(String path){

    }
    public void saveImage(String path) {

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
    public Mat detectEdges(Boolean isOrignal){
        throw new NotImplementedException();
    }

    public Mat blurImage(Boolean isOrignal){
        throw new NotImplementedException();
    }
    public Mat sharpenImage(Boolean isOrignal){
        throw new NotImplementedException();
    }
    public Mat equalizeImage(Boolean isOrignal){
        throw new NotImplementedException();
    }
    public Mat addLighting(Boolean isOrignal) {
        throw new NotImplementedException();
    }

}
