package gameobj;

import controllers.ImageController;
import controllers.ImageOperate;

import java.awt.*;

public class Enemy1 extends Actor{
    public Enemy1(int x,int y,boolean isenemy){
        super(x,y,75,90);
        this.image= ImageController.getInstance().tryGet("/enemy1.png");
<<<<<<< HEAD
        hp=100; //血量
        atk=50; //攻擊力
        atkSpeed=5; //功速
        speed=10; //移動速度
        def=50; //防禦力
        atkdis=30; //攻擊距離
        this.isenemy=isenemy; //敵我單位
=======
        this.setSpeed(3);
>>>>>>> 3543b6226323450109415b8bb87d1c96e18ab941
    }
    @Override
    public void paint(Graphics g) {
        g.drawImage(image,(int)this.painter().left(),(int)this.painter().top(),(int)this.painter().right(),(int)this.painter().bottom(),
                0,0,75,90,null);
    }

    @Override
    public void update() {
    }
}
