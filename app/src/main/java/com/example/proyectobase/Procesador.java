package com.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Author: Mario Velasco Casquero
 * Date: 17/04/2016
 * Email: m3ario@gmail.com
 */
public class Procesador {
    Mat red;
    Mat green;
    Mat blue;
    Mat maxGB;

    public Procesador() { //Constructor
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxGB = new Mat();
    }

    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        Core.extractChannel(entrada, red, 0);
        Core.extractChannel(entrada, green, 1);
        Core.extractChannel(entrada, blue, 2);

        Core.max(green, blue, maxGB);
        Core.subtract(red, maxGB, salida);
        if(salida.channels() == 1)
            Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);
        return salida;
    }
}
