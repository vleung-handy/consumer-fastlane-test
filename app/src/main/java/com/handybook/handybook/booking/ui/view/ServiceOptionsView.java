package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class ServiceOptionsView extends RelativeLayout
{
    @Bind(R.id.close_services_button_wrapper)
    View mCloseButtonWrapper;
    @Bind(R.id.services_wrapper)
    ViewGroup mServicesWrapper;
    private ArrayList<ServiceOptionView> mServiceOptionViews;
    private OnClickListeners mOnClickListeners;

    public ServiceOptionsView(final Context context)
    {
        super(context);
    }

    public ServiceOptionsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ServiceOptionsView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public boolean isReady()
    {
        return mServiceOptionViews != null && mServiceOptionViews.size() > 0
                && mOnClickListeners != null;
    }

    public void init(@NonNull List<Service> services, @NonNull OnClickListeners onClickListeners)
    {
        setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setVisibility(GONE);
        setBackgroundColor(getResources().getColor(R.color.handy_black_trans));
        setClickable(true);

        mOnClickListeners = onClickListeners;
        LayoutInflater.from(getContext()).inflate(R.layout.view_service_options, this);
        ButterKnife.bind(this);

        mServiceOptionViews = new ArrayList<>();
        for (final Service service : services)
        {
            final ServiceOptionView serviceOptionView = new ServiceOptionView(getContext());
            serviceOptionView.init(service);
            serviceOptionView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    mOnClickListeners.onServiceOptionClicked(serviceOptionView, service);
                }
            });
            mServiceOptionViews.add(serviceOptionView);
            mServicesWrapper.addView(serviceOptionView,
                    mServicesWrapper.getChildCount() - 1);
        }
    }

    public void show()
    {
        setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        setVisibility(VISIBLE);
        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                for (ServiceOptionView view : mServiceOptionViews)
                {
                    float fromYDelta = mCloseButtonWrapper.getY() - view.getY();
                    TranslateAnimation animation =
                            new TranslateAnimation(0, 0, fromYDelta, 0);
                    animation.setDuration(200);
                    animation.setInterpolator(new OvershootInterpolator());
                    view.startAnimation(animation);
                    view.setVisibility(VISIBLE);
                }
            }
        }, 200);
    }

    @OnClick(R.id.close_services_button)
    public void hide()
    {
        setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
        setVisibility(GONE);
        for (ServiceOptionView view : mServiceOptionViews)
        {
            view.setVisibility(INVISIBLE);
        }
        mOnClickListeners.onHideServiceOptions();
    }

    public interface OnClickListeners
    {
        void onHideServiceOptions();

        void onServiceOptionClicked(ServiceOptionView view, Service service);
    }
}
