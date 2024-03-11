import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import StartMenu;

//Wendy Shen, June 7, 2022
//A shooter game (I mixed up my lefts and rights a few times)

public class Game extends JPanel implements MouseListener, KeyListener, WindowListener, Runnable {

	static Image menuArt = Toolkit.getDefaultToolkit().getImage("menuArt.png");

	static Image savePoint = Toolkit.getDefaultToolkit().getImage("savePoint.png");
	Image instrus = Toolkit.getDefaultToolkit().getImage("instructions.png");
	Image bg1 = Toolkit.getDefaultToolkit().getImage("bg1.png");
	Image bg2 = Toolkit.getDefaultToolkit().getImage("bg2.png");
	Image bg2point5 = Toolkit.getDefaultToolkit().getImage("bg2.5.png");
	Image bg3 = Toolkit.getDefaultToolkit().getImage("bg3.png");

	static Image darkness = Toolkit.getDefaultToolkit().getImage("darkness.png");
	static Image exclam = Toolkit.getDefaultToolkit().getImage("atkNotif.png");
	static Image door = Toolkit.getDefaultToolkit().getImage("door.png");
	static Image spike = Toolkit.getDefaultToolkit().getImage("spike.png");

	Image dummy = Toolkit.getDefaultToolkit().getImage("dummy.png");
	static Image charac = Toolkit.getDefaultToolkit().getImage("sprite1.png");
	static Image fireBall = Toolkit.getDefaultToolkit().getImage("fireball.png");

	static Image heart = Toolkit.getDefaultToolkit().getImage("hp.png");
	static Image heartx3 = Toolkit.getDefaultToolkit().getImage("hpx3.png");

	static Image crack = Toolkit.getDefaultToolkit().getImage("crack.png");
	static Image[] sprites = new Image[4];

	static Image[] slimeUp = new Image[3];//slime facing left
	static Image[] slimeDown = new Image[3];//slime facing right
	static Image[] slimeLeft = new Image[3];
	static Image[] slimeRight = new Image[3];

	static Image[] shroom = new Image[4];
	static Image[] shock = new Image[2];

	static Image[] cutScene1 = new Image[8];

	static int mouseX, mouseY, len = 0, len2 = 0, len3 = 0, len4 = 0;

	static boolean up, down, left, right;
	static int charC[] = {0,0};//ind 0 = x pos, ind 1 = y pos
	static int lastCharC[] = {1, 0};
	//ind 0 = x, ind 1 = y, ind 2 = type of interaction (1 = door, 2 = item, 3 = respawn, 4 = save), ind 3 = direction (0 = up, 1 = down, 2 = left, 3 = right)

	//static boolean clicked = false;
	static int charSize = 80, spriteNum = 0, speed = 1, maxHP = 20, currentHP = maxHP;

	final int WIDTH = 1200, HEIGHT = 600;
	static String scene = "start", nextRoom, lastRoom;
	static boolean[] roomPassed = new boolean[5];

	static Font buttonFont, switchFont = new Font("Ariel", Font.BOLD, 30), bigFont, smallFont;

	static Clip clickSound, bgm, hit, doorOpeningSound, introMusic, deathMusic, saveSound, honk, pop, laser, shockwaveSound;
	static Clip dungeon, keQing, overHealing, heal;

	static boolean[] onOff = {true, true, false, true};//index 0 = music, index 1 = sound effects, index 2 = infintie hp, index 3 = skip dialogue
	static int[][] onOffDim = new int[4][2];//dimensions of switches

	Thread thread;
	int FPS = 30, frameCount = 0;

	long startTime, timeElapsed;

	static int[][] projC = new int[10][2];
	static double[][] projC2 = new double[10][2];

	static int[][] endProj = new int[10][2];
	static int[][] startProj = new int[10][2];
	static boolean[] projOut = new boolean[10];
	static int projsOut = 0;

	static int str = 2, dmg = 1, hearts = 0;


	static int[][][] enemyC = new int[6][][];
	static double[][][] enemyC2 = new double[6][][];
	static int[][] enemyHP = new int[6][];
	static int[][] enemyMaxHP = new int[6][];
	static int[][][] enemySize = new int[6][][];//width, height
	static int[][] enemyDirect = new int[6][];
	static int[][] enemyAtkType = new int[6][];
	static int[][] enemyAtkX = new int[6][];
	static int[][] enemyAtkY = new int[6][];
	static double[][] enemyAtkX2 = new double[6][];
	static double[][] enemyAtkY2 = new double[6][];
	static boolean[][] enemyAtkOut = new boolean[6][];
	static int[][] enemyCounter = new int[6][];
	static int[][] enemyPhase = new int[6][];

	static int roomNum = 0;

	static int frame = 0, dmgCountDown = 0;

	static boolean interact = false, playing = false, played = false, easterEgg = false;
	static boolean dmgTaken = false, invincible = false, saveInter = false;

	int sceneCount1 = 0, secretSceneCount1 = 0, sceneCount2 = 0, secretSceneCount2 = 0;

	static PrintWriter outFile = null;
	//	outFile = new PrintWriter(new File("prevGameData.txt"));
	static Scanner inFile = null;
	static String saveScene;
	static int saveHP, saveX, saveY;

	static boolean[] cutScenePlayed = {false, false, false};//0 = secret scene 1, 1 = almost ending, 2 = secret scene 2
	
	static boolean[] heartsCollected = new boolean[4];
	static boolean pebbleContact = false, aikoBag = false, aikoEquip = false;

	static int bossEndX = 75, bossEndY = 75, bossX = 450, bossY = 200, bossStartX, bossStartY, bossSize = 150;
	static int bossMaxHP = 50, bossCurrentHP = bossMaxHP;
	static double bossX2 = bossX, bossY2 = bossY;
	static int bossPhase = 1;//0 = not walking, 1 = walking, 2 = charging, 3 = attaking
	static int bossAtk = 0;//0 = not attaking, 1 = spinny, 2 = summon, 3 = quick strikes, 4 = ghost
	final static int NUMOFBOSSATKS = 3;
	static int bossTimer;
	static Image[] bossSprites = new Image[4];

	static int stunlock = 0;
	static boolean stunned = false, hearty = false;
	
	//constructor
	public Game() throws UnsupportedAudioFileException, IOException, LineUnavailableException, FontFormatException  {

		int[] enemiesPerRoom = {1,2,4,8,5,8};

		for(int room = 0; room < 6; room++) {
			enemyC[room] = new int[enemiesPerRoom[room]][2];
			enemyC2[room] = new double[enemiesPerRoom[room]][2];
			enemyHP[room] = new int[enemiesPerRoom[room]];
			enemyMaxHP[room] = new int[enemiesPerRoom[room]];
			enemySize[room] = new int[enemiesPerRoom[room]][2];//width, height
			enemyDirect[room] = new int[enemiesPerRoom[room]];
			enemyAtkType[room] = new int[enemiesPerRoom[room]];
			enemyAtkX[room] = new int[enemiesPerRoom[room]];
			enemyAtkY[room] = new int[enemiesPerRoom[room]];
			enemyAtkX2[room] = new double[enemiesPerRoom[room]];
			enemyAtkY2[room] = new double[enemiesPerRoom[room]];
			enemyAtkOut[room] = new boolean[enemiesPerRoom[room]];
			enemyCounter[room] = new int[enemiesPerRoom[room]];
			enemyPhase[room] = new int[enemiesPerRoom[room]];
		}

		//Tutorial

		int rom = 0, enem = 0;
		enemyC[rom][enem][0] = 705;
		enemyC[rom][enem][1] = 225;

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 100;

		enemyAtkType[rom][enem] = 0;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		//---------------------------- room 1
		rom = 1;
		enem = 0;
		enemyC[rom][enem][0] = 800;
		enemyC[rom][enem][1] = 50;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 90;
		enemySize[rom][enem][1] = 50;
		enemyDirect[rom][enem] = 1;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;
		enemyPhase[rom][enem] = 1;

		enem = 1;
		enemyC[rom][enem][0] = 380;
		enemyC[rom][enem][1] = 150;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 20;
		enemyDirect[rom][enem] = 1;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;
		enemyPhase[rom][enem] = 2;

		//------------------------------ room 2
		rom = 2;
		enem = 0;
		enemyC[rom][enem][0] = 830;
		enemyC[rom][enem][1] = 430;
		enemyC2[rom][enem][0] = enemyC[2][enem][0];
		enemyC2[rom][enem][1] = enemyC[2][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][0] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 1;
		enemyC[rom][enem][0] = 150;
		enemyC[rom][enem][1] = 100;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][0] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 2;
		enemyC[rom][enem][0] = 150;
		enemyC[rom][enem][1] = 400;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][enem] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 3;
		enemyC[rom][enem][0] = 300;
		enemyC[rom][enem][1] = 430;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][0] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		//-----------------------------------room 3 TODO
		rom = 3;
		enem = 0;
		enemyC[rom][enem][0] = 120;
		enemyC[rom][enem][1] = 100;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 150;
		enemySize[rom][enem][1] = 70;
		enemyDirect[rom][enem] = 1;

		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 10;
		enemyMaxHP[rom][enem] = 10;
		enemyPhase[rom][enem] = 1;

		enem = 1;
		enemyC[rom][enem][0] = 120;
		enemyC[rom][enem][1] = 150;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyAtkOut[rom][enem] = false;
		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 2;
		enemyC[rom][enem][0] = 540;
		enemyC[rom][enem][1] = 100;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][0] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyAtkOut[rom][enem] = false;
		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 3;
		enemyC[rom][enem][0] = 705;
		enemyC[rom][enem][1] = 75;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][enem] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];
		enemyAtkOut[rom][enem] = true;
		enemyCounter[rom][enem] = 7000001;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 4;
		enemyC[rom][enem][0] = 780;
		enemyC[rom][enem][1] = 75;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][enem] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];
		enemyAtkOut[rom][enem] = true;
		enemyCounter[rom][enem] = 7000001;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 5;
		enemyC[rom][enem][0] = 705;
		enemyC[rom][enem][1] = 135;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][enem] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];
		enemyAtkOut[rom][enem] = true;
		enemyCounter[rom][enem] = 7000001;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 6;
		enemyC[rom][enem][0] = 780;
		enemyC[rom][enem][1] = 135;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][enem] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];
		enemyAtkOut[rom][enem] = true;
		enemyCounter[rom][enem] = 7000001;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 7;
		enemyC[rom][enem][0] = 640;
		enemyC[rom][enem][1] = 440;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		//--------------------------------------------------------room 4
		rom = 4;
		enem = 0;

		enemyC[rom][enem][0] = 550;
		enemyC[rom][enem][1] = 250;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 40;

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;
		enemyPhase[rom][enem] = 2;

		enem = 1;
		enemyC[rom][enem][0] = 445;
		enemyC[rom][enem][1] = 200;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 100;

		enemyAtkType[rom][enem] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 2;
		enemyC[rom][enem][0] = 340;
		enemyC[rom][enem][1] = 200;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 100;

		enemyAtkType[rom][enem] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 3;
		enemyC[rom][enem][0] = 235;
		enemyC[rom][enem][1] = 200;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 100;

		enemyAtkType[rom][enem] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		enem = 4;
		enemyC[rom][enem][0] = 130;
		enemyC[rom][enem][1] = 200;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 100;

		enemyAtkType[rom][enem] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 5;
		enemyMaxHP[rom][enem] = 5;

		//-----------------------------------------------------------boss room
		rom = 5;
		enem = 0;
		enemyC[rom][enem][0] = 800;
		enemyC[rom][enem][1] = 50;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 90;
		enemySize[rom][enem][1] = 50;
		enemyDirect[rom][enem] = 1;

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;
		enemyPhase[rom][enem] = 1;

		enem = 1;
		enemyC[rom][enem][0] = 380;
		enemyC[rom][enem][1] = 150;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 20;
		enemyDirect[rom][enem] = 1;

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;
		enemyPhase[rom][enem] = 2;
		
		enem = 2;
		enemyC[rom][enem][0] = 830;
		enemyC[rom][enem][1] = 430;
		enemyC2[rom][enem][0] = enemyC[2][enem][0];
		enemyC2[rom][enem][1] = enemyC[2][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][0] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;

		enem = 3;
		enemyC[rom][enem][0] = 150;
		enemyC[rom][enem][1] = 100;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkType[rom][0] = 1;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;
		
		enem = 4;
		enemyC[rom][enem][0] = 120;
		enemyC[rom][enem][1] = 100;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 150;
		enemySize[rom][enem][1] = 70;
		enemyDirect[rom][enem] = 1;

		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 10;
		enemyPhase[rom][enem] = 1;

		enem = 5;
		enemyC[rom][enem][0] = 120;
		enemyC[rom][enem][1] = 150;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 50;
		enemySize[rom][enem][1] = 50;

		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyAtkOut[rom][enem] = false;
		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;
		
		enem = 6;

		enemyC[rom][enem][0] = 550;
		enemyC[rom][enem][1] = 250;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 40;

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;
		enemyPhase[rom][enem] = 2;

		enem = 7;
		enemyC[rom][enem][0] = 445;
		enemyC[rom][enem][1] = 200;
		enemyC2[rom][enem][0] = enemyC[rom][enem][0];
		enemyC2[rom][enem][1] = enemyC[rom][enem][1];

		enemySize[rom][enem][0] = 100;
		enemySize[rom][enem][1] = 100;

		enemyAtkType[rom][enem] = 3;
		enemyAtkX[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY[rom][enem] = enemyC[rom][enem][1];
		enemyAtkX2[rom][enem] = enemyC[rom][enem][0];
		enemyAtkY2[rom][enem] = enemyC[rom][enem][1];

		enemyHP[rom][enem] = 0;
		enemyMaxHP[rom][enem] = 5;
		
		//--------------------------------------------------------------
		slimeUp[0] = Toolkit.getDefaultToolkit().getImage("slime1.png");
		slimeUp[1] = Toolkit.getDefaultToolkit().getImage("slime2.png");
		slimeUp[2] = Toolkit.getDefaultToolkit().getImage("slime4.png");

		slimeDown[0] = Toolkit.getDefaultToolkit().getImage("slime11.png");
		slimeDown[1] = Toolkit.getDefaultToolkit().getImage("slime22.png");
		slimeDown[2] = Toolkit.getDefaultToolkit().getImage("slime5.png");

		slimeLeft[0] = Toolkit.getDefaultToolkit().getImage("slime1.png");
		slimeLeft[1] = Toolkit.getDefaultToolkit().getImage("slime2.png");
		slimeLeft[2] = Toolkit.getDefaultToolkit().getImage("slime3.png");

		slimeRight[0] = Toolkit.getDefaultToolkit().getImage("slime11.png");
		slimeRight[1] = Toolkit.getDefaultToolkit().getImage("slime22.png");
		slimeRight[2] = Toolkit.getDefaultToolkit().getImage("slime33.png");

		inFile = new Scanner(new File("prevGameData.txt"));

		if(!inFile.nextLine().equals("0")) {
			played = true;
			saveHP = Integer.parseInt(inFile.nextLine());
			saveX = Integer.parseInt(inFile.nextLine());
			saveY = Integer.parseInt(inFile.nextLine());
			saveScene = inFile.nextLine();
		}
		//this.addWindowListener();

		thread = new Thread(this);
		thread.start();

		buttonFont = Font.createFont(Font.TRUETYPE_FONT, new File("Gamer.ttf")).deriveFont(50f);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Gamer.ttf")));

		bigFont = Font.createFont(Font.TRUETYPE_FONT, new File("Gamer.ttf")).deriveFont(150f);

		smallFont = Font.createFont(Font.TRUETYPE_FONT, new File("Gamer.ttf")).deriveFont(25f);

		setPreferredSize (new Dimension(1200,600));

		addMouseListener (this);
		addKeyListener(this);
		setFocusable(true);

		//music + sound effects
		AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("Bird That Carries You Over A Disproportionately Small Gap.wav"));
		bgm = AudioSystem.getClip();
		bgm.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("click.wav"));
		clickSound = AudioSystem.getClip();
		clickSound.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("hit.wav"));
		hit = AudioSystem.getClip();
		hit.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("doorOpening.wav"));
		doorOpeningSound = AudioSystem.getClip();
		doorOpeningSound.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("Spider Dance.wav"));
		introMusic = AudioSystem.getClip();
		introMusic.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("Annoying Dog Theme.wav"));
		deathMusic = AudioSystem.getClip();
		deathMusic.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("savePoint.wav"));
		saveSound = AudioSystem.getClip();
		saveSound.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("honk honk.wav"));
		honk = AudioSystem.getClip();
		honk.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("Pop sound effect.wav"));
		pop = AudioSystem.getClip();
		pop.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("LASER SOUND EFFECT.wav"));
		laser = AudioSystem.getClip();
		laser.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("DOOM SOUND EFFECT DDG USES.wav"));
		shockwaveSound = AudioSystem.getClip();
		shockwaveSound.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("Dungeon Door sound effect.wav"));
		dungeon = AudioSystem.getClip();
		dungeon.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("ka-ching sound effect.wav"));
		keQing = AudioSystem.getClip();
		keQing.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("Wrong Answer Sound effect.wav"));
		overHealing = AudioSystem.getClip();
		overHealing.open(sound);

		sound = AudioSystem.getAudioInputStream(new File ("Crunch sound effect _ No copyright.wav"));
		heal = AudioSystem.getClip();
		heal.open(sound);

		for (int i = 1 ; i <= 4 ; i++){
			String imageFileName = "sprite" + i + ".png";
			sprites [i - 1]= Toolkit.getDefaultToolkit().getImage(imageFileName);
		}

		for (int i = 1 ; i <= 4 ; i++){
			String imageFileName = "shroom" + i + ".png";
			shroom [i - 1]= Toolkit.getDefaultToolkit().getImage(imageFileName);
		}

		shock[0] = Toolkit.getDefaultToolkit().getImage("shock1.png");
		shock[1] = Toolkit.getDefaultToolkit().getImage("shock2.png");

		//cutScene1 = 8

		for (int i = 1 ; i <= 8 ; i++){
			String imageFileName = "cutScene1" + i + ".png";
			cutScene1 [i - 1]= Toolkit.getDefaultToolkit().getImage(imageFileName);
		}

		for(int i = 1; i <= 4; i++) {
			String imageFileName = "bossSprite" + i + ".png";
			bossSprites [i - 1]= Toolkit.getDefaultToolkit().getImage(imageFileName);
		}

	}

	public void initialize() {
		//setups before the game starts running
		System.out.println("Thread: Initializing game");
		startTime = System.currentTimeMillis();
		timeElapsed = 0;
		FPS = 30;
		for(int i = 0; i < 100000; i++) {
			frame++;

			// this is just to delay time
			String s = "set up stuff blah blah blah";
			s.toUpperCase();
		}
		System.out.println("Thread: Done initializing game");
	}

	public void update() {
		//update stuff
		timeElapsed = System.currentTimeMillis() - startTime;
		frameCount++;
	}

	//Description: draws a button 
	//Parameters: graphics, buttName = text that will be on button, xPos & yPos = coordinates, 
	//w = width (same height for all buttons), change for what scene to change to
	//Return: no return, just draws button and calls mouseListener
	public static void button(Graphics g, String buttName, int xPos, int yPos, int w) {
		//		PointerInfo a = MouseInfo.getPointerInfo();
		//		Point b = a.getLocation();
		//		mouseX = (int) b.getX();
		//		mouseY = (int) b.getY();

		g.setColor(Color.black);
		g.fillRect(xPos, yPos, w, 60);
		g.setColor(Color.white);
		g.drawRect(xPos, yPos, w, 60);
		g.setFont(buttonFont);
		g.drawString(buttName, xPos, yPos + 40);

		//MOVE THIS STUFF TO MOUSECLICKED (make a seperate if statement for each button ehe...
		//		if(clicked == true && mouseX >= xPos && mouseX <= xPos + w && mouseY >= yPos && mouseY <= mouseY + 60) {
		//			scene = change;
		//			if(onOff[1]) {
		//				clickSound.setFramePosition (0);
		//				clickSound.start();
		//			}
		//		}
	}

	//Description: draws a switch (mainly used in settings)
	//Parameters: graphics, buttName = text next to switch, xpos & ypos = coordinates, change = thing that will be changed
	//Return = no return, just draws switch and calls mouselistener
	public static void switchButton (Graphics g, String buttName,int xPos, int yPos, int change) {
		g.setFont(buttonFont);
		g.setColor(Color.white);
		g.drawString(buttName, xPos, yPos + 35);
		int width = g.getFontMetrics().stringWidth(buttName);

		onOffDim[change][0] = width;
		onOffDim[change][1] = yPos;

		if (onOff[change] == true) {
			g.setColor(new Color(95, 186, 76));
			g.fillRect(xPos + 40 + width, yPos - 5, 80, 60);
			g.fillOval(xPos + 15 + width, yPos - 5, 60, 60); // left
			g.fillOval(xPos + 90 + width, yPos - 5, 60, 60); // right

			g.setColor(new Color(194, 255, 179));
			g.fillRect(xPos + 25 + 20 + width, yPos, 75, 50);
			g.fillOval(xPos + 20 + width, yPos, 50, 50); // left
			g.setColor(new Color(95, 186, 76));
			g.fillOval(xPos + 75 + 20 + width, yPos, 50, 50); // right
			g.setFont(switchFont);
			g.drawString("ON", xPos + 35 + width, yPos + 35);
			//			if(clicked == true && mouseX >= xPos + 15 + width && mouseX <= xPos + 150 + width && mouseY >= yPos - 5 && mouseY <= yPos + 55)
			//				onOff[change] = false;
		}else {
			g.setColor(new Color(186, 76, 76));
			g.fillRect(xPos + 20 + 20 + width, yPos - 5, 80, 60);
			g.fillOval(xPos - 5 + 20 + width, yPos - 5, 60, 60); // left
			g.fillOval(xPos + 70 + 20 + width, yPos - 5, 60, 60); // right

			g.setColor(new Color(255, 145, 145));
			g.fillRect(xPos + 25 + 20 + width, yPos, 75, 50);
			g.fillOval(xPos + 75 + 20 + width, yPos, 50, 50); // right

			g.setColor(new Color(186, 76, 76));
			g.fillOval(xPos + 20 + width, yPos, 50, 50); // left

			g.setFont(switchFont);
			g.drawString("OFF", xPos + 55 + 20 + width, yPos + 35);

		}

		//System.out.println(clicked);
	}

	//Description: draws character and moves 
	//Parameters: graphics
	//Return: no return, just draws character
	void chara (Graphics g) {
		if(stunned)
			speed = 0;
		if(stunlock > 700000)
			stunned = false;
		//System.out.println(up);
		g.setColor(new Color(0,0,0,150));
		g.fillOval(charC[0], charC[1] + charSize - 20, 70, 30);
		g.drawImage(sprites[spriteNum], charC[0], charC[1], charSize, charSize, null);
		if(left) {
			charC[0] -= speed;
			spriteNum = 1;
		}else if(right) {
			charC[0] += speed;
			spriteNum = 2;
		}

		if(up) {
			charC[1] -= speed;
			spriteNum = 3;
		}else if(down) {
			charC[1] += speed;
			spriteNum = 0;
		}

		if(currentHP <= 0)
			scene = "death";

		if(dmgTaken){//TODO
			if(!invincible && !onOff[2])
				currentHP-= dmg;
			dmgTaken = false;
			invincible = true;
			dmgCountDown = 0;
		} 
		if(dmgCountDown > 100000) {
			invincible = false;
		}

		if(scene.equals("Boss Room")) {
			System.out.println("asdasd");
			int cRight = charC[0] + charSize, cLeft = charC[0], cTop = charC[1], cBottom = charC[1] + charSize;
			int wRight = bossX + bossSize, wLeft = bossX, wTop = bossY, wBottom = bossY + bossSize;
			if(cRight > wLeft && cLeft < wLeft &&
					cRight - wLeft < cBottom - wTop && 
					cRight - wLeft < wBottom - cTop){//collide with left side of wall
				charC[0] = wLeft - charSize;
			} else if(cLeft < wRight && cRight > wRight &&
					wRight - cLeft < cBottom - wTop && 
					wRight - cLeft < wBottom - cTop){//collide with right side of wall
				charC[0] = wLeft + bossSize;
			}
			else if(cBottom > wTop && cTop < wTop &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
				charC[1] = wTop - charSize;
			}else if(cTop < wBottom && cBottom > wBottom &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
				charC[1] = wTop + bossSize;
			}
		}
		
		if(charC[0] < 50)
			charC[0] = 50;
		if(charC[1] < 20)
			charC[1] = 20;
		if(charC[0] + charSize > 75 + 50 + 855)
			charC[0] = 75 + 50 + 855 - charSize;
		if(charC[1] + charSize > 525)
			charC[1] = 525 - charSize;
	}

	//Description: detects collisions
	//Paramters: anything starting with w = object 1, c = object 2 left, top, w & h = dimensions
	//Return: boolean of whether or not a collision has happened
	public static boolean collide(int wLeft, int wTop, int cLeft, int cTop, int wW, int wH, int cW, int cH) {
		boolean collide = false;
		int cRight = cLeft + cW, cBottom = cTop + cH;
		int wRight = wLeft + wW, wBottom = wTop + wH;
		if(cRight >= wLeft && cLeft <= wLeft &&
				cRight - wLeft <= cBottom - wTop && 
				cRight - wLeft <= wBottom - cTop){//collide with left side of wall
			collide = true;
		} else if(cLeft <= wRight && cRight >= wRight &&
				wRight - cLeft <= cBottom - wTop && 
				wRight - cLeft <= wBottom - cTop){//collide with right side of wall
			collide = true;
		}
		else if(cBottom >= wTop && cTop <= wTop &&
				(cRight >= wLeft && cLeft <= wRight)){//rect collides from top side of the wall
			collide = true;
		}else if(cTop <= wBottom && cBottom >= wBottom &&
				(cRight >= wLeft && cLeft <= wRight)){//rect collides from bottom side of the wall
			collide = true;
		} else if(cLeft >= wLeft && cRight <= wRight && cTop >= wTop && cBottom <= wBottom)//if we're inside
			collide = true;

		return collide;
	}
	//Description: draws walls + detects character, projectile, etc. collisions
	//Parameters: graphics, xPos & yPos = coordinates, w & h = dimensions of wall
	//Return: no return

	public static void wall (Graphics g, int xPos, int yPos, int w, int h) {
		g.setColor(new Color(70, 11, 8));
		g.fillRect(xPos, yPos, w, h);

		int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
		int wLeft = xPos, wRight = xPos + w, wTop = yPos, wBottom = yPos + h;

		if(cRight > wLeft && cLeft < wLeft &&
				cRight - wLeft < cBottom - wTop && 
				cRight - wLeft < wBottom - cTop){//collide with left side of wall
			charC[0] = xPos - charSize;
		} else if(cLeft < wRight && cRight > wRight &&
				wRight - cLeft < cBottom - wTop && 
				wRight - cLeft < wBottom - cTop){//collide with right side of wall
			charC[0] = xPos + w;
		}
		else if(cBottom > wTop && cTop < wTop &&
				(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
			charC[1] = yPos - charSize;
		}else if(cTop < wBottom && cBottom > wBottom &&
				(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
			charC[1] = yPos + h;
		}

		cLeft = bossX; 
		cRight = bossX + bossSize;
		cTop = bossY;
		cBottom = bossY + bossSize;
		wLeft = xPos;
		wRight = xPos + w;
		wTop = yPos;
		wBottom = yPos + h;

		if(cRight > wLeft && cLeft < wLeft &&
				cRight - wLeft < cBottom - wTop && 
				cRight - wLeft < wBottom - cTop){//collide with left side of wall
			bossX = xPos - bossSize;
			bossPhase = 0;
		} else if(cLeft < wRight && cRight > wRight &&
				wRight - cLeft < cBottom - wTop && 
				wRight - cLeft < wBottom - cTop){//collide with right side of wall
			bossX = xPos + w;
			bossPhase = 0;
		}
		else if(cBottom > wTop && cTop < wTop &&
				(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
			bossY = yPos - bossSize;
			bossPhase = 0;
		}else if(cTop < wBottom && cBottom > wBottom &&
				(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
			bossY = yPos + h;
			bossPhase = 0;
		}

		for(int i = 0; i < 10; i++) {//projectiles
			if(projOut[i]) {
				cLeft = projC[i][0];
				cRight = cLeft + 30;
				cTop = projC[i][1];
				cBottom = cTop + 30;
				if(collide(wLeft, wTop, cLeft, cTop, w, h, 30, 30)) {
					projOut[i] = false;
					if(onOff[1]) {
						hit.setFramePosition (0);
						hit.start();
					}
				}
			}
		}

		for(int i = 0; i < enemyAtkX[roomNum].length; i++) //HOMING PROJECTILES
			if(enemyAtkOut[roomNum][i]) {//if it's a homing projectile
				cLeft = (int)enemyAtkX2[roomNum][i];
				cTop = (int)enemyAtkY2[roomNum][i];
				if(collide(wLeft, wTop, cLeft, cTop, w, h, 20, 20)) {
					enemyCounter[roomNum][i] = 0;
					enemyAtkOut[roomNum][i] = false;

					if(onOff[1]) {
						hit.setFramePosition (0);
						hit.start();
					}
				}

			}

	}

	//Description: me being lazy and make a method for a back button
	//Parameters: graphics
	//Return: no return, only button
	public static void back (Graphics g) {
		button(g, "  Back", 10, 530, 140);
	}

	//Description: detects if character is within the area needed to interact with something
	//Parameters: graphics, xpos & ypos = where interact box starts, w & h = how big the interact box is, thing = type of thing to interact with, direct = direction of door, room = which scene to change to
	//Return: no return
	public static void interact (Graphics g, int xPos, int yPos, int w, int h, String thing, String direct, String room) throws IOException {
		if(charC[0] < xPos + w && charC[0] + charSize > xPos && charC[1] < yPos + h && charC[1] + charSize > yPos) {
			g.setColor(new Color(0, 0, 0));
			g.fillRect(940, 490, 250, 100);
			g.setFont(buttonFont);
			g.setColor(new Color(252, 232, 3));
			g.fillRect(940, 490, 10, 100);//left
			g.fillRect(1180, 490, 10, 100);//right
			g.fillRect(940, 480, 250, 10);//top
			g.fillRect(940, 580, 250, 10);//bottom
			g.drawString("Press [F] to", 960, 520);
			g.drawString("  Interact", 960, 570);
			//System.out.println("asdas");
			interact = true;
			if(thing.equals("door")) {
				lastCharC[0] = 1;
				if(direct.equals("up"))
					lastCharC[1] = 0;
				else if(direct.equals("down"))
					lastCharC[1] = 1;
				else if(direct.equals("left"))
					lastCharC[1] = 2;
				else
					lastCharC[1] = 3;
				nextRoom = room;
			} else if (thing.equals("respawn")) {
				lastCharC[0] = 3;
			} else if (thing.equals("save")) {
				saveInter = true;
				lastCharC[0] = 4;
			} else if(thing.equals("heart")) {
				lastCharC[0] = 5;
			}

		} else {
			saveInter = false;
		}
	}

	//Description: determines whether or not we can shoot then sets up nums
	//Parameters: endX & endY = ending coordinates, projNum = which projectile in array
	//Return: no return
	public static void shoot(int endX, int endY, int projNum) {//TODO
		if(!projOut[projNum]) {
			projC2[projNum][0] = charC[0] + 40;
			projC2[projNum][1] = charC[1] + 40;
			projOut[projNum] = true;
			startProj[projNum][0] = charC[0];
			startProj[projNum][1] = charC[1];
			endProj[projNum][0] = endX;
			endProj[projNum][0] *= 1200;
			endProj[projNum][1] = endY;
			endProj[projNum][1] *= 1200;
		}


	}

	//Description: draws the projectile
	//Parameters: graphics, which projectile it's drawing 
	//Return: no return
	public static void drawShoot(Graphics g, int projNum) {
		if(projOut[projNum]) {

			double adj, opp, speed = 0.8, hyp, mult;
			//calculating the course of the projectile
			adj = endProj[projNum][0] - startProj[projNum][0] * 1200;
			if(adj < 0)
				adj *= -1;
			opp = endProj[projNum][1] - startProj[projNum][1] * 1200;
			if(opp < 0)
				opp *= -1;

			//calculating speed
			hyp = Math.sqrt(Math.pow(adj, 2) + Math.pow(opp, 2));

			mult = hyp/speed;
			//mult *=10;

			//figuring out which direction the things are going in
			if(endProj[projNum][0]/1200 != startProj[projNum][0])
				if(endProj[projNum][0]/1200 < startProj[projNum][0]) {
					projC2[projNum][0] -= adj / mult ;
				}
				else
					projC2[projNum][0] += adj / mult;
			//projC[0] += (int)Math.sqrt((1 - )
			if(endProj[projNum][1]/1200 != startProj[projNum][1])
				if(endProj[projNum][1]/1200 < startProj[projNum][1])
					projC2[projNum][1] -= opp / mult;
				else
					projC2[projNum][1] += opp / mult;

			//put it in integer format
			projC[projNum][0] = (int)projC2[projNum][0];
			projC[projNum][1] = (int)projC2[projNum][1];

			//draw projectile
			//g.fillOval(projC[projNum][0], projC[projNum][1], size, size);
			//shadow
			g.setColor(new Color(0,0,0,150));
			g.fillOval(projC[projNum][0] + 5, projC[projNum][1] + 40, 20, 10);

			g.drawImage(fireBall, projC[projNum][0], projC[projNum][1], 30, 30, null);

			//glow
			g.setColor(new Color(252, 111, 3, 25));
			g.fillOval(projC[projNum][0] - 10, projC[projNum][1] - 10, 50, 50);
			g.setColor(new Color(255, 179, 87, 50));
			g.fillOval(projC[projNum][0] - 5, projC[projNum][1] - 5, 40, 40);

			if(scene.equals("Boss Room") && collide(bossX, bossY, projC[projNum][0], projC[projNum][1], bossSize, bossSize, 20, 20)) {
				projOut[projNum] = false;
				bossCurrentHP -= str;
			}
		}

	}

	//Description: draws pause screen
	//Parameters: graphics
	//Return: no return
	public static void pauseScreen(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, 1200, 600);
		button(g, "  Back to Game", 450, 300, 300);
		button(g, "    Main Menu", 450, 370, 300);
		button(g, "        Exit", 450, 440, 300);
		g.setColor(Color.white);
		g.setFont(bigFont);
		g.drawString("GAME PAUSED", 290, 250);

	}

	//Description: draws game border that we can't walk into
	//Parameters: graphics
	//Return: no return
	public static void gameBorder(Graphics g) {
		//actual border
		g.setColor(Color.black);
		g.fillRect(0, 0, 75, 600);//left
		g.fillRect(0, 0, 1200, 20);//top
		g.fillRect(930, 0, 270, 600);//right
		g.fillRect(0, 520, 1200, 130);//bottom

		//how many hearts we have
		if(hearts > 0) {
			g.drawImage(heart,945,20,50,50,null);
			g.setColor(Color.white);
			g.setFont(buttonFont);
			g.drawString("x" + hearts, 1000, 60);
			len = g.getFontMetrics().stringWidth("x" + hearts);
			len2 = g.getFontMetrics().stringWidth(" Use ");
			button(g," Use ", 945 + len + 70, 20, len2);

		}
		if(pebbleContact) {
			Image phone = Toolkit.getDefaultToolkit().getImage("phone.png");
			g.drawImage(phone, 945, 90, 50, 50, null);
			button(g," Use ", 1005, 90, len2);
		}

		if(aikoBag) {
			Image bag = Toolkit.getDefaultToolkit().getImage("bag.png");
			g.drawImage(bag, 945, 160, 50, 50, null);
			if(aikoEquip) {
				len3 = g.getFontMetrics().stringWidth(" Unequip ");
				button(g," Unequip ", 1005, 160, len3);
				speed = 3;
			} else {
				len3 = g.getFontMetrics().stringWidth(" Equip ");
				button(g," Equip ", 1005, 160, len3);
				speed = 1;
			}
		}
		//shows which room we're in
		g.setColor(Color.white);
		g.setFont(buttonFont);
		g.drawString("" + scene, 945, 460);

		//how many balls we have left
		int spellsLeft = 0;
		for(int i = 0; i < projOut.length - 1; i++) {
			if(!projOut[i]) {
				spellsLeft++;
			}
		}
		g.drawString("Shots left: " + spellsLeft, 945, 430);

		//hp bar
		g.setColor(Color.red);
		g.fillRect(75, 540, 300, 30);
		g.setColor(Color.green);
		g.fillRect(75, 540, (int)(300 * (currentHP * 1.0 / maxHP)), 30);
		g.setColor(Color.yellow);
		g.setFont(buttonFont);

		if(!onOff[2])
			g.drawString("" + currentHP + "/" + maxHP + " HP", 85, 565);
		else
			g.drawString("Infinite HP", 85, 565);

		//pause button
		g.setColor(Color.black);
		g.fillRect(10, 10, 50, 50);
		g.setColor(Color.white);
		g.fillRect(10, 10, 5, 50);//left
		g.fillRect(10, 10, 50, 5);//top
		g.fillRect(10, 60, 55, 5);//bottom
		g.fillRect(60, 10, 5, 50);//right

		int[] xPoints = {25, 25, 55};
		int[] yPoints = {20, 55, 37};

		g.fillPolygon(xPoints, yPoints, 3);
		g.drawImage(darkness, 0,0,null);

		if(interact) {
			g.setColor(new Color(0, 0, 0));
			g.fillRect(940, 490, 250, 100);
			g.setFont(buttonFont);
			g.setColor(new Color(252, 232, 3));
			g.fillRect(940, 490, 10, 100);//left
			g.fillRect(1180, 490, 10, 100);//right
			g.fillRect(940, 480, 250, 10);//top
			g.fillRect(940, 580, 250, 10);//bottom
			g.drawString("Press [F] to", 960, 520);
			g.drawString("  Interact", 960, 570);
		}
		if(hearty) {
			g.setColor(Color.white);
			g.setFont(buttonFont);
			g.drawString("Use hearts to heal!", 390, 560);
		}
	}

	//Description: draws slimes and enemy attacks (laser)
	//Parameters: graphics, direction it's going in (up/down, left/right), min & max = area of movement, 
	//speed = speed of enemy movement (not attacks), image of enemy
	//Return: no return
	public static void slime(Graphics g, int direct, int min, int max, int enemyNum, double speed, int dmg1) {
		Image enemyImg = slimeUp[0];
		//enemyDirect 1 = posititve, -1 = negative
		//direct 1 = up down, direct 0 = left right
		if(enemyHP[roomNum][enemyNum] > 0) {
			g.setColor(new Color(0,0,0,150));
			g.fillOval(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1] - 20, enemySize[roomNum][enemyNum][0], 40);

			double sped = speed;
			if(enemyPhase[roomNum][enemyNum] == 2 || enemyPhase[roomNum][enemyNum] == 3) {
				sped = speed;
			}

			if (direct == 1 || direct == 3) {// if we're going up/down
				if(enemyDirect[roomNum][enemyNum] == 1) {//if we're going positive
					if(enemyC2[roomNum][enemyNum][1]> min)
						enemyC2[roomNum][enemyNum][1] -= sped;
					else
						enemyDirect[roomNum][enemyNum] *= -1;
				} else if (enemyDirect[roomNum][enemyNum] == -1){
					if(enemyC2[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1] < max)
						enemyC2[roomNum][enemyNum][1] += sped;
					else
						enemyDirect[roomNum][enemyNum] *= -1;
				}
			} else if (direct == 0 || direct == 2) {// if we're going left/right
				if(enemyDirect[roomNum][enemyNum] == 1) {//if we're going positive
					if(enemyC2[roomNum][enemyNum][0] > min)
						enemyC2[roomNum][enemyNum][0] -= sped;
					else
						enemyDirect[roomNum][enemyNum] *= -1;
				} else if (enemyDirect[roomNum][enemyNum] == -1){
					if(enemyC2[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] < max)
						enemyC2[roomNum][enemyNum][0] += sped;
					else
						enemyDirect[roomNum][enemyNum] *= -1;
				}
			}

			enemyC[roomNum][enemyNum][0] = (int)enemyC2[roomNum][enemyNum][0];
			enemyC[roomNum][enemyNum][1] = (int)enemyC2[roomNum][enemyNum][1];

			int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
			int wLeft = enemyC[roomNum][enemyNum][0], wRight = enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0], wTop = enemyC[roomNum][enemyNum][1], wBottom = enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1];

			if(cRight > wLeft && cLeft < wLeft &&
					cRight - wLeft < cBottom - wTop && 
					cRight - wLeft < wBottom - cTop){//collide with left side of wall
				charC[0] = wLeft - charSize;
			} else if(cLeft < wRight && cRight > wRight &&
					wRight - cLeft < cBottom - wTop && 
					wRight - cLeft < wBottom - cTop){//collide with right side of wall
				charC[0] = wRight;
			}
			else if(cBottom > wTop && cTop < wTop &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
				charC[1] = wTop - charSize;
			}else if(cTop < wBottom && cBottom > wBottom &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
				charC[1] = wBottom;
			}

			//being hit by us
			for(int i = 0; i < 10; i++) //projectiles
				if(projOut[i]) {
					cLeft = projC[i][0];
					cRight = cLeft + 30;
					cTop = projC[i][1];
					cBottom = cTop + 30;

					if(collide(wLeft, wTop, cLeft, cTop, enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], 30, 30)) {
						projOut[i] = false;
						enemyHP[roomNum][enemyNum]-= str;

						if(onOff[1]) {
							hit.setFramePosition (0);
							hit.start();
						}
					}

				}

			if(enemyPhase[roomNum][enemyNum] == 1) {
				laser.stop();
				if(enemyCounter[roomNum][enemyNum] > 3000000) {
					enemyPhase[roomNum][enemyNum] = 2;
					enemyCounter[roomNum][enemyNum] = 0;
				} 
				if(direct == 1) {
					enemyImg = slimeUp[0];
				} else if(direct == 2) {
					enemyImg = slimeDown[0];
				} else if(direct == 3) {
					enemyImg = slimeRight[0];
				} else
					enemyImg = slimeLeft[0];
			} else if(enemyPhase[roomNum][enemyNum] == 2) {
				if(enemyCounter[roomNum][enemyNum] > 1000000) {
					enemyPhase[roomNum][enemyNum] = 3;
					enemyCounter[roomNum][enemyNum] = 0;
				} else {
					g.setColor(new Color(255, 0, 0, 100));
					if(direct == 1) {//left
						g.fillRect(0, enemyC[roomNum][enemyNum][1], enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1]);
						enemyImg = slimeLeft[1];
					} else if(direct == 3) {//right
						g.fillRect(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], 1200, enemySize[roomNum][enemyNum][1]);
						enemyImg = slimeRight[1];
					} else if(direct == 0) {//up
						g.fillRect(enemyC[roomNum][enemyNum][0], 0, enemySize[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1]);
						enemyImg = slimeUp[1];
					} else {//down
						g.fillRect(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], enemySize[roomNum][enemyNum][0], 600);
						enemyImg = slimeDown[1];
					}
				}
			} else if (playing){
				laser.start();
				laser.loop(Clip.LOOP_CONTINUOUSLY);				
				if(enemyCounter[roomNum][enemyNum] > 3000000) {
					enemyPhase[roomNum][enemyNum] = 1;
					enemyCounter[roomNum][enemyNum] = 0;

				} 
				g.setColor(Color.white);
				if(direct == 1) {//left
					enemyImg = slimeLeft[2];
					g.fillRect(0, enemyC[roomNum][enemyNum][1], enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2, enemySize[roomNum][enemyNum][1]);
					if(collide(0,enemyC[roomNum][enemyNum][1],
							charC[0], charC[1], 
							enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0], 
							enemySize[roomNum][enemyNum][1], charSize, charSize)) {
						dmgTaken = true;

					}
				} else if(direct == 3) {//right
					enemyImg = slimeRight[2];
					g.fillRect(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], 1200, enemySize[roomNum][enemyNum][1]);
					if(collide(enemyC[roomNum][enemyNum][0],enemyC[roomNum][enemyNum][1],
							charC[0], charC[1], 
							1200,enemySize[roomNum][enemyNum][1], 
							charSize, charSize)) {
						dmgTaken = true;
					}
				} else if(direct == 0) {//up
					enemyImg = slimeUp[2];
					g.fillRect(enemyC[roomNum][enemyNum][0], 0, enemySize[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1]);
					if(collide(enemyC[roomNum][enemyNum][0],0,
							charC[0], charC[1], 
							enemySize[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1], 
							charSize, charSize)) {
						dmgTaken = true;
					}
				} else {//down
					enemyImg = slimeDown[2];
					g.fillRect(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], enemySize[roomNum][enemyNum][0], 600);
					if(collide(enemyC[roomNum][enemyNum][0],enemyC[roomNum][enemyNum][1],
							charC[0], charC[1], 
							enemySize[roomNum][enemyNum][0], 600, 
							charSize, charSize)) {
						dmgTaken = true;
					}
				}
			}

			//health bar
			g.setColor(Color.black);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 34, enemyC[roomNum][enemyNum][1] - 24, 68, 18);

			g.setColor(Color.red);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, 60, 10);

			g.setColor(Color.green);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, (int)(60 * (1.0 * enemyHP[roomNum][enemyNum]/enemyMaxHP[roomNum][enemyNum])), 10);

			g.drawImage(enemyImg, enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], null);

			if (enemyPhase[roomNum][enemyNum] == 2){
				g.drawImage(exclam, enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] - 20, enemyC[roomNum][enemyNum][1] - 40, 20, 40, null);
			}
		}
	}

	//Description: draws enemy and enemy attacks
	//Parameters: graphics, direction it's going in (up/down, left/right), min & max = area of movement, 
	//speed = speed of enemy movement (not attacks), image of enemy
	//Return: no return
	public static void shock (Graphics g, int enemyNum, int dmg1) {
		Image enemyImg = shock[1];
		//enemyDirect 1 = posititve, -1 = negative
		//direct 1 = up down, direct 0 = left right
		if(enemyHP[roomNum][enemyNum] > 0) {
			//shadow
			g.setColor(new Color(0,0,0,150));
			g.fillOval(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1] - 20, enemySize[roomNum][enemyNum][0], 40);

			//us walking into it
			int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
			int wLeft = enemyC[roomNum][enemyNum][0], wRight = enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0], wTop = enemyC[roomNum][enemyNum][1], wBottom = enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1];

			if(cRight > wLeft && cLeft < wLeft &&
					cRight - wLeft < cBottom - wTop && 
					cRight - wLeft < wBottom - cTop){//collide with left side of wall
				charC[0] = wLeft - charSize;
			} else if(cLeft < wRight && cRight > wRight &&
					wRight - cLeft < cBottom - wTop && 
					wRight - cLeft < wBottom - cTop){//collide with right side of wall
				charC[0] = wRight;
			}
			else if(cBottom > wTop && cTop < wTop &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
				charC[1] = wTop - charSize;
			}else if(cTop < wBottom && cBottom > wBottom &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
				charC[1] = wBottom;
			}

			//being hit by us
			for(int i = 0; i < 10; i++) //projectiles
				if(projOut[i]) {
					cLeft = projC[i][0];
					cRight = cLeft + 30;
					cTop = projC[i][1];
					cBottom = cTop + 30;

					if(collide(wLeft, wTop, cLeft, cTop, enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], 30, 30)) {
						projOut[i] = false;
						enemyHP[roomNum][enemyNum]-= str;

						if(onOff[1]) {
							hit.setFramePosition (0);
							hit.start();
						}
					}

				}

			enemyAtkX[roomNum][enemyNum] -= 1;
			enemyAtkY[roomNum][enemyNum] -= 1;
			if(enemyAtkX[roomNum][enemyNum] > enemyC[roomNum][enemyNum][0] - 150) {//changing opacity of circle + how far
				//as we get away from center
				int x = enemyAtkX[roomNum][enemyNum] - enemyC[roomNum][enemyNum][0];
				x *= -1;
				int sizeAtk = x * 2;
				int opacity = 255 - x;

				g.setColor(new Color(255, 255, 255, opacity));
				g.fillOval(enemyAtkX[roomNum][enemyNum], enemyAtkY[roomNum][enemyNum], sizeAtk, sizeAtk);

				//checking for collisions
				//got help from: https://yal.cc/rectangle-circle-intersection-test/
				int checkX, checkY, radius;
				radius = enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - enemyAtkX[roomNum][enemyNum];
				checkX = (enemyAtkX[roomNum][enemyNum] + radius) - Math.max(charC[0], Math.min((enemyAtkX[roomNum][enemyNum] + radius), charC[0] + charSize));
				checkY = (enemyAtkY[roomNum][enemyNum] + radius) - Math.max(charC[1], Math.min((enemyAtkY[roomNum][enemyNum] + radius), charC[1] + charSize));
				if((checkX * checkX + checkY * checkY) < (radius * radius)){
					dmg = dmg1;
					dmgTaken = true;
				}

			} 

			//health bar
			g.setColor(Color.black);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 34, enemyC[roomNum][enemyNum][1] - 24, 68, 18);

			g.setColor(Color.red);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, 60, 10);

			g.setColor(Color.green);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, (int)(60 * (1.0 * enemyHP[roomNum][enemyNum]/enemyMaxHP[roomNum][enemyNum])), 10);

			if(enemyAtkX[roomNum][enemyNum] <= enemyC[roomNum][enemyNum][0] - 750) {
				enemyAtkX[roomNum][enemyNum] = enemyC[roomNum][enemyNum][0];
				enemyAtkY[roomNum][enemyNum] = enemyC[roomNum][enemyNum][1];
				if(onOff[1]) {
					shockwaveSound.setFramePosition(0);
					shockwaveSound.start();
				}
			} else if(enemyAtkX[roomNum][enemyNum] <= enemyC[roomNum][enemyNum][0] - 500) {
				g.drawImage(exclam, enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] - 20, enemyC[roomNum][enemyNum][1] - 40, 20, 40, null);
				enemyImg = shock[0];
			} 
			g.drawImage(enemyImg, enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], null);

		}
	}

	//Description: draws shroom and enemy attacks (homing projectiles)
	//Parameters: graphics, direction it's going in (up/down, left/right), min & max = area of movement, 
	//speed = speed of enemy movement (not attacks), image of enemy
	//Return: no return
	public static void shroom(Graphics g, int direct, int enemyNum, int dmg1) {
		//enemyDirect 1 = posititve, -1 = negative
		//direct 1 = up down, direct 0 = left right
		Image enemyImg = shroom[0];
		if(enemyHP[roomNum][enemyNum] > 0) {
			//which direction we're facing
			if(direct == 0) {
				enemyImg = shroom[0];
			} else
				enemyImg = shroom[2];

			//shadow
			g.setColor(new Color(0,0,0,150));
			g.fillOval(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1] - 20, enemySize[roomNum][enemyNum][0], 40);

			int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
			int wLeft = enemyC[roomNum][enemyNum][0], wRight = enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0], wTop = enemyC[roomNum][enemyNum][1], wBottom = enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1];

			if(cRight > wLeft && cLeft < wLeft &&
					cRight - wLeft < cBottom - wTop && 
					cRight - wLeft < wBottom - cTop){//collide with left side of wall
				charC[0] = wLeft - charSize;
			} else if(cLeft < wRight && cRight > wRight &&
					wRight - cLeft < cBottom - wTop && 
					wRight - cLeft < wBottom - cTop){//collide with right side of wall
				charC[0] = wRight;
			}
			else if(cBottom > wTop && cTop < wTop &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
				charC[1] = wTop - charSize;
			}else if(cTop < wBottom && cBottom > wBottom &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
				charC[1] = wBottom;
			}

			//being hit by us
			for(int i = 0; i < 10; i++) //projectiles
				if(projOut[i]) {
					cLeft = projC[i][0];
					cRight = cLeft + 30;
					cTop = projC[i][1];
					cBottom = cTop + 30;

					if(collide(wLeft, wTop, cLeft, cTop, enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], 30, 30)) {
						projOut[i] = false;
						enemyHP[roomNum][enemyNum]-= str;

						if(onOff[1]) {
							hit.setFramePosition (0);
							hit.start();
						}
					}

				}

			if(enemyAtkOut[roomNum][enemyNum]) {
				double hyp, opp, adj;
				adj = charC[0] - enemyAtkX[roomNum][enemyNum];

				if(adj<0)
					adj *= -1;
				opp = charC[1] - enemyAtkY[roomNum][enemyNum];
				if(opp<0)
					opp*= -1;

				hyp = Math.sqrt((Math.pow(adj, 2) + Math.pow(opp, 2)));

				double atkSpeed = 0.3/hyp;

				if(charC[0] < enemyAtkX[roomNum][enemyNum])
					enemyAtkX2[roomNum][enemyNum] -= adj * atkSpeed;
				else if(charC[0] > enemyAtkX[roomNum][enemyNum])
					enemyAtkX2[roomNum][enemyNum] += adj * atkSpeed;

				if(charC[1] < enemyAtkY[roomNum][enemyNum])
					enemyAtkY2[roomNum][enemyNum] -= opp * atkSpeed;
				else if(charC[1] > enemyAtkY[roomNum][enemyNum])
					enemyAtkY2[roomNum][enemyNum] += opp * atkSpeed;
				enemyAtkX[roomNum][enemyNum] = (int)enemyAtkX2[roomNum][enemyNum];
				enemyAtkY[roomNum][enemyNum] = (int)enemyAtkY2[roomNum][enemyNum];

				wLeft = enemyAtkX[roomNum][enemyNum];
				wRight = wLeft + 20;
				wTop = enemyAtkY[roomNum][enemyNum];
				wBottom = wTop + 20;

				if(collide(wLeft, wTop, cLeft, cTop, 20, 20, charSize, charSize)) {
					dmg = dmg1;
					dmgTaken = true;
					enemyCounter[roomNum][enemyNum] = 0;
					enemyAtkOut[roomNum][enemyNum] = false;

				}
				g.setColor(Color.white);
				g.fillOval(enemyAtkX[roomNum][enemyNum], enemyAtkY[roomNum][enemyNum], 20, 20);
			}

			//health bar
			g.setColor(Color.black);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 34, enemyC[roomNum][enemyNum][1] - 24, 68, 18);

			g.setColor(Color.red);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, 60, 10);

			g.setColor(Color.green);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, (int)(60 * (1.0 * enemyHP[roomNum][enemyNum]/enemyMaxHP[roomNum][enemyNum])), 10);
			if(enemyCounter[roomNum][enemyNum] > 1200000 && !enemyAtkOut[roomNum][enemyNum]) {
				enemyAtkX[roomNum][enemyNum] = enemyC[roomNum][enemyNum][0];
				enemyAtkY[roomNum][enemyNum] = enemyC[roomNum][enemyNum][1];
				enemyAtkX2[roomNum][enemyNum] = enemyC[roomNum][enemyNum][0];
				enemyAtkY2[roomNum][enemyNum] = enemyC[roomNum][enemyNum][1];
				enemyAtkOut[roomNum][enemyNum] = true;
				pop.setFramePosition(0);
				pop.start();

			} else if(!enemyAtkOut[roomNum][enemyNum] && enemyCounter[roomNum][enemyNum] > 800000) {
				g.drawImage(exclam, enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] - 20, enemyC[roomNum][enemyNum][1] - 40, 20, 40, null);
				if(direct == 0) {
					enemyImg = shroom[1];
				} else
					enemyImg = shroom[3];
			} 

			g.drawImage(enemyImg, enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], null);
			//System.out.println(enemyAtkOut[roomNum][enemyNum]);
		} 
	}

	//Description: draws enemy and enemy attacks
	//Parameters: graphics, direction it's going in (up/down, left/right), min & max = area of movement, 
	//speed = speed of enemy movement (not attacks), image of enemy
	//Return: no return
	public static void dummy(Graphics g, int enemyNum, Image enemyImg) {
		//enemyDirect 1 = posititve, -1 = negative
		//direct 1 = up down, direct 0 = left right
		if(enemyHP[roomNum][enemyNum] >= 0) {
			g.setColor(new Color(0,0,0,150));
			g.fillOval(enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1] - 20, enemySize[roomNum][enemyNum][0], 40);

			int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
			int wLeft = enemyC[roomNum][enemyNum][0], wRight = enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0], wTop = enemyC[roomNum][enemyNum][1], wBottom = enemyC[roomNum][enemyNum][1] + enemySize[roomNum][enemyNum][1];

			if(cRight > wLeft && cLeft < wLeft &&
					cRight - wLeft < cBottom - wTop && 
					cRight - wLeft < wBottom - cTop){//collide with left side of wall
				charC[0] = wLeft - charSize;
			} else if(cLeft < wRight && cRight > wRight &&
					wRight - cLeft < cBottom - wTop && 
					wRight - cLeft < wBottom - cTop){//collide with right side of wall
				charC[0] = wRight;
			}
			else if(cBottom > wTop && cTop < wTop &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
				charC[1] = wTop - charSize;
			}else if(cTop < wBottom && cBottom > wBottom &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
				charC[1] = wBottom;
			}

			//being hit by us
			for(int i = 0; i < 10; i++) //projectiles
				if(projOut[i]) {
					cLeft = projC[i][0];
					cRight = cLeft + 30;
					cTop = projC[i][1];
					cBottom = cTop + 30;

					if(collide(wLeft, wTop, cLeft, cTop, enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], 30, 30)) {
						projOut[i] = false;
						enemyHP[roomNum][enemyNum]-= str;

						if(onOff[1]) {
							hit.setFramePosition (0);
							hit.start();
						}
					}

				}
			//health bar
			g.setColor(Color.black);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 34, enemyC[roomNum][enemyNum][1] - 24, 68, 18);

			g.setColor(Color.red);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, 60, 10);

			g.setColor(Color.green);
			g.fillRect(enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0]/2 - 30, enemyC[roomNum][enemyNum][1] - 20, (int)(60 * (1.0 * enemyHP[roomNum][enemyNum]/enemyMaxHP[roomNum][enemyNum])), 10);

			g.drawImage(enemyImg, enemyC[roomNum][enemyNum][0], enemyC[roomNum][enemyNum][1], enemySize[roomNum][enemyNum][0], enemySize[roomNum][enemyNum][1], null);

			if(enemyAtkType[roomNum][enemyNum] == 1 && (enemyAtkX[roomNum][enemyNum] <= enemyC[roomNum][enemyNum][0] - 500 || enemyAtkX[roomNum][enemyNum] > enemyC[roomNum][enemyNum][0] - 250)) {
				g.drawImage(exclam, enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] - 20, enemyC[roomNum][enemyNum][1] - 40, 20, 40, null);
			} else if (enemyAtkType[roomNum][enemyNum] == 2 && enemyPhase[roomNum][enemyNum] == 2){
				g.drawImage(exclam, enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] - 20, enemyC[roomNum][enemyNum][1] - 40, 20, 40, null);
			} else if(enemyAtkType[roomNum][enemyNum] == 3 && !enemyAtkOut[roomNum][enemyNum] && frame > 5000000) {
				g.drawImage(exclam, enemyC[roomNum][enemyNum][0] + enemySize[roomNum][enemyNum][0] - 20, enemyC[roomNum][enemyNum][1] - 40, 20, 40, null);
			}
		}
	}


	//Description: draws save point
	//Parameters: graphics, x & y = coordinates
	//Return: no return
	public static void savePoint(Graphics g, int x, int y) throws IOException, FileNotFoundException {
		g.drawImage(savePoint, x, y, 70, 90, null);
		interact(g, x, y, 70, 90, "save", "", "");
		if(saveInter) {
			g.setColor(Color.white);
			g.setFont(buttonFont);
			g.drawString("This is a save point", 390, 560);
		}
	}

	//Description: draws dialogue box for cutscenes
	//Parameters: graphics
	//Return: no return
	public static void dialogue(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(40, 460, 1120, 80);
		g.setFont(smallFont);
		g.fillRect(40, 430, 170, 20);
		g.setColor(Color.white);
		g.drawString("Click to continue", 50, 445);
		back(g);
		g.setColor(Color.white);
		g.setFont(buttonFont);
	}

	public static void spikes(Graphics g, int x, int y) {

		int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
		int wLeft = x, wRight = x + 50, wTop = y, wBottom = y + 50;

		if(cRight > wLeft && cLeft < wLeft &&
				cRight - wLeft < cBottom - wTop && 
				cRight - wLeft < wBottom - cTop){//collide with left side of wall
			charC[0] = x - charSize - 5;
			dmgTaken = true;
			dmg = 1;
		} else if(cLeft < wRight && cRight > wRight &&
				wRight - cLeft < cBottom - wTop && 
				wRight - cLeft < wBottom - cTop){//collide with right side of wall
			charC[0] = x + 100 + 5;
			dmgTaken = true;
			dmg = 1;
		}
		else if(cBottom > wTop && cTop < wTop &&
				(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
			charC[1] = y - charSize - 5;
			dmgTaken = true;
			dmg = 1;
		}else if(cTop < wBottom && cBottom > wBottom &&
				(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
			charC[1] = y + 100 + 5;
			dmgTaken = true;
			dmg = 1;
		}

		g.drawImage(spike, x, y, 50, 50, null);
	}

	public static void pickHeart (Graphics g, int x, int y) throws IOException {
		g.drawImage(heartx3, x, y, 80,50,null);
		interact(g, x, y, 80,50,"heart", "", "");
		hearty = true;
	}

	public static void boss(Graphics g) {

		if(bossCurrentHP <= 0) {
			scene = "end";
		}
		Image bossImg = bossSprites[0];
		if(bossPhase == 1) {//walking
			double bossSpeed = 0.7;
			double adj, opp, hyp, mult;

			boolean faceSide = true;

			//calculating the course of the projectile
			adj = bossEndX - bossX2 * 1200;
			if(adj < 0)
				adj *= -1;
			opp = bossEndY - bossY2 * 1200;
			if(opp < 0)
				opp *= -1;

			//calculating speed
			hyp = Math.sqrt(Math.pow(adj, 2) + Math.pow(opp, 2));

			mult = hyp/bossSpeed;
			//mult *=10;

			//figuring out which direction the things are going in
			if(bossEndX != bossStartX)
				if(bossEndX < bossStartX) {
					bossX2 -= adj / mult;
					bossImg = bossSprites[1];
				}
				else {
					bossX2 += adj / mult;
					bossImg = bossSprites[2];
				}
			//projC[0] += (int)Math.sqrt((1 - )
			if(bossEndY != bossStartY) {
				if(opp/mult > adj/mult)
					faceSide = false;
				if(bossEndY < bossStartY) {
					bossY2 -= opp / mult;
					if(!faceSide) {
						bossImg = bossSprites[0];
					}
				}
				else {
					bossY2 += opp / mult;
					if(!faceSide) {
						bossImg = bossSprites[3];
					}
				}
			}

			//put it in integer format
			bossX = (int)bossX2;
			bossY = (int)bossY2;

			if(collide(charC[0], charC[1], bossX, bossY, charSize, charSize, bossSize, bossSize)) {
				bossPhase = 0;
				bossTimer = 0;

			}
		} else if (bossPhase == 0){//doing nothing
			if (bossTimer > 1000000) {
				bossTimer = 0;
				bossPhase = (int)(Math.random() * 2) + 1;//choose between phase 1 and 2
				if(bossPhase == 1) {
					bossStartX = bossX;
					bossStartY = bossY;
					bossEndX = charC[0];
					bossEndY = charC[1];
				} else {
					bossAtk = (int)(Math.random() * NUMOFBOSSATKS) + 1;//figure out which attack to do
				}

			}
		} else if(bossPhase == 2) {//charging
			if(bossTimer < 8000000) {
				if(bossAtk == 1) {//spinner
					bossImg = Toolkit.getDefaultToolkit().getImage("spin0.png");
				} else if(bossAtk == 2) {//spinner
					bossImg = Toolkit.getDefaultToolkit().getImage("summon.png");
				} else if(bossAtk == 3) {//spinner
					bossImg = Toolkit.getDefaultToolkit().getImage("swipe1.png");
				}
			} else {
				if(bossAtk == 3) {
					bossX = charC[0] + 20;
					bossY = charC[1] + 20;
				}
				bossTimer = 0;
				bossPhase = 3;
			}
			
		} else if(bossPhase == 3) {
			if(bossTimer > 20000000) {
				bossPhase = 1;
				bossTimer = 0;
			}else if(bossAtk == 1) {//spin attack
				bossImg = Toolkit.getDefaultToolkit().getImage("spin1.png");
				//moving but slower----------------------------------------
				double hyp, opp, adj;
				adj = charC[0] - bossX;

				if(adj<0)
					adj *= -1;
				opp = charC[1] - bossY;
				if(opp<0)
					opp*= -1;

				hyp = Math.sqrt((Math.pow(adj, 2) + Math.pow(opp, 2)));

				double atkSpeed = 0.5/hyp;

				if(charC[0] < bossX)
					bossX2 -= adj * atkSpeed;
				else if(charC[0] > bossX)
					bossX2 += adj * atkSpeed;

				if(charC[1] < bossY)
					bossY2 -= opp * atkSpeed;
				else if(charC[1] > bossY)
					bossY2 += opp * atkSpeed;

				if(collide(bossX, bossY, charC[0], charC[1], bossSize, bossSize, charSize, charSize)) {
					dmg = 1;
					dmgTaken = true;
					if(!stunned) {
						stunned = true;
						stunlock = 0;
					}
				}
				bossX = (int)bossX2;
				bossY = (int)bossY2;
			} else if(bossAtk == 2) {//summoning
				int rand = (int)(Math.random() * 8);
				enemyHP[5][rand] = enemyMaxHP[5][rand];
				bossPhase = 1;
				bossTimer = 0;
			} else if(bossAtk == 3) {
				bossImg = Toolkit.getDefaultToolkit().getImage("swipe2.png");
				if(collide(bossX, bossY, charC[0], charC[1], bossSize, bossSize, charSize, charSize)) {
					dmg = 5;
					dmgTaken = true;
					stunned = true;
				}
			}
		}

		//health bar
		g.setColor(Color.black);
		g.fillRect(bossX, bossY - 20, bossSize, 20);
		g.setColor(Color.red);
		g.fillRect(bossX + 2, bossY - 22, bossSize - 4, 18);
		g.setColor(Color.green);
		g.fillRect(bossX + 2, bossY - 22, (int)((bossSize - 4) * (1.0 * bossCurrentHP/bossMaxHP)), 18);

		g.drawImage(bossImg, bossX, bossY, bossSize,bossSize, null);
	}

	public void paintComponent(Graphics g){
		hearty = false;
		cutScenePlayed[0] = false;
		playing = false;
		interact = false;

		//--------------------------------------------------------
		super.paintComponent(g);

		if (scene.equals("start")) {
			int[] enemiesPerRoom = {1,2,4,8,5,8};
			String[] rooms = {"Tutorial", "Room 1", "Room 2", "Room 3", "Room 4", "Boss Room"};

			for(int i = 0; i < rooms.length - 1; i++) {//resetting room if we left it
				if(saveScene.equals(rooms[i])) {
					for(int j  = 0; j < enemiesPerRoom[i]; j++) {
						enemyHP[i][j] = enemyMaxHP[i][j];
					}
				}
			}

			g.drawImage(menuArt, 0, 0, null);

			button(g, "   New", 110, 530, 150);

			g.setColor(new Color(50,50,50));
			g.fillRect(270, 530, 210, 60);
			g.setColor(new Color(150,150,150));
			g.setFont(buttonFont);
			g.drawString("  Continue", 270, 530 + 40);

			try {
				inFile = new Scanner(new File("prevGameData.txt"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			if(played) {
				//played = true;
				button(g, "  Continue", 270, 530, 210);
			}

			button(g, " Instructions", 490, 530, 250);
			button(g, "  Settings", 750, 530, 200);
			button(g, "  Exit", 960, 530, 130);
			//reinitialize variables for character movement
			charC[0] = 427 + charSize/2;//x
			charC[1] = 225;//y

			currentHP = maxHP;

			spriteNum = 0;

		}else if (scene.equals(""))
			back(g);

		else if(scene.equals("instru")) {
			g.drawImage(instrus, 0, 0, null);
			back(g);
			button(g, "  Credits", 160, 530, 170);

		}else if (scene.equals("settings")){
			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			back(g);
			switchButton(g, "Music", 30, 50, 0);
			switchButton(g, "Sound Effects", 30, 150, 1);
			switchButton(g, "Infinite HP", 30, 250, 2);
			switchButton(g, "Skip Dialogue", 30, 350, 3);

		}else if(scene.equals("credits")) {
			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			g.setFont(buttonFont);
			g.setColor(Color.white);
			g.drawString("Art & Design - Wendy, Zaachary Mann", 50, 50);
			g.drawString("Coding - Wendy", 50, 120);
			g.drawString("Ghost Writing - Sophia Chen, Zaachary Mann", 50, 190);
			g.drawString("Sound Effects / Music - Listed in README", 50, 260);
			back(g);
		} else if(scene.equals("pause"))
			pauseScreen(g);
		else if(scene.equals("death")) {
			g.setColor(new Color(31, 0, 0));
			g.fillRect(0,0,1200,600);
			g.setColor(new Color(255, 196, 196));
			g.setFont(bigFont);
			g.drawString("You Died!", 370, 300);
			button(g, "         Main Menu", 360, 330, 480);

		}
		//ACTUAL GAMEPLAY TODO------------------------------------------
		else if(scene.equals("Tutorial")) {
			roomNum = 0;
			g.drawImage(bg1, 75, 20, null);
			lastRoom = scene;
			playing = true;

			for(int i = 0; i < 10; i++)
				drawShoot(g, i);

			wall(g, 75, 0, 25, 600);//left
			wall(g, WIDTH - 25 - 270, 0, 25, 600);//right
			wall(g, 0, 20, 550, 25);//top
			wall(g, 650, 20, 550, 25);//top
			wall(g, 550, -5, 100, 25);//top
			wall(g, 0, HEIGHT - 25 - 80, 1200, 25);//bottom

			wall(g, 375, 175, 25, 200);//random wall


			g.drawImage(Toolkit.getDefaultToolkit().getImage("crack1.png"), 0, 200, 100,100,null);
			gameBorder(g);
			
			try {
				interact (g, 0, 200, 125, 125, "door", "right", "Boss Room");
			} catch (IOException e2) {
				e2.printStackTrace();
			}


			try {
				savePoint(g, 100, 400);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if(enemyHP[0][0] > 0) 
				dummy(g, 0, dummy);
			else {
				if(!heartsCollected[0]) {
					try {
						pickHeart(g,550,150);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(!roomPassed[0]) {
					if(onOff[1]) {
						doorOpeningSound.setFramePosition (0);
						doorOpeningSound.start();
					}
				}
				roomPassed[0] = true;
				try {
					interact (g, 730, 250, 50, 50, "respawn", "", "");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}




			if(!roomPassed[0]) {
				wall(g, 550, 20, 100, 25);//top
				g.drawImage(door, 550, 20, null);

			} else if (roomPassed[0])
				try {
					interact(g, 550,0,100,50, "door", "up", "Room 1");
				} catch (IOException e) {
					e.printStackTrace();
				}
			chara(g);

		}else if(scene.equals("Room 1")) {//------------------------------
			g.drawImage(bg2, 75, 20, null);

			int spikesX = 450;

			//draw spikes
			for(int i = 0; i < 7; i++) {//big middle bit
				for(int j = 0; j < 5; j++) {
					spikes(g,spikesX + 50 * j,45 + 45 * i);
				}
			}

			for(int i = 0; i < 5; i++) {//bottom left
				for(int j = 0; j < 3; j++)
					spikes(g,75 + 50 * i, 475 - 45*j);
			}

			for(int i = 0; i < 6; i++) {//top left
				for(int j = 0; j < 3; j++)
					spikes(g,75 + 50 * i, 45 + 45*j);
			}

			for(int i = 0; i < 3; i++) {//bit that's sticking out on left
				for(int j = 0; j < 2; j++)
					spikes(g,225 + 50 * i, 180 + 45*j);
			}

			roomNum = 1;
			heartsCollected[roomNum - 1]=true;

			for(int i = 0; i < 10; i++)
				drawShoot(g, i);
			lastRoom = scene;
			playing = true;
			wall(g, 75, 0, 25, 200);//left

			if(!roomPassed[1] && enemyHP[1][0] <= 0 && enemyHP[1][1] <= 0) {
				roomPassed[1] = true;
				doorOpeningSound.setFramePosition(0);
				doorOpeningSound.start();

			}

			if(!roomPassed[1])
				wall(g, 75, 200, 25, 100);//left
			else {
				enemyHP[1][0] = 0;
				enemyHP[1][1] = 0;
				try {
					interact(g,50,200,100,100,"door", "right","Room 2");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!heartsCollected[roomNum]) {
					try {
						pickHeart(g,150,200);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			slime(g, 1, 75, 400, 0, 0.4, 1);
			slime(g, 2, 370, 450, 1, 0.2, 1);

			wall(g, 50, 200, 25, 100);//left
			wall(g, 75, 300, 25, 600);//left

			wall(g, WIDTH - 25 - 270, 0, 25, 600);//right
			wall(g, 0, HEIGHT - 25 - 80, 550, 25);//bottom
			wall(g, 650, HEIGHT - 25 - 80, 550, 25);//bottom
			wall(g, 550, HEIGHT - 80, 100, 25);//bottom
			wall(g, 0, 20, 1200, 25);//top
			
			roomPassed[0] = true;

			gameBorder(g);

			try {
				interact(g, 550,HEIGHT-50-80,100,50, "door", "down", "Tutorial");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				savePoint(g, 450, 400);
			} catch (IOException e) {
				e.printStackTrace();
			}

			g.drawImage(Toolkit.getDefaultToolkit().getImage("crack3.png"), WIDTH-270-25, 250, 100,100,null);

			try {
				if(!onOff[3] || !cutScenePlayed[0])
					interact(g,WIDTH-270-50,250,100,100,"door", "","Secret Scene 1");
				else
					interact(g,WIDTH-270-50,250,100,100,"door", "left","Secret Room 1");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			chara(g);

		}else if(scene.equals("Room 2")) {//-------------------------------------------------------------
			roomPassed[1] = true;
			playing = true;
			roomNum = 2;
			heartsCollected[roomNum - 1]=true;
			g.drawImage(bg2point5, 75, 20, null);
			for(int i = 0; i < 10; i++)
				drawShoot(g, i);


			wall(g, WIDTH - 25 - 270, 0, 25, 200);//left
			wall(g, WIDTH - 270, 200, 25, 100);//left
			wall(g, WIDTH - 25 - 270, 300, 25, 100);//left
			wall(g, WIDTH - 270, 400, 25, 100);//left
			wall(g, WIDTH - 25 - 270, 500, 25, 600);//left
			wall(g, WIDTH - 270 - 600, 300, 700, 75);//random part sticking out

			wall(g, 75, 0, 25, 600);//right

			//middle wall
			wall(g,WIDTH - 270 - 600,200,450,175);

			wall(g, 0, HEIGHT - 25 - 80, 1200, 25);//bottom
			wall(g, 0, 20, 1200, 25);//top

			g.drawImage(Toolkit.getDefaultToolkit().getImage("crack1.png"), 0,250,100,100,null);
			try {
				if(!onOff[3] || !cutScenePlayed[2])
					interact(g,50,250,100,100,"door", "right","Secret Scene 2");
				else
					interact(g,50,250,100,100,"door", "right","Secret Room 2");

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				interact(g,WIDTH - 50 - 270,200,100,100,"door", "left","Room 1");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(roomPassed[2]) {
				if(!heartsCollected[roomNum]) {
					try {
						pickHeart(g,WIDTH - 200 - 270,400);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				enemyHP[2][0] = 0;
				enemyHP[2][1] = 0;
				enemyHP[2][2] = 0;
				enemyHP[2][3] = 0;
				try {
					interact(g,WIDTH - 50 - 270,400,100,100,"door", "left","Room 3");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				wall(g, WIDTH - 25 - 270, 400,100,100);
				if(enemyHP[roomNum][0] <= 0 && enemyHP[roomNum][1] <= 0) {
					roomPassed[2] = true;
					if(onOff[1]) {
						doorOpeningSound.setFramePosition (0);
						doorOpeningSound.start();
					}
				}
			}
			shroom(g,1,0,1);
			shock(g,1,1);
			shroom(g,0,2,1);
			shock(g,3,1);

			gameBorder(g);
			try {
				savePoint(g,WIDTH - 370 , 100);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			chara(g);
		} else if(scene.equals("Room 3")) {//--------------------------------------------------

			playing = true;
			roomNum = 3;
			heartsCollected[roomNum - 1]=true;

			roomPassed[2] = true;

			g.drawImage(bg2, 75, 20, null);

			for(int i = 0; i < 4; i++) {//right side i = x, j = y
				for(int j = 0; j < 6; j++)
					spikes(g,705 + 50 * i, 45 + 45*(j + 4));
			}

			slime(g,2,100,600,0,0.4,2);
			shock(g,1,1);
			shock(g,2,1);
			shock(g,7,2);

			for(int i = 0; i < 10; i++)
				drawShoot(g, i);


			wall(g, 75, 0, 25, 200);//left top
			wall(g, 50, 100, 25, 600);//left opening			

			wall(g, 75, 300, 25, 100);//left middle bit

			wall(g,100,300,500,100);//bit sticking out

			wall(g, WIDTH - 25 - 270, 0, 25, 600);//right

			wall(g, 0, HEIGHT - 25 - 80, 1200, 25);//bottom
			wall(g, 0, 20, 1200, 25);//top

			shroom(g,1,3,1);
			shroom(g,1,4,1);
			shroom(g,1,5,1);
			shroom(g,1,6,1);

			if(!roomPassed[3] && enemyHP[roomNum][0] <= 0 && enemyHP[roomNum][1] <= 0 && enemyHP[roomNum][2] <= 0 && enemyHP[roomNum][3] <= 0 &&
					enemyHP[roomNum][4] <= 0 && enemyHP[roomNum][5] <= 0 && enemyHP[roomNum][6] <= 0 && enemyHP[roomNum][7] <= 0) {
				roomPassed[3] = true;
				if(onOff[1]) {
					doorOpeningSound.setFramePosition (0);
					doorOpeningSound.start();
				}
			}

			if(roomPassed[3]) {
				try {
					interact(g,50,200,100,100,"door", "right","Room 4");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(!heartsCollected[roomNum]) {
					System.out.println("asdas");
					try {
						pickHeart(g,150,150);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				for(int i = 0; i < 8; i++)
					enemyHP[roomNum][i] = 0;
			} else
				wall(g, 75, 0,25,600);

			try {
				interact(g,100,400,100,100,"door", "right","Room 2");
			} catch (IOException e) {
				e.printStackTrace();
			}

			gameBorder(g);
			try {
				savePoint(g,200,400);
			} catch (IOException e) {
				e.printStackTrace();
			}
			chara(g);
		} else if (scene.equals("Room 4")) {
			g.drawImage(bg2, 75, 20, null);
			playing = true;
			roomNum = 4;
			roomPassed[3] = true;
			heartsCollected[roomNum - 1]=true;

			//spikes
			for(int i = 0; i< 19; i++) {//i = x, j = y
				for(int j = 0; j < 5; j++) {
					spikes(g,50 * i,45 * j - 30);
				}
			}

			//spikes
			for(int i = 0; i< 19; i++) {//i = x, j = y
				for(int j = 0; j < 4; j++) {
					spikes(g,50 * i,45 * j + 305);
				}
			}

			slime(g,3,0,0,0,0,1);
			shroom(g,0,1,1);
			shock(g,2,1);
			shroom(g,0,3,1);
			shock(g,4,1);

			for(int i = 0; i < 10; i++)
				drawShoot(g, i);

			wall(g, 75, 0, 25, 200);//left top
			wall(g, 50, 100, 25, 600);//left opening			
			wall(g, 75, 300, 25, 500);//left middle bit

			wall(g, WIDTH - 25 - 270, 0, 25, 200);//left top
			wall(g, WIDTH - 270, 100, 25, 600);//left opening			
			wall(g, WIDTH - 25 - 270, 300, 25, 500);//left middle bit

			wall(g, 0, HEIGHT - 25 - 80, 1200, 25);//bottom
			wall(g, 0, 20, 1200, 25);//top

			if(!roomPassed[4] && enemyHP[4][0] <= 0 && enemyHP[4][1] <= 0 && enemyHP[4][2] <= 0 &&
					enemyHP[4][3] == 0 && enemyHP[4][4] == 0) {
				roomPassed[4] = true;
				dungeon.start();
			}

			if(roomPassed[4]) {
				if(!heartsCollected[roomNum]) {
					try {
						pickHeart(g,550,150);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			gameBorder(g);
			try {
				savePoint(g, WIDTH - 200 - 270, 210);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				interact(g,WIDTH - 270 - 25,200,100,100,"door", "left","Room 3");
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if(cutScenePlayed[1])
					interact(g,50,200,100,100,"door", "right","Boss Room");
				else
					interact(g,50,200,100,100,"door", "right","Almost at Boss");
			} catch (IOException e) {
				e.printStackTrace();
			}

			chara(g);
		} else if(scene.equals("Boss Room")) {
			g.drawImage(bg3, 75, 20, null);
			playing = true;
			roomNum = 5;
			slime(g, 1, 75, 400, 0, 0.4, 1);
			slime(g, 2, 370, 450, 1, 0.2, 1);
			shroom(g,1,2,1);
			shock(g,3,1);
			slime(g,2,100,600,4,0.4,2);
			shock(g,5,1);
			slime(g,3,0,0,6,0,1);
			shroom(g,0,7,1);
			
			for(int i = 0; i < 10; i++)
				drawShoot(g, i);
			wall(g, 75, 0, 25, 200);//left top
			wall(g, 75, 300, 25, 500);//left middle bit

			wall(g, WIDTH - 25 - 270, 0, 25, 200);//right top
			wall(g, WIDTH - 270, 100, 25, 600);//right opening			
			wall(g, WIDTH - 25 - 270, 300, 25, 500);//right middle bit

			wall(g, 0, HEIGHT - 25 - 80, 1200, 25);//bottom
			wall(g, 0, 20, 1200, 25);//top

			gameBorder(g);

			boss(g);
			chara(g);
		}
		//------------------------------------------
		//cutscenes
		else if(scene.equals("cutScene1")) {//intro
			Image bg = cutScene1[0];
			if(sceneCount1 == 0) {
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("On Earth,", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			} else if(sceneCount1 == 1) {
				bg = cutScene1[1];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("On Earth, portals started randomly opening up ", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			}else if(sceneCount1 == 2) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				dialogue(g);
				g.drawString("From those portals came monsters.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			}else if(sceneCount1 == 3) {
				bg = cutScene1[2];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("Vicious creatures with deceptively cute designs.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			}else if(sceneCount1 == 4) {
				bg = cutScene1[3];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("They held magic that we've never seen before.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			} else if(sceneCount1 == 5) {
				bg = cutScene1[4];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("Overnight, people suddenly gained unimaginable powers.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			}else if(sceneCount1 == 6) {
				bg = cutScene1[5];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("We named the portals 'The Void'.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			}else if(sceneCount1 == 7) {
				bg = cutScene1[6];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("My sister suddenly went missing a few months after.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			} else if(sceneCount1 == 8) {
				bg = cutScene1[6];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("She's always been interested in The Void.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			} else if(sceneCount1 == 9) {
				bg = cutScene1[7];
				g.drawImage(bg, 0,0,null);
				dialogue(g);
				g.drawString("I'm going to get her back.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			} else if(sceneCount1 == 10) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("This is the story.", 80, 510);
				button(g, "   Main Menu", 160, 530, 250);
			} else if(sceneCount1 == 11) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				g.setColor(Color.white);
				g.setFont(bigFont);
				g.drawString("Tutorial", 200, 330);
			}

		}
		else if(scene.equals("Secret Scene 1")) {//pebble
			if(secretSceneCount1 == 0) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("...", 80, 510);
			} else if(secretSceneCount1 == 1) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene1.png"), 0,0,null);
				dialogue(g);
				g.drawString("!", 80, 510);
			} else if(secretSceneCount1 == 2) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene1.png"), 0,0,null);
				dialogue(g);
				g.drawString("[unknown] Hello! I'm surprised you managed to find me,", 80, 510);
			} else if(secretSceneCount1 == 3) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene2.png"), 0,0,null);
				dialogue(g);
				g.drawString("[unknown] I've just been sitting in the dark for...", 80, 510);
			} else if(secretSceneCount1 == 4) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene2.png"), 0,0,null);
				dialogue(g);
				g.drawString("[unknown] Well I don't know!", 80, 510);
			} else if(secretSceneCount1 == 5) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene2.png"), 0,0,null);
				dialogue(g);
				g.drawString("[unknown] My mom told me stay here,", 80, 510);
			} else if(secretSceneCount1 == 6) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene3.png"), 0,0,null);
				dialogue(g);
				g.drawString("[unknown] something about the outside world and danger...", 80, 510);
			} else if(secretSceneCount1 == 7) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene4.png"), 0,0,null);
				dialogue(g);
				g.drawString("[unknown] OH silly me, taking up all your time", 80, 510);
			} else if(secretSceneCount1 == 8) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene4.png"), 0,0,null);
				dialogue(g);
				g.drawString("[Pebble] I'm pebble by the way (sorry, not used to talking)", 80, 510);
			} else if(secretSceneCount1 == 9) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene2.png"), 0,0,null);
				dialogue(g);
				g.drawString("[Pebble] Well regardless thanks for stopping by!", 80, 510);
			} else if(secretSceneCount1 == 10) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("secretScene2.png"), 0,0,null);
				dialogue(g);
				g.drawString("[Pebble] If you ever need my help you can count on me!", 80, 510);
				cutScenePlayed[0] = true;
			} else if(secretSceneCount1 == 11) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				g.setColor(Color.white);
				g.setFont(bigFont);
				g.drawString("You gained Pebble's", 125, 200);
				g.drawString("Contact info!", 125, 300);
				g.setFont(buttonFont);
				g.drawString("A one time use item, calls in pebble and her homemade", 80, 390);
				g.drawString("bombs to destroy all the enemies in a room!", 80, 450);
				g.drawString("(Not applicable to the boss)", 80, 510);
				cutScenePlayed[0] = true;
			}
		}else if(scene.equals("Almost at Boss")) {
			if(sceneCount2 == 0) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("", 80, 510);
			} else if(sceneCount2 == 1) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("...", 80, 510);
			} else if(sceneCount2 == 2) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("Your sister is waiting.", 80, 510);
			} else if(sceneCount2 == 3) {
				//draw background
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg.png"), 0,0,null);
				dialogue(g);
				g.drawString("You look around the room. It’s dark", 80, 510);
			} else if(sceneCount2 == 4) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg.png"), 0,0,null);
				dialogue(g);
				g.drawString("Small patches of luminescent growth cover the walls.", 80, 510);
			} else if(sceneCount2 == 5) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg.png"), 0,0,null);
				dialogue(g);
				g.drawString("A dimly glowing portal hums at the end of the room.", 80, 510);
			} else if(sceneCount2 == 6) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg.png"), 0,0,null);
				dialogue(g);
				g.drawString("Your sister is nowhere to be seen, and neither is the voice.", 80, 510);
			} else if(sceneCount2 == 7) {
				//same bg as before but zoomed in to make it look like we're walking
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("", 80, 510);
			} else if(sceneCount2 == 8) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("You feel a tremble in the ground", 80, 510);
			} else if(sceneCount2 == 9) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("It cannot be stopped, little girl. ", 80, 510);
			} else if(sceneCount2 == 10) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("What makes you think your powers will stop me?", 80, 510);
			} else if(sceneCount2 == 11) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("...", 80, 510);
			} else if(sceneCount2 == 12) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("A quiet one, I see. What’s the matter with you? ", 80, 510);
			} else if(sceneCount2 == 13) {
				//same bg as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("Coming here,", 80, 510);
			} else if(sceneCount2 == 14) {
				//same bg as before but darker
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				g.setColor(new Color(0,0,0,20));
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("down", 80, 510);
			} else if(sceneCount2 == 15) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				g.setColor(new Color(0,0,0,40));
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("a", 80, 510);
			} else if(sceneCount2 == 16) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				g.setColor(new Color(0,0,0,60));
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("dark", 80, 510);
			} else if(sceneCount2 == 17) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				g.setColor(new Color(0,0,0,80));
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("dangerous", 80, 510);
			} else if(sceneCount2 == 18) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg2.png"), 0,0,null);
				g.setColor(new Color(0,0,0,100));
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("cave.", 80, 510);
			} else if(sceneCount2 == 19) {
				g.setColor(Color.black);
				g.fillRect(0,0,1200,600);
				dialogue(g);
				g.drawString("Wouldn't your parents get worried?", 80, 510);
			} else if(sceneCount2 == 20) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				dialogue(g);
				g.drawString("My parents are-", 80, 510);
			} else if(sceneCount2 == 21) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				dialogue(g);
				g.drawString("Dead, yes yes I know,", 80, 510);
			} else if(sceneCount2 == 22) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				dialogue(g);
				g.drawString("I just wanted to remind you.", 80, 510);
			} else if(sceneCount2 == 23) {
				//colored linearte of sister looking scared
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg3.png"), 0,0,null);
				dialogue(g);
				g.drawString("And your sister...", 80, 510);
			} else if(sceneCount2 == 24) {
				//bg, make it look like we opened our eyes + yoru
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg4.png"), 0,0,null);
				dialogue(g);
				g.drawString("I have her", 80, 510);
			} else if(sceneCount2 == 25) {
				//same as before, but make it look like we're fully awake
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg4.png"), 0,0,null);
				dialogue(g);
				g.drawString("But you know this already don't you?", 80, 510);
			} else if(sceneCount2 == 26) {
				//same as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg4.png"), 0,0,null);
				dialogue(g);
				g.drawString("What the hell is all of this? Monsters, portals, kind of magic?", 80, 510);
			} else if(sceneCount2 == 27) {
				//same as before
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg4.png"), 0,0,null);
				dialogue(g);
				g.drawString("The walls are f*cking corrupting!", 80, 510);
			} else if(sceneCount2 == 28) {
				//same as before but yoru closes his eyes
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg4.png"), 0,0,null);
				dialogue(g);
				g.drawString("I see somebody is perceptive.", 80, 510);
			} else if(sceneCount2 == 29) {
				//close up of yoru, fancy space stuff in bg
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg4.png"), 0,0,null);
				dialogue(g);
				g.drawString("It is a rift to an alternate dimension,", 80, 510);
			} else if(sceneCount2 == 30) {
				//close up of yoru, fancy space stuff in bg
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg5.png"), 0,0,null);
				dialogue(g);
				g.drawString("but you would not understand what that truly entails.", 80, 510);
			} else if(sceneCount2 == 31) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg5.png"), 0,0,null);
				dialogue(g);
				g.drawString("I will give you two options, either you leave now,", 80, 510);
			} else if(sceneCount2 == 32) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				//yoru open eyes
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg5.png"), 0,0,null);
				dialogue(g);
				g.drawString("and return back safely, I promise I will not hurt you,", 80, 510);
			} else if(sceneCount2 == 33) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg5.png"), 0,0,null);
				dialogue(g);
				g.drawString("or you futilely try to save your sister and die trying.", 80, 510);
			} else if(sceneCount2 == 34) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg5.png"), 0,0,null);
				dialogue(g);
				g.drawString("or you futilely try to save your sister and die trying.", 80, 510);
			} else if(sceneCount2 == 35) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg6.png"), 0,0,null);
				dialogue(g);
				g.setColor(Color.red);
				g.drawString("You think i’ll leave my sister here alone with a psycho", 80, 510);
			} else if(sceneCount2 == 36) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg6.png"), 0,0,null);
				dialogue(g);
				g.setColor(Color.red);
				g.drawString("I’ll kill you and bring her back home.", 80, 510);
			} else if(sceneCount2 == 37) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg6.png"), 0,0,null);
				dialogue(g);
				g.setColor(Color.red);
				g.drawString("I’ll kill you and bring her back home.", 80, 510);
			} else if(sceneCount2 == 38) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg7.png"), 0,0,null);
				dialogue(g);
				g.drawString("Ohh yes, that’s good", 80, 510);
			} else if(sceneCount2 == 39) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg7.png"), 0,0,null);
				dialogue(g);
				g.drawString("Why yes, I quite like that.", 80, 510);
			} else if(sceneCount2 == 40) {
				//same close up, but one side shows happiness and bliss, the other shows depressing and sadness
				g.drawImage(Toolkit.getDefaultToolkit().getImage("portalbg7.png"), 0,0,null);
				dialogue(g);
				g.drawString("Then come on, show me a good time!", 80, 510);
				cutScenePlayed[1] = true;
			}
		} else if(scene.equals("Secret Scene 2")) {
			if(secretSceneCount2 == 0) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				//				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg.png"), 0,0,null);

				dialogue(g);
				g.drawString("Such a beautiful flower garden... and yet", 80, 510);
			} else if(secretSceneCount2 == 1) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg.png"), 0,0,null);

				dialogue(g);
				g.drawString("What happened here?", 80, 510);
			} else if(secretSceneCount2 == 2) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg.png"), 0,0,null);

				dialogue(g);
				g.drawString("And sister...", 80, 510);
			} else if(secretSceneCount2 == 3) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("you never left this bag behind. You adored it!", 80, 510);
			} else if(secretSceneCount2 == 4) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("Sometimes I even thought you loved it more than me...", 80, 510);
			} else if(secretSceneCount2 == 5) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("But it makes sense doesn't it? After all,", 80, 510);
			} else if(secretSceneCount2 == 6) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("This is one of the last things we have of them...", 80, 510);
			} else if(secretSceneCount2 == 7) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blossombg2.png"), 0,0,null);
				dialogue(g);
				g.drawString("This is one of the last things we have of them...", 80, 510);
			} else if(secretSceneCount2 == 8) {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1200, 600);
				g.setColor(Color.white);
				g.setFont(bigFont);
				g.drawString("You gained Aiko's", 125, 200);
				g.drawString("Bag!", 125, 300);
				g.setFont(buttonFont);
				g.setColor(new Color(255, 212, 212));
				g.drawString("One of the last things mom made for us before she...", 80, 390);
				g.drawString("Well, no point in dwelling on the past.", 80, 450);
				g.setColor(Color.white);
				g.drawString("If equipped, speed +2", 80, 510);
				cutScenePlayed[2] = true;
			}
		}
		//secret rooms
		else if(scene.equals("Secret Room 1")) {
			pebbleContact = true;
			g.drawImage(Toolkit.getDefaultToolkit().getImage("racgrass_shop_thumb.png"), 75, 75, 750,750,null);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("pebble.png"), 575,275, 75,75, null);
			
			int cLeft = charC[0], cRight = charC[0] + charSize, cTop = charC[1], cBottom = charC[1] + charSize;
			int wLeft = 575, wRight = 575 +75, wTop = 275, wBottom = 275 + 75;

			if(cRight > wLeft && cLeft < wLeft &&
					cRight - wLeft < cBottom - wTop && 
					cRight - wLeft < wBottom - cTop){//collide with left side of wall
				charC[0] = 575 - charSize;
			} else if(cLeft < wRight && cRight > wRight &&
					wRight - cLeft < cBottom - wTop && 
					wRight - cLeft < wBottom - cTop){//collide with right side of wall
				charC[0] = 575 + 75;
			}
			else if(cBottom > wTop && cTop < wTop &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from top side of the wall
				charC[1] = 275 - charSize;
			}else if(cTop < wBottom && cBottom > wBottom &&
					(cRight > wLeft && cLeft < wRight)){//rect collides from bottom side of the wall
				charC[1] = 275 + 75;
			}
			
			playing = true;
			
			for(int i = 0; i < 10; i++)
				drawShoot(g, i);

			wall(g,WIDTH-400,0,200,600);//left
			wall(g,75,0,300,250);//right
			wall(g,65,250,10,100);//right
			wall(g,75,350,300,200);//right

			wall(g,0,0,1200,100);//top
			wall(g,0,400,1200,150);//bottom
			
			try {
				interact(g,75,250,100,100,"door", "right","Room 1");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gameBorder(g);

			chara(g);
		} else if (scene.equals("Secret Room 2")){
			aikoBag = true;
			
			Image ground = Toolkit.getDefaultToolkit().getImage("Screenshot 2022-06-21 163137.png");
			
			for(int i = 0; i < 11; i++) {//i = x, j = y
				for(int j = 0; j < 10; j++) {
					g.drawImage(ground, 90 * i, 60 * j, 90, 60, null);
				}
			}
			
			playing = true;
			
			for(int i = 0; i < 10; i++)
				drawShoot(g, i);

			wall(g,75,0,200,600);//left
			
			wall(g,WIDTH-400,0,300,250);//right
			wall(g,WIDTH - 270,250,10,100);//right
			wall(g,WIDTH-400,350,300,200);//right

			wall(g,0,0,1200,100);//top
			wall(g,0,400,1200,150);//bottom
			//end up on right side [   *]
			//					interact(g,0,250,100,100,"door", "left","Secret Scene 2");

			try {
				interact(g,WIDTH-270-25,250,100,100,"door", "left","Room 2");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gameBorder(g);

			chara(g);
		} else if(scene.equals("end")) {
			g.drawImage(Toolkit.getDefaultToolkit().getImage("end.png"), 0,0, null);
			back(g);
		}
		//------------------------------------------
		//MUSIC------------------------------------------------
		if((onOff[0] && (scene.equals("start") || scene.equals("settings") || scene.equals("instru") || scene.equals("credits")))) {
			introMusic.start();
			introMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}else {
			introMusic.stop();
			introMusic.setFramePosition (0);
		}

		if(onOff[0] && playing) {
			played = true;
			bgm.start();
			bgm.loop(Clip.LOOP_CONTINUOUSLY);
		}else {
			bgm.stop();
			bgm.setFramePosition (0);
		}

		if(onOff[0] && scene.equals("death")) {
			deathMusic.start();
			deathMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}else {
			deathMusic.stop();
			deathMusic.setFramePosition (0);
		}
		repaint();

	}


	//MOUSE-------------------------------------------	
	@Override
	public void mouseClicked(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		//clicked = true;
		/*
		 * if(mouseX >= xPos && mouseX <= xPos + w && mouseY >= yPos && mouseY <= mouseY + 60) {
		 */
		if(scene.equals("start")) {
			if(mouseX >= 110 && mouseX <= 110 + 150 && mouseY >= 530 && mouseY <= 530 + 60) {//start
				sceneCount1 = 0;
				if(!onOff[3])
					scene = "cutScene1";
				else
					scene = "Tutorial";
				roomPassed[0] = false;
			} else if(played && mouseX >= 270 && mouseX <= 270 + 210 && mouseY >= 530 && mouseY <= 530 + 60) {//continue

				scene = saveScene;
				currentHP = saveHP;
				charC[0] = saveX;
				charC[1] = saveY;

			} else if(mouseX >= 490 && mouseX <= 490 + 210 && mouseY >= 530 && mouseY <= 530 + 60) {//instruction
				scene = "instru";
			} else if(mouseX >= 750 && mouseX <= 750 + 210 && mouseY >= 530 && mouseY <= 530 + 60) {//setttings
				scene = "settings";
			} else if(mouseX >= 960 && mouseX <= 960 + 210 && mouseY >= 530 && mouseY <= 530 + 60) {//exit
				try {
					outFile = new PrintWriter(new File("prevGameData.txt"));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				if(played) {
					outFile.println(1);
					outFile.println(saveHP);
					outFile.println(saveX);
					outFile.println(saveY);
					outFile.println(saveScene);
				}
				else
					outFile.println(0);

				outFile.close();
				System.exit(0);
			}
		} else if(scene.equals("Tutorial") || scene.equals("credits") || scene.equals("Room 1") || scene.equals("end")) {
			if(mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				scene = "start";
			}
		} else if(scene.equals("instru")) {
			if(mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				scene = "start";
			} else if(mouseX >= 160 && mouseX <= 160 + 170 && mouseY >= 530 && mouseY <= 530 + 60) {//credits
				scene = "credits";
			}
		} else if(scene.equals("settings")) {
			//	g.fillRect(545, 165, 265, 30);
			if(mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				scene = "start";
			} 
			//SWITCHES
			for(int i = 0; i < 4; i++) 
				if(mouseX >= 30 + 15 + onOffDim[i][0] && mouseX <= 30 + 150 + onOffDim[i][0] && mouseY >= onOffDim[i][1] - 5 && mouseY <= onOffDim[i][1] + 55) 
					if(onOff[i] == true)
						onOff[i] = false;
					else
						onOff[i] = true;				
		} else if(scene.equals("pause")) {

			if(mouseX >= 450 && mouseX <= 750 && mouseY >= 300 && mouseY <= 360) { //back to game
				scene = lastRoom;
			} else if(mouseX >= 450 && mouseX <= 750 && mouseY >= 370 && mouseY <= 430) { //main menu
				scene = "start";
			} else if(mouseX >= 450 && mouseX <= 750 && mouseY >= 440 && mouseY <= 500) {//exit
				try {
					outFile = new PrintWriter(new File("prevGameData.txt"));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				if(played) {
					outFile.println(1);
					outFile.println(saveHP);
					outFile.println(saveX);
					outFile.println(saveY);
					outFile.println(saveScene);
				}
				else
					outFile.println(0);
				outFile.close();
				System.exit(0);
			}


		} else if(scene.equals("death")) {
			//			button(g, "         Main Menu", 360, 330, 480);
			if(mouseX >= 360 && mouseX <= 840 && mouseY >= 330 && mouseY <= 390)
				scene = "start";
		} else if(scene.equals("cutScene1")) {
			//back(g);
			//button(g, "Main Menu", 160, 530, 300);
			if(sceneCount1 > 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				sceneCount1--;
			} else if((sceneCount1 == 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) || mouseX >= 160 && mouseX <= 160 + 250 && mouseY >= 530 && mouseY <= 530 + 60) {
				scene = "start";
			} else if(sceneCount1 < 11 && (mouseX <= 160 || mouseX >= 160 + 250 || mouseY <= 530 || mouseY >= 530 + 60))
				sceneCount1++;
			else
				scene = "Tutorial";
		} else if(scene.equals("Secret Scene 1")) {
			//back(g);
			//button(g, "Main Menu", 160, 530, 300);
			if(secretSceneCount1 > 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				secretSceneCount1--;
			} else if((secretSceneCount1 == 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) || mouseX >= 160 && mouseX <= 160 + 250 && mouseY >= 530 && mouseY <= 530 + 60) {
				scene = "Room 1";
			} else if(secretSceneCount1 < 11 && (mouseX <= 160 || mouseX >= 160 + 250 || mouseY <= 530 || mouseY >= 530 + 60))
				secretSceneCount1++;
			else {
				scene = "Secret Room 1";
				charC[0] = 75;
			}
		} else if(scene.equals("Almost at Boss")) {
			//back(g);
			//button(g, "Main Menu", 160, 530, 300);
			if(sceneCount2 > 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				sceneCount2--;
			} else if((sceneCount2 == 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) || mouseX >= 160 && mouseX <= 160 + 250 && mouseY >= 530 && mouseY <= 530 + 60) {
				scene = "Room 4";
			} else if(sceneCount2 <= 40 && (mouseX <= 160 || mouseX >= 160 + 250 || mouseY <= 530 || mouseY >= 530 + 60))
				sceneCount2++;
			else
				scene = "Boss Room";
		}  else if(scene.equals("Secret Scene 2")) {
			//back(g);
			//button(g, "Main Menu", 160, 530, 300);
			if(secretSceneCount2 > 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) {//back
				secretSceneCount2--;
			} else if((secretSceneCount2 == 0 && mouseX >= 10 && mouseX <= 10 + 140 && mouseY >= 530 && mouseY <= 530 + 60) || mouseX >= 160 && mouseX <= 160 + 250 && mouseY >= 530 && mouseY <= 530 + 60) {
				scene = "Room 2";
			} else if(secretSceneCount2 <= 7 && (mouseX <= 160 || mouseX >= 160 + 250 || mouseY <= 530 || mouseY >= 530 + 60))
				secretSceneCount2++;
			else {
				scene = "Secret Room 2";
				charC[0] = WIDTH-270-charSize;
			}
		} 

		if(scene.equals("credits")) {
			if(mouseX >= 545 && mouseX <= 810 && mouseY >= 165 && mouseY <= 195) {

				if(onOff[1]) {
					honk.setFramePosition(0);
					honk.start();
				}
				if(!easterEgg) {
					fireBall = Toolkit.getDefaultToolkit().getImage("amooga.png");
					easterEgg = true;
				}
				else {
					fireBall = Toolkit.getDefaultToolkit().getImage("fireball.png");
					easterEgg = false;
				}
			}
		}
		if(playing) {

			if(mouseX >= 10 && mouseX <= 65 && mouseY >= 10 && mouseY <= 65) 
				scene = "pause";
			else if (hearts > 0 && mouseX >= 945 + len + 70 && mouseX <= 945 + len + 70 + len2 && mouseY >= 20 && mouseY <= 80) {
				if(currentHP <= maxHP-1) {
					currentHP += 3;
					if(currentHP > maxHP)
						currentHP = maxHP;
					hearts--;
					if(onOff[1]) {
						heal.setFramePosition(0);
						heal.start();
					}
				} else {
					if(onOff[1]) {
						overHealing.setFramePosition(0);
						overHealing.start();
					}
				}
			} else if(pebbleContact && mouseX >= 1005 && mouseX <= 1005 + len && mouseY >= 90 && mouseY <= 150 ) {
				for(int i = 0; i < enemyHP[roomNum].length; i++) {
					enemyHP[roomNum][i] = 0;
					honk.setFramePosition(0);
					honk.start();
					pebbleContact = false;
				}
			} else if(aikoBag && mouseX >= 1005 && mouseX <= 1005 + len3 && mouseY >= 160 && mouseY <= 220 ) {
				if(aikoEquip)
					aikoEquip = false;
				else
					aikoEquip = true;
			}
			else {	
				projsOut++;
				if (projsOut == 9)
					projsOut = 0;

				shoot(mouseX - 20, mouseY - 20, projsOut);

			}
		}
		if(onOff[1]) {
			clickSound.setFramePosition (0);
			clickSound.start();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {


	}

	@Override
	public void mouseEntered(MouseEvent e) {


	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	//MOUSE-------------------------------------------
	//KEY-------------------------------------------

	@Override
	public void keyTyped(KeyEvent e) {


	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(playing) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_A) {
				left = true;
				right = false;
			}else if(key == KeyEvent.VK_D) {
				right = true;
				left = false;
			}else if(key == KeyEvent.VK_W) {
				up = true;
				down = false;
			}else if(key == KeyEvent.VK_S) {
				down = true;
				up = false;
			}

			if(key == KeyEvent.VK_J) {
				projsOut++;
				if (projsOut == 9)
					projsOut = 0;
				//				int randx = (int)(Math.random()*(1200));
				//				int randy = (int)(Math.random()*(600));
				int endX = 0, endY = 0, numEnemies = 0, closest = -1;
				int[] enemies;

				//counting how many enemies there are
				for(int j = 0; j < enemyC[roomNum].length; j++)
					if(enemyHP[roomNum][j] > 0)
						numEnemies++;


				enemies = new int[numEnemies];//array of potential enemies to target
				int previous = 0;//for current index in new array

				//for(int i = 0; i < enemyC.length; i++) {//go through whole list for enemies
				for(int j = 0; j < enemyC[roomNum].length; j++)
					if(enemyHP[roomNum][j] > 0) {//if enemy is alive, add it to list
						enemies[previous] = j;
						previous++;//change array index
					}
				//}

				double prevLowest = 1200;

				if(numEnemies == 0) {
					if(spriteNum == 0)
						endY = charC[1] + 1;
					else if(spriteNum == 1)
						endX = charC[0] - 1;
					else if(spriteNum == 2)
						endX = charC[0] + 1;
					else if(spriteNum == 3)
						endY = charC[1] - 1;
				} else {
					closest = enemies[0];
					for(int i = 0; i < numEnemies; i++) {//calculate which enemy is the closest
						double adj;
						adj = enemyC[roomNum][enemies[i]][0] - charC[0];
						if(adj < 0)
							adj *= -1;
						if(adj < prevLowest) {
							prevLowest = adj;
							closest = enemies[i];
						}
					}
					endX = enemyC[roomNum][closest][0];
					endY = enemyC[roomNum][closest][1];
				}

				shoot(endX, endY, projsOut);

			}

			if(key == KeyEvent.VK_ESCAPE && !scene.equals("pause")) {
				lastRoom = scene;
				scene = "pause";
			} 

			if(interact) {
				if(key == KeyEvent.VK_F) {
					if(lastCharC[0] == 1) {
						for(int i = 0; i < projOut.length;i++) {
							projOut[i] = false;
						}
						if(onOff[1]) {
							doorOpeningSound.setFramePosition (0);
							doorOpeningSound.start();
						}
						scene = nextRoom;
						//for lastCharC
						//ind 0 = x, ind 1 = y, ind 2 = type of interaction (1 = door, 2 = item), ind 3 = direction (0 = up, 1 = down, 2 = left, 3 = right)
						if(lastCharC[1] == 0) {
							charC[1] = HEIGHT - charSize - 80;//sends us to bottom
						}else if(lastCharC[1] == 1){
							charC[1] = 20;//sends us to top
						}else if(lastCharC[1] == 2) {
							charC[0] = 75;//sends us to left
						}else
							charC[0] = WIDTH - charSize - 270;//sends us to right
					} else if(lastCharC[0] == 3) {
						enemyHP[0][0] = 5;
					} else if (lastCharC[0] == 4) {
						saveSound.setFramePosition(0);
						saveSound.start();
						saveScene = scene;
						saveHP = currentHP;
						saveX = charC[0];
						saveY = charC[1];
					} else if(lastCharC[0] == 5) {
						hearts += 3;
						heartsCollected[roomNum] = true;
						keQing.setFramePosition(0);
						keQing.start();
					}
					interact = false;
				}
			}
		}


	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_A) {
			//walkSound.stop();
			left = false;
		}else if(key == KeyEvent.VK_D) {
			//walkSound.stop();
			right = false;
		}else if(key == KeyEvent.VK_W) {
			//walkSound.stop();
			up = false;
		}else if(key == KeyEvent.VK_S) {
			//walkSound.stop();
			down = false;
		}
	}
	//KEY-------------------------------------------



	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, FontFormatException {

		JFrame frame = new JFrame("Into the Void");
		Game panel = new Game(); //calls constructor right away
		frame.add(panel); //combines panel with frame
		panel.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.addWindowListener(panel);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);

	}

	@Override
	public void run() {

		System.out.println("Thread: Starting thread");
		initialize();
		while(true) {

			if(stunned)
				stunlock++;
			if(invincible)
				dmgCountDown++;
			if(scene.equals("Boss Room"))
				bossTimer++;
			//			if(frame%10000000==0)
			//				System.out.println("pog");
			//main game loop
			for(int i = 0; i < enemyPhase.length; i++) {
				for(int j = 0; j < enemyC[i].length; j++)

					if(enemyPhase[i][j] != 0 && playing) {
						enemyCounter[i][j]++;
					} else if(playing && (enemyAtkType[i][j] == 1 || enemyAtkType[i][j] == 3) && !enemyAtkOut[i][j]) {
						enemyCounter[i][j]++;
					}
			}
			update();
			this.repaint();
			try {
				Thread.sleep(1/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		try {
			inFile = new Scanner(new File("prevGameData.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if(Integer.parseInt(inFile.nextLine()) != 0) {
			played = true;

		}


	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			outFile = new PrintWriter(new File("prevGameData.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if(played) {
			outFile.println(1);
			outFile.println(saveHP);
			outFile.println(saveX);
			outFile.println(saveY);
			outFile.println(saveScene);
		}
		else
			outFile.println(0);


		outFile.close();


	}

	@Override
	public void windowClosed(WindowEvent e) {


	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}



}
