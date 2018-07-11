package com.wright.android.t_minus.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import com.wright.android.t_minus.R;

import java.util.ArrayList;
import java.util.List;

public class ArOverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ArPoint> arPoints;
    private Bitmap scaledBitmap;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] cameraCoordinateVector = new float[4];

    public ArOverlayView(Context context) {
        super(context);
    }

    public ArOverlayView(Context context, ArrayList<ArPoint> _arPoints) {
        super(context);
        this.context = context;
        Bitmap largeBitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.rocket_default_image);
        scaledBitmap = Bitmap.createScaledBitmap(largeBitmap, 200, 200, false);
        arPoints = _arPoints;
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentLocation == null) {
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

//                canvas.drawCircle(x, y, radius, paint);
                canvas.drawBitmap(scaledBitmap, x, y, paint);
                canvas.drawText(arPoints.get(i).getName(), x - (15 * arPoints.get(i).getName().length() / 2), y - 40, paint);
            }
        }
    }
}
