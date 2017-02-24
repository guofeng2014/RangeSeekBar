package seekbar.com.rangeseekbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RangeSeekBarView seekBarView = (RangeSeekBarView) findViewById(R.id.seekbar);
        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<LeveBean> data = seekBarView.getSelectedData();
                Toast.makeText(MainActivity.this, data.get(0).name + "=" + data.get(1).name, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
