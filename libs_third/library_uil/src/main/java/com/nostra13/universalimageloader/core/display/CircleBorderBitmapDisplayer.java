/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.display;

import android.graphics.*;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Can display bitmap with rounded corners. This implementation works only with
 * ImageViews wrapped in ImageViewAware. <br />
 * This implementation is inspired by <a href=
 * "http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/"
 * > Romain Guy's article</a>. It rounds images using custom drawable drawing.
 * Original bitmap isn't changed. <br />
 * <br />
 * If this implementation doesn't meet your needs then consider <a
 * href="https://github.com/vinc3m1/RoundedImageView">RoundedImageView</a> or <a
 * href="https://github.com/Pkmmte/CircularImageView">CircularImageView</a>
 * projects for usage.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 * 
 *        modify by wenjun at 2014-09-13
 */
public class CircleBorderBitmapDisplayer implements BitmapDisplayer {

	protected final int margin;
	protected final float border;

	public CircleBorderBitmapDisplayer() {
		this(0, 2);
	}

	public CircleBorderBitmapDisplayer(int marginPixels, float borderPixels) {
		this.margin = marginPixels;
		this.border = borderPixels;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware,
			LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException(
					"ImageAware should wrap ImageView. ImageViewAware is expected.");
		}
		imageAware
				.setImageDrawable(new RoundedDrawable(bitmap, margin, border));
	}

	public static class RoundedDrawable extends Drawable {

		protected final int margin;

		protected final RectF mRect = new RectF(), mBitmapRect;
		protected final RectF mBorderRect = new RectF();
		protected final BitmapShader bitmapShader;
		protected final Paint paint, borderPaint;

		/***
		 * 外边框，0->没有
		 */
		protected float border = 2;
		/***
		 * 外边框颜色
		 */
		protected final int COLOR_BORDER = 0XFFFB6755;

		public RoundedDrawable(Bitmap bitmap, int margin, float border) {
			this.margin = margin;
			this.border = border;
			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.MIRROR,
					Shader.TileMode.MIRROR);
			mBitmapRect = new RectF(margin, margin, bitmap.getWidth() - margin,
					bitmap.getHeight() - margin);

			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
			paint.setStyle(Paint.Style.FILL);

			borderPaint = new Paint();
			borderPaint.setStyle(Paint.Style.STROKE);
			borderPaint.setAntiAlias(true);
			borderPaint.setColor(COLOR_BORDER);
			borderPaint.setStrokeWidth(border);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(margin, margin, bounds.width() - margin, bounds.height()
					- margin);

			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect,
					Matrix.ScaleToFit.START);
			bitmapShader.setLocalMatrix(shaderMatrix);

			mBorderRect.set(mRect);
			mBorderRect.inset((border) / 2, (border) / 2);
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawOval(mRect, paint);
			canvas.drawOval(mBorderRect, borderPaint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}
}
