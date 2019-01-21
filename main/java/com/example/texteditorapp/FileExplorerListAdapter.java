package com.example.texteditorapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

class FileExplorerListAdapter extends ArrayAdapter<File> {
    private Context context;
    private ArrayList<File> filesList = new ArrayList<>(30);
    private Uri explorerUri;
    private static SimpleDateFormat dateFormat
            = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

    FileExplorerListAdapter(@NonNull Context context, Uri startingUri) {
        super(context, 0);
        this.context = context;
        this.explorerUri = startingUri;
        extractFilesData(new File(explorerUri.getPath()));
    }

    private void extractFilesData(File currentDir) {
        File[] dirContents = currentDir.listFiles();
        filesList.addAll(Arrays.asList(dirContents));
    }

    void loadFileItemsFromPosition(int position) {
        File explorerDir = filesList.get(position);
        explorerUri = Uri.parse(explorerDir.toURI().toString());
        filesList.clear();
        extractFilesData(explorerDir);
        notifyDataSetChanged();
    }

    Uri getUriFromPosition(int position) {
        return Uri.parse(filesList.get(position).toURI().toString());
    }

    Uri getExplorerUri() {
        return explorerUri;
    }

    boolean isDirectoryItem(int position) {
        return filesList.get(position).isDirectory();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.file_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.ivIcon = convertView.findViewById(R.id.image_listItem);
            viewHolder.tvName = convertView.findViewById(R.id.text_listItem_name);
            viewHolder.tvSize = convertView.findViewById(R.id.text_listItem_size);
            viewHolder.tvLastModified = convertView.findViewById(R.id.text_listItem_lastModified);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon.setImageResource(filesList.get(position).isDirectory()
                ? R.drawable.ic_folder
                : R.drawable.ic_file);
        viewHolder.tvName.setText(filesList.get(position).getName());
        viewHolder.tvSize.setVisibility(filesList.get(position).isDirectory()
                ? View.INVISIBLE
                : View.VISIBLE);
        viewHolder.tvSize.setText(String.valueOf(filesList.get(position).length() / 1024f) + " KB");
        viewHolder.tvLastModified.setText(
                dateFormat.format(filesList.get(position).lastModified()));

        return convertView;
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Nullable
    @Override
    public File getItem(int position) {
        return filesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvSize, tvLastModified;
    }
}
