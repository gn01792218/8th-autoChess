package scene;

import controllers.AudioResourceController;
import controllers.ImageController;
import controllers.SceneController;
import menu.*;
import menu.Button;
import menu.Label;
import utils.CommandSolver;
import utils.Global;
import utils.Player;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;
//角色資訊欄位-->裡面有多個labl(放攻擊 防禦 等等)。
//需要技能顯示資訊
//做確認訊息視窗
//買技能要扣榮譽值

public class UserScene extends Scene{ //改成單例模式!!!
    public static UserScene userScene;
    private BufferedImage backGround;//背景圖
    private BufferedImage backCover;//背景透明板
    private BufferedImage barImage;//
    private ArrayList<ActorButton> actorButtons;
    private Button roundStart;// 進入回合的按鈕
    private Button secrt;//機密檔案(敵軍資料)按鈕
    private Button arrowR;
    private Button arrowL;
    private boolean arrowRUseable;
    private boolean arrowLUseable;
    private Label armyLabel; //購買軍隊的標籤
    private Label enemyLabel; //敵軍機密的標籤
    private PopWindowScene popupWindow;//敵方資訊場景
    //關於彈跳視窗內的按鈕位置問題:1.就算按鈕做在PopWindow內部，也必須要在外面的場景中paint出來(做出getButton方法)，否則按鈕位置會有誤差。
    private UserScene(){}
    public static UserScene getInstance(){
        if(userScene==null){
            userScene=new UserScene();
        }
        return userScene;
    }
    @Override
    public void sceneBegin() {
        //進入回合的按鈕
        backGround=ImageController.getInstance().tryGet("/UserSceneBack.png");
        backCover=ImageController.getInstance().tryGet("/UserBackCover.png");
        roundStart=new Button(950,800,new Style.StyleRect(150,150,
                new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/start.png"))));
        secrt=new Button(1230, 600, new Style.StyleRect(548,356,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/secret-1.png"))));
        secrt.setStyleHover(new Style.StyleRect(548,356,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/secret-2.png"))));
            actorButtons=Global.getActorButtons();//得到Global的角色按鈕
        arrowR=new Button(1370,380,new Style.StyleRect(187,189,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/arrowR.png"))));
        arrowR.setStyleHover(new Style.StyleRect(187,187,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/arrowRH.png"))));
        arrowL=new Button(280,380,new Style.StyleRect(182,189,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/arrowL.png"))));
        arrowL.setStyleHover(new Style.StyleRect(187,187,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/arrowLH.png"))));
        armyLabel=new Label(390,80,new Style.StyleRect(214,58,true,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/army.png"))));
        enemyLabel=new Label(1200,550,new Style.StyleRect(214,58,true,new BackgroundType.BackgroundImage(ImageController.getInstance().tryGet("/enemy.png"))));
        barImage=ImageController.getInstance().tryGet("/bar.png");
        popupWindow=new PopWindowScene(130,50,1300,600);
        popupWindow.setCancelable();
        popupWindow.hide();
    }
    @Override
    public void sceneEnd() {
    }
    @Override
    public CommandSolver.MouseListener mouseListener() {
        return new CommandSolver.MouseListener() {
            @Override
            public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
                if (state != null) {
                    if (popupWindow.isShow()) {
                        popupWindow.mouseListener().mouseTrig(e, state, trigTime);
                    }else {
                        switch (state) {
                            case MOVED: //負責監聽浮現的資訊欄
                                for (int i = 0; i < actorButtons.size(); i++) {    //每個按鈕監聽滑鼠移動
                                    if (actorButtons.get(i).isTouch(e.getX(), e.getY())) { //移動到角色上會有訊息欄
                                        //座標產生資訊圖片-->把角色圖片資訊設成visabl
                                        actorButtons.get(i).setInfoVisable(true);
                                    } else {
                                        actorButtons.get(i).setInfoVisable(false);
                                    }
                                }
                                if (secrt.isTouch(e.getX(), e.getY())) { //機密文件
                                    secrt.isHover(true);
                                } else {
                                    secrt.isHover(false);
                                }
                                if(arrowR.isTouch(e.getX(),e.getY())){
                                    arrowR.isHover(true);
                                }else{arrowR.isHover(false);}
                                if(arrowL.isTouch(e.getX(),e.getY())){
                                    arrowL.isHover(true);
                                }else{arrowL.isHover(false);}
                                break;
                            case CLICKED: //負責監聽升級和購買-->左鍵購買；右鍵取消
                                if (e.getButton() == 1) { //左鍵
                                    if (roundStart.isTouch(e.getX(), e.getY())) {//1.觸發換場的按鈕
                                        SceneController.getInstance().changeScene(new SkillScene());
                                    }
                                    for (int i = 0; i < actorButtons.size(); i++) { //2.角色購買
                                        if (actorButtons.get(i).isTouch(e.getX(), e.getY())) {
                                            System.out.println("點到了!!!");
                                            //購買軍隊
                                            if (Player.getInstance().getMoney() >= actorButtons.get(i).getCostMoney()
                                                    && actorButtons.get(i).left() >= 830 && actorButtons.get(i).right() <= 1330 && actorButtons.get(i).isUnLocked()) { //金錢大於0才可以
                                                System.out.println("買到了!!!");
                                                actorButtons.get(i).offSetNumber(1); //點一下增加一單位
                                                AudioResourceController.getInstance().shot("/skillSound.wav");
                                                Player.getInstance().offsetMoney(-actorButtons.get(i).getCostMoney()); //扣錢
                                            }
                                        }
                                    }
                                    if (arrowR.isTouch(e.getX(), e.getY())) {//右箭頭-->最底是火箭
                                        AudioResourceController.getInstance().shot("/skillSound.wav");
                                        if (actorButtons.get(0).left() < 700) {
                                            for (int i = 0; i < actorButtons.size(); i++) {//全部都不能動
                                                actorButtons.get(i).offSetXY(500, 0);
                                            }
                                        }

                                    }
                                    if (arrowL.isTouch(e.getX(), e.getY())) { //左箭頭-->最底是Tank1
                                        AudioResourceController.getInstance().shot("/skillSound.wav");
                                        if (actorButtons.get(3).left() > 700) {
                                            for (int i = 0; i < actorButtons.size(); i++) {
                                                actorButtons.get(i).offSetXY(-500, 0);
                                            }
                                        }
                                    }
                                    if (secrt.isTouch(e.getX(), e.getY())) {
                                        popupWindow.sceneBegin();
                                        popupWindow.show();
                                    }else{popupWindow.hide();}
                                }
                                if (e.getButton() == 3) {//點擊右鍵 取消
                                    for (int i = 0; i < actorButtons.size(); i++) { //1.角色取消購買
                                        if (actorButtons.get(i).isTouch(e.getX(), e.getY()) && actorButtons.get(i).getNumber() > 0) { //觸碰到 且數量大於0時，才可以取消
                                            AudioResourceController.getInstance().shot("/buttonSound2.wav");
                                            actorButtons.get(i).offSetNumber(-1); //點一下增加一單位
                                            Player.getInstance().offsetMoney(+actorButtons.get(i).getCostMoney());  //把錢+回來
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            }
            };
    }

    @Override
    public CommandSolver.KeyListener keyListener() {
        return null;
    }
    @Override
    public void paint(Graphics g) {
        g.drawImage(backGround,160,0,null);
        g.drawImage(backCover,830,180,null);
        Player.getInstance().paint(g); //畫出 玩家金錢和榮譽
        roundStart.paint(g); //畫出開始回合的按鈕
        secrt.paint(g);//化機密檔案
        armyLabel.paint(g);
        enemyLabel.paint(g);
        for(int i=0;i<actorButtons.size();i++){
            if(actorButtons.get(i).left()<1230 && actorButtons.get(i).right()>830) {
                actorButtons.get(i).paint(g);
            }
        }
        if(arrowRUseable){
            arrowR.paint(g);
        }
        if(arrowLUseable){
            arrowL.paint(g);
        }
        g.drawImage(barImage,200,850,null);
        if(popupWindow.isShow()) {
            popupWindow.paint(g);
            popupWindow.getArrowR().paint(g);
            popupWindow.getArrowL().paint(g);
        }
    }
    @Override
    public void update() {
        if(actorButtons.get(0).left()<=830 && actorButtons.get(0).left()>=-170){ //當T1在第一張時
            arrowLUseable=true;
        }else{arrowLUseable=false;}
        if(actorButtons.get(3).right()>=1330 && actorButtons.get(3).right()<=2330) {
            arrowRUseable = true;
        }else{arrowRUseable=false;}
    }
}
