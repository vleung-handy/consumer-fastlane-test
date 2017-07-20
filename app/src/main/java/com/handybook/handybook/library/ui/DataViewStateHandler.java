package com.handybook.handybook.library.ui;

/**
 * interface for view handlers that need to display something based on loaded data
 *
 * it forces implementors to handle the following cases:
 * - when the data is being loaded (ex. main tab page loading, or user presses submit button)
 * - when the data is loaded and valid (ex. main tab page loaded, or booking successfully made response received)
 * - when the data is loaded and invalid, or there is an error getting it (ex. network timeout, or user input invalid)
 *
 */
public interface DataViewStateHandler<ErrorDataClass, LoadedDataClass> {

    void showErrorView(ErrorDataClass errorData);

    void showLoadingView();

    void showLoadedDataView(LoadedDataClass data);
}
