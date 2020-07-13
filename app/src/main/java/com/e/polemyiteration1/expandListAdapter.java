package com.e.polemyiteration1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class expandListAdapter  extends BaseExpandableListAdapter  {
    private String[] groupString = {"Allergic Rhinitis","Asthma","Sinusitis",
                                  "Chronic Obstructive Respiratory Disease (COPD)","Otitis Media"};
    private String[][] childString = {{"- It is caused by the nose and/or eyes coming into contact with allergens in the environment.  Exposure to one or more allergens can directly trigger an asthma attack.\n- Symptoms include inflammation of the nose and is characterised by nasal symptoms including anterior or posterior rhinorrhoea (runny nose), sneezing, nasal blockage and/or itching of the nose.\n\n" +
            "Intermittent Allergic Rhinitis \n- It defined by symptoms that are present for less than 4 days per week, or for less than 4 weeks at a time. Persistent Allergic Rhinitis (PER) is vice versa.\n-Moderate / Severe allergic rhinitis may cause sleep impairment."}, {"- It is a common chronic condition of the airways. People with asthma experience episodes of wheezing, shortness of breath, coughing and chest tightness due to widespread narrowing of the airways."},
            {"- It is the inflammation of the membranes lining the large air cavities(sinuses) inside the face bones. It can cause discomfort and pain and is often linked to similar inflammation inside the nose."}, {"- It is a serious, progressive and disabling disease in which destruction of lung tissue and narrowing of the air passages causes shortness of breath and reduced exercise capacity. This disease is closely related to asthma and can worsen due to reduce air quality."},
            {"- It is an inflammatory disease of the lining of the middle ear. Otitis media can cause ear pain and fever in the acute stage, and can lead to hearing loss if allowed to become chronic."}};

    /**
     *
     * @return the length of item in GROUP
     */
    @Override
    public int getGroupCount() {
        return groupString.length;
    }

    /**
     *
     * @param groupPosition
     * @return return the length of a certain item in childString
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return childString[groupPosition].length;
    }

    /**
     *
     * @param groupPosition
     * @return return a certain item in groupString
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groupString[groupPosition];
    }

    /**
     *
     * @param groupPosition
     * @param childPosition
     * @return return a certain item in childString
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childString[groupPosition][childPosition];
    }

    /**
     *
     * @param groupPosition
     * @return return the position in groupString
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     *
     * @param groupPosition
     * @param childPosition
     * @return return position in childString
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return return parentView of expandList
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_item,parent,false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.label_group_normal);
            convertView.setTag(groupViewHolder);
            if (groupPosition == 0){
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#41C9C5"));
            } else if (groupPosition == 1){
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#7ACDF1"));
            } else if (groupPosition == 2){
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#4974A7"));
            } else if (groupPosition == 3){
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#6B5FAE"));
            } else {
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#524364"));
            }
        }else {
            groupViewHolder = (GroupViewHolder)convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groupString[groupPosition]);
        return convertView;
    }

    /**
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return return childView of expandList
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item,parent,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.expand_child);
            convertView.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(childString[groupPosition][childPosition]);
        if (groupPosition == 0){
            childViewHolder.tvTitle.setBackgroundColor(android.graphics.Color.parseColor("#41C9C5"));
        } else if (groupPosition == 1 ){
            childViewHolder.tvTitle.setBackgroundColor(android.graphics.Color.parseColor("#7ACDF1"));
        } else if (groupPosition == 2 ){
            childViewHolder.tvTitle.setBackgroundColor(android.graphics.Color.parseColor("#4974A7"));
        } else if (groupPosition == 3 ){
            childViewHolder.tvTitle.setBackgroundColor(android.graphics.Color.parseColor("#6B5FAE"));
        } else {
            childViewHolder.tvTitle.setBackgroundColor(android.graphics.Color.parseColor("#524364"));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    static class GroupViewHolder {
        TextView tvTitle;
    }

    static class ChildViewHolder {
        TextView tvTitle;

    }
}
