package com.example.texteditorapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FileExplorerActivity extends AppCompatActivity {
    private ListView lvFiles;
    private FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        lvFiles = findViewById(R.id.list_fileExplorer);
        fabSave = findViewById(R.id.fab_fileExplorer_save);

        final FileExplorerListAdapter adapter = new FileExplorerListAdapter(FileExplorerActivity.this,
                Uri.parse(Environment.getExternalStorageDirectory().toURI().toString()));
        lvFiles.setAdapter(adapter);

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.isDirectoryItem(position)) {
                    adapter.loadFileItemsFromPosition(position);
                }
                else {
                    final Uri saveFileUri = adapter.getUriFromPosition(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileExplorerActivity.this);
                    builder.setTitle("Confirm Overwrite")
                            .setMessage("This will replace the existing file.")
                            .setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent returnIntent = new Intent().setData(saveFileUri);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            })
                            .setNeutralButton("Cancel", null);
                    builder.create().show();
                }
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                View dialogLayout = inflater.inflate(R.layout.file_name_dialog, null);
                final EditText etFileName = dialogLayout.findViewById(R.id.editText_dialog_name);

                AlertDialog.Builder builder = new AlertDialog.Builder(FileExplorerActivity.this);
                builder.setTitle("Name the file")
                        .setView(dialogLayout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String fileName = etFileName.getText().toString();
                                if ("".equals(fileName)) {
                                    Toast.makeText(FileExplorerActivity.this, "File name not valid", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Uri saveFileUri = Uri.withAppendedPath(adapter.getExplorerUri(), fileName);
                                    Intent returnIntent = new Intent().setData(saveFileUri);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            }
                        })
                        .setNeutralButton("Cancel", null);
                builder.create().show();
            }
        });
    }
}
