package com.example.texteditorapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ContentFragment extends Fragment {
    private static final String ARG_FILE_URI = "ARG_FILE_URI";

    public static ContentFragment newInstance(Uri file) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle(1);
        args.putParcelable(ARG_FILE_URI, file);
        fragment.setArguments(args);
        return fragment;
    }

    private Uri fileUri;
    private EditText etContent;

    public ContentFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileUri = getArguments().getParcelable(ARG_FILE_URI);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_content, container, false);
        etContent = layout.findViewById(R.id.editText_content);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (fileUri != null && getActivity() != null) {
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(fileUri);
                if (inputStream != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                    etContent.setText(fileContent.toString());
                    inputStream.close();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void saveFile() {
        if (fileUri != null) {
            try {
                OutputStream outputStream = getActivity().getContentResolver().openOutputStream(fileUri);
                if (outputStream != null) {
                    outputStream.write(etContent.getText().toString().getBytes());
                    Toast.makeText(getContext(), "File saved", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem saveItem = menu.findItem(R.id.action_toolbar_save);
        if (saveItem != null) {
            saveItem.setEnabled(fileUri != null);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem saveItem = menu.findItem(R.id.action_toolbar_save);
        if (saveItem != null) {
            saveItem.setEnabled(fileUri != null);
        }
    }

    void saveFileAs(Uri saveFileUri) {
        try {
            OutputStream outputStream = getActivity().getContentResolver().openOutputStream(saveFileUri);
            if (outputStream != null) {
                outputStream.write(etContent.getText().toString().getBytes());
                Toast.makeText(getContext(), "File saved", Toast.LENGTH_SHORT).show();
                fileUri = saveFileUri;
                getActivity().invalidateOptionsMenu();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getContentFileName(int position) {
        if (fileUri == null) {
            return "Untitled " + position;
        }
        else {
            return new File(fileUri.getPath()).getName().split("[:]", 2)[1]; // Split to remove "Primary:" in name.
        }
    }
}
