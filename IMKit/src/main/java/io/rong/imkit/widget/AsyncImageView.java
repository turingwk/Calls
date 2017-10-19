//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

import io.rong.common.RLog;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.DisplayImageOptions.Builder;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.ImageSize;
import io.rong.imageloader.core.assist.LoadedFrom;
import io.rong.imageloader.core.display.CircleBitmapDisplayer;
import io.rong.imageloader.core.display.RoundedBitmapDisplayer;
import io.rong.imageloader.core.display.SimpleBitmapDisplayer;
import io.rong.imageloader.core.imageaware.ImageViewAware;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import io.rong.imkit.R.styleable;
import io.rong.imkit.utilities.RongUtils;

public class AsyncImageView extends ImageView {
    private static final String TAG = "AsyncImageView";
    private boolean isCircle;
    private float minShortSideSize = 0.0F;
    private int mCornerRadius = 0;
    private static final int AVATAR_SIZE = 80;
    private Drawable mDefaultDrawable;
    private WeakReference<Bitmap> mWeakBitmap;
    private WeakReference<Bitmap> mShardWeakBitmap;
    private boolean mHasMask;

    public AsyncImageView(Context context) {
        super(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!this.isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, styleable.AsyncImageView);
            int resId = a.getResourceId(styleable.AsyncImageView_RCDefDrawable, 0);
            this.isCircle = a.getInt(styleable.AsyncImageView_RCShape, 0) == 1;
            this.minShortSideSize = a.getDimension(styleable.AsyncImageView_RCMinShortSideSize, 0.0F);
            this.mCornerRadius = (int)a.getDimension(styleable.AsyncImageView_RCCornerRadius, 0.0F);
            this.mHasMask = a.getBoolean(styleable.AsyncImageView_RCMask, false);
            if(resId != 0) {
                this.mDefaultDrawable = this.getResources().getDrawable(resId);
            }

            a.recycle();
            if(this.mDefaultDrawable != null) {
                DisplayImageOptions options = this.createDisplayImageOptions(resId, false);
                Drawable drawable = options.getImageForEmptyUri((Resources)null);
                Bitmap bitmap = this.drawableToBitmap(drawable);
                ImageViewAware imageViewAware = new ImageViewAware(this);
                options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
            }

        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void getShardImage(Drawable drawable_bg, Bitmap bp, Canvas canvas) {
        int width = bp.getWidth();
        int height = bp.getHeight();
        Bitmap bitmap = this.mShardWeakBitmap == null?null:(Bitmap)this.mShardWeakBitmap.get();
        if(width > 0 && height > 0) {
            if(bitmap != null && !bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap, 0.0F, 0.0F, (Paint)null);
            } else {
                try {
                    bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                } catch (OutOfMemoryError var14) {
                    RLog.e("AsyncImageView", "getShardImage OutOfMemoryError");
                    var14.printStackTrace();
                    System.gc();
                }

                if(bitmap != null) {
                    Canvas rCanvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    Rect rect = new Rect(0, 0, width, height);
                    Rect rectF = new Rect(1, 1, width - 1, height - 1);
                    BitmapDrawable drawable_in = new BitmapDrawable(bp);
                    drawable_in.setBounds(rectF);
                    drawable_in.draw(rCanvas);
                    if(drawable_bg instanceof NinePatchDrawable) {
                        NinePatchDrawable patchDrawable = (NinePatchDrawable)drawable_bg;
                        patchDrawable.setBounds(rect);
                        Paint maskPaint = patchDrawable.getPaint();
                        maskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
                        patchDrawable.draw(rCanvas);
                    }

                    this.mShardWeakBitmap = new WeakReference(bitmap);
                    canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
                }
            }

        }
    }

    protected void onDetachedFromWindow() {
        Bitmap bitmap;
        if(this.mWeakBitmap != null) {
            bitmap = (Bitmap)this.mWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mWeakBitmap = null;
        }

        if(this.mShardWeakBitmap != null) {
            bitmap = (Bitmap)this.mShardWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mShardWeakBitmap = null;
        }

        super.onDetachedFromWindow();
    }

    public void invalidate() {
        Bitmap bitmap;
        if(this.mWeakBitmap != null) {
            bitmap = (Bitmap)this.mWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mWeakBitmap = null;
        }

        if(this.mShardWeakBitmap != null) {
            bitmap = (Bitmap)this.mShardWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mShardWeakBitmap = null;
        }

        super.invalidate();
    }

    public void setDefaultDrawable() {
        if(this.mDefaultDrawable != null) {
            DisplayImageOptions options = this.createDisplayImageOptions(0, false);
            Bitmap bitmap = this.drawableToBitmap(this.mDefaultDrawable);
            ImageViewAware imageViewAware = new ImageViewAware(this);
            options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
        }

    }

    public void setResource(Uri imageUri) {
        DisplayImageOptions options = this.createDisplayImageOptions(0, true);
        if(imageUri != null) {
            File file = new File(imageUri.getPath());
            if(!file.exists()) {
                ImageViewAware bitmap = new ImageViewAware(this);
                ImageLoader.getInstance().displayImage(imageUri.toString(), bitmap, options, (ImageLoadingListener)null, (ImageLoadingProgressListener)null);
            } else {
                Bitmap bitmap1 = this.getBitmap(imageUri);
                if(bitmap1 != null) {
                    this.setLayoutParam(bitmap1);
                    this.setImageBitmap(bitmap1);
                } else {
                    this.setImageBitmap((Bitmap)null);
                    LayoutParams params = this.getLayoutParams();
                    params.height = RongUtils.dip2px(80.0F);
                    params.width = RongUtils.dip2px(110.0F);
                    this.setLayoutParams(params);
                }
            }
        }

    }

    public void setCircle(boolean circle) {
        this.isCircle = circle;
    }

    public void setResource(String imageUri, int defaultResId) {
        if(imageUri != null || defaultResId > 0) {
            DisplayImageOptions options = this.createDisplayImageOptions(defaultResId, true);
            ImageLoader.getInstance().displayImage(imageUri, this, options);
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE?Config.ARGB_8888:Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public void setAvatar(String imageUri, int defaultResId) {
        ImageViewAware imageViewAware = new ImageViewAware(this);
        ImageSize imageSize = new ImageSize(80, 80);
        DisplayImageOptions options = this.createDisplayImageOptions(defaultResId, true);
        ImageLoader.getInstance().displayImage(imageUri, imageViewAware, options, imageSize, (ImageLoadingListener)null, (ImageLoadingProgressListener)null);
    }

    public void setAvatar(Uri imageUri) {
        if(imageUri != null) {
            ImageViewAware imageViewAware = new ImageViewAware(this);
            ImageSize imageSize = new ImageSize(80, 80);
            DisplayImageOptions options = this.createDisplayImageOptions(0, true);
            ImageLoader.getInstance().displayImage(imageUri.toString(), imageViewAware, options, imageSize, (ImageLoadingListener)null, (ImageLoadingProgressListener)null);
        }

    }

    private Bitmap getBitmap(Uri uri) {
        Bitmap bitmap = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options = new Options();

        try {
            bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
        } catch (Exception var5) {
            RLog.e("AsyncImageView", "getBitmap Exception : " + uri);
            var5.printStackTrace();
        }

        return bitmap;
    }

    private DisplayImageOptions createDisplayImageOptions(int defaultResId, boolean cacheInMemory) {
        Builder builder = new Builder();
        Drawable defaultDrawable = this.mDefaultDrawable;
        if(defaultResId > 0) {
            try {
                defaultDrawable = this.getContext().getResources().getDrawable(defaultResId);
            } catch (NotFoundException var6) {
                var6.printStackTrace();
            }
        }

        if(defaultDrawable != null) {
            builder.showImageOnLoading(defaultDrawable);
            builder.showImageForEmptyUri(defaultDrawable);
            builder.showImageOnFail(defaultDrawable);
        }

        if(this.isCircle) {
            builder.displayer(new CircleBitmapDisplayer());
        } else if(this.mCornerRadius > 0) {
            builder.displayer(new RoundedBitmapDisplayer(this.mCornerRadius));
        } else {
            builder.displayer(new SimpleBitmapDisplayer());
        }

        DisplayImageOptions options = builder.resetViewBeforeLoading(false).cacheInMemory(cacheInMemory).cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();
        return options;
    }

    public int getCornerRadius() {
        return this.mCornerRadius;
    }

    public void setCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    private void setLayoutParam(Bitmap bitmap) {
        float width = (float)bitmap.getWidth();
        float height = (float)bitmap.getHeight();
        byte minSize = 100;
        if(this.minShortSideSize > 0.0F) {
            if(width > this.minShortSideSize && height > this.minShortSideSize) {
                LayoutParams params2 = this.getLayoutParams();
                params2.height = (int)height;
                params2.width = (int)width;
                this.setLayoutParams(params2);
            } else {
                float params = width / height;
                int finalWidth;
                int finalHeight;
                if(params > 1.0F) {
                    finalHeight = (int)(this.minShortSideSize / params);
                    if(finalHeight < minSize) {
                        finalHeight = minSize;
                    }

                    finalWidth = (int)this.minShortSideSize;
                } else {
                    finalHeight = (int)this.minShortSideSize;
                    finalWidth = (int)(this.minShortSideSize * params);
                    if(finalWidth < minSize) {
                        finalWidth = minSize;
                    }
                }

                LayoutParams params1 = this.getLayoutParams();
                params1.height = finalHeight;
                params1.width = finalWidth;
                this.setLayoutParams(params1);
            }
        }

    }
}
