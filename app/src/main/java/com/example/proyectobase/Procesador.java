package com.example.proyectobase;

import org.opencv.core.Mat;

/**
 * Author: Mario Velasco Casquero
 * Date: 17/04/2016
 * Email: m3ario@gmail.com
 */
public class Procesador {

    public Procesador() { //Constructor

    }

    public Mat procesa(Mat entrada) {
        Mat salida = entrada.clone();
        return salida;
    }
}
