package com.webcloud.map.street;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;

/**
 * 绘制街景标记图层。
 * 1.使用手工会出地图图层bitmap
 * 2.也可以使用布局，然后ondraw实现更方便
 * 
 * @author  bangyue
 * @version  [版本号, 2013-10-31]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class StreetMarkBitmap{
    public static final String TAG = "StreetMarkBitmap";
    
    public static final int maxWidth = 240;
    
    public static final int minWidth = 120;
    
    public static final int txtHeight = 45;
    
    public static final int bottomHeight = 70;

    public static final int lineHeight = 30;
    
    /**字体大小*/
    private int textSize;
    /**位置字体颜色*/
    private int positionColor;
    /**距离字体颜色*/
    private int distanceColor;
    /**背景颜色*/
    private int bgColor;
    /**背景透明度*/
    private int alpha;
    /**边框颜色*/
    private int borderColor;
    
    public StreetMarkBitmap(int textSize, int positionColor, int distanceColor,
        int bgColor, int alpha, int borderColor) {
        super();
        this.textSize = textSize;
        this.positionColor = positionColor;
        this.distanceColor = distanceColor;
        this.bgColor = bgColor;
        this.alpha = alpha;
        this.borderColor = borderColor;
    }

    public int getTextSize() {
        return textSize;
    }


    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }


    public int getPositionColor() {
        return positionColor;
    }


    public void setPositionColor(int positionColor) {
        this.positionColor = positionColor;
    }


    public int getDistanceColor() {
        return distanceColor;
    }


    public void setDistanceColor(int distanceColor) {
        this.distanceColor = distanceColor;
    }


    public int getBgColor() {
        return bgColor;
    }


    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }


    public int getAlpha() {
        return alpha;
    }


    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getBorderColor() {
        return borderColor;
    }


    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }


    /**
     * 根据文字长度，生存图片。
     * 生存地图标准格式图像。
     * 
     * @param bitmap
     * @param corner
     * @return
     */
    public Bitmap drawMap(String positionName,String distance) {
        Bitmap newBitmap = null;
        try {
            //位置文字画笔
            TextPaint txtPt1 = new TextPaint();
            txtPt1.setColor(positionColor);
            txtPt1.setAntiAlias(true);//取消锯齿效果
            txtPt1.setTextSize(textSize);
            
            //txtPt1.setTextAlign(Align.CENTER);
            //距离文字画笔
            TextPaint txtPt2 = new TextPaint();
            txtPt2.setColor(distanceColor);
            txtPt2.setAntiAlias(true);//取消锯齿效果
            txtPt2.setTextSize(textSize);
            
            //背景画笔
            Paint bgPt = new Paint();
            //bgPt.setAlpha((int)(alpha * 100));//设置透明度
            //bgPt.setColor(Color.YELLOW);
            //60%透明2e2e2e
            bgPt.setARGB(153, 0x2e, 0x2e, 0x2e);
            
            //背景画笔
            Paint linePt = new Paint();
            linePt.setColor(positionColor);
            
            //得到文字长度
            //文字长度+10作为图片长度,因为两段文字中间还要放置一个横线
            int txtWidth1 = (int)txtPt1.measureText(positionName);
            int txtWidth2 = (int)txtPt2.measureText(distance);
            float fontWidth = txtWidth1 + txtWidth2 + 30;
            
            if (fontWidth < minWidth)
                fontWidth = minWidth;
            /*else if (fontWidth > maxWidth)
                fontWidth = maxWidth;*/
            
            //位图资源
            newBitmap = Bitmap.createBitmap((int)fontWidth, txtHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            /*
            int mapWidth = newBitmap.getWidth();
            Rect txtRec1 = new Rect();
            txtPt1.getTextBounds(positionName, 0, positionName.length()-1, txtRec1);
            Rect txtRec2 = new Rect();
            txtPt1.getTextBounds(distance, 0, distance.length(), txtRec2);
            */
            
            canvas.drawPaint(bgPt);     
            //画文字是以文字底部基线为准，padding左边5
            /*p.setTextAlign(Paint.Align.CENTER);

            Paint.FontMetrics fm = p.getFontMetrics();*/
            canvas.drawText(positionName, 5, txtHeight/2+textSize/2-2, txtPt1);
            //画线条
            float x = 5 + txtPt1.measureText(positionName) + 10;
            float starty = (txtHeight - lineHeight)/2;
            float stopy = starty + lineHeight;
            canvas.drawLine(x, starty, x, stopy, linePt);
            canvas.drawText(distance, x + 10, txtHeight/2+textSize/2-2, txtPt2);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, this.toString());
        return newBitmap;
    }
    
    public Bitmap drawMapWithBorder(Bitmap src,String positionName,String distance){
        Bitmap newBitmap = null;
        try {
            if(src == null){
                newBitmap = this.drawMap(positionName,distance);
            } else newBitmap = src;
            //创建一张新图
            newBitmap = Bitmap.createBitmap(src);
            
            Canvas canvas = new Canvas(newBitmap);
            // 画边框  
            Rect rec = canvas.getClipBounds();  
            rec.bottom--;  
            rec.right--;  
            Paint paint = new Paint();  
            //设置边框颜色  
            paint.setColor(borderColor);  
            paint.setStyle(Paint.Style.STROKE);  
            //设置边框宽度  
            paint.setStrokeWidth(2);  
            canvas.drawRect(rec, paint);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, this.toString());
        return newBitmap;
    }

    @Override
    public String toString() {
        return "StreetMarkBitmap [textSize=" + textSize + ", positionColor=" + positionColor + ", distanceColor="
            + distanceColor + ", bgColor=" + bgColor + ", alpha=" + alpha + ", borderColor=" + borderColor + "]";
    }
}
