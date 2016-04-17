package com.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Author: Mario Velasco Casquero
 * Date: 17/04/2016
 * Email: m3ario@gmail.com
 */
public class Procesador {

    public Procesador() { //Constructor

    }

    public Mat procesa(Mat entrada) {
        double tam = 11;
        Mat SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam, tam));

        Mat gray_dilation = new Mat(); // Result
        Mat gray_erosion = new Mat(); // Result

        Imgproc.dilate(entrada, gray_dilation, SE ); // 3x3 dilation
        Imgproc.erode(entrada, gray_erosion, SE ); // 3x3 erosion
        Mat dilation_residue = new Mat();
        //Mat erosion_residue = new Mat();
        Core.subtract(gray_dilation, entrada, dilation_residue);
        //Core.subtract(entrada, gray_erosion, erosion_residue);
        return dilation_residue;
    }
}
