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

@SuppressLint("UseSparseArrays")
public class QuestionListAdapter extends BaseAdapter {

  private LayoutInflater layoutInflater;

  private List<Object> items = new ArrayList<Object>();
  private Map<Integer, View> views = new HashMap<Integer, View>();

  public QuestionListAdapter(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  public QuestionListAdapter(Context context, List<Object> list) {
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
    if (position == 0 || position == getCount() - 1) {
      view = (View) items.get(position);
    } else {
      view = layoutInflater.inflate(R.layout.constitution_question, parent, false);
      Question question = (Question) items.get(position);
      TextView questionTextView = (TextView) view.findViewById(R.id.question_text);
      questionTextView.setText(position + ". " + question.getTitle());
      questionTextView.setHint(question.getTitle());
    }
    views.put(position, view);
    return view;
  }

}
