package com.handybook.handybook.promos.persistent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * a toolbar that shows a preview of the persistent promo
 */
public class PersistentPromoPreviewToolbar extends android.support.v7.widget.Toolbar {

    @Bind(R.id.persistent_promo_preview_text)
    TextView mPreviewText;

    public PersistentPromoPreviewToolbar(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_persistent_promo_preview_toolbar, this);
        ButterKnife.bind(this);
        setNavigationIcon(null);
    }

    public PersistentPromoPreviewToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersistentPromoPreviewToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void updateWithModel(@NonNull PersistentPromo persistentPromo) {
        if (persistentPromo.getPreviewText() != null) {
            mPreviewText.setText(persistentPromo.getPreviewText());
            mPreviewText.setVisibility(VISIBLE);
        }
        else {
            mPreviewText.setVisibility(GONE);
        }
    }
}
