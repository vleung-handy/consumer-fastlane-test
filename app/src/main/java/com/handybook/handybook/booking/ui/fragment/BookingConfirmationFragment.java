package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.core.OnOneClickListener;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.booking.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.ui.widget.PasswordInputTextView;
import com.handybook.handybook.util.TextUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;


//THIS CLASS HAS BEEN BROKEN UP INTO SUB FRAGMENT TYPES BUT WE ARE STILL USING THE ORIGINAL FRAGMENT IN THE BOOKING FLOW, SEE NOTE
//TODO: Break this into distinct fragments, see BookingEditNoteToPro, BookingEditEntryInformation, still need to do the password prompt
public final class BookingConfirmationFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener
{
    private static final int PAGE_ENTRY_INFORMATION = 0;
    private static final int PAGE_NOTE_TO_PRO = 1;
    private static final int PAGE_PASSWORD_PROMPT = 2;

    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    private static final String STATE_KEY_HIGHLIGHT = "KEY_HIGHLIGHT";
    private static final String STATE_PWD_HIGHLIGHT = "PWD_HIGHLIGHT";

    private BookingOptionsView optionsView;
    private BookingPostInfo postInfo;
    private int page;
    private boolean isNewUser;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.header_text)
    TextView headerText;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.keys_text)
    BasicInputTextView keysText;
    @Bind(R.id.pwd_text)
    PasswordInputTextView pwdText;

    public static BookingConfirmationFragment newInstance(final int page, final boolean isNewUser)
    {
        final BookingConfirmationFragment fragment = new BookingConfirmationFragment();
        final Bundle args = new Bundle();

        args.putInt(EXTRA_PAGE, page);
        args.putBoolean(EXTRA_NEW_USER, isNewUser);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(EXTRA_PAGE, 0);
        isNewUser = getArguments().getBoolean(EXTRA_NEW_USER, false);
        mixpanel.trackEventAppTrackConfirmation();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_confirmation, container, false);

        ButterKnife.bind(this, view);

        keysText.setMinLength(2);
        keysText.setHint(getString(R.string.where_hide_key));
        keysText.addTextChangedListener(keyTextWatcher);

        postInfo = bookingManager.getCurrentPostInfo();

        if (page == PAGE_ENTRY_INFORMATION)
        {
            final String text = getString(R.string.payment_confirmed);
            final SpannableString spanText = new SpannableString(text);

            spanText.setSpan(new CalligraphyTypefaceSpan(TextUtils.get(getActivity(),
                            "CircularStd-Medium.otf")), 0, text.indexOf("\n"),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            headerText.setText(spanText, TextView.BufferType.SPANNABLE);

            final BookingOption option = new BookingOption();
            option.setType(BookingOption.TYPE_OPTION);
            option.setDefaultValue("0");

            option.setOptions(new String[]{getString(R.string.will_be_home),
                    getString(R.string.doorman), getString(R.string.will_hide_key)});

            optionsView = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
            ((BookingOptionsSelectView) optionsView).hideTitle();

            final String indexStr = postInfo.getGetInId();
            final int index = indexStr == null ? 0 : Integer.parseInt(indexStr);
            ((BookingOptionsSelectView) optionsView).setCurrentIndex(index);
        }
        else if (page == PAGE_NOTE_TO_PRO)
        {
            headerText.setText(getString(R.string.pro_to_know));
            if (!isNewUser)
            {
                nextButton.setText(getString(R.string.finish));
            }

            final BookingOption option = new BookingOption();
            option.setType(BookingOption.TYPE_TEXT);
            option.setDefaultValue(getString(R.string.additional_pro_info));

            optionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);
            ((BookingOptionsTextView) optionsView).setValue(postInfo.getExtraMessage());
        }
        else if (page == PAGE_PASSWORD_PROMPT)
        {
            headerText.setText(getString(R.string.use_your_pwd));
            nextButton.setText(getString(R.string.finish));
            pwdText.setVisibility(View.VISIBLE);
        }

        if (page == PAGE_ENTRY_INFORMATION || page == PAGE_NOTE_TO_PRO)
        {
            optionsLayout.addView(optionsView, 0);
        }

        nextButton.setOnClickListener(nextClicked);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_KEY_HIGHLIGHT))
            {
                keysText.highlight();
            }
            if (savedInstanceState.getBoolean(STATE_PWD_HIGHLIGHT))
            {
                pwdText.highlight();
            }
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_HIGHLIGHT, keysText.isHighlighted());
        outState.putBoolean(STATE_PWD_HIGHLIGHT, pwdText.isHighlighted());
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        if (page == PAGE_ENTRY_INFORMATION)
        {
            ((BaseActivity) getActivity()).setOnBackPressedListener(this);
        }
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        if (page == PAGE_ENTRY_INFORMATION)
        {
            ((BaseActivity) getActivity()).setOnBackPressedListener(null);
        }
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setEnabled(false);
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setEnabled(true);
        nextButton.setClickable(true);
    }

    private boolean validateFields()
    {
        boolean validate = true;

        if (page == PAGE_ENTRY_INFORMATION && ((BookingOptionsSelectView) optionsView).getCurrentIndex() == 2
                && !keysText.validate())
        {
            validate = false;
        }

        if (page == PAGE_PASSWORD_PROMPT)
        {
            if (!pwdText.validate())
            {
                validate = false;
            }
            if (pwdText.getPassword().length() < 8)
            {
                validate = false;
                pwdText.highlight();
                toast.setText(getString(R.string.pwd_length_error));
                toast.show();
            }
        }

        return validate;
    }

    @Override
    public final void onBack()
    {
        if (page == PAGE_ENTRY_INFORMATION)
        {
            showBookings();
        }
    }

    private final View.OnClickListener nextClicked = new OnOneClickListener()
    {
        @Override
        public void onOneClick(final View view)
        {
            if (!validateFields() ||
                    bookingManager.getCurrentTransaction() == null)
                    /*
                    hot fix to prevent NPE caused by rapid multi-click
                    of the next button
                     */
            {
                return;
            }

            disableInputs();
            progressDialog.show();

            //TODO: Finish breaking up booking confirmation fragment/activity and then call the specific fragment instead of passing along an EXTRA_PAGE

            if (page == PAGE_ENTRY_INFORMATION)
            {
                //goto the next page / activity
                final Intent intent = new Intent(getActivity(), BookingConfirmationActivity.class);
                intent.putExtra(BookingConfirmationActivity.EXTRA_PAGE, 1);
                intent.putExtra(BookingConfirmationActivity.EXTRA_NEW_USER, isNewUser);
                startActivity(intent);
            }
            else if (page == PAGE_NOTE_TO_PRO && isNewUser)
            {
                //goto the next page / activity
                final Intent intent = new Intent(getActivity(), BookingConfirmationActivity.class);
                intent.putExtra(BookingConfirmationActivity.EXTRA_PAGE, 2);
                intent.putExtra(BookingConfirmationActivity.EXTRA_NEW_USER, isNewUser);
                startActivity(intent);
            }
            else
            {
                //submit the information
                if (page == PAGE_PASSWORD_PROMPT)
                {
                    postInfo.setPassword(pwdText.getPassword());
                }

                dataManager.addBookingPostInfo(bookingManager.getCurrentTransaction().getBookingId(),
                        postInfo, new DataManager.Callback<Void>()
                        {
                            @Override
                            public void onSuccess(final Void response)
                            {
                                if (!allowCallbacks ||
                                        bookingManager.getCurrentTransaction() == null)
                                    /*
                                    hot fix to prevent NPE caused by rapid multi-click
                                    of the next button
                                     */
                                {
                                    return;
                                }

                                String bookingId = Integer.toString(bookingManager.getCurrentTransaction().getBookingId());
                                showBookingDetails(bookingId);
                                enableInputs();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error)
                            {
                                if (!allowCallbacks)
                                {
                                    return;
                                }

                                enableInputs();
                                progressDialog.dismiss();
                                dataManagerErrorHandler.handleError(getActivity(), error);
                            }
                        });
            }
        }
    };

    private void showBookings()
    {
        bookingManager.clearAll();

        final Intent intent = new Intent(getActivity(), BookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showBookingDetails(String bookingId) {
        bookingManager.clearAll();
        User user = userManager.getCurrentUser();
        dataManager.getBooking(bookingId,
                new DataManager.Callback<Booking>()
                {
                    @Override
                    public void onSuccess(final Booking booking)
                    {
                        final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                        intent.putExtra(BundleKeys.BOOKING, booking);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        dataManagerErrorHandler.handleError(getActivity(), error);
                        startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
                    }
                });
    }

    private final BookingOptionsView.OnUpdatedListener textUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            postInfo.setExtraMessage(view.getCurrentValue());
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated;
    {
        optionUpdated = new BookingOptionsView.OnUpdatedListener()
        {
            @Override
            public void onUpdate(final BookingOptionsView view)
            {
                final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
                postInfo.setGetInId(Integer.toString(index));

                if (index == 2)
                {
                    keysText.setVisibility(View.VISIBLE);
                }
                else
                {
                    keysText.unHighlight();
                    keysText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onShowChildren(final BookingOptionsView view,
                                       final String[] items)
            {
            }

            @Override
            public void onHideChildren(final BookingOptionsView view,
                                       final String[] items)
            {
            }
        };
    }

    private final TextWatcher keyTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
                                      final int count, final int after)
        {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
                                  final int before, final int count)
        {
        }

        @Override
        public void afterTextChanged(final Editable editable)
        {
            postInfo.setGetInText(keysText.getInput());
        }
    };
}
