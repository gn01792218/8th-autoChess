package menu;

import controllers.ImageController;
import gameobj.Actor;
import utils.Delay;
import utils.Global;

import java.awt.*;
import java.util.ArrayList;

public class DefUp extends SkillButton{
    private Label label;

    private final float defEffect=0.2f; //要增加幾成的防禦力
    private final int bufftime=300; //持續X/60秒

    public DefUp(int x, int y, Style style, Global.SkillName skillName, int cost){
        super(x,y,style,skillName,cost);
        this.buffTime=new Delay(bufftime);//增加防禦時間5秒
        info.getPaintStyle().setText("全體防禦力+"+defEffect+"("+defEffect*10+"成)，持續"+bufftime/60+"秒");
        this.label=new Label(this.getCenterX(),this.bottom(),new Style.StyleRect(10,10,true,null).setText("花費:"+this.cost).setTextFont(new Font("標楷體",Font.ITALIC,22)));
        infoVisable=false; //一開始不顯現
    }

    @Override
    public void skillExection(ArrayList<Actor> actors) {
            for (int i = 0; i < actors.size(); i++) {
                System.out.println("第"+(i+1)+"台原始防禦力為"+actors.get(i).getDef());
                actors.get(i).setSkillName(this.getSkillName()); //將該角色身上的當前招式名稱更改
                actors.get(i).offsetDef(defEffect); //本身+0.2-->提高兩成防禦力 0.5+0.2=0.7
                actors.get(i).setOnBuff(true); //標示為Buff狀態
                System.out.println("第"+(i+1)+"台增加防禦力為"+actors.get(i).getDef()); //測試用，外面要記得打印攻擊力測試時間內的攻擊力
            }
    }

    @Override
    public void skillReset(ArrayList<Actor> actors) {
        for(int i=0;i<actors.size();i++){
            actors.get(i).offsetDef(-defEffect);
            actors.get(i).setOnBuff(false); //標示為非Buff狀態
            System.out.println("防禦力回復原廠設定"+actors.get(i).getDef());
        }
        setUsed(true); //被施放過了
        System.out.println("技能: "+this.getSkillName()+"施放結束");
    }
    @Override
    public void paint(Graphics g){
        if (super.getPaintStyle() != null) {
            super.getPaintStyle().paintComponent(g, super.getX(), super.getY());
        }
        if(info!=null && infoVisable){info.paint(g);}
        if(label!=null){
            label.paint(g);
        }
    }

}
