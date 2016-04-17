package com.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Author: Mario Velasco Casquero
 * Date: 17/04/2016
 * Email: m3ario@gmail.com
 */
public class Procesador {
    Mat Gx;
    Mat Gy;
    Mat Gx2;
    Mat Gy2;
    Mat ModGrad2;
    Mat ModGrad;
    Mat ModAng;
    Mat red, green, blue, maxGB;

    public Procesador() { //Constructor
        Gx = new Mat();
        Gy = new Mat();
        Gx2 = new Mat();
        Gy2 = new Mat();
        ModGrad = new Mat();
        ModGrad2 = new Mat();
        ModAng = new Mat();
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxGB = new Mat();
    }

    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        Mat entradaRoja = new Mat();
        Core.extractChannel(entrada, red, 0);
        Core.extractChannel(entrada, green, 1);
        Core.extractChannel(entrada, blue, 2);

        Core.max(green, blue, maxGB);
        Core.subtract(red, maxGB, entradaRoja);
        //if(entradaRoja.channels() == 1)
        //    Imgproc.cvtColor(entradaRoja, entradaRoja, Imgproc.COLOR_GRAY2RGBA);

        Imgproc.Sobel(entradaRoja, Gx, CvType.CV_32FC1 , 1, 0); //Derivada primera rto x
        Imgproc.Sobel(entradaRoja, Gy, CvType.CV_32FC1, 0, 1); //Derivada primera rto y
        Core.multiply(Gx, Gx, Gx2); //Gx2 = Gx*Gx elemento a elemento
        Core.multiply(Gy, Gy , Gy2); //Gy2 = Gy*Gy elemento a elemento
        ModGrad = new Mat();
        Core.add(Gx2, Gy2, ModGrad2);
        ModGrad = new Mat();
        Core.sqrt(ModGrad2, ModGrad);
        Core.cartToPolar(Gx, Gy, ModGrad, ModAng);
        ModGrad.convertTo(salida, CvType.CV_8UC1);
        return salida;
    }
}
