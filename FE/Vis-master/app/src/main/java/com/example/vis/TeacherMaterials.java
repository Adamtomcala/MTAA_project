package com.example.vis;


import android.widget.Button;

public class TeacherMaterials {
    private String main_materials;
    private String creator_materials;
    private String created_materials;
    private String description_materials;
    private String description_materials2;
    private boolean expandable;

    public String getDescription_materials2() {
        return description_materials2;
    }

    public void setDescription_materials2(String description_materials2) {
        this.description_materials2 = description_materials2;
    }

    @Override
    public String toString() {
        return "TeacherMaterials{" +
                "main_materials='" + main_materials + '\'' +
                ", creator_materials='" + creator_materials + '\'' +
                ", created_materials='" + created_materials + '\'' +
                ", description_materials='" + description_materials + '\'' +
                ", description_materials2='" + description_materials2 + '\'' +
                ", expandable=" + expandable +
                '}';
    }


    public TeacherMaterials(String main_materials, String creator_materials, String created_materials, String description_materials, String description_materials2) {
        this.main_materials = main_materials;
        this.creator_materials = creator_materials;
        this.created_materials = created_materials;
        this.description_materials = description_materials;
        this.description_materials2 = description_materials2;
        this.expandable = false;
    }

    public String getMain_materials() {
        return main_materials;
    }

    public void setMain_materials(String main_materials) {
        this.main_materials = main_materials;
    }

    public String getCreator_materials() {
        return creator_materials;
    }

    public void setCreator_materials(String creator_materials) {
        this.creator_materials = creator_materials;
    }

    public String getCreated_materials() {
        return created_materials;
    }

    public void setCreated_materials(String created_materials) {
        this.created_materials = created_materials;
    }

    public String getDescription_materials() {
        return description_materials;
    }

    public void setDescription_materials(String description_materials) {
        this.description_materials = description_materials;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}
