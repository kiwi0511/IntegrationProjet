package be.ti.groupe2.projetintegration;

import android.content.Context;
import android.content.res.Resources;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import android.os.SystemClock;

import android.util.AttributeSet;

import android.view.View;


public class Compa extends View {

    private float etapeOrientation = 0;

    private Paint circlePaint;
    private Paint EtapePaint;
    private Paint southPaint;

    private Path trianglePath;

    private final int DELAY = 20;
    private final int DURATION = 1000;

    private float startEtapeOrientation;
    private float endEtapeOrientation;

    private long startTime;

    private float perCent;
    private long curTime;
    private long totalTime;

    private Runnable animationTask = new Runnable() {
        public void run() {
            curTime   = SystemClock.uptimeMillis();
            totalTime = curTime - startTime;

            if (totalTime > DURATION) {
                etapeOrientation = endEtapeOrientation % 360;
                removeCallbacks(animationTask);
            } else {
                perCent = ((float) totalTime) / DURATION;

                perCent          = (float) Math.sin(perCent * 1.5);
                perCent          = Math.min(perCent, 1);
                etapeOrientation = (float) (startEtapeOrientation + perCent * (endEtapeOrientation - startEtapeOrientation));
                postDelayed(this, DELAY);
            }

            invalidate();
        }
    };

    public Compa(Context context) {
        super(context);
        initView();
    }

    // Constructeur utilis� pour instancier la vue depuis sa
    // d�claration dans un fichier XML
    public Compa(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    // idem au pr�c�dant
    public Compa(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    //~--- get methods --------------------------------------------------------

    // permet de r�cup�rer l'orientation de la boussole
    public float getEtapeOrientation() {
        return etapeOrientation;
    }

    //~--- set methods --------------------------------------------------------

    // permet de changer l'orientation de la boussole
    public void setEtapeOrientation(float rotation) {

        // on met � jour l'orientation uniquement si elle a chang�
        if (rotation != this.etapeOrientation) {
            //Arr�ter l'ancienne animation
            removeCallbacks(animationTask);

            //Position courante
            this.startEtapeOrientation = this.etapeOrientation;
            //Position d�sir�e
            this.endEtapeOrientation   = rotation;

            //D�termination du sens de rotation de l'aiguille
            if ( ((startEtapeOrientation + 180) % 360) > endEtapeOrientation)
            {
                //Rotation vers la gauche
                if ( (startEtapeOrientation - endEtapeOrientation) > 180 )
                {
                    endEtapeOrientation+=360;
                }
            } else {
                //Rotation vers la droite
                if ( (endEtapeOrientation - startEtapeOrientation) > 180 )
                {
                    startEtapeOrientation+=360;
                }
            }

            //Nouvelle animation
            startTime = SystemClock.uptimeMillis();
            postDelayed(animationTask, DELAY);
        }
    }

    //~--- methods ------------------------------------------------------------

    // Initialisation de la vue
    private void initView() {
        Resources r = this.getResources();

        // Paint pour l'arri�re plan de la boussole
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.compassCircle));

        // Paint pour les 2 aiguilles, Nord et Sud
        EtapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        EtapePaint.setColor(r.getColor(R.color.northPointer));
        southPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        southPaint.setColor(r.getColor(R.color.southPointer));

        // Path pour dessiner les aiguilles
        trianglePath = new Path();
    }

    // Permet de d�finir la taille de notre vue
    // /!\ par d�faut un cadre de 100x100 si non red�fini
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth  = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        // Notre vue sera un carr�, on garde donc le minimum
        int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(d, d);
    }

    // D�terminer la taille de notre vue
    private int measure(int measureSpec) {
        int result   = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {

            // Taille par d�faut
            result = 200;
        } else {

            // On va prendre la taille de la vue parente
            result = specSize;
        }

        return result;
    }

    // Appel�e pour redessiner la vue
    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        // On d�termine le diam�tre du cercle (arri�re plan de la boussole)
        int radius = Math.min(centerX, centerY);

        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // On sauvegarde la position initiale du canvas
        canvas.save();

        // On tourne le canvas pour que le nord pointe vers le haut
        canvas.rotate(-etapeOrientation, centerX, centerY);

        // on cr�er une forme triangulaire qui part du centre du cercle et
        // pointe vers le haut
        trianglePath.reset();    // RAZ du path (une seule instance)
        trianglePath.moveTo(centerX, 10);
        trianglePath.lineTo(centerX - 10, centerY);
        trianglePath.lineTo(centerX + 10, centerY);

        // On d�signe l'aiguille Nord
        canvas.drawPath(trianglePath, EtapePaint);

        // On tourne notre vue de 180� pour d�signer l'auguille Sud
        canvas.rotate(180, centerX, centerY);
        canvas.drawPath(trianglePath, southPaint);

        // On restaure la position initiale (inutile, mais pr�voyant)
        canvas.restore();
    }
}
