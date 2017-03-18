package com.hdaheizi.base.fight.common;

/**
 * 战斗公式
 * @author daheiz
 * @Date 2016年5月23日 上午12:22:31
 */
public class FightFormula {


	/**
	 * 计算两点之间的距离
	 * @param srcX
	 * @param srcY
	 * @param toX
	 * @param toY
	 * @return
	 * @Date 2016年6月12日 下午10:46:49
	 */
	public static int calcDis(int srcX, int srcY, int toX, int toY){
		int a = Math.abs(toX - srcX);
		int b = Math.abs(toY - srcY);
		return (int) Math.sqrt(a * a + b * b);
	}


	/**
	 * 计算长度
	 * @param p1
	 * @param p2
	 * @return
	 * @Date 2016年6月14日 下午9:45:12
	 */
	public static int calcLen(int p1, int p2){
		return Math.abs(p1 - p2);
	}


	/**
	 * 计算闪避率
	 * @param dodge
	 * @param hit
	 * @return
	 * @Date 2016年6月16日 下午10:38:34
	 */
	public static double calcDodgeRatio(int dodge, int hit){
		return Math.sqrt(Math.max(dodge - hit, 0)) / 100;
	}


	/**
	 * 计算暴击率
	 * @param crip
	 * @return
	 * @Date 2016年6月16日 下午10:40:27
	 */
	public static double calcCritRatio(int crip){
		return Math.sqrt(crip) / 100;
	}


	/**
	 * 计算基础伤害
	 * @param att 攻击力
	 * @param def 防御力
	 * @return
	 * @Date 2016年6月15日 下午11:32:42
	 */
	public static int calcDam(int att, int def){
		if(att >= 1.125 * def){
			return att - def;
		}
		return (int) (att * att /(att + 9 * def));
	}


	/**
	 * 计算技能伤害
	 * @param att 物理攻击
	 * @param matt 魔法攻击
	 * @param def 物理防御
	 * @param mdef 魔法防御
	 * @param damR 物理伤害系数
	 * @param mdamR 魔法伤害系数
	 * @param damE 附加物理伤害
	 * @param mdamE 附加魔法伤害
	 * @return
	 * @Date 2016年6月16日 上午12:09:01
	 */
	public static int calcSkillDam(
			int att, int matt, 
			int def, int mdef, 
			double damR, double mdamR, 
			int damE, int mdamE){

		// 基础物理伤害
		int baseNDam = calcDam(att, def);
		// 基础魔法伤害
		int baseMDam = calcDam(matt, mdef);

		// 物理伤害加成
		int skillNDam = (int) (baseNDam * damR + damE);
		// 魔法伤害加成
		int skillMDam = (int) (baseMDam * mdamR + mdamE);

		return skillNDam + skillMDam;
	}


}
