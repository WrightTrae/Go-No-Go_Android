package com.wright.android.t_minus.ar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

public class ArUtils {
  private static final String TAG = "SceneformDemoUtils";
  public static void displayError(
      final Context context, final String errorMsg, @Nullable final Throwable problem) {
    final String tag = context.getClass().getSimpleName();
    final String toastText;
    if (problem != null && problem.getMessage() != null) {
      Log.e(tag, errorMsg, problem);
      toastText = errorMsg + ": " + problem.getMessage();
    } else if (problem != null) {
      Log.e(tag, errorMsg, problem);
      toastText = errorMsg;
    } else {
      Log.e(tag, errorMsg);
      toastText = errorMsg;
    }

    new Handler(Looper.getMainLooper())
        .post(
            () -> {
              Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
            });
  }

  public static Session createArSession(Activity activity, boolean installRequested)
      throws UnavailableException {
    Session session = null;
    // if we have the camera permission, create the session
    if (hasCameraPermission(activity)) {
      switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
        case INSTALL_REQUESTED:
          return null;
        case INSTALLED:
          break;
      }
      session = new Session(activity);
      // IMPORTANT!!!  ArSceneView needs to use the non-blocking update mode.
      Config config = new Config(session);
      config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
      session.configure(config);
    }
    return session;
  }

  /** Check to see we have the necessary permissions for this app, and ask for them if we don't. */
  public static void requestCameraPermission(Activity activity, int requestCode) {
    ActivityCompat.requestPermissions(
        activity, new String[] {Manifest.permission.CAMERA}, requestCode);
  }

  /** Check to see we have the necessary permissions for this app. */
  public static boolean hasCameraPermission(Activity activity) {
    return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED;
  }

  public static void handleSessionException(
      Activity activity, UnavailableException sessionException) {

    String message;
    if (sessionException instanceof UnavailableArcoreNotInstalledException) {
      message = "Please install ARCore";
    } else if (sessionException instanceof UnavailableApkTooOldException) {
      message = "Please update ARCore";
    } else if (sessionException instanceof UnavailableSdkTooOldException) {
      message = "Please update this app";
    } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
      message = "This device does not support AR";
    } else {
      message = "Failed to create AR session";
      Log.e(TAG, "Exception: " + sessionException);
    }
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
  }
}
