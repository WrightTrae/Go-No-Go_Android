package com.wright.android.t_minus.business.edit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.business.edit.BusinessListFragment.OnListFragmentInteractionListener;
import com.wright.android.t_minus.objects.Business;

import java.util.List;

public class MyBusinessRecyclerViewAdapter extends RecyclerView.Adapter<MyBusinessRecyclerViewAdapter.ViewHolder> {

    private final List<Business> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyBusinessRecyclerViewAdapter(List<Business> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mAddressView.setText(mValues.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mNameView;
        private final TextView mAddressView;
        public Business mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.cell_business_name);
            mAddressView = view.findViewById(R.id.cell_business_address);
        }
    }
}
