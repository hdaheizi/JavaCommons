package top.daheizi.commons.fight.common;

public class FightConstants {

    /** 帧频(Hz)*/
    public static final int FRAME_RATE = 20;

    /** 房间宽度 */
    public static final int ROOM_WIDTH = 18;
    /** 房间高度 */
    public static final int ROOM_HEIGHT = 6;

    /** 移动一个单位的基准时间 */
    public static final int MOVE_TIME_PER_STEP = 333;

    /** buff类型 -- 增益 */
    public static final int BUFF_TYPE_GAINBUFF = 1;
    /** buff类型 -- 减益 */
    public static final int BUFF_TYPE_DEBUFF = 2;
    /** buff类型 -- 控制 */
    public static final int BUFF_TYPE_CONTROL = 3;


    /** AI类型 -- 英雄 */
    public static final int AI_TYPE_HERO = 1;
    /** AI类型 -- 宠物 */
    public static final int AI_TYPE_PET = 2;



    /** 攻击方法 -- 物理攻击*/
    public static final int ATT_METHOD_NORM = 1;
    /** 攻击方法 -- 魔法攻击 */
    public static final int ATT_METHOD_MAGIC = 2;


    /** 攻击类型 -- 普攻 */
    public static final int ATT_TYPE_NORM = 1;
    /** 攻击类型 -- 技能 */
    public static final int ATT_TYPE_SKILL = 2;
    /** 攻击类型 -- buff */
    public static final int ATT_TYPE_BUFF = 3;
    /** 攻击类型 -- 自残 */
    public static final int ATT_TYPE_SELF = 4;


    /** 查找类型 -- 敌方全体 */
    public static final int FIND_METHOD_1 = 1;
    /** 查找类型 -- 友方全体 */
    public static final int FIND_METHOD_2 = 2;
    /** 查找类型 -- 全体 */
    public static final int FIND_METHOD_3 = 3;
    /** 查找类型 -- 敌方最前排 */
    public static final int FIND_METHOD_4 = 4;
    /** 查找类型 -- 敌方最后排 */
    public static final int FIND_METHOD_5 = 5;


    /** 势力 -- 攻击方 */
    public static final int FORCE_ATT = 1;
    /** 势力 -- 防守方 */
    public static final int FORCE_DEF = 2;

}
