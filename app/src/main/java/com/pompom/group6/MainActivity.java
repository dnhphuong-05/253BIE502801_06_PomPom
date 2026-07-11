package com.pompom.group6;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.pompom.group6.activities.AiChatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.pompom.group6.adapters.MainViewPagerAdapter;
import com.pompom.group6.databinding.ActivityMainBinding;
import com.pompom.group6.fragments.HomeFragment;
import com.pompom.group6.fragments.ShopFragment;
import com.pompom.group6.fragments.CommunityFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewPagerAdapter adapter;
    private com.pompom.group6.utils.MascotVoiceAssistant mascotVoiceAssistant;
    private boolean isBottomNavVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Modern Edge-to-Edge: Content flows behind status and navigation bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        // Make navigation bar truly transparent to allow app background to show through
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // Disable system-enforced contrast to keep the bar truly transparent (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bottom nav giờ là pill nổi (không còn chạm đáy màn hình) — cộng thêm inset của
        // thanh điều hướng hệ thống vào MARGIN đáy (chứ không phải padding bên trong nữa) để
        // cả pill trôi lên trên thanh gesture, giữ đúng khoảng cách nổi cố định (4dp) phía trên nó.
        int floatingGapPx = (int) (4 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = floatingGapPx + systemBars.bottom;
            v.setLayoutParams(params);
            return insets;
        });

        // Handle status bar insets if needed for other components
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Root padding should not be applied if we want full screen, 
            // but we can pass it down if needed.
            return insets;
        });

        setupViewPager();
        setupNavigation();
        setupFloatingMascot();
        mascotVoiceAssistant = new com.pompom.group6.utils.MascotVoiceAssistant(
                this, binding.ivFloatingMascot, (ViewGroup) binding.getRoot());

        // Initial state
        updateNavUI(0);
        applyOpenTabExtra(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        applyOpenTabExtra(intent);
    }

    /** Cho phép màn con (vd gợi ý sản phẩm theo da) mở thẳng 1 tab cụ thể khi quay lại MainActivity. */
    public static final String EXTRA_TAB = "open_tab";

    private void applyOpenTabExtra(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_TAB)) return;
        int tab = intent.getIntExtra(EXTRA_TAB, -1);
        if (tab >= 0) switchToTab(tab);
        intent.removeExtra(EXTRA_TAB);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mascotVoiceAssistant != null) mascotVoiceAssistant.onPermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mascotVoiceAssistant != null) mascotVoiceAssistant.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyItemChanged(4);
        }
    }

    private void setupViewPager() {
        adapter = new MainViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false); // Disable swiping as per user request

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateNavUI(position);
            }
        });
        
        // Prevent swiping away from Me screen if it's the last one, or keep it consistent
        binding.viewPager.setOffscreenPageLimit(3);
    }

    public void switchToTab(int index) {
        binding.viewPager.setCurrentItem(index);
    }

    /** Cuộn xuống → ẩn bottom nav (và mascot nổi cùng lúc, vì nó neo ngay trên nav bar);
     * cuộn lên → hiện lại. Gọi từ các fragment tab qua {@link com.pompom.group6.utils.BottomNavScrollHelper}. */
    public void hideBottomNav() {
        if (!isBottomNavVisible) return;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.bottomNavContainer.getLayoutParams();
        // Cộng thêm margin đáy (khoảng nổi + inset hệ thống) để pill trôi hẳn ra khỏi màn hình,
        // không chỉ vừa khít mép dưới.
        float distance = binding.bottomNavContainer.getHeight() + params.bottomMargin;
        if (distance <= 0) return;
        isBottomNavVisible = false;
        binding.bottomNavContainer.animate().translationY(distance).setDuration(200).start();
        binding.ivFloatingMascot.animate().translationY(distance).setDuration(200).start();
    }

    public void showBottomNav() {
        if (isBottomNavVisible) return;
        isBottomNavVisible = true;
        binding.bottomNavContainer.animate().translationY(0).setDuration(200).start();
        binding.ivFloatingMascot.animate().translationY(0).setDuration(200).start();
    }

    private void setupNavigation() {
        binding.navHome.setOnClickListener(v -> binding.viewPager.setCurrentItem(0));
        binding.navShop.setOnClickListener(v -> binding.viewPager.setCurrentItem(1));
        binding.btnAi.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(2);
            // Handle AI button click - bounce animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.15f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.15f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY);
            set.setDuration(300);
            set.start();
        });
        binding.navCommunity.setOnClickListener(v -> binding.viewPager.setCurrentItem(3));
        binding.navMe.setOnClickListener(v -> binding.viewPager.setCurrentItem(4));
    }

    private void setupFloatingMascot() {
        // OnTouchListener bên dưới tự xử lý kéo-thả và trả về true cho mọi sự kiện, nên
        // setOnLongClickListener chuẩn của Android KHÔNG BAO GIỜ được gọi (nó chỉ chạy trong
        // onTouchEvent(), mà onTouchEvent() không chạy nữa khi onTouch() đã trả về true).
        // Vì vậy phải tự đếm giờ long-press bằng Handler ở đây.
        final Handler longPressHandler = new Handler();
        final boolean[] longPressTriggered = {false};
        final Runnable longPressRunnable = () -> {
            longPressTriggered[0] = true;
            onMascotLongPress();
        };

        binding.ivFloatingMascot.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private float startX, startY;
            private static final int CLICK_ACTION_THRESHOLD = 10;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        startX = event.getRawX();
                        startY = event.getRawY();
                        longPressTriggered[0] = false;
                        longPressHandler.postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!isAClick(startX, event.getRawX(), startY, event.getRawY())) {
                            longPressHandler.removeCallbacks(longPressRunnable);
                        }
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;

                    case MotionEvent.ACTION_UP:
                        longPressHandler.removeCallbacks(longPressRunnable);
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (!longPressTriggered[0] && isAClick(startX, endX, startY, endY)) {
                            view.performClick();
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        longPressHandler.removeCallbacks(longPressRunnable);
                        break;

                    default:
                        return false;
                }
                return true;
            }

            private boolean isAClick(float startX, float endX, float startY, float endY) {
                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
            }
        });

        binding.ivFloatingMascot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AiChatActivity.class);
            startActivity(intent);
        });
    }

    /** Long-press mascot: phát âm thanh rồi mascot tự "lắng nghe" và trả lời ngay tại chỗ —
     * không mở màn hình chat. */
    private void onMascotLongPress() {
        playMascotSound();
        if (mascotVoiceAssistant != null) mascotVoiceAssistant.startListening();
    }

    private void playMascotSound() {
        if (!com.pompom.group6.utils.AiSettings.isMascotSoundEnabled(this)) return;
        try {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80);
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 200);
        } catch (Exception ignored) {
            // Best-effort feedback only.
        }
    }

    private void updateNavUI(int index) {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        if (controller != null) {
            // Dynamic Status Bar Icon Color based on Fragment background.
            // Me (4) chỉ có header hồng khi ĐÃ đăng nhập (PremiumProfileFragment); khi chưa
            // đăng nhập là màn guest nền sáng -> phải để icon tối cho dễ nhìn.
            boolean meLoggedIn = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getBoolean("is_logged_in", false);
            boolean pinkHeader = index == 0 || index == 1 || index == 3 || (index == 4 && meLoggedIn);
            // Pink background -> white icons (light status bar = false); light background -> dark icons.
            controller.setAppearanceLightStatusBars(!pinkHeader);
            
            // Always keep light navigation bar as bottom nav is light-colored
            controller.setAppearanceLightNavigationBars(true);
        }

        selectTab(binding.navHome, binding.ivHome, binding.tvHome,
                index == 0 ? R.drawable.ic_home_pink : R.drawable.ic_home_border, index == 0);
        selectTab(binding.navShop, binding.ivShop, binding.tvShop,
                index == 1 ? R.drawable.ic_shop_pink : R.drawable.ic_shop_border, index == 1);
        
        // AI Center button visual state
        if (index == 2) {
            binding.btnAi.setAlpha(1.0f);
            binding.btnAi.setScaleX(1.1f);
            binding.btnAi.setScaleY(1.1f);
        } else {
            binding.btnAi.setAlpha(0.85f);
            binding.btnAi.setScaleX(1.0f);
            binding.btnAi.setScaleY(1.0f);
        }

        selectTab(binding.navCommunity, binding.ivCommunity, binding.tvCommunity,
                index == 3 ? R.drawable.ic_community_pink : R.drawable.ic_community_border, index == 3);
        selectTab(binding.navMe, binding.ivMe, binding.tvMe,
                index == 4 ? R.drawable.ic_me_pink : R.drawable.ic_me_border, index == 4);

        // Mascot chỉ nổi ở tab Home — các tab khác (đặc biệt Me) có nhiều card/list dày đặc,
        // mascot neo cố định theo màn hình nên hay đè lên nội dung thật khi ở đó.
        binding.ivFloatingMascot.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
    }

    private void selectTab(View container, ImageView icon, TextView label,
                           int iconRes, boolean selected) {
        // Set icon image explicitly
        icon.setImageResource(iconRes);
        // Set state for color selectors
        icon.setSelected(selected);
        label.setSelected(selected);
        container.setSelected(selected);

        if (selected) {
            // Pop-up bounce when selected
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 0.85f, 1.15f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 0.85f, 1.15f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY);
            set.setDuration(250);
            set.start();
            label.setTextSize(11.5f);
        } else {
            icon.setScaleX(1f);
            icon.setScaleY(1f);
            label.setTextSize(11f);
        }
    }
}
