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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jkydjk.healthier.clock.entity.FileExtension;
import com.jkydjk.healthier.clock.util.FileUtil;
import com.jkydjk.healthier.clock.util.FileListAdapter;
import com.jkydjk.healthier.clock.widget.FileItem;
import com.jkydjk.healthier.clock.widget.FileFlipBook;
import com.jkydjk.healthier.clock.widget.FilePage;

//public class FileBrower extends ListActivity {
public class FileBrower extends BaseActivity implements OnClickListener, OnItemClickListener {

    public static File currentFileDirectory;

    private LayoutInflater inflater;

    private MediaPlayer mediaPlayer;

    private boolean filter = false;
    private int filterFileType = 0;

    private Uri fileUri;
    private Uri currentPlay;

    private View cancelAction;
    private View enterAction;

    private FileFlipBook fileView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        inflater = LayoutInflater.from(this);

        setContentView(R.layout.file_browser);

        Intent intent = getIntent();
        fileUri = (Uri) intent.getParcelableExtra("file");
        
        filter = true;
        filterFileType = FileUtil.AUDIO;

        cancelAction = findViewById(R.id.cancel);
        cancelAction.setOnClickListener(this);

        enterAction = findViewById(R.id.enter);
        enterAction.setOnClickListener(this);

        fileView = (FileFlipBook) findViewById(R.id.scroll_view);

        FilePage rootFolderPage = (FilePage) inflater.inflate(R.layout.file_page, null);

        rootFolderPage.setFileExtension(FileExtension.SDCARD_FILE_EXTENSION);

        fileView.appendPage(rootFolderPage, true);

        openOrBrowseTo(rootFolderPage);

    }

    private void openOrBrowseTo(final FilePage filePage) {
        FileExtension fileExtension = filePage.getFileExtension();
        if (fileExtension != null) {
            if (fileExtension.isDirectory()) {
                File[] files = fileExtension.listFiles();
                if (files.length > 0) {
                    List<FileExtension> fileExtensionEntries = new ArrayList<FileExtension>();
                    for (File file : files) {
                        if (file.isHidden()) {
                            continue;
                        }
                        if (filter && !FileUtil.fileFilter(file, filterFileType) && !file.isDirectory()) {
                            continue;
                        }

                        fileExtensionEntries.add(new FileExtension(file));
                    }
                    if (fileExtensionEntries.size() > 0) {
                        Collections.sort(fileExtensionEntries);
                        ListView fileList = (ListView) filePage.findViewById(R.id.list);
                        fileList.setAdapter(new FileListAdapter(this, fileExtensionEntries));
                        fileList.setOnItemClickListener(this);
                    } else {
                        filePage.showTipLayout(true, FileExtension.NO_FILES_IN_THE_FLODER);
                    }
                } else {
                    filePage.showTipLayout(true, FileExtension.FLODER_IS_EMPTY);
                }

            } else {
                openFile(fileExtension);
            }
            currentFileDirectory = fileExtension.getFile();
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileItem item = (FileItem) view;
        item.setIsSelected(true);
        FileExtension selectedFileExtension = (FileExtension) parent.getAdapter().getItem(position);
        if (selectedFileExtension.isDirectory()) {
            FilePage filePage = (FilePage) inflater.inflate(R.layout.file_page, null);
            filePage.setFileExtension(selectedFileExtension);
            openOrBrowseTo(filePage);
            fileView.appendPage(filePage, true);
        } else {
            openFile(selectedFileExtension);
            fileView.scrollToPage(selectedFileExtension.getLevel());
            currentFileDirectory = selectedFileExtension.getFile();
        }
    }

    // 打开指定文件
    protected void openFile(FileExtension fileExtension) {
        File file = fileExtension.getFile();
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
