package com.handybook.handybook.booking.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.core.ui.descriptor.ServiceCategoryListDescriptor;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sng on 1/30/17.
 */

public class ServicesCategoryHomeAdapter extends BaseAdapter
{
    private List<Service> mServices;
    private Context mContext;

    public ServicesCategoryHomeAdapter(Context context, List<Service> services)
    {
        mContext = context;
        refreshData(services);
    }

    @Override
    public int getCount()
    {
        return mServices == null ? 0 : mServices.size();
    }

    @Override
    public Service getItem(final int index)
    {
        return mServices == null ? null : mServices.get(index);
    }

    @Override
    public long getItemId(final int index)
    {
        return mServices == null ? 0 : mServices.get(index).getId();
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup)
    {
        CategoryViewHolder viewHolder;
        if (view == null)
        {
            view = View.inflate(mContext, R.layout.layout_services_home_category_view, null);
            viewHolder = new CategoryViewHolder(view);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (CategoryViewHolder) view.getTag();
        }

        ServiceCategoryListDescriptor descriptor = ServiceCategoryListDescriptor.getServiceDescriptorFromService(
                mServices.get(position));
        viewHolder.mIcon.setImageResource(descriptor.getIconDrawable());
        viewHolder.mText.setText(descriptor.getTitleString());
        viewHolder.mSubText.setText(descriptor.getSubtitleString());

        return view;
    }

    public void refreshData(List<Service> services)
    {
        mServices = services;
        notifyDataSetChanged();
    }

    public class CategoryViewHolder
    {
        @Bind(R.id.icon)
        ImageView mIcon;
        @Bind(R.id.text)
        TextView mText;
        @Bind(R.id.sub_text)
        TextView mSubText;

        public CategoryViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }

        public ImageView getIcon()
        {
            return mIcon;
        }
    }
}
