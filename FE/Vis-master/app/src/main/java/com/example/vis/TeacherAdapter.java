package com.example.vis;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;
public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.MaterialsVH>{

    List<TeacherMaterials> materialsList;


    public TeacherAdapter(List<TeacherMaterials> materialsList) {
        this.materialsList = materialsList;
    }


    @NonNull
    @Override
    public MaterialsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row2,parent,false);
        return new TeacherAdapter.MaterialsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsVH holder, int position) {
        TeacherMaterials materials = materialsList.get(position);
        holder.mainTxt.setText(materials.getMain_materials());
        holder.creatorTxt.setText(materials.getCreator_materials());
        holder.createdTxt.setText(materials.getCreated_materials());
        holder.descriptionTxt.setText(materials.getDescription_materials());
        holder.descriptionTxt2.setText(materials.getDescription_materials2());


        boolean isExpandable = materialsList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE: View.GONE);
    }



    @Override
    public int getItemCount() {
        return materialsList.size();
    }

    public class MaterialsVH extends RecyclerView.ViewHolder {
        TextView mainTxt, creatorTxt, createdTxt, descriptionTxt, descriptionTxt2;
        LinearLayout linearLayout2;
        RelativeLayout expandableLayout;
        public MaterialsVH(View itemView) {
            super(itemView);
            mainTxt = itemView.findViewById(R.id.main_materials);
            creatorTxt= itemView.findViewById(R.id.creator_materials);
            createdTxt=itemView.findViewById(R.id.created_materials);
            descriptionTxt=itemView.findViewById(R.id.description_materials);
            descriptionTxt2=itemView.findViewById(R.id.all_materials);

            linearLayout2 = itemView.findViewById(R.id.linear_layout_materials);
            expandableLayout= itemView.findViewById(R.id.expandable_layout_materials);
            linearLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherMaterials materials = materialsList.get(getAdapterPosition());
                    materials.setExpandable(!materials.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });


        }
    }
}
