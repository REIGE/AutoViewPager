package reige.com.autoviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AutoViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        String[] imageUrls = {"https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3261561758,2570279118&fm=23&gp=0.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1494739654901&di=2b2c84354acd353e95070af01a1b7181&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fpic%2Fitem%2Fd6ca7bcb0a46f21fb16c5ee4f6246b600c33ae28.jpg",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1595855163,1815593911&fm=23&gp=0.jpg",
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3520325925,1812633280&fm=23&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2352583640,615617582&fm=23&gp=0.jpg"};
        viewPager.setImages(imageUrls);
        viewPager.setOnPagerClickListener(new AutoViewPager.OnPagerClickListener() {
            @Override
            public void onPagerClick(int position) {
                Toast.makeText(getApplicationContext(),"点击"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        viewPager = (AutoViewPager)findViewById(R.id.autoViewPager);
    }
}
