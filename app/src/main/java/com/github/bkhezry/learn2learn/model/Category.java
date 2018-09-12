package com.github.bkhezry.learn2learn.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Category{

	@SerializedName("fa_name")
	private String faName;

	@SerializedName("skills")
	private List<SkillsItem> skills;

	@SerializedName("en_name")
	private String enName;

	@SerializedName("id")
	private int id;

	public void setFaName(String faName){
		this.faName = faName;
	}

	public String getFaName(){
		return faName;
	}

	public void setSkills(List<SkillsItem> skills){
		this.skills = skills;
	}

	public List<SkillsItem> getSkills(){
		return skills;
	}

	public void setEnName(String enName){
		this.enName = enName;
	}

	public String getEnName(){
		return enName;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}