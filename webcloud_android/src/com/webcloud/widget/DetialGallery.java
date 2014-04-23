package com.webcloud.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class DetialGallery extends Gallery {
	private Camera mCamera = new Camera();// 相机类
	private int mMaxRotationAngle = 60;// 最大转动角度
	private int mMaxZoom = -300;// //最大缩放值
	private int mCoveflowCenter;// 半径值
	private boolean mAlphaMode = true;
	private boolean mCircleMode = false;

	public DetialGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public boolean getAlphaMode() {
		return mAlphaMode;
	}

	public void setAlphaMode(boolean isAlpha) {
		mAlphaMode = isAlpha;
	}

	public boolean getCircleMode() {
		return mCircleMode;
	}

	public void setCircleMode(boolean isCircle) {
		mCircleMode = isCircle;
	}

	public int getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	// 控制gallery中每个图片的旋转(重写的gallery中方法)
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		// 取得当前子view的半径值
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		// 旋转角度
		int rotationAngle = 0;
		// 重置转换状态
		t.clear();
		// 设置转换类型
		t.setTransformationType(Transformation.TYPE_MATRIX);
		// 如果图片位于中心位置不需要进行旋转
		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			// 根据图片在gallery中的位置来计算图片的旋转角度
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			// 如果旋转角度绝对值大于最大旋转角度返回（-mMaxRotationAngle或mMaxRotationAngle;）
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
						: mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}
		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {
		// 对效果进行保存
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		// 图片高度
		final int imageHeight = child.getLayoutParams().height;
		// 图片宽度
		final int imageWidth = child.getLayoutParams().width;

		// 返回旋转角度的绝对值
		final int rotation = Math.abs(rotationAngle);

		// 在Z轴上正向移动camera的视角，实际效果为放大图片。
		// 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
		mCamera.translate(0.0f, 0.0f, 100.0f);
		if (rotation <= mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
//			if (mCircleMode) {
//				if (rotation < 40)
//					mCamera.translate(0.0f, 155, 0.0f);
//				else
//					mCamera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
//			}
//			if (mAlphaMode) {
//				((ImageView) (child)).setAlpha((int) (255 - rotation * 2.5));
//			}
		}

		// As the angle of the view gets less, zoom in
		// if (rotation < mMaxRotationAngle) {
		// float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
		// mCamera.translate(0.0f,
		// 0.0f, zoomAmount);
		// if (mAlphaMode) {
		// ((ImageView) (child))
		// .setAlpha((int) (255 - rotation * 2.5));
		// }
		//
		// }
		// 在Y轴上旋转，对应图片竖向向里翻转。
		// 如果在X轴上旋转，则对应图片横向向里翻转。
		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
