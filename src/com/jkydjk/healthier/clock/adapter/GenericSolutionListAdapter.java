package com.jkydjk.healthier.clock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.BaseSolution;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class GenericSolutionListAdapter<T> extends BaseAdapter {

  private LayoutInflater layoutInflater;

  private List<T> items = new ArrayList<T>();

  private Map<Integer, View> views = new HashMap<Integer, View>();

  ImageLoader imageLoader;

  DisplayImageOptions options;

  public GenericSolutionListAdapter(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  public GenericSolutionListAdapter(Context context, List<T> list) {
    layoutInflater = LayoutInflater.from(context);
    items = list;

    imageLoader = ImageLoader.getInstance();

    options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_preview_thumb).showImageForEmptyUri(R.drawable.image_preview_thumb).resetViewBeforeLoading().cacheInMemory()
        .cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(1000).build();

  }

  public int getCount() {
    return items.size();
  }

  public Object getItem(int position) {
    return items.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View view = views.get(position);
    if (view != null) {
      return view;
    }

    view = layoutInflater.inflate(R.layout.solution_list_item, parent, false);

    GenericSolution solution = (GenericSolution) items.get(position);

    ImageView imageView = (ImageView) view.findViewById(R.id.image);

    imageLoader.displayImage(RequestRoute.REQUEST_PATH + solution.getListImage(), imageView, options);

    TextView titleTextView = (TextView) view.findViewById(R.id.title);
    titleTextView.setText(solution.getTitle());

    TextView introTextView = (TextView) view.findViewById(R.id.intro);
    introTextView.setText(solution.getIntro());

    views.put(position, view);

    return view;
  }
}
