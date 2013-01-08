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
import com.jkydjk.healthier.clock.entity.Solution;

@SuppressLint("UseSparseArrays")
public class SolutionListAdapter extends BaseAdapter {

  private LayoutInflater layoutInflater;

  private List<Solution> items = new ArrayList<Solution>();

  private Map<Integer, View> views = new HashMap<Integer, View>();

  public SolutionListAdapter(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  public SolutionListAdapter(Context context, List<Solution> list) {
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
    View view = views.get(position);
    if (view != null) {
      return view;
    }

    view = layoutInflater.inflate(R.layout.solution_list_item, parent, false);

    Solution solution = (Solution) items.get(position);

    TextView titleTextView = (TextView) view.findViewById(R.id.title);
    titleTextView.setText(solution.getTitle());
    
    TextView introTextView = (TextView)view.findViewById(R.id.intro);
    introTextView.setText(solution.getEffect());

    views.put(position, view);
    
    return view;
  }

}
