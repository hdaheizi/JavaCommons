package org.daheiz.base.sdata;

/**
 * 技能配置
 * @author daheiz
 * @Date 2016年7月10日 下午11:12:16
 */
public class Skill {

	/** 技能id */
	private int skillId;
	
	/** 技能名称 */
	private String name;

	/** 参数 */
	private String paramStr;
	
	/** 效果 */
	private String effectStr;
	
	/** 属性字符串 */
	private String attributeStr;
	
	/** 查找方法 */
	private int findMethod;
	
	/** 打断级别 */
	private int breakLv;
	
	
	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getParamStr() {
		return paramStr;
	}

	public void setParamStr(String paramStr) {
		this.paramStr = paramStr;
	}

	public String getEffectStr() {
		return effectStr;
	}

	public void setEffectStr(String effectStr) {
		this.effectStr = effectStr;
	}

	public String getAttributeStr() {
		return attributeStr;
	}

	public void setAttributeStr(String attributeStr) {
		this.attributeStr = attributeStr;
	}
	
	public int getFindMethod() {
		return findMethod;
	}

	public void setFindMethod(int findMethod) {
		this.findMethod = findMethod;
	}

	public int getBreakLv() {
		return breakLv;
	}

	public void setBreakLv(int breakLv) {
		this.breakLv = breakLv;
	}

}
