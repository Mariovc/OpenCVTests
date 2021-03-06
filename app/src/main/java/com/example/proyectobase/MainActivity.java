package com.example.proyectobase;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2,
        LoaderCallbackInterface {
    private static final String TAG = "MainActivity";
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    private int tipoEntrada = 0;  // 0 -> cámara  1 -> fichero1  2 -> fichero2
    Mat imagenRecurso_;
    boolean recargarRecurso = false;
    private boolean guardarSiguienteImagen = false;

    private CameraBridgeViewBase cameraView;
    private int indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
    private int cam_anchura = 320;// resolucion deseada de la imagen
    private int cam_altura = 240;
    private Procesador procesador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        PermissionManager.checkCameraPermission(this);
        cameraView = (CameraBridgeViewBase) findViewById(R.id.vista_camara);
        cameraView.setCvCameraViewListener(this);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current camera index.
        savedInstanceState.putInt(STATE_CAMERA_INDEX, indiceCamara);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            indiceCamara = savedInstanceState.getInt(
                    STATE_CAMERA_INDEX, CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        cameraView.setCameraIndex(indiceCamara);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
    }

//Interface CvCameraViewListener2

    public void onCameraViewStarted(int width, int height) {
        procesador = new Procesador();
        cam_altura = height; //Estas son las que se usan de verdad
        cam_anchura = width;
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat entrada;
        if (tipoEntrada == 0) {
            entrada = inputFrame.gray();
        } else {
            if (recargarRecurso == true) {
                imagenRecurso_ = new Mat();
//Poner aqui el nombre de los archivos copiados
                int RECURSOS_FICHEROS[] = {0, R.raw.img1, R.raw.img2};
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        RECURSOS_FICHEROS[tipoEntrada]);
//Convierte el recurso a una Mat de OpenCV
                Utils.bitmapToMat(bitmap, imagenRecurso_);
                recargarRecurso = false;
            }
            entrada = imagenRecurso_;
        }
        Mat salida = procesador.procesa(entrada);

        if (guardarSiguienteImagen) {//Para foto salida debe ser rgba
            takePhoto(entrada, salida);
            guardarSiguienteImagen = false;
        }
        if(tipoEntrada > 0) {
        //Es necesario que el tamaño de la salida coincida con el real de la captura de la camara
            Imgproc.resize(salida, salida, new Size(cam_anchura , cam_altura));
        }
        return salida;
    }

    private void takePhoto(final Mat input, final Mat output) {
        // Determina la ruta para crear los archivos
        final long currentTimeMillis = System.currentTimeMillis();
        final String appName = getString(R.string.app_name);
        final String galleryPath = Environment
                .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString();
        final String albumPath = galleryPath + "/" + appName;
        final String photoPathIn = albumPath + "/In_" + currentTimeMillis
                + ".png";
        final String photoPathOut = albumPath + "/Out_" + currentTimeMillis
                + ".png";
        // Asegurarse que el directorio existe
        File album = new File(albumPath);
        if (!album.isDirectory() && !album.mkdirs()) {
            Log.e(TAG, "Error al crear el directorio " + albumPath);
            return;
        }
        // Intenta crear los archivos
        Mat mBgr = new Mat();
        if (output.channels() == 1)
            Imgproc.cvtColor(output, mBgr, Imgproc.COLOR_GRAY2BGR, 3);
        else
            Imgproc.cvtColor(output, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
        if (!Imgcodecs.imwrite(photoPathOut, mBgr)) {
            Log.e(TAG, "Fallo al guardar " + photoPathOut);
        }
        if (input.channels() == 1)
            Imgproc.cvtColor(input, mBgr, Imgproc.COLOR_GRAY2BGR, 3);
        else
            Imgproc.cvtColor(input, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
        if (!Imgcodecs.imwrite(photoPathIn, mBgr))
            Log.e(TAG, "Fallo al guardar " + photoPathIn);
        mBgr.release();
        return;
    }

//Interface LoaderCallbackInterface

    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS:
                Log.i(TAG, "OpenCV se cargo correctamente");
                cameraView.setMaxFrameSize(cam_anchura, cam_altura);
                cameraView.enableView();
                break;
            default:
                Log.e(TAG, "OpenCV no se cargo");
                Toast.makeText(MainActivity.this, "OpenCV no se cargo",
                        Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    public void onPackageInstall(int operation,
                                 InstallCallbackInterface callback) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        openOptionsMenu();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cambiarCamara:
                if (indiceCamara == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    indiceCamara = CameraBridgeViewBase.CAMERA_ID_FRONT;
                } else {
                    indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
                }
                recreate();
                break;
            case R.id.resolucion_800x600:
                cam_anchura = 800;
                cam_altura = 600;
                reiniciarResolucion();
                break;
            case R.id.resolucion_640x480:
                cam_anchura = 640;
                cam_altura = 480;
                reiniciarResolucion();
                break;
            case R.id.resolucion_320x240:
                cam_anchura = 320;
                cam_altura = 240;
                reiniciarResolucion();
                break;
            case R.id.entrada_camara:
                tipoEntrada = 0;
                break;
            case R.id.entrada_fichero1:
                tipoEntrada = 1;
                recargarRecurso = true;
                break;
            case R.id.entrada_fichero2:
                tipoEntrada = 2;
                recargarRecurso = true;
                break;
            case R.id.guardar_imagenes:
                guardarSiguienteImagen = true;
                break;
        }
        String msg = "W=" + Integer.toString(cam_anchura) + " H= " +
                Integer.toString(cam_altura) + " Cam= " +
                Integer.toBinaryString(indiceCamara);
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_LONG).show();
        return true;
    }

    public void reiniciarResolucion() {
        cameraView.disableView();
        cameraView.setMaxFrameSize(cam_anchura, cam_altura);
        cameraView.enableView();
    }
}
