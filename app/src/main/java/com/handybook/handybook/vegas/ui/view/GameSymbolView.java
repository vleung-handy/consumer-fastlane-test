package com.handybook.handybook.vegas.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringDef;
import android.util.AttributeSet;

import com.handybook.handybook.R;

import java.lang.annotation.Retention;
import java.util.HashMap;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class GameSymbolView extends android.support.v7.widget.AppCompatImageView {

    public static final String SYMBOL_DUSTPAN = "dustpan";
    public static final String SYMBOL_BUCKET_GREEN = "bucket_green";
    public static final String SYMBOL_HANDY_LOGO = "handy_logo";
    public static final String SYMBOL_HEART = "heart";
    public static final String SYMBOL_PIGGY_BANK = "piggy_bank";
    public static final String SYMBOL_SOAP_BOTTLE = "soap_bottle";
    public static final String SYMBOL_STAR = "star";
    public static final int[] RESOURCE_IDS = {
            R.drawable.img_game_scratch_off_dustpan,
            R.drawable.img_game_symbol_bucket,
            R.drawable.img_game_symbol_logo_handy,
            R.drawable.img_game_symbol_heart,
            R.drawable.img_game_symbol_piggy_bank,
            R.drawable.img_game_symbol_soap_bottle,
            R.drawable.img_game_symbol_star

    };
    public static final
    @Symbol String[] SYMBOLS = {
            SYMBOL_DUSTPAN,
            SYMBOL_BUCKET_GREEN,
            SYMBOL_HANDY_LOGO,
            SYMBOL_HEART,
            SYMBOL_PIGGY_BANK,
            SYMBOL_SOAP_BOTTLE,
            SYMBOL_STAR
    };
    public static final HashMap<String, Integer> SYMBOLS_TO_RESOURCE_IDS = new HashMap<>();
    public static final HashMap<Integer, String> RESOURCE_IDS_TO_SYMBOLS = new HashMap<>();

    private String mSymbol;

    static {
        for (int i = 0; i < SYMBOLS.length; i++) {
            SYMBOLS_TO_RESOURCE_IDS.put(SYMBOLS[i], RESOURCE_IDS[i]);
            RESOURCE_IDS_TO_SYMBOLS.put(RESOURCE_IDS[i], SYMBOLS[i]);
        }
    }

    public GameSymbolView(final Context context) {
        super(context);
        init(null, 0, 0);
    }

    public GameSymbolView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public GameSymbolView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    private void init(final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        //setSaveEnabled(true);
        final TypedArray ta = getContext()
                .getTheme()
                .obtainStyledAttributes(attrs, R.styleable.GameSymbolView, 0, 0);

        try {
            setSymbol(RESOURCE_IDS_TO_SYMBOLS.get(ta.getInt(R.styleable.GameSymbolView_symbol, 0)));
        }
        finally {
            ta.recycle();
        }
    }

    private void update() {
        if (mSymbol == null) {
            return;
        }
        setImageResource(gerResourceId(mSymbol));
    }

    public void setSymbol(final String symbol) {
        mSymbol = symbol;
        update();
    }

    public static int gerResourceId(final String symbol) {
        return SYMBOLS_TO_RESOURCE_IDS.get(symbol);
    }

    @Retention(SOURCE)
    @StringDef({
                       SYMBOL_DUSTPAN,
                       SYMBOL_BUCKET_GREEN,
                       SYMBOL_HANDY_LOGO,
                       SYMBOL_HEART,
                       SYMBOL_PIGGY_BANK,
                       SYMBOL_SOAP_BOTTLE, SYMBOL_STAR
               })
    public @interface Symbol {

    }

}
