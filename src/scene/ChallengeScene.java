package scene;

import controllers.AudioResourceController;
import controllers.ImageController;
import controllers.SceneController;
import gameobj.*;
import menu.*;
import utils.CommandSolver;
import utils.Delay;
import utils.Global;
import utils.Player;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ChallengeScene extends Scene{
    private BufferedImage image; //背景圖
    private BufferedImage image1_1;
    private BufferedImage image2; //失敗的圖片
    private BufferedImage image3;//倒數10秒圖片
    private BufferedImage image4;// 挑戰成功的圖片
    private ArrayList<Actor> alliance; //角色陣列
    private ArrayList<Actor> castles; //城堡陣列
    private ArrayList<Actor> enemys; //敵軍
    private ArrayList<Actor> boss; //敵軍
    private ArrayList<SkillButton> skill;//技能陣列
    private boolean isFlagUsable;
    private int step;
    private Delay gameBegin; //過場秒數
    private Delay gameOver; //過場秒數
    private Delay enemyLoop;
    private Delay rewardLoop;

    private Actor allianceControl;//受旗子控制的我軍
    private int count = 0;//回合數
    private float mouseX;
    private float mouseY;
    private Player player;

    private BufferedImage imageTank1;
    private BufferedImage imageTank2;
    private BufferedImage imageLaserCar;
    private BufferedImage imageRocket;

    @Override
    public void sceneBegin() {
        image = ImageController.getInstance().tryGet("/GameScene1.png"); //場景圖
        image1_1=ImageController.getInstance().tryGet("/GameScene1-1.png");
        image2 = ImageController.getInstance().tryGet("/fail2.png");
        isFlagUsable = true;
        gameBegin=new Delay(300);
        gameOver=new Delay(180);
        enemyLoop=new Delay(1200);
        rewardLoop=new Delay(600);
        gameBegin.play();
        count=1;
        player = Player.getInstance();
        player.setMoney(600);
        player.setHonor(250);

        imageTank1= ImageController.getInstance().tryGet("/AB-Tank1-Small.png");
        imageTank2= ImageController.getInstance().tryGet("/AB-Tank2-Small.png");
        imageLaserCar= ImageController.getInstance().tryGet("/AB-LaserCar-Small.png");
        imageRocket= ImageController.getInstance().tryGet("/AB-Rocket-Small.png");

//        作技能
        skill=new ArrayList<>();
        ArrayList<SkillButton> temp=Global.getSkillButtons(); //從世界技能紐中下訂單

        int skillCount=0;
        skill.add(new SpeedUp(1390,325,temp.get(2).getStyleNormal(),temp.get(2).getSkillName(),temp.get(2).getCost())); //設置在場中的位置
        skill.add(new HpUp(1490,325,temp.get(3).getStyleNormal(),temp.get(3).getSkillName(),temp.get(3).getCost())); //設置在場中的位置
        skill.add(new AttackUp(1590,325,temp.get(0).getStyleNormal(),temp.get((0)).getSkillName(),temp.get((0)).getCost())); //設置在場中的位置
        skill.add(new DefUp(1690,325,temp.get(1).getStyleNormal(),temp.get(1).getSkillName(),temp.get(1).getCost())); //設置在場中的位置

        skill.add(new Reinforcements(1440,425,temp.get(4).getStyleNormal(),temp.get(4).getSkillName(),temp.get(4).getCost()));
        skill.add(new ElectWave(1540,425,temp.get(5).getStyleNormal(),temp.get(5).getSkillName(),temp.get(5).getCost()));
        skill.add(new AtkSpeedUp(1640,425,temp.get(6).getStyleNormal(),temp.get(6).getSkillName(),temp.get(6).getCost()));
        skill.get(4).setUnLocked(true);
        skill.get(5).setUnLocked(true);
        skill.get(6).setUnLocked(true);


        //做軍隊
        alliance = new ArrayList<>();
        alliance.add(new Tank1(850,600,false));
        alliance.add(new Tank1(750,600,false));
        alliance.add(new Tank1(950,700,false));
        alliance.add(new Tank1(650,700,false));
        castles = new ArrayList<>();
        castles.add(new Castle(400,730));
        castles.get(0).painter().setLeft(100);
        castles.add(new Castle(1150,730));
        enemys = new ArrayList<>();
        boss = new ArrayList<>();

    }

    @Override
    public void sceneEnd() {
    }

    @Override
    public CommandSolver.KeyListener keyListener() {
        return new CommandSolver.KeyListener() {
            @Override
            public void keyPressed(int commandCode, long trigTime) {

            }

            @Override
            public void keyReleased(int commandCode, long trigTime) {
                switch (commandCode){
                    case 11:
                        if(player.getMoney()>250){
                            Actor temp = new Tank1(800,1100,false);
                            alliance.add(temp);
                            temp.setStrategyXY(mouseX,mouseY);
                            player.offsetMoney(-250);
                        }
                        break;
                    case 12:
                        if(player.getMoney()>280){
                            Actor temp = new Tank2(800,1100,false);
                            alliance.add(temp);
                            temp.setStrategyXY(mouseX,mouseY);
                            player.offsetMoney(-280);
                        }

                        break;
                    case 13:
                        if(player.getMoney()>250){
                            Actor temp = new LaserCar(800,1100,false);
                            alliance.add(temp);
                            temp.setStrategyXY(mouseX,mouseY);
                            player.offsetMoney(-300);
                        }
                        break;
                    case 14:
                        if(player.getMoney()>500){
                            Actor temp = new Rocket(800,1100,false);
                            alliance.add(temp);
                            temp.setStrategyXY(mouseX,mouseY);
                            player.offsetMoney(-500);
                        }
                        break;
                }
            }

            @Override
            public void keyTyped(char c, long trigTime) {

            }
        };
    }
    @Override
    public CommandSolver.MouseListener mouseListener() {
        return new CommandSolver.MouseListener() {
            @Override
            public void mouseTrig(MouseEvent e, CommandSolver.MouseState state, long trigTime) {
                if (state != null) {
                    switch (state) {
                        case CLICKED:
                            if (e.getButton() == e.BUTTON1) {
                                if (isFlagUsable) {  //當旗子還可以使用的時候
                                    for (int i = 0; i < alliance.size(); i++) { //控制權現在在誰身上
                                        if (alliance.get(i).isTouch(e.getX(), e.getY())) { //假如被點到了
                                            allianceControl = alliance.get(i);  //被點到的人會變被控制者
                                            allianceControl.setControl(true); //設成被控制中
                                            System.out.println("第"+i+"台被點到"+"種類是"+allianceControl.getType());  //很不好點
                                        }else{
                                            alliance.get(i).setControl(false);
                                        }
                                    }
                                }
                                for(int i=0;i<skill.size();i++){ //監聽玩家是否有點技能按鈕
                                    if(skill.get(i).isTouch(e.getX(),e.getY()) && skill.size()>0 && !skill.get(i).isUsed()){ //按鈕被點時 且 還有按鈕時
                                        if(player.getHonor()>skill.get(i).getCost()){
                                            AudioResourceController.getInstance().play("/skillSound.wav");// 音效聲音，可以大聲點嗎?
                                            skill.get(i).skillBufftimePlay();// 才啟動技能
                                            if(skill.get(i).getSkillName()== Global.SkillName.ELECTWAVE){
                                                skill.get(i).skillExection(enemys);
                                            }else{
                                                skill.get(i).skillExection(alliance); //執行技能~
                                            }
//                                            skill.get(i).setUsed(true);
                                            player.offsetHonor(-skill.get(i).getCost());
                                        }
                                    }
                                }
                                System.out.println("左鍵");
                            } else if (e.getButton() == e.BUTTON2) {
                                System.out.println("中鍵");
                            } else if (e.getButton() == 3) {//也可以這樣
                                //旗子在可以使用的狀態才接收座標
                                System.out.println("右鍵");
                                if (isFlagUsable&&allianceControl!=null) {
                                    allianceControl.getFlag().setCenter(e.getX(), e.getY());
                                }
                            }
                        case MOVED:
                            mouseX = e.getX();
                            mouseY = e.getY();

                            for(int i=0;i<skill.size();i++){
                                if(skill.get(i).isTouch(e.getX(),e.getY())){
                                    skill.get(i).setInfoVisable(true);
                                }else{ skill.get(i).setInfoVisable(false);}
                            }
                    }
                }
            }
        };
    }

    @Override
    public void paint(Graphics g) {

        g.drawImage(image, 0, -150, null);
        player.paint(g);
        g.drawString("WAVE："+count,50,100);

        g.drawImage(imageTank1, 1700, 410, null);
        g.drawImage(imageTank2, 1700,560 , null);
        g.drawImage(imageLaserCar, 1700, 680, null);
        g.drawImage(imageRocket, 1700, 830, null);
        g.drawString("1",1850,570);
        g.drawString("2",1850,720);
        g.drawString("3",1850,850);
        g.drawString("4",1850,990);

        for (int i = 0; i < alliance.size(); i++) {
            alliance.get(i).paint(g); //畫我軍
        }

        for (int i = 0; i < enemys.size(); i++) {
            enemys.get(i).paint(g); //畫敵軍
        }
        if(skill.size()>0) {
            for (int i = 0; i < skill.size(); i++) {
                if(!skill.get(i).isUsed()) {
                    skill.get(i).paint(g); //畫技能
                }
            }
        }
        if (isFlagUsable && allianceControl!=null) {
            allianceControl.getFlag().paint(g); //旗子可以使用的時候才畫出來
        }

        g.drawImage(image1_1,0,-150,null);
        for(int i = 0; i< castles.size(); i++){
            castles.get(i).paint(g);
        }
        if(castles.size()<=0){
            g.drawImage(image2, 350, 250, null);
        }


    }
    //當偵測到被點到，開啟可以移動，時才移動，並一直移動到目標點，然後
    @Override
    public void update() {

        //技能update
        if(skill.size()>0) {
            for (int i = 0; i < skill.size(); i++) {
                if (skill.get(i).isUsed()) { //沒有被施放過
                    if (skill.get(i).getBuffTime().count()) {
                        if(skill.get(i).getSkillName()==Global.SkillName.ELECTWAVE){//電磁波的~!
                            skill.get(i).skillReset(enemys);
                        }else{ skill.get(i).skillReset(alliance);}//時間到全軍恢復原廠設置~!
                    }
                }
            }
        }
        //我軍的update
        for (int i = 0; i < alliance.size(); i++) {  //我軍自己移動
            if(alliance.get(i).collider().centerY()>Global.BOUNDARY_Y2){
                alliance.get(i).moveToField();
            }else {
                alliance.get(i).standAttack(enemys, alliance);
                alliance.get(i).bulletsUpdate(enemys); //發射子彈
            }
            if (!alliance.get(i).isAlive()) { //沒有活著的時候移除
                alliance.remove(i);
                break;
            }
        }
        for(int i = 0; i< castles.size(); i++){
            //攻擊條件
            castles.get(i).autoAttack(enemys,alliance);
            castles.get(i).bulletsUpdate(enemys); //發射子彈

            if (!castles.get(i).isAlive()) { //沒有活著的時候移除
                castles.remove(i);
                break;
            }
        }
        //敵軍update
        for (int i = 0; i < enemys.size(); i++) {
            if(enemys.get(i).collider().centerY()<100){
                enemys.get(i).moveToField();
            }else {
                enemys.get(i).straightAttack(alliance,castles);
                enemys.get(i).bulletsUpdate(alliance);
                enemys.get(i).bulletsUpdate(castles);
            }

            if (!enemys.get(i).isAlive()) {
                enemys.remove(i);
                player.offsetMoney(25);
                break;
            }
        }
        //boss update
        for(int i=0;i<boss.size();i++){
            boss.get(i).autoAttack(alliance,castles);
            if (!boss.get(i).isAlive()) {
                boss.remove(i);
                player.offsetMoney(100);
                break;
            }
        }


        //REWARD
        if(rewardLoop.count()){
            player.offsetMoney(600);
            player.offsetHonor(150);
        }

        //第一回合
        if(gameBegin.count()){
            AudioResourceController.getInstance().play("/cinematic-dramatic-brass-hit_G_major.wav");
            for(int i=0;i<(count+1)*2;i++){
                enemys.add(new Enemy1(Global.random(650, 950), -Global.random(0, 50), true));
            }
            for(int i=0;i<enemys.size();i++){
                enemys.get(i).setStrategyXY(Global.random(Global.BOUNDARY_X1,Global.BOUNDARY_X2),250);
            }
            enemyLoop.loop();
            rewardLoop.loop();
        }

        //回合++
        if(enemyLoop.count()){
            AudioResourceController.getInstance().play("/cinematic-dramatic-brass-hit_G_major.wav");
            count++;
            for(int i=0;i<count*4;i++){
                enemys.add(new Enemy1(Global.random(650, 950), -Global.random(0,150), true));
            }
            if(count>2){
                for(int i=0;i<(count-1)*4;i++){
                    enemys.add(new Enemy2(Global.random(650, 950), -Global.random(50,200), true));
                }
            }
            if(count>3){
                for(int i=0;i<(count-1)*3;i++){
                    enemys.add(new Enemy3(Global.random(650, 950), -Global.random(50,200), true));
                }
            }
            if(count>4){
                for(int i=0;i<(count-2)*3;i++){
                    enemys.add(new Enemy4(Global.random(650, 950), -Global.random(0,400), true));
                }
            }
            if(count>5){
                for(int i=0;i<count-5;i++){
                    enemys.add(new Boss(Global.random(650, 950), -Global.random(50,200), true));
                }
            }
            //隨機給予敵人攻擊位置
            for(int i=0;i<enemys.size();i++){
                enemys.get(i).setStrategyXY(Global.random(Global.BOUNDARY_X1,Global.BOUNDARY_X2),200);
            }
        }


        if (castles.size() <= 0) { //挑戰失敗
            gameOver.play();

            if(gameOver.count()) {
                Global.rankList.newScore(count);
                SceneController.getInstance().changeScene(new OpenScene());
            }
        }

    }
}
