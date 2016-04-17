package com.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Author: Mario Velasco Casquero
 * Date: 17/04/2016
 * Email: m3ario@gmail.com
 */
public class Procesador {
    Mat paso_bajo;
    public Procesador() {
        paso_bajo= new Mat();
    }

    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s=new Size(filter_size,filter_size);

        Imgproc.blur(entrada, paso_bajo, s);

        // Hacer la resta. Los valores negativos saturan a cero
        Core.subtract(paso_bajo, entrada, salida);

        //Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);

        return salida;
    }
}
