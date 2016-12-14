package com.handybook.handybook.booking.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CommitmentPricesMap extends HashMap<String, CommitmentType>
{
    public ArrayList<BookingPriceInfo> toPriceTable()
    {
        final ArrayList<BookingPriceInfo> priceTable = new ArrayList<>();

        HashSet<String> keys = (HashSet<String>) get("no_commitment")
                .get("0")
                .getFrequencyHashMap()
                .get("price")
                .getPriceItemHashMap()
                .keySet();

        for (String key : get("no_commitment")
                .get("0")
                .getFrequencyHashMap()
                .get("price")
                .getPriceItemHashMap().get(key))
        {
            priceTable.add(new BookingPriceInfo(

            ))
        } ;
        BookingPriceInfo bpi = new BookingPriceInfo();
        return priceTable;
    }
}
