package reige.com.autoviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * description
 * author 陈锐
 * version 1.0
 * created 2017/5/10
 */

public class AutoViewPager extends FrameLayout {
    private Context context;
    private String[] images;
    private View contentView;
    private ViewPager vpImage;
    private LinearLayout llDot;
    private List<View> viewList;//ViewPager里的View

    private int dotSize = 15;//指示器大小
    private int dotSpace = 15;//指示器间距
    private int delay = 3;//自动刷新时间间隔
    private int MAX = 50000;//新闻轮播最大值
    private OnPagerClickListener onPagerClickListener;
    private int imgLength;//图片数组长度
    private Disposable disposable;

    public AutoViewPager(Context context) {
        this(context, null);
    }

    public AutoViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.autoviewpager_layout, this, true);
        vpImage = (ViewPager) findViewById(R.id.vp_image);
        llDot = (LinearLayout) findViewById(R.id.ll_dot);
    }


    public void setImages(final String[] images) {
        this.images = images;
        imgLength =images.length;
        viewList = new ArrayList<>();
        for (String img : images) {
            View view = LayoutInflater.from(context).inflate(R.layout.autoviewpager_content_layout,null);
            ImageView ivImage = (ImageView) view.findViewById(R.id.iv_image);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            Glide.with(context).load(img).centerCrop().into(ivImage);
            viewList.add(view);
        }
        vpImage.setAdapter(new AutoViewPagerAdapter());
        int mid = MAX/2 - MAX%imgLength;
        vpImage.setCurrentItem(mid);//将viewPager当前位置设置为最大值的中间位置
        setIndicator();//初始化指示器
        start();//开始轮播
        vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                position = position%imgLength;
                llDot.getChildAt(position).setScaleX(1.5f - positionOffset * 0.5f);
                llDot.getChildAt(position).setScaleY(1.5f - positionOffset * 0.5f);
                llDot.getChildAt((position + 1)%imgLength).setScaleX(1f + positionOffset * 0.5f);
                llDot.getChildAt((position + 1)%imgLength).setScaleY(1f + positionOffset * 0.5f);
            }

            @Override
            public void onPageSelected(int position) {
                for(int i= 0;i<llDot.getChildCount();i++){
                    llDot.getChildAt(i).setBackgroundResource(position%imgLength==i?R.drawable.dot_selected:R.drawable.dot_unselected);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state){
                    case  ViewPager.SCROLL_STATE_IDLE:
                        start();
                        break;
                    case  ViewPager.SCROLL_STATE_DRAGGING:
                        stop();
                        break;
                    case  ViewPager.SCROLL_STATE_SETTLING:
                        stop();
                        break;
                }
            }
        });
    }

    private void stop() {
        if(disposable!=null&&!disposable.isDisposed()) disposable.dispose();
    }
    private void start() {
        disposable = Observable.interval(delay, delay, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        vpImage.setCurrentItem(vpImage.getCurrentItem() + 1);
                    }
                });
    }

    private void setIndicator() {
        llDot.removeAllViews();// 记得创建前先清空数据，否则会受遗留数据的影响。
        for (int i = 0; i < imgLength; i++) {
            View view = new View(context);
            view.setBackgroundResource(R.drawable.dot_unselected);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dotSize, dotSize);
            layoutParams.leftMargin = dotSpace / 2;
            layoutParams.rightMargin = dotSpace / 2;
            layoutParams.topMargin = dotSpace / 2;
            layoutParams.bottomMargin = dotSpace / 2;
            llDot.addView(view, layoutParams);
        }
        llDot.getChildAt(0).setBackgroundResource(R.drawable.dot_selected);
    }

    class AutoViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final int newPosition = position % imgLength;
            View view = viewList.get(newPosition);
            if (onPagerClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPagerClickListener.onPagerClick(newPosition);
                    }
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    interface OnPagerClickListener {
        void onPagerClick(int position);
    }

    public void setOnPagerClickListener(OnPagerClickListener onPagerClickListener) {
        this.onPagerClickListener = onPagerClickListener;
    }
}
