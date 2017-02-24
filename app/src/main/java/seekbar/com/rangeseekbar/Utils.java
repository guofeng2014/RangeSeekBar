package seekbar.com.rangeseekbar;

import android.content.Context;

/**
 * 作者：guofeng
 * ＊ 日期:2017/2/23
 */

public class Utils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
