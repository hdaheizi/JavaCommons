package org.daheiz.base.test;

import java.util.ArrayList;
import java.util.List;

import org.daheiz.base.fight.event.AddUnitEvent;
import org.daheiz.base.fight.pot.Pot;
import org.daheiz.base.fight.pot.PotFactory;
import org.daheiz.base.fight.room.FightRoom;
import org.daheiz.base.fight.skill.SkillAttribute;
import org.daheiz.base.fight.unit.DefaultAIUnit;
import org.daheiz.base.fight.unit.HeroUnit;
import org.daheiz.base.sdata.Potential;
import org.daheiz.base.sdata.Skill;
import org.daheiz.base.stl.Tuple;

/**
 * 战斗测试类
 * @author daheiz
 * @Date 2016年7月10日 下午11:26:54
 */
public class TestFight {
    
    public static void main(String[] args){
        
        DefaultAIUnit att1 = new HeroUnit();
        att1.memberId = 1001;
        att1.attLen = 4;
        att1.yPriority = 4;
        att1.attCd = 1100;
        att1.attMethod = 1;
        att1.findMethod = 1;
        att1.maxHp = 2800;
        att1.maxMp = 100;
        att1.hp = att1.maxHp;
        att1.mp = 0;
        att1.att = 120;
        att1.def = 35;
        att1.matt = 60;
        att1.mdef = 10;
        att1.crip = 80;
        att1.hit = 200;
        att1.dodge = 400;
        att1.critRatio = 1.5;
        att1.critDam = 70;
        att1.angerConv = 1.8f;
        att1.moveSpeed = 1.2f;
        att1.attSpeed = 1.0f;
        att1.autoPlaySkill = true;
        att1.effectList.add(new Tuple<String, Integer>("prep", 540));
        att1.effectList.add(new Tuple<String, Integer>("fly", 18));
        att1.effectList.add(new Tuple<String, Integer>("norm", 432));
        
        Skill skill = new Skill();
        skill.setSkillId(7);
        skill.setFindMethod(1);
        skill.setBreakLv(1);
        skill.setName("huoyanranshao");
        skill.setAttributeStr("damE=1.2;mdamE=25");
        skill.setParamStr("huoyan=6,2,2,3");
        skill.setEffectStr("prep=1200;hit=100;norm=34;huoyan=1;norm=356");
        
        SkillAttribute skillAttribute = new SkillAttribute(skill, 1);
        att1.skillMap.put(skill.getSkillId(), skillAttribute);
        
        
        
        
        
        DefaultAIUnit def1 = new HeroUnit();
        def1.memberId = 1002;
        def1.attLen = 2;
        def1.yPriority = 2;
        def1.attCd = 1800;
        def1.attMethod = 1;
        def1.findMethod = 5;
        def1.maxHp = 1500;
        def1.maxMp = 100;
        def1.hp = def1.maxHp;
        def1.mp = 0;
        def1.att = 100;
        def1.def = 38;
        def1.matt = 20;
        def1.mdef = 10;
        def1.crip = 100;
        def1.hit = 600;
        def1.dodge = 300;
        def1.critRatio = 2;
        def1.critDam = 60;
        def1.angerConv = 1.6f;
        def1.moveSpeed = 1f;
        def1.attSpeed = 1f;
        def1.autoPlaySkill = true;
        def1.effectList.add(new Tuple<String, Integer>("prep", 760));
        def1.effectList.add(new Tuple<String, Integer>("hit", 100));
        def1.effectList.add(new Tuple<String, Integer>("norm", 453));
        
        Skill skill2 = new Skill();
        skill2.setSkillId(13);
        skill2.setFindMethod(1);
        skill2.setBreakLv(2);
        skill2.setName("liuxingchui");
        skill2.setAttributeStr("damR=2;damE=30");
        skill2.setParamStr("xuanyun=4,1");
        skill2.setEffectStr("prep=800;hit=100;xuanyun=1;norm=300");
        
        SkillAttribute skillAttribute2 = new SkillAttribute(skill2, 1);
        def1.skillMap.put(skill2.getSkillId(), skillAttribute2);
        
        
        
        
        
        
        
        
        DefaultAIUnit def2 = new HeroUnit();
        def2.memberId = 1003;
        def2.attLen = 6;
        def2.yPriority = 3;
        def2.attCd = 900;
        def2.attMethod = 1;
        def2.findMethod = 4;
        def2.maxHp = 1200;
        def2.maxMp = 100;
        def2.hp = def2.maxHp;
        def2.mp = 0;
        def2.att = 90;
        def2.def = 80;
        def2.matt = 40;
        def2.mdef = 10;
        def2.crip = 50;
        def2.hit = 600;
        def2.dodge = 300;
        def2.critRatio = 2;
        def2.critDam = 80;
        def2.angerConv = 1f;
        def2.moveSpeed = 1f;
        def2.attSpeed = 1.2f;
        def2.autoPlaySkill = false;
        def2.effectList.add(new Tuple<String, Integer>("prep", 520));
        def2.effectList.add(new Tuple<String, Integer>("hit", 100));
        def2.effectList.add(new Tuple<String, Integer>("norm", 340));
        
        List<DefaultAIUnit> attList = new ArrayList<>();
        List<DefaultAIUnit> defList = new ArrayList<>();
        attList.add(att1);
        
        defList.add(def1);
//        defList.add(def2);
        
        def2.x = 18;
        def2.y = 1;
        def2.side = 2;
        AddUnitEvent event = new AddUnitEvent();
        event.heroList.add(def2);
        event.delay = 8 * 1000;
        
        
        Potential potential1 = new Potential();
        potential1.setId(1);
        potential1.setName("qiangligedang");
        potential1.setParamStr("0.3,0.95");
        Pot pot1 = PotFactory.createPotential(def1, potential1);
        def1.pots = new Pot[]{pot1};
        
        FightRoom room = new FightRoom(attList, defList);
        room.addEvent(event);
        
        room.startFight();
        
    }
    
}
