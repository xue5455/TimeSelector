package com.zxmn.time.adapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by XUE on 2016/9/27.
 */
public abstract class WheelAdapter {

    private Set<OnDataSetChangedListener> mListeners = new HashSet<>();

    public void registerDataSetChangedListener(OnDataSetChangedListener listener) {
        mListeners.add(listener);
    }

    public void unregisterDataSetChangedListener(OnDataSetChangedListener listener) {
        mListeners.remove(listener);
    }

    public abstract String getItem(int position);

    public abstract int getItemCount();

    public void notifyDataSetChanged() {
        for (OnDataSetChangedListener listener : mListeners) {
            listener.onDataSetChanged();
        }
    }

    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    public abstract int getItemHeight();
}
