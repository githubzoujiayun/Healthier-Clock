package com.jkydjk.healthier.clock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jkydjk.healthier.clock.entity.FileExtension;
import com.jkydjk.healthier.clock.util.FileUtil;
import com.jkydjk.healthier.clock.util.FileListAdapter;

//public class FileBrower extends ListActivity {
public class FileBrower extends BaseActivity implements OnClickListener, OnItemClickListener {

    private FileExtension SDCARD_FILE_EXTENSION;

    private LayoutInflater mFactory;

    private MediaPlayer mediaPlayer;

    private Uri fileUri;
    private Uri currentPlay;

    private List<FileExtension> directoryEntries = new ArrayList<FileExtension>();

    private FileExtension currentFileDirectoryExtension;

    private View cancelAction;
    private View enterAction;

    private ListView fileList;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.file_browser);

        mFactory = LayoutInflater.from(this);
        
        SDCARD_FILE_EXTENSION = new FileExtension(SDCARD, getString(R.string.go_root_path), R.drawable.icon_home);

        Intent intent = getIntent();
        fileUri = (Uri) intent.getParcelableExtra("file");

        cancelAction = findViewById(R.id.cancel);
        cancelAction.setOnClickListener(this);

        enterAction = findViewById(R.id.enter);
        enterAction.setOnClickListener(this);

        fileList = (ListView) findViewById(R.id.files);
        
        openOrBrowseTo(SDCARD_FILE_EXTENSION);
    }

    private void openOrBrowseTo(final FileExtension fileExtension) {
        if (fileExtension != null) {
            if (fileExtension.isDirectory()) {
                currentFileDirectoryExtension = fileExtension;
                
                Log.v("fileExtension.isDirectory() : "+ fileExtension);
                fill(fileExtension.listFiles());
            } else {
                
                Log.v("fileExtension not directory : "+ fileExtension);
                openFile(fileExtension);
            }
        }
    }

    private void fill(File[] files) {
        directoryEntries.clear();
        
        for (File file : files) {
            if (file.isHidden()) {
                continue;
            }
            directoryEntries.add(new FileExtension(file));
        }

        Collections.sort(directoryEntries);

        // 如果不是根目录则添加 根目录项 上一级目录项
        if (!SDCARD_FILE_EXTENSION.equals(currentFileDirectoryExtension)) {
            directoryEntries.add(0, SDCARD_FILE_EXTENSION);
            directoryEntries.add(1, new FileExtension(currentFileDirectoryExtension.getParentFile(), getString(R.string.go_parent_directory), R.drawable.icon_back));
        }

        FileListAdapter itla = new FileListAdapter(this, directoryEntries);

        fileList.setAdapter(itla);
        fileList.setOnItemClickListener(this);
    }

    // 得到当前目录的绝对路径
    public String getCurrentDirectory() {
        return currentFileDirectoryExtension.getAbsolutePath();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        FileExtension selectedFileExtension = directoryEntries.get(position);

        if (selectedFileExtension.equals(SDCARD_FILE_EXTENSION)) {
            openOrBrowseTo(SDCARD_FILE_EXTENSION);

        } else if (selectedFileExtension.equals(currentFileDirectoryExtension.getParentFile())) {
            openOrBrowseTo(new FileExtension(currentFileDirectoryExtension.getParentFile()));

        } else {
            openOrBrowseTo(directoryEntries.get(position));
        }
    }
    
    // 打开指定文件
    protected void openFile(FileExtension fileExtension) {
        File file = fileExtension;
        switch (FileUtil.fileType(file)) {
        case FileUtil.IMAGE:

            break;

        case FileUtil.WEBTEXT:

            break;

        case FileUtil.PACKAGE:

            break;

        case FileUtil.AUDIO:
            autoPlayMedia(Uri.fromFile(file));
            break;

        case FileUtil.VIDEO:

            break;

        default:

            break;
        }
    }

    private void autoPlayMedia(Uri uri) {
        fileUri = uri;
        if (currentPlay != uri) {
            if (mediaPlayer != null) {
                stopPlayMedia();
            }
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();
            currentPlay = uri;

            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    stopPlayMedia();
                }
            });

        } else {
            stopPlayMedia();
        }
    }

    private void stopPlayMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            currentPlay = null;
        }
    }
    
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.cancel:
            finish();
            break;

        case R.id.enter:
            // Intent intent = getIntent();
            // intent.putExtra("alert", alert);
            // setResult(RESULT_OK, intent);
            finish();
            break;

        default:
            break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayMedia();
    }

}
