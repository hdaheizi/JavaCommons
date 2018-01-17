package com.daheiz.base.fight.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daheiz.base.sdata.Skill;
import com.daheiz.base.stl.Tuple;

/**
 * 技能属性类
 * @author daheiz
 * @Date 2016年6月6日 下午11:16:26
 */
public class SkillAttribute{

	/** 技能配置类 */
	public Skill skill;

	/** 属性参数map */
	public Map<String, Double> attributeMap = new HashMap<>();

	/** 技能效果列表 */
	public List<Tuple<String, Integer>> effectList = new ArrayList<>();

	/** 效果参数map */
	public Map<String, double[]> paramMap = new HashMap<>();


	/**
	 * 构造函数
	 * @param skill
	 */
	public SkillAttribute(Skill skill, int lv){
		// 内置技能配置
		this.skill = skill;
		// 解析属性
		for(String node : skill.getAttributeStr().split(";")){
			String[] params = node.split("=");
			attributeMap.put(params[0], Double.valueOf(params[1]) * lv);
		}
		// 解析效果
		for(String node : skill.getEffectStr().split(";")){
			String[] params = node.split("=");
			effectList.add(new Tuple<>(params[0], Integer.valueOf(params[1])));
		}
		// 解析参数
		for(String node : skill.getParamStr().split(";")){
			String[] params = node.split("=");
			String[] valueStrs = params[1].split(",");
			double[] values = new double[valueStrs.length];
			for(int i = 0; i < valueStrs.length; i++){
				values[i] = Double.valueOf(valueStrs[i]);
			}
			paramMap.put(params[0], values);
		}
	}



}
