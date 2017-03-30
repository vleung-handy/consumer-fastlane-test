package com.handybook.handybook.autocomplete;

import android.support.v7.app.AppCompatActivity;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jtse on 10/7/16.
 */
public class AutoCompleteAddressFragmentTest extends RobolectricGradleTestWrapper {

    private static final String ADDRESS_LINE_1 = "1720 77th Street";
    private static final String ADDRESS_LINE_2 = "APT 6";
    private static final String ADDRESS_CITY = "New York";
    private static final String ADDRESS_STATE = "NY";

    private AutoCompleteAddressFragment mFragment;

    @Inject
    AddressAutoCompleteManager mDataManager;
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;

        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        String json = IOUtils.getJsonStringForTest("place_prediction_1720.json");

        PlacePredictionResponse predictionResponse1720 = new GsonBuilder()
                .create()
                .fromJson(json, PlacePredictionResponse.class);

        when(mDataManager.getAddressPrediction("1720")).thenReturn(predictionResponse1720);

        Configuration config = mock(Configuration.class);
        when(config.isAddressAutoCompleteEnabled()).thenReturn(true);
        mFragment = AutoCompleteAddressFragment.newInstance(
                null,
                ADDRESS_LINE_1,
                ADDRESS_LINE_2,
                ADDRESS_CITY,
                ADDRESS_STATE,
                config
        );

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    /**
     * Tests that the API is able to at least successfully return the number of predictions that come back.
     */
    @Test
    public void testBaseCase() {
        assertEquals(
                "There should be 6 predictions",
                6,
                mDataManager.getAddressPrediction("1720").predictions.size()
        );
    }

    /**
     * Obviously, if we submit the wrong query, no results should come back.
     */
    @Test
    public void testNoResult() {
        PlacePredictionResponse addressPrediction = mDataManager.getAddressPrediction("234rsdf");
        assertNull("There should be no predictions", addressPrediction);
    }

    /**
     * This tests that we only want the street_address types. Not geocode, not route, etc.
     */
    @Test
    public void testFilterStreetAddress() {
        PlacePredictionResponse response = mDataManager.getAddressPrediction("1720");
        response.filter(null);

        assertEquals(
                "There should be 2 predictions",
                2,
                mDataManager.getAddressPrediction("1720").predictions.size()
        );
    }

    /**
     * We only want to show results that fall under the same city/zip as the user.
     */
    @Test
    public void testCityStateFilter() {

        PlacePredictionResponse response = mDataManager.getAddressPrediction("1720");

        ZipValidationResponse.ZipArea mFilter;
        mFilter = new ZipValidationResponse.ZipArea("Brooklyn", "NY", "11214");
        response.filter(mFilter);

        assertEquals("There should be 1 predictions", 1, response.predictions.size());

        assertEquals("City should be Brooklyn", "Brooklyn", response.predictions.get(0).getCity());
        assertEquals("State should be NY", "NY", response.predictions.get(0).getState());
    }

    /**
     * For users with an existing address, the address should already be pre-filled.
     */
    @Test
    public void testAddressPrefill() {
        assertEquals(
                "Address line 1 should be prefilled",
                ADDRESS_LINE_1,
                mFragment.mStreet.getText().toString()
        );
    }

    /**
     * There should be only 1 suggestion in the autocomplete dropdown.
     */
    @Test
    public void testSuggestionBinding() {
        List<String> strings = mFragment.makeApiCall("1720");
        mFragment.onAutoCompleteResultsReceived(strings);

        assertEquals(
                "There should be 2 results showing (since we didn't filter from zip)",
                2,
                mFragment.mListPopupWindow.getListView().getAdapter().getCount()
        );
    }

    /**
     * The suggestion list should show if there are results and should hide when there aren't
     */
    @Test
    public void testListPopupBehavior() {
        List<String> strings = mFragment.makeApiCall("1720");

        mFragment.onAutoCompleteResultsReceived(strings);
        assertEquals("Listbox should be showing", true, mFragment.mListPopupWindow.isShowing());

        //now set the list to be empty
        mFragment.onAutoCompleteResultsReceived(new ArrayList<String>());
        assertEquals("Listbox should be invisible", false, mFragment.mListPopupWindow.isShowing());
    }

    /**
     * Tests that the address gets populated correctly with the user-selected autocomplete option
     */
    @Test
    public void testUserSelection() {

        mFragment.mStreet.setText("FAKE ADDRESS");
        List<String> strings = mFragment.makeApiCall("1720");
        mFragment.onAutoCompleteResultsReceived(strings);

        mFragment.mListPopupWindow.performItemClick(0);
        assertEquals(
                "The street address needs to be set accordingly",
                ADDRESS_LINE_1,
                mFragment.mStreet.getText().toString()
        );
    }

    /**
     * Validation states that if the street isn't filled, then fails validation
     */
    @Test
    public void testAddressValidation() {
        mFragment.mStreet.setText("     ");
        assertEquals(false, mFragment.validateFields());

        //false because it's less than 2 significant letters
        mFragment.mStreet.setText("  d   ");
        assertEquals(false, mFragment.validateFields());

        mFragment.mStreet.setText("  d  sd s ");
        assertEquals(true, mFragment.validateFields());

        mFragment.mStreet.setText(null);
        assertEquals(false, mFragment.validateFields());
    }
}
