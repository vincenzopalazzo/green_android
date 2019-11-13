package com.greenaddress.greenbits.ui.assets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenaddress.greenapi.data.AssetInfoData;
import com.greenaddress.greenapi.data.BalanceData;
import com.greenaddress.greenapi.data.EntityData;
import com.greenaddress.greenapi.data.NetworkData;
import com.greenaddress.greenapi.model.Model;
import com.greenaddress.greenbits.GaService;
import com.greenaddress.greenbits.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.Item> {

    private static final ObjectMapper mObjectMapper = new ObjectMapper();
    private final Map<String, Long> mAssets;
    private final List<String> mAssetsIds;
    private final OnAssetSelected mOnAccountSelected;
    private final GaService mService;
    private final NetworkData mNetworkData;
    private final Model mModel;

    @FunctionalInterface
    public interface OnAssetSelected {
        void onAssetSelected(String assetSelected);
    }

    public AssetsAdapter(final Map<String, Long> assets, final GaService service,
                         final NetworkData networkData,
                         final OnAssetSelected cb, final Model model) {
        mAssets = assets;
        mService = service;
        mOnAccountSelected = cb;
        mNetworkData = networkData;
        mModel = model;
        mAssetsIds = new ArrayList<>(mAssets.keySet());
        if (mAssetsIds.contains("btc")) {
            // Move btc as first in the list
            mAssetsIds.remove("btc");
            mAssetsIds.add(0,"btc");
        }
    }

    @Override
    public Item onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                          .inflate(R.layout.list_element_asset, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(final Item holder, final int position) {
        final String assetId = mAssetsIds.get(position);
        final boolean isBTC = "btc".equals(assetId);
        final Long satoshi = mAssets.get(assetId);
        final AssetInfoData assetInfo = mModel.getAssetsObservable().getAssetsInfos().get(assetId);
        if (mOnAccountSelected != null)
            holder.mAssetLayout.setOnClickListener(v -> mOnAccountSelected.onAssetSelected(assetId));
        if (isBTC) {
            holder.mAssetName.setText("L-BTC");
            holder.mAssetValue.setText(mService.getValueString(satoshi, false, true));
            holder.mAssetDomain.setVisibility(View.GONE);
        } else {
            holder.mAssetName.setText(assetInfo != null ? assetInfo.getName() : assetId);
            holder.mAssetValue.setText(mService.getValueString(satoshi, assetId, assetInfo, true));
            final EntityData entity = assetInfo != null ? assetInfo.getEntity() : null;
            if (entity != null && entity.getDomain() != null && !entity.getDomain().isEmpty()) {
                holder.mAssetDomain.setVisibility(View.VISIBLE);
                holder.mAssetDomain.setText(entity.getDomain());
            } else {
                holder.mAssetDomain.setVisibility(View.GONE);
            }
        }
        // Get l-btc & asset icon from asset icon map
        final Map<String, Bitmap> icons =  mModel.getAssetsObservable().getAssetsIcons();
        final String asset = isBTC ? mNetworkData.getPolicyAsset() : assetId;
        if (icons.containsKey(asset)) {
            holder.mAssetIcon.setImageBitmap(icons.get(asset));
        } else {
            holder.mAssetIcon.setImageResource(R.drawable.ic_generic_asset_icon);
        }
    }

    @Override
    public int getItemCount() {
        return mAssets.size();
    }

    static class Item extends RecyclerView.ViewHolder {

        final LinearLayout mAssetLayout;
        final TextView mAssetName;
        final TextView mAssetDomain;
        final TextView mAssetValue;
        final ImageView mAssetIcon;

        Item(final View v) {
            super(v);
            mAssetLayout = v.findViewById(R.id.assetLayout);
            mAssetName = v.findViewById(R.id.assetName);
            mAssetDomain = v.findViewById(R.id.assetDomain);
            mAssetValue = v.findViewById(R.id.assetValue);
            mAssetIcon = v.findViewById(R.id.assetIcon);
        }
    }
}