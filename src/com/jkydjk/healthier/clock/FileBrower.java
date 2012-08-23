package com.jkydjk.healthier.clock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.jkydjk.healthier.clock.util.FileUtil;
import com.jkydjk.healthier.clock.util.IconifiedTextListAdapter;
import com.jkydjk.healthier.clock.widget.IconifiedText;

//public class FileBrower extends ListActivity {
public class FileBrower extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private LayoutInflater mFactory;
	
	private MediaPlayer mediaPlayer;

	private Uri fileUri;
	private Uri currentPlay;

	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();

	private File currentDirectory;

	private View cancelAction;
	private View enterAction;

	private ListView fileList;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.file_browser);
		
		mFactory = LayoutInflater.from(this);
		
		Intent intent = getIntent();
		fileUri = (Uri) intent.getParcelableExtra("file");

		cancelAction = findViewById(R.id.cancel);
		cancelAction.setOnClickListener(this);

		enterAction = findViewById(R.id.enter);
		enterAction.setOnClickListener(this);

		fileList = (ListView) findViewById(R.id.files);

		browseTo(SDCARD);
	}

	private void browseTo(final File file) {
		if (file != null) {
			this.setTitle(file.getAbsolutePath());
			// 浏览指定的目录,如果是文件则进行打开操作
			if (file.isDirectory()) {
				currentDirectory = file;
				fill(file.listFiles());
			} else {
				openFile(file);
			}
		}
	}

	// 打开指定文件
	protected void openFile(File file) {

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

	private void fill(File[] files) {
		directoryEntries.clear();
		Drawable currentIcon = null;

		for (File file : files) {

			if (file.isHidden()) {
				continue;
			}

			if (file.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.icon_folder);
			} else {

				if (FileUtil.fileType(file) != FileUtil.AUDIO) {
					continue;
				}
				
				switch (FileUtil.fileType(file)) {
				case FileUtil.IMAGE:
					currentIcon = getResources().getDrawable(R.drawable.image);
					break;

				case FileUtil.WEBTEXT:
					currentIcon = getResources().getDrawable(R.drawable.webtext);
					break;

				case FileUtil.PACKAGE:
					currentIcon = getResources().getDrawable(R.drawable.packed);
					break;

				case FileUtil.AUDIO:
					currentIcon = getResources().getDrawable(R.drawable.icon_music);
					break;

				case FileUtil.VIDEO:
					currentIcon = getResources().getDrawable(R.drawable.video);
					break;

				default:
					currentIcon = getResources().getDrawable(R.drawable.text);
					break;
				}

			}

			// 确保只显示文件名、不显示路径如：/sdcard/111.txt就只是显示111.txt
			int currentPathStringLenght = currentDirectory.getAbsolutePath().length();
			
//			new CheckedTextView(this);
//			View view = mFactory.inflate(R.layout.system_ringtone_item, null, false);
			
			directoryEntries.add(new IconifiedText(file.getAbsolutePath().substring(currentPathStringLenght), currentIcon));
		}

		Collections.sort(directoryEntries);

		// 如果不是根目录则添加 根目录项 上一级目录项
		if (!SDCARD.equals(currentDirectory)) {
			directoryEntries.add(0, new IconifiedText(getString(R.string.go_root_path), getResources().getDrawable(R.drawable.goroot)));
			directoryEntries.add(1, new IconifiedText(getString(R.string.go_parent_directory), getResources().getDrawable(R.drawable.uponelevel)));
		}

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);

		itla.setListItems(directoryEntries);

		fileList.setAdapter(itla);
		fileList.setOnItemClickListener(this);
		// fileList.setSelection(0);
	}

	// 得到当前目录的绝对路径
	public String getCurrentDirectory() {
		return currentDirectory.getAbsolutePath();
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

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		String selectedFileString = directoryEntries.get(position).getText();

		if (selectedFileString.equals(getString(R.string.go_root_path))) { // 返回根目录
			browseTo(SDCARD);

		} else if (selectedFileString.equals(getString(R.string.go_parent_directory))) { // 返回上一级目录
			browseTo(currentDirectory.getParentFile());

		} else {
			browseTo(new File(currentDirectory.getAbsolutePath() + directoryEntries.get(position).getText()));
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
