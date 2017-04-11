package com.handybook.handybook.library.ui.view.snowflake;

class Random {

    private static final java.util.Random RANDOM = new java.util.Random();

    public float getRandom(float lower, float upper) {
        float min = Math.min(lower, upper);
        float max = Math.max(lower, upper);
        return getRandom(max - min) + min;
    }

    public float getRandom(float upper) {
        return RANDOM.nextFloat() * upper;
    }

    public int getRandom(int upper) {
        if (upper <= 0) {
            return 0;
        }
        return RANDOM.nextInt(upper);
    }

}
