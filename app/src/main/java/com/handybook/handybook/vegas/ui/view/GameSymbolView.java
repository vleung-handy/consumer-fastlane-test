package com.handybook.handybook.vegas.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.handybook.handybook.R;
import com.handybook.handybook.vegas.model.GameSymbol;

import java.util.HashMap;

public class GameSymbolView extends android.support.v7.widget.AppCompatImageView {

    public static final int[] RESOURCE_IDS = {
            R.drawable.img_game_symbol_dustpan,
            R.drawable.img_game_symbol_bucket_green,
            R.drawable.img_game_symbol_logo_handy,
            R.drawable.img_game_symbol_heart,
            R.drawable.img_game_symbol_piggy_bank,
            R.drawable.img_game_symbol_soap_bottle,
            R.drawable.img_game_symbol_star,
            R.drawable.img_game_symbol_sponge_crying


    };
    public static final GameSymbol[] SYMBOLS = {
            GameSymbol.DUSTPAN,
            GameSymbol.BUCKET_GREEN,
            GameSymbol.HANDY_LOGO,
            GameSymbol.HEART,
            GameSymbol.PIGGY_BANK,
            GameSymbol.SOAP_BOTTLE,
            GameSymbol.STAR,
            GameSymbol.CRYING_SPONGE
    };
    public static final HashMap<GameSymbol, Integer> SYMBOLS_TO_RESOURCE_IDS = new HashMap<>();
    public static final HashMap<Integer, GameSymbol> RESOURCE_IDS_TO_SYMBOLS = new HashMap<>();

    private GameSymbol mSymbol;

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
            setSymbol(RESOURCE_IDS_TO_SYMBOLS.get(
                    RESOURCE_IDS[ta.getInt(R.styleable.GameSymbolView_symbol, 0)]
            ));
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

    public void setSymbol(final GameSymbol symbol) {
        mSymbol = symbol;
        update();
    }

    private static int gerResourceId(final GameSymbol symbol) {
        return SYMBOLS_TO_RESOURCE_IDS.get(symbol);
    }

}
