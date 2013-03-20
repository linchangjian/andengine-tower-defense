package biz.abits.towertest;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.util.Log;

/**
 * Basic Tower class contains it's own projectiles and provides methods for firing
 *
 * @author Andrew Binning
 * @see Projectile
 * @see SplashTower
 */
public class Tower extends Sprite{
	//I am Tower class and have my own bullets //
	//TODO fire range, acquisition range, pattern/type.
	private static String texture = "tower.png";
	private long cooldown = 500; //in milliseconds | 1 sec = 1,000 millisec 
	private long credits = 50; //cost to build tower in credits
	private int level = 1; //level of tower
	private int maxLevel = 10; //level of tower
	public int damage = 100; //Tower damage
	public String damageType = "normal";
	private float cdMod = 0.5f;
	private long lastFire = 0;
	private static int total = 0; //total number of this type of tower
	TextureRegion tower;
	TextureRegion bullet;
	private static String strFire = "tower.ogg"; 
	private static Sound soundFire;
	float x,y;
	float targetX;
	float targetY;
	private boolean placeError = false;
	private boolean hitAreaShown = false;
	private boolean hitAreaGoodShown = false;
	private boolean hitAreaBadShown = false;
	TextureRegion hitAreaTextureGood;
	TextureRegion hitAreaTextureBad;
	TowerRange towerRangeGood;
	TowerRange towerRangeBad;
	//final Rectangle towerRange = new Rectangle(0, 0, TowerTest.mTMXTiledMap.getTileWidth(), TowerTest.mTMXTiledMap.getTileHeight(), this.getVertexBufferObjectManager());
	boolean moveable = true;
	Projectile SpriteBullet;
	//int speed = 500;
	VertexBufferObjectManager vbom;
	ArrayList<Projectile> arrayBullets; //may change to spritebatch

	//Body range = PhysicsFactory.createCircularBody();

	//constructor
	/**
	 * Constructor for tower class
	 * 
	 * @param b TextureRegion for bullet
	 * @param pX x coordinate of tower to create
	 * @param pY y coordinate of tower to create
	 * @param pWidth width of tower
	 * @param pHeight height of tower
	 * @param pTextureRegion I don't think this is even used? :-\
	 * @param tvbom VertexBufferObjectManager
	 */
	public Tower(Scene scene, TextureRegion b,float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion, TextureRegion hitAreaTextureGood, TextureRegion hitAreaTextureBad, VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, pTextureRegion,tvbom);
		//towerRangeGood.setPosition(pX, pY);
		vbom = tvbom;
		bullet = b; // we need bullet TextureRegion to make one
		x=pX; //some x n y of the tower
		y=pY;
		arrayBullets = new ArrayList<Projectile>(); // create a new ArrayList
		towerRangeGood = new TowerRange(0, 0, hitAreaTextureGood, vbom);
		towerRangeBad = new TowerRange(0, 0, hitAreaTextureBad, vbom);
		towerRangeGood.setPosition(this.getWidth()/2-towerRangeGood.getWidth()/2, this.getHeight()/2-360/2);
		towerRangeBad.setPosition(this.getWidth()/2-towerRangeBad.getWidth()/2, this.getHeight()/2-towerRangeBad.getHeight()/2);
		total++;
	}

	/**
	 * Fires projectiles
	 * check cooldown in milli seconds with: <br>
	 *   long elapsedTime = System.currentTimeMillis() - towerVar.getLastFire;
	 * @param targetX target attacking
	 * @param targetY target attacking
	 * @param tx location of projectile
	 * @param ty location of projectile
	 * @return boolean True if tower fired (created bullet sprite), else false
	 */
	public boolean fire(float targetX,float targetY,float tx,float ty){
		//TODO move bullet to mouth of cannon
		long elapsed = System.currentTimeMillis() - lastFire;
		//only fire if tower is off cool down
		if( elapsed > cooldown * cdMod && !moveable){ //not on cooldown, and not actively being placed
			SpriteBullet  = new Projectile(tx,ty, 10f, 10f, bullet,vbom); //READY?!?
			SpriteBullet.setTarget(targetX, targetY); //AIM...
			SpriteBullet.shoot(); //FIIIIIRE!!!!
			arrayBullets.add(SpriteBullet);
			lastFire = System.currentTimeMillis();
			//TODO check sound settings
			soundFire.play();
			return true;
		}
		else return false;
	}

	/**
	 * This function will remove all current bullets
	 */
	public void ceaseFire(Scene scene) {
		for(int i=0;i<arrayBullets.size();i++){
			arrayBullets.get(i).stop(scene, arrayBullets);
		}
	}

	public void fire(Enemy enemy, Scene scene, ArrayList<Enemy> arrayEn){
		if (!TowerTest.paused) {        	
			targetX = enemy.getX()+enemy.getWidth()/2; // simple get the enemy x,y and center it and tell the bullet where to aim and fire
			targetY = enemy.getY()+enemy.getHeight()/2;
			//call fire from the tower
			boolean fired = this.fire(targetX, targetY,this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2); //Asks the tower to open fire and places the bullet in middle of tower
			if(fired){
				ArrayList<Projectile> towerBulletList = this.getArrayList(); //gets bullets from Tower class where our bullets are fired from
				Sprite myBullet = this.getLastBulletSprite();
				scene.attachChild(myBullet);
				//for(Sprite bullet : towerBulletList){
				for(int i = 0; i < towerBulletList.size(); i++){
					Projectile bullet;
					bullet = towerBulletList.get(i);
					if(bullet.collidesWith(enemy)){
						//WARNING: This function should be called from within postRunnable(Runnable) which is registered to a Scene or the Engine itself, because otherwise it may throw an IndexOutOfBoundsException in the Update-Thread or the GL-Thread!
						//bullet.stop();
						scene.detachChild(bullet); // When else should we remove bullets? Check its range?
						towerBulletList.remove(bullet);  // also remove it from array so we don't check it again
						//enemy takes
						if(enemy.takeDamage(this.damage,this.damageType) < 1){ //then the enemy dies
							TowerTest.addCredits(enemy.getCredits());
							scene.detachChild(enemy);
							arrayEn.remove(enemy);
							//this.ceaseFire(scene);
							//TODO play death animation enemy function pass scene to detach
						}
						i = towerBulletList.size();
						break; // take a break
						//this else if may be completely useless..... or wrong
						//I don't think we need this
						//} else if (bullet.isDone()) {
						//I disabled it but if we start seeing stray bullets, we should enable it
						//if the bullet is done moving, AND it's not hitting an enemy, then that means it's done, and should be removed!
						//bullet.stop(scene, towerBulletList);
					}
				}
			}
		}
	}

	/**
	 * Get the cool down milliseconds
	 * @return cool down in milliseconds
	 */
	public long getCD(){ return cooldown; }

	/**
	 * Get the cool down Modifier as a float to represent a percentage
	 * if( elapsed > cooldown * cdMod)
	 * @return cool down Modifier float
	 */
	public float getCDMod(){ return cdMod; }

	/**
	 * Set the cool down Modifier as a float to represent a percentage
	 * if( elapsed > cooldown * cdMod)
	 */
	public void setCDMod(long cdm){ cdMod = cdm; }

	/**
	 * This function sets the texture for the tower type and returns a Texture Region to preload the texture.
	 * @param tm Texture manager; usually passed in as this.getTextureManager()
	 * @param c Context; usually passed in as this
	 * @return TextureRegion to load
	 */
	public static TextureRegion loadSprite(TextureManager tm, Context c){
		TextureRegion tr;
		Log.i("Location:","Tower loadSprite");
		BitmapTextureAtlas towerImage;
		towerImage = new BitmapTextureAtlas(tm,512,512);
		tr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(towerImage, c, texture, 0, 0);
		tm.loadTexture(towerImage);
		return tr;
	}

	/**
	 * Loads the sound from mfx/
	 * @param sm SoundManager passed from engine
	 * @param act SimpleBaseGameActivity Base class (this) 
	 */
	public static void loadSound(SoundManager sm, SimpleBaseGameActivity act){
		try {
			soundFire = SoundFactory.createSoundFromAsset(sm, act, strFire);
		} catch (final IOException e) {  Debug.e(e);  }
	}

	public Sprite getLastBulletSprite(){
		return SpriteBullet; // our main class uses this to attach to the scene
	}

	public ArrayList<Projectile> getArrayList(){ 
		return arrayBullets; // our main class uses this to check bullets etc
	}

	/**
	 * Get to cost to build this tower
	 * @return build cost in credits
	 */
	public long getCredits(){ return credits; }

	/**
	 * Get Current tower level
	 * @return tower level
	 */
	public int getLevel(){ return level; }

	/**
	 * Upgrade tower one level
	 * @return Returns false if tower already at max level
	 */
	public boolean upgradeLevel(){ if(level == maxLevel)  return false; else level++; return true; }

	/**
	 * Get the total number of these towers that have been built
	 * @return number of this tower that has been built
	 */
	public int getTotal(){ return total;}

	/**
	 * Sell tower, return credit value
	 * @return credit value of tower
	 */ //TODO finish this function (pass in scene/list to remove it maybe)
	public long sell(){ total--; return credits;}

	/**
	 * This checks through all the bullets that the tower has, to make sure any non-moving bullets get erased
	 * @param scene
	 */
	public void checkBullets(Scene scene) {
		for(int i=0;i<arrayBullets.size();i++)
			if (arrayBullets.get(i).isDone())
				arrayBullets.get(i).stop(scene, arrayBullets);
	}

	/**This gets the X attachment point (allows you to offset where you grab it at, 0,0 is the upper-left) */
	public float getXHandleOffset(){
		return (this.getWidth()/2) * 1.5f; //asjusted by 1.5 so it looks like you're grabbing the center of the turret
	}

	/**This gets the Y attachment point (allows you to offset where you grab it at, 0,0 is the upper-left) */
	public float getYHandleOffset(){
		return this.getHeight()/2; //default to the middle of the sprite
	}
	
	/**This tells the tower if it is allowed to be placed where it is, when it is being placed */
	public void setTowerPlaceError(Scene scene, boolean towerPlaceError) {
		placeError = towerPlaceError;
		if (hitAreaShown) {
			//update the hit area to reflect this
			setHitAreaShown(scene, hitAreaShown);
		}
		//update graphics
	}
	
	/**Enables or disables the display of the "hit area" */
	public void setHitAreaShown(Scene scene, boolean showHitArea) {
		if (showHitArea) {
			//we attach it to this sprite, that way it's tied to it!
			if (placeError) {
				if (!hitAreaBadShown) {
					this.attachChild(towerRangeBad);
					hitAreaBadShown = true;
				}
				if (hitAreaGoodShown) {
					this.detachChild(towerRangeGood);
					hitAreaGoodShown = false;
				}
			} else {
				if (hitAreaBadShown) {
					this.detachChild(towerRangeBad);
					hitAreaBadShown = false;
				}
				if (!hitAreaGoodShown) {
					this.attachChild(towerRangeGood);
					hitAreaGoodShown = true;
				}
			}
		} else {
			//detach both!
			if (hitAreaGoodShown) {
				this.detachChild(towerRangeGood);
				hitAreaGoodShown = false;
			}
			if (hitAreaBadShown) {
				this.detachChild(towerRangeBad);
				hitAreaBadShown = false;
			}
			this.detachChild(towerRangeGood);
		}
		hitAreaShown = showHitArea;
	}
	
	/**tells us if the tower can be placed where it is */
	public boolean hasPlaceError() { 
		return this.placeError; 
	}
}
