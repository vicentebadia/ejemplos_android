package course.examples.Graphics.CanvasBubble;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class BubbleActivity extends Activity {
	protected static final String TAG = "BubbleActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final RelativeLayout frame = (RelativeLayout) findViewById(R.id.frame);
		final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.b128); // <-- Obtenemos el bitmap de /res
		final BubbleView bubbleView = new BubbleView(getApplicationContext(),bitmap); // <-- Creamos nuestra vista custom que hemos definido abajo

		frame.addView(bubbleView); // <-- Añadimos la vista al RelativeLayout que hemos llamado frame

		new Thread(new Runnable() { // <-- Creamos un Thread al que le pasamos un Runnable
			@Override
			public void run() {
				while (bubbleView.move()) { // <-- Mientras aplicar el metodo move sobre nuestra customview BubbleView devuelva true...
					bubbleView.postInvalidate(); // <-- Llamamos al postInvalidate de la clase View, que fuerza a redibujar la vista (llamada a onDraw)
					try {
						Thread.sleep(1000); // <-- Espera de 1 seg
					} catch (InterruptedException e) {
						Log.i(TAG, "InterruptedException");
					}

				}
			}
		}).start(); // <-- Y lanzamos el Thread
	}

	private class BubbleView extends View { // <-- Clase customizada donde luego cargamos el bmp de la burbuja

		private static final int STEP = 100;
		final private Bitmap mBitmap;
		
		// Objetos de una clase customizada para gestionar el movimiento:
		private Coords mCurrent; // <-- Posición instantanea
		final private Coords mDxDy; // <-- Delta de posición para cada iteración

		final private DisplayMetrics mDisplayMetrics; // <-- Objeto para poder acceder a las dimensiones del dispositivo sobre el que corre la app
		final private int mDisplayWidth; // <-- Ancho de la pantalla
		final private int mDisplayHeight; // <-- Altura de la pantalla
		final private int mBitmapWidthAndHeight, mBitmapWidthAndHeightAdj; // <-- Alto y ancho de bitmap
		final private Paint mPainter = new Paint(); 

		public BubbleView(Context context, Bitmap bitmap) { // Constructor
			super(context);

			mBitmapWidthAndHeight = (int) getResources().getDimension(R.dimen.image_height); // <-- Definimos el tamaño del bitmap (200dp)
			this.mBitmap = Bitmap.createScaledBitmap(bitmap,mBitmapWidthAndHeight, mBitmapWidthAndHeight, false); // <-- Creamos el bitmap con el tamaño definido arriba

			mBitmapWidthAndHeightAdj = mBitmapWidthAndHeight + 20; // <-- Añadimos un pequeño margen de 20dp
			
			// Obtenemos el tamaño de la pantalla
			mDisplayMetrics = new DisplayMetrics();
			BubbleActivity.this.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
			mDisplayWidth = mDisplayMetrics.widthPixels;
			mDisplayHeight = mDisplayMetrics.heightPixels;

			Random r = new Random(); 
			float x = (float) r.nextInt(mDisplayWidth); // <-- Generamos un número aleatorio menor o igual que el ancho de la pantalla
			float y = (float) r.nextInt(mDisplayHeight); // <-- Generamos un número aleatorio menor o igual que el alto de la pantalla
			mCurrent = new Coords(x, y); // <-- Inicializamos el obtejo tipo coordenadas con los números aleatorios que acabamos de generar

			float dy = (float) r.nextInt(mDisplayHeight) / mDisplayHeight; // <-- Desplazamiento en y aleatorio
			dy *= r.nextInt(2) == 1 ? STEP : -1 * STEP; // <-- Dirección del desplazamiento aleatorio
			float dx = (float) r.nextInt(mDisplayWidth) / mDisplayWidth;
			dx *= r.nextInt(2) == 1 ? STEP : -1 * STEP;
			mDxDy = new Coords(dx, dy); // <-- Coordenadas del delta de desplazamiento

			mPainter.setAntiAlias(true); // <-- Para suavizar bordes de la imagen

		}

		@Override
		protected void onDraw(Canvas canvas) {
			Coords tmp = mCurrent.getCoords();
			canvas.drawBitmap(mBitmap, tmp.mX, tmp.mY, mPainter);
		}

		protected boolean move() {
			mCurrent = mCurrent.move(mDxDy);  // <-- Aplica el objeto tipo coordenadas el método move que incrementa el valor actual en mDxDy

			if (mCurrent.mY < 0 - mBitmapWidthAndHeightAdj // <-- Comprobaciones para ver si la burbuja se ha salido de la pantalla
					|| mCurrent.mY > mDisplayHeight + mBitmapWidthAndHeightAdj
					|| mCurrent.mX < 0 - mBitmapWidthAndHeightAdj
					|| mCurrent.mX > mDisplayWidth + mBitmapWidthAndHeightAdj) {
				return false; // <-- Si se ha cumplido cualquier requisito de los de arriba, es que la burbuja se ha salido de la pantalla
			} else {
				return true; // <-- En caso contrario la burbuja sigue siendo visible así que devolvemos true para seguir moviendola
			}
		}
	}
}