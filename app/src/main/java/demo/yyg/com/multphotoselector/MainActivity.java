package demo.yyg.com.multphotoselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg.photoselect.photoselector.model.PhotoModel;
import com.yyg.photoselect.photoselector.ui.PhotoSelectorActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PIC_SELECT = 11;

    TextView tvText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = (TextView) findViewById(R.id.textview);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoSelectorActivity.class);
                intent.putExtra(PhotoSelectorActivity.KEY_MAX, 5);
                startActivityForResult(intent, REQUEST_PIC_SELECT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PIC_SELECT && null != data) {
                if (data.getExtras() != null) {
                    @SuppressWarnings("unchecked")
                    List<PhotoModel> photos = (List<PhotoModel>) data.getExtras().getSerializable("photos");
                    if (photos == null || photos.isEmpty()) {
                        Toast.makeText(this,"select pic error",Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuffer sb = new StringBuffer();
                        for (PhotoModel model : photos) {
                            sb.append("1.");
                            sb.append(model.getOriginalPath());
                            sb.append("\n");
                        }
                        tvText.setText(sb.toString());
                    }
                }
            }
        }
    }
}
