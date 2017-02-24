package seekbar.com.rangeseekbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：guofeng
 * ＊ 日期:2017/2/22
 */

public class RangeSeekBarView extends View {

    private Bitmap startCurseBitmap, endCurseBitmap, startTitleBitmap, endTitleBitmap;

    private Paint greenPaint;

    private Paint grayPaint;

    private Paint textPaint;

    private int seekBarHeight;

    private int titleDescHeight;

    private int titleDecWidth;

    private List<LeveBean> data;

    private int seekBarTopPadding;

    private int seekBarBottomPadding;

    private int startIndex;

    private int endIndex;

    private int textPaddingLeftAndRight;


    public RangeSeekBarView(Context context) {
        this(context, null);
    }

    public RangeSeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setData(createData());
    }

    private List<LeveBean> createData() {
        List<LeveBean> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LeveBean leveBean = new LeveBean();
            if (i > 10) {
                leveBean.name = "测试数据" + String.valueOf(i);
            } else {
                leveBean.name = String.valueOf(i);
            }
            data.add(leveBean);
        }
        return data;
    }

    public void setData(List<LeveBean> data) {
        if (data == null || data.size() == 0) {
            throw new NullPointerException("source data can not be null or empty");
        }
        this.data = data;
        startIndex = 0;
        endIndex = data.size() - 1;
    }

    /**
     * 获得选择的数据,返回集合,结合大小2个,第一个是开始位置,第二个是结束位置
     */
    public List<LeveBean> getSelectedData() {
        if (data == null) {
            throw new NullPointerException("source date can not be null,please invoke setDate() to init  source data ");
        }
        List<LeveBean> list = new ArrayList<>();
        LeveBean start = data.get(startIndex);
        LeveBean end = data.get(endIndex);
        list.add(start);
        list.add(end);
        return list;
    }

    private void init() {
        seekBarHeight = Utils.dip2px(getContext(), 3);
        seekBarTopPadding = Utils.dip2px(getContext(), 3);
        seekBarBottomPadding = Utils.dip2px(getContext(), 3);
        titleDecWidth = Utils.dip2px(getContext(), 39);
        titleDescHeight = Utils.dip2px(getContext(), 27);
        textPaddingLeftAndRight = Utils.dip2px(getContext(), 3);

        startCurseBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cursor);
        endCurseBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cursor);

        startTitleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_title);
        endTitleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_title);

        greenPaint = new Paint();
        greenPaint.setAntiAlias(false);
        greenPaint.setDither(true);
        greenPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
        greenPaint.setStyle(Paint.Style.FILL);

        grayPaint = new Paint();
        greenPaint.setAntiAlias(false);
        greenPaint.setDither(true);
        grayPaint.setColor(ContextCompat.getColor(getContext(), R.color.gray));
        grayPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(false);
        textPaint.setDither(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(Utils.dip2px(getContext(), 14));
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        textPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 绘制SeekBar底部灰色圆角区域
     */
    private RectF grayRect = new RectF();
    /**
     * 绘制SeekBar滑动过绿色圆角区域
     */
    private RectF greenRect = new RectF();
    /**
     * 计算SeekBar距离左边和右边的间距
     */
    private int seekBarPadding;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        grayRect.left = seekBarPadding;
        grayRect.top = titleDescHeight + seekBarTopPadding;
        grayRect.right = getWidth() - seekBarPadding;
        grayRect.bottom = titleDescHeight + seekBarHeight + seekBarTopPadding;

        greenRect.left = seekBarPadding;
        greenRect.top = titleDescHeight + seekBarTopPadding;
        greenRect.right = getWidth() - seekBarPadding;
        greenRect.bottom = titleDescHeight + seekBarHeight + seekBarTopPadding;

        seekBarPadding = Math.max(startCurseBitmap.getWidth(), titleDecWidth) / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        refreshView(canvas);
        canvas.drawRoundRect(grayRect, 7, 7, grayPaint);
        canvas.drawRoundRect(greenRect, 7, 7, greenPaint);
    }

    /**
     * 根据外面传入集合的大小,
     * 动态算出每俩点之间的距离
     */
    private int itemWidth;

    /**
     * 计算每格的宽度
     *
     * @return
     */
    private int getItemWidth() {
        if (itemWidth == 0) {
            itemWidth = (getWidth() - seekBarPadding * 2) / (data.size() - 1);
        }
        return itemWidth;
    }

    /**
     * 绘制顶部开始描述矩阵范围{左上点坐标,右下点坐标}
     */
    private Rect startTitleRect = new Rect();
    /**
     * 绘制顶部结束描述矩阵范围{左上点坐标,右下点坐标}
     */
    private Rect endTitleRect = new Rect();
    private static final String TAG = "RangeSeekBarView";

    /**
     * 根据下标转化成对应滑动位置
     */
    private void refreshView(Canvas canvas) {
        if (startIndex > endIndex) return;
        //计算每格的宽度 ,俩个点是一个格子
        //计算底部SeekBar起点和终点
        grayRect.left = seekBarPadding;
        grayRect.right = getWidth() - seekBarPadding;
        //计算选中SeekBar颜色
        greenRect.left = seekBarPadding + startIndex * getItemWidth();
        greenRect.right = getWidth() - seekBarPadding - (data.size() - 1 - endIndex) * getItemWidth();

        //后操作的在上面
        if (touchCursor == TOUCH_START_CURSOR) {
            //绘制底部游标
            canvas.drawBitmap(endCurseBitmap, seekBarPadding + endIndex * getItemWidth() - startCurseBitmap.getWidth() / 2, titleDescHeight + seekBarHeight + seekBarTopPadding + seekBarBottomPadding, null);
            canvas.drawBitmap(startCurseBitmap, seekBarPadding - startCurseBitmap.getWidth() / 2 + startIndex * getItemWidth(), titleDescHeight + seekBarHeight + seekBarTopPadding + seekBarBottomPadding, null);
            //绘制结束描述
            getNinePath(endTitleRect, endTitleBitmap, endIndex).draw(canvas, endTitleRect);
            drawText(canvas, endTitleRect, data.get(endIndex).name);
            //绘制开始描述
            getNinePath(startTitleRect, startTitleBitmap, startIndex).draw(canvas, startTitleRect);
            Log.d(TAG, "refreshView: " + startIndex);
            drawText(canvas, startTitleRect, data.get(startIndex).name);
        } else {
            //绘制底部游标
            canvas.drawBitmap(startCurseBitmap, seekBarPadding - startCurseBitmap.getWidth() / 2 + startIndex * getItemWidth(), titleDescHeight + seekBarHeight + seekBarTopPadding + seekBarBottomPadding, null);
            canvas.drawBitmap(endCurseBitmap, seekBarPadding + endIndex * getItemWidth() - startCurseBitmap.getWidth() / 2, titleDescHeight + seekBarHeight + seekBarTopPadding + seekBarBottomPadding, null);
            //绘制开始描述
            getNinePath(startTitleRect, startTitleBitmap, startIndex).draw(canvas, startTitleRect);
            drawText(canvas, startTitleRect, data.get(startIndex).name);
            //绘制结束描述
            getNinePath(endTitleRect, endTitleBitmap, endIndex).draw(canvas, endTitleRect);
            drawText(canvas, endTitleRect, data.get(endIndex).name);
        }
    }

    /**
     * 绘制.9图片
     *
     * @param rect   Rect对象
     * @param bitmap 绘制.9的bitmap对象
     * @param index  当前是开始还是结束的下标
     * @return
     */
    private NinePatch getNinePath(Rect rect, Bitmap bitmap, int index) {
        NinePatch endTitlePath = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
        rect.left = seekBarPadding - (titleDecWidth / 2) + (index * getItemWidth());
        rect.top = 0;
        rect.right = rect.left + titleDecWidth;
        rect.bottom = titleDescHeight;
        return endTitlePath;
    }


    /**
     * 绘制文字
     *
     * @param canvas 画布
     * @param rect   文字所在的矩阵
     * @param text   绘制的文字
     */
    private void drawText(Canvas canvas, Rect rect, String text) {
        //裁切文字在控件范围内
        int index = textPaint.breakText(text.toCharArray(), 0, text.length(), titleDecWidth - textPaddingLeftAndRight * 2, null);
        text = text.substring(0, index);
        float width = textPaint.measureText(text);
        float x = rect.left + (rect.width() - width) / 2;
        Paint.FontMetrics font = textPaint.getFontMetrics();
        int textHeight = (int) (Math.ceil(font.descent - font.ascent) + 2);
        float y = (rect.height() - textHeight) / 2 + textHeight - font.bottom;
        // y是字符baseLine(底线)在屏幕的位置,
        canvas.drawText(text, x, y, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkMoveDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                finalTouchCursor = touchCursor;
                touchCursor = TOUCH_NONE_CURSOR;
                lastTouchX = 0;
                break;
        }
        return true;
    }


    /**
     * 当前滑动的是哪个游标
     * {@link #TOUCH_START_CURSOR}滑动的是开始游标
     * {@link #TOUCH_END_CURSOR}滑动的是结束游标
     */
    private int touchCursor = TOUCH_NONE_CURSOR;
    /**
     * 记录最后一次滑动的游标
     */
    private int finalTouchCursor = TOUCH_NONE_CURSOR;
    /**
     * 当前没有可以操作的游标
     */
    private static final int TOUCH_NONE_CURSOR = -1;
    /**
     * 当前可以操作或正在操作的游标是开始游标
     */
    private static final int TOUCH_START_CURSOR = 1;
    /**
     * 当前可以操作或正在操作的游标是结束游标
     */
    private static final int TOUCH_END_CURSOR = 2;


    /**
     * 检测按下事件
     *
     * @param event 触摸事件
     */
    private void checkMoveDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        lastTouchX = x;
        //如果重合了,保留上次最后滑动的在最上面
        if (startIndex == endIndex) {
            //最后一次滑动的是开始游标
            if (finalTouchCursor == TOUCH_START_CURSOR) {
                if (checkIsTouchInStartCursor(x, y)) return;
            } else if (finalTouchCursor == TOUCH_END_CURSOR) {
                //最后一次滑动的是结束游标
                if (checkIsTouchInEndCUrsor(x, y)) return;
            }
        }
        //滑动开始游标
        if (checkIsTouchInStartCursor(x, y)) return;
        //滑动结束游标
        if (checkIsTouchInEndCUrsor(x, y)) return;
        //不可以滑动
        touchCursor = TOUCH_NONE_CURSOR;
    }

    /**
     * 是否在开始游标内滑动
     *
     * @param x 触摸点x的坐标
     * @param y 触摸点y坐标
     * @return true 在开始游标内滑动false无效的滑动
     */
    private boolean checkIsTouchInStartCursor(float x, float y) {
        //点击的是开始游标
        boolean isTouchStartCursor = isTouchIn(x, y, startIndex);
        if (isTouchStartCursor) {
            touchCursor = TOUCH_START_CURSOR;
            return true;
        }
        return false;
    }

    /**
     * 检测是否在结束游标内滑动
     *
     * @param x 触摸坐标x
     * @param y 触摸坐标y
     * @return true在结束游标false不在
     */
    private boolean checkIsTouchInEndCUrsor(float x, float y) {
        //点击的是结束游标
        boolean isTouchEndCursor = isTouchIn(x, y, endIndex);
        if (isTouchEndCursor) {
            touchCursor = TOUCH_END_CURSOR;
            return true;
        }
        return false;
    }

    /**
     * 记录滑动过程上次X坐标
     */
    private float lastTouchX;

    /**
     * 监控滑动事件
     *
     * @param event
     */
    private void onTouchMove(MotionEvent event) {
        //不在有效的滑动范围内
        if (touchCursor == TOUCH_NONE_CURSOR) return;
        float x = event.getX();
        //滑动距离,正数向右滑动,负数向左滑动
        float scrollDistance = x - lastTouchX;
        //滑动距离大于一个格子,计算下标
        if (Math.abs(scrollDistance) >= getItemWidth()) {
            if (touchCursor == TOUCH_START_CURSOR) {
                if (scrollDistance > 0 && startIndex < endIndex) {
                    startIndex++;
                } else if (scrollDistance < 0 && startIndex <= endIndex) {
                    startIndex--;
                }
            } else if (touchCursor == TOUCH_END_CURSOR) {
                if (scrollDistance > 0 && startIndex <= endIndex) {
                    endIndex++;
                } else if (scrollDistance < 0 && startIndex < endIndex) {
                    endIndex--;
                }
            }
            lastTouchX = x;
            //防止越界
            if (startIndex < 0) {
                startIndex = 0;
            }
            int max = data.size() - 1;
            if (endIndex > max) {
                endIndex = max;
            }
            //重新绘制
            invalidate();
        }
    }

    /**
     * 检测是否可以滑动
     *
     * @param scrollDistance 手指滑动的距离
     * @return
     */
    private boolean isTouchOutOfBound(float scrollDistance) {
        //开始游标只能小于等于结束游标
        if (startIndex > endIndex) {
            return true;
        }
        //向右边滑动
        if (scrollDistance > 0) {
            if (touchCursor == TOUCH_END_CURSOR && greenRect.right == getWidth() - seekBarPadding) {
                return true;
            }
        } else {//向左边滑动
            if (touchCursor == TOUCH_START_CURSOR && greenRect.left == seekBarPadding) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否触摸在游标处
     *
     * @param x
     * @param y
     * @param index
     * @return
     */
    private boolean isTouchIn(float x, float y, int index) {
        int w = startCurseBitmap.getWidth();
        int h = startCurseBitmap.getHeight();
        int cursorStartX = seekBarPadding - (w / 2) + getItemWidth() * index;
        int cursorEndX = cursorStartX + w;
        int cursorStartY = titleDescHeight + seekBarHeight;
        int cursorEndY = cursorStartY + h;
        return x >= cursorStartX && x <= cursorEndX && y >= cursorStartY && y <= cursorEndY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minHeight = titleDescHeight + seekBarHeight + startCurseBitmap.getHeight() + seekBarTopPadding + seekBarBottomPadding;
        int heightSpec = MeasureSpec.makeMeasureSpec(minHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightSpec);
    }

}
