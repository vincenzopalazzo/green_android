package com.greenaddress.greenbits.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.greenaddress.greenapi.ConnectionManager;
import com.greenaddress.greenapi.data.NetworkData;
import com.greenaddress.greenapi.model.Model;
import com.greenaddress.greenbits.GaService;
import com.greenaddress.greenbits.GreenAddressApplication;
import com.greenaddress.greenbits.ui.preferences.PrefKeys;

import static android.content.Context.MODE_PRIVATE;

public abstract class GAFragment extends Fragment {
    private GreenAddressApplication mApp;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        mApp = (GreenAddressApplication) getActivity().getApplication();

        try {
            context.getTheme().applyStyle(ThemeUtils.getThemeFromNetworkId(mApp.getCurrentNetworkData(), context, null),
                                          true);
        } catch (final Exception e) {
            // Some reports show NullPointer Exception in applying style
            // Applying theme is not mandatory, doing nothing here
        }
    }

    protected boolean isDisconnected() {
        return getConnectionManager() != null && getConnectionManager().isDisconnected();
    }

    protected GaActivity getGaActivity() {
        return (GaActivity) getActivity();
    }

    protected NetworkData getNetwork() {
        return ((GaActivity) getActivity()).getNetwork();
    }

    protected Model getModel() {
        return mApp.getModel();
    }

    protected ConnectionManager getConnectionManager() {
        return mApp.getConnectionManager();
    }

    public SharedPreferences cfg() {
        return getContext().getSharedPreferences(network(), MODE_PRIVATE);
    }

    public String network() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getString(PrefKeys.NETWORK_ID_ACTIVE,
                                                                                     "mainnet");
    }
}
