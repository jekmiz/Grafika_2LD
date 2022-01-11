package com.example.jekaterinaleitarte2uzd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Defines a custom SurfaceView class which handles the drawing thread
 **/
public class ManaVirsma extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable
{

    /**
     * Holds the surface frame
     */
    private SurfaceHolder holder;

    /**
     * Draw thread
     */
    private Thread drawThread;

    /**
     * True when the surface is ready to draw
     */
    private boolean surfaceReady = false;

    /**
     * Drawing thread flag
     */
    private boolean drawingActive = false;


    /**
     * Time per frame for 60 FPS
     */
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0);

    private static final String LOGTAG = "surface";

    protected Paint globalaisPaint = new Paint();
    protected Vector<PointF> Aplis = new Vector<>();
    protected Path path = new Path();
    int level = 0;

    public ManaVirsma(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setOnTouchListener(this);

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if (width == 0 || height == 0)
        {
            return;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        this.holder = holder;

        if (drawThread != null)
        {
            Log.d(LOGTAG, "draw thread still active..");
            drawingActive = false;
            try
            {
                drawThread.join();
            } catch (InterruptedException e)
            { // do nothing
            }
        }


        surfaceReady = true;
        startDrawThread();

        Log.d(LOGTAG, "Created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Surface is not used anymore - stop the drawing thread
        stopDrawThread();
        // and release the surface
        holder.getSurface().release();

        this.holder = null;
        surfaceReady = false;
        Log.d(LOGTAG, "Destroyed");
    }

    /**
     * Stops the drawing thread
     */
    public void stopDrawThread()
    {
        if (drawThread == null)
        {
            Log.d(LOGTAG, "DrawThread is null");
            return;
        }
        drawingActive = false;
        while (true)
        {
            try
            {
                Log.d(LOGTAG, "Request last frame");
                drawThread.join(5000);
                break;
            } catch (Exception e)
            {
                Log.e(LOGTAG, "Could not join with draw thread");
            }
        }
        drawThread = null;
    }

    /**
     * Creates a new draw thread and starts it.
     */
    public void startDrawThread()
    {
        if (surfaceReady && drawThread == null)
        {
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "Draw thread started");
        long frameStartTime;
        long frameTime;

        /*
         * In order to work reliable on Nexus 7, we place ~500ms delay at the start of drawing thread
         * (AOSP - Issue 58385)
         */
        if (android.os.Build.BRAND.equalsIgnoreCase("google") && android.os.Build.MANUFACTURER.equalsIgnoreCase("asus") && android.os.Build.MODEL.equalsIgnoreCase("Nexus 7"))
        {
            Log.w(LOGTAG, "Sleep 500ms (Device: Asus Nexus 7)");
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException ignored)
            {
            }
        }
        try
        {
            while (drawingActive)
            {
                if (holder == null)
                {
                    return;
                }

                frameStartTime = System.nanoTime();
                Canvas canvas = holder.lockCanvas();
                if (canvas != null)
                {
                    try {
                        synchronized (holder) {
                            // clear the screen using black
                            canvas.drawARGB(255, 0, 0, 0);
                            tick();
                            render(canvas);
                        }
                    } finally {

                        holder.unlockCanvasAndPost(canvas);
                    }
                }

                // calculate the time required to draw the frame in ms
                frameTime = (System.nanoTime() - frameStartTime) / 1000000;

                if (frameTime < MAX_FRAME_TIME) // faster than the max fps - limit the FPS
                {
                    try
                    {
                        Thread.sleep(MAX_FRAME_TIME - frameTime);
                    } catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
            }
        } catch (Exception e)
        {
            Log.w(LOGTAG, "Exception while locking/unlocking");
        }
        Log.d(LOGTAG, "Draw thread finished");
    }



    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        // Šeit tiek apstrādāti pieskāriena notikumi

        float pointX = event.getX();
        float pointY = event.getY();


        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Starts a new line in the path
                path.moveTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                // Draws line between last point and this point
                path.lineTo(event.getX(), event.getY());
                break;
            default:
                return false;
        }


        return true;
    }

    public void tick(){
        // Šeit tiek apstrādāta Spēles loģika

    }

    public void clearScreen(){
        Aplis.clear();

        path.reset();

    }

    public void render(Canvas c){
        //šeit tiek apstrādātas visas zīmēšanas darbības
        globalaisPaint.setStyle(Paint.Style.FILL);
        globalaisPaint.setColor(Color.BLUE);
        c.drawRect(0, 0, getWidth(), getHeight()/3.0f, globalaisPaint);

        globalaisPaint.setColor(0xffff0000);
        c.drawRect(0,getHeight()/2.0f, getWidth(), getHeight(),globalaisPaint);

        globalaisPaint.setStyle(Paint.Style.STROKE);
        globalaisPaint.setStrokeWidth(10);
        for (int i=0;i<Aplis.size();i++){
            c.drawCircle(Aplis.elementAt(i).x, Aplis.elementAt(i).y, 50,globalaisPaint);
        }


        globalaisPaint.setColor(getResources().getColor(R.color.white));
        globalaisPaint.setTextSize(100);
        globalaisPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText("Jekaterina Leitarte", getWidth()/2.0f, 100,globalaisPaint);

        globalaisPaint.setColor(Color.YELLOW);
        globalaisPaint.setStrokeWidth(level+1);
        c.drawPath(path, globalaisPaint);





    }

    public void ChangeBrush(int level){
        this.level = level;
    }
}
