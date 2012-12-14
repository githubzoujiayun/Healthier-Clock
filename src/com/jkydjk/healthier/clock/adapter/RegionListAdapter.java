package com.jkydjk.healthier.clock.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.Question;
import com.jkydjk.healthier.clock.entity.Region;

@SuppressLint("UseSparseArrays")
public class RegionListAdapter extends BaseAdapter {

  private LayoutInflater layoutInflater;

  private List<Region> items = new ArrayList<Region>();

  private Map<Integer, View> views = new HashMap<Integer, View>();

  public RegionListAdapter(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  public RegionListAdapter(Context context, List<Region> list) {
    layoutInflater = LayoutInflater.from(context);
    items = list;
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

    Region region = (Region) items.get(position);

    View view = views.get(region.getId());

    if (view != null) {
      
      return view;
      
    } else {

      view = layoutInflater.inflate(R.layout.region_item, parent, false);

      TextView name = (TextView) view.findViewById(R.id.name);

      name.setText(region.getName());

      views.put(region.getId(), view);
    }

    return view;
  }

}
