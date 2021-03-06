package com.reversecoder.content.demo.toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.reversecoder.content.demo.R;
import com.reversecoder.content.demo.activity.ApplicationsActivity;
import com.reversecoder.content.demo.activity.MoviesActivity;
import com.reversecoder.content.demo.activity.MusicActivity;
import com.reversecoder.content.demo.activity.OthersActivity;
import com.reversecoder.content.demo.activity.PictureActivity;
import com.reversecoder.content.demo.adapter.StorageAdapter;
import com.reversecoder.content.demo.fragment.InstalledAppFragment;
import com.reversecoder.content.demo.fragment.UnusedAppFragment;

import java.util.ArrayList;

/**
 * @author Md. Rashadul Alam
 */
public class ToolbarActionModeCallback<T> implements ActionMode.Callback {

    private Context context;
    private StorageAdapter storageAdapter;
    private ArrayList<T> data;
    private boolean isFragmentInstalledApp = false;

    public ToolbarActionModeCallback(Context context, StorageAdapter listView_adapter, ArrayList<T> data) {
        this.context = context;
        this.storageAdapter = listView_adapter;
        this.data = data;
    }

    public ToolbarActionModeCallback(Context context, StorageAdapter listView_adapter, ArrayList<T> data, boolean isFragmentInstalledApp) {
        this.context = context;
        this.storageAdapter = listView_adapter;
        this.data = data;
        this.isFragmentInstalledApp = isFragmentInstalledApp;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_action, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmationDialog();
                break;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        storageAdapter.removeSelection();  // remove selection
        if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.MUSIC) {
            ((MusicActivity) context).setNullToActionMode();
        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.PICTURE) {
            ((PictureActivity) context).setNullToActionMode();
        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.MOVIE) {
            ((MoviesActivity) context).setNullToActionMode();
        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.OTHER) {
            ((OthersActivity) context).setNullToActionMode();
        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.APPLICATION_UNUSED) {
            if (isFragmentInstalledApp) {
//                Fragment listFragment = ((ApplicationsActivity) context).getFragment(0);
//                if (listFragment != null)
//                    ((InstalledAppFragment) listFragment).setNullToActionMode();
            } else {
                Fragment listFragment = ((ApplicationsActivity) context).getFragment(1);
                if (listFragment != null)
                    ((UnusedAppFragment) listFragment).setNullToActionMode();
            }
        }
    }

    private void showConfirmationDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.MUSIC) {
                            ((MusicActivity) context).deleteRows();
                        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.PICTURE) {
                            ((PictureActivity) context).deleteRows();
                        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.MOVIE) {
                            ((MoviesActivity) context).deleteRows();
                        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.OTHER) {
                            ((OthersActivity) context).deleteRows();
                        } else if (storageAdapter.getAdapterType() == StorageAdapter.ADAPTER_TYPE.APPLICATION_UNUSED) {
                            if (isFragmentInstalledApp) {
//                                Fragment listFragment = ((ApplicationsActivity) context).getFragment(0);
//                                if (listFragment != null)
//                                    ((InstalledAppFragment) listFragment).deleteRows();
                            } else {
                                Fragment listFragment = ((ApplicationsActivity) context).getFragment(1);
                                if (listFragment != null)
                                    ((UnusedAppFragment) listFragment).deleteRows();
                            }
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        String selectedItem = storageAdapter.getSelectedCount() > 1 ? storageAdapter.getSelectedCount() + " items?" : storageAdapter.getSelectedCount() + " item?";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_attention))
                .setMessage(context.getString(R.string.dialog_message) + " " + selectedItem)
                .setPositiveButton(context.getString(R.string.dialog_button_ok), dialogClickListener)
                .setNegativeButton(context.getString(R.string.dialog_button_cancel), dialogClickListener)
                .show();
    }
}
