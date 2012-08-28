package com.jkydjk.healthier.clock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jkydjk.healthier.clock.entity.FileExtension;
import com.jkydjk.healthier.clock.util.FileUtil;
import com.jkydjk.healthier.clock.util.FileListAdapter;
import com.jkydjk.healthier.clock.widget.FileView;
import com.jkydjk.healthier.clock.widget.FilePage;

//public class FileBrower extends ListActivity {
public class FileBrowerOther extends BaseActivity implements OnClickListener, OnItemClickListener {

    private FileExtension SDCARD_FILE_EXTENSION;

    private LayoutInflater inflater;

    private View contentView;

    private MediaPlayer mediaPlayer;

    private Uri fileUri;
    private Uri currentPlay;

    private FileExtension currentFileDirectoryExtension;

    private View cancelAction;
    private View enterAction;

    private FileView scrollView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        inflater = LayoutInflater.from(this);

        contentView = inflater.inflate(R.layout.file_browser_other, null);

        setContentView(contentView);

        SDCARD_FILE_EXTENSION = new FileExtension(SDCARD, getString(R.string.go_root_path), R.drawable.icon_home);

        Intent intent = getIntent();
        fileUri = (Uri) intent.getParcelableExtra("file");

        cancelAction = findViewById(R.id.cancel);
        cancelAction.setOnClickListener(this);

        enterAction = findViewById(R.id.enter);
        enterAction.setOnClickListener(this);

        scrollView = (FileView) findViewById(R.id.scroll_view);

        FilePage rootFolderPage = (FilePage) inflater.inflate(R.layout.file_page, null);

        scrollView.setMainView(rootFolderPage);

        ListView fileList = (ListView) rootFolderPage.findViewById(R.id.list);

        openOrBrowseTo(SDCARD_FILE_EXTENSION, fileList);

    }

    private void openOrBrowseTo(final FileExtension fileExtension, ListView fileList) {
        if (fileExtension != null) {
            if (fileExtension.isDirectory()) {
                 currentFileDirectoryExtension = fileExtension;
                fill(fileExtension.listFiles(), fileList);
            } else {
                openFile(fileExtension);
            }
        }
    }

    private void fill(File[] files, ListView fileList) {
        List<FileExtension> directoryEntries = new ArrayList<FileExtension>();
        for (File file : files) {
            if (file.isHidden()) {
                continue;
            }
            directoryEntries.add(new FileExtension(file));
        }
        Collections.sort(directoryEntries);

        fileList.setAdapter(new FileListAdapter(this, directoryEntries));
        fileList.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
        view.setSelected(true);

        FileExtension selectedFileExtension = (FileExtension) parent.getAdapter().getItem(position);
        
        Log.v(SDCARD_FILE_EXTENSION+"");
        Log.v(selectedFileExtension+"");

        FilePage filePage = (FilePage) inflater.inflate(R.layout.file_page, null);
        filePage.setId(selectedFileExtension.hashCode());

        ListView fileList = (ListView) filePage.findViewById(R.id.list);

        openOrBrowseTo(selectedFileExtension, fileList);
        
        scrollView.appendView(filePage);
        scrollView.scrollTo(filePage);
        
        // if (selectedFileExtension.equals(SDCARD_FILE_EXTENSION)) {
        // openOrBrowseTo(SDCARD_FILE_EXTENSION);
        //
        // } else if
        // (selectedFileExtension.equals(currentFileDirectoryExtension.getParentFile()))
        // {
        // openOrBrowseTo(new
        // FileExtension(currentFileDirectoryExtension.getParentFile()));
        //
        // } else {
        // openOrBrowseTo(directoryEntries.get(position));
        // }
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
