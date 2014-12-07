package levels;

import java.util.ArrayList;

import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import lando.systems.ld31.Assets;
import lando.systems.ld31.GameConstants;
import lando.systems.ld31.ParticleSystem;
import levels.intercellular.BloodCell;
import levels.intercellular.TileType;

/**
 * Created by vandillen on 12/6/14.
 */
public class IntercellularLevel extends GameLevel {

    // Constants
    public final int tile_size  = 32;
    private final int tiles_wide = 320 + 16;
    private final int tiles_high = GameConstants.ScreenHeight / tile_size;
    public Vector2 spawnPoint = new Vector2(GameConstants.GameWidth/2.0f, 40);
    public BloodCell spawnCell;

    public float nextSpawn = 0;

    public Rectangle gameBounds;
    public ArrayList<BloodCell> cells;

    
    // Class constructor
    public IntercellularLevel() {

    	cells = new ArrayList<BloodCell>();
    	gameBounds = new Rectangle((GameConstants.GameWidth - (tile_size * 10 + 16)) / 2.0f, 0, tile_size * 10 + 16, GameConstants.ScreenHeight);
    	spawnCell = new BloodCell(spawnPoint.x, spawnPoint.y, this, false);
    }
    


    // 2d array to a 1d:
    // index = x + (y * width);
    // x = index%width;
    // y = index/width;

    // random bulge method initializer

    // bulge method if falling cell makes contact with bulge

    // random method for incoming cells to shoot



    @Override
    public int hasThreat() {
        return 0;
    }

    @Override
    public void handleInput(float dt) {

    }

    public void wipeBoard(){
    	cells.clear();
    }
    
    public void lose(){
    	wipeBoard();
    }
    
    public void addChains(){
    	for(int i = 0; i < cells.size(); i++){
    		BloodCell cell = cells.get(i);
    		cell.gridPos.y += 2;
    		cell.pos.y -= 64;
    		if (cell.pos.y <= 0){
    			lose();
    		}
    	}
    	for (int y = 1; y < 3; y++){
	    	for (int x = 0; x < 10; x++){
	    		Vector2 gamePos = BloodCell.gridPosToGame(new Vector2(x,y), this);
	    		BloodCell cell = new BloodCell(gamePos.x, gamePos.y, this, true);
	    		cell.gridPos = new Vector2(x,y);
	    		cell.alive = true;
	    		cell.settled = true;
	    		cells.add(cell);
	    	}
    	}
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int button) {
    	if (cellsMoving() || nextSpawn <= 0) return false;
    	Vector2 gamePos = getGamePos(new Vector2(screenX, screenY));
    	float rot = gamePos.sub(16,16).sub(spawnPoint).angle();
    	spawnCell.fire(rot);
    	cells.add(spawnCell);
    	spawnCell = new BloodCell(spawnPoint.x, spawnPoint.y, this, false);
    	return true;
    };
    
    public boolean cellsMoving(){
    	boolean moving = false;
    	for (int i =0; i < cells.size(); i++){
    		if (!cells.get(i).settled) moving = true;
    	}
    	return moving;
    }
    
    @Override
    public void update(float dt) {
    	
    	for (int i = 0; i < cells.size(); i++){
    		cells.get(i).update(dt);
    	}
    	for (int i = cells.size() -1; i >= 0; i--){
    		if (!cells.get(i).alive) {
    			BloodCell cell = cells.remove(i);
    			for (int j = 0; j < 40; j++){
    				Vector2 cellCenter = new Vector2(16,16).add(cell.pos);
    				Vector2 dest = new Vector2(1,0).rotate(Assets.rand.nextInt(360)).scl(Assets.rand.nextFloat() * 40)
    						.add(cellCenter);
    				particles.addParticle(cellCenter, dest, Color.WHITE, Color.RED, 1f, Quad.OUT);
    			}
    		}
    	}
    	if (nextSpawn <= 0 && !cellsMoving()){
    		addChains();
    		nextSpawn += 10;
    	}
    	nextSpawn = Math.max(nextSpawn - dt, 0);
    }
    
    public BloodCell getCellAtPos(Vector2 pos){
    	for (int i = 0; i < cells.size(); i++){
    		if (pos.equals(cells.get(i).gridPos)) return cells.get(i);
    	}
    	return null;
    }
    
    public void fixHangers(){
    	// TODO maybe do this.
    }
    
    public ArrayList<BloodCell> getNeighbors(BloodCell cell){
    	ArrayList<BloodCell> neighbors= new ArrayList<BloodCell>();
    	Vector2 grid = cell.gridPos;
    	int xOffset = 0;
    	if ((int)grid.y % 2 == 0 ) xOffset = 1;
    	
    	BloodCell neighbor = getCellAtPos(new Vector2(grid.x-1 + xOffset, grid.y -1));
    	if (neighbor != null) neighbors.add(neighbor);
        neighbor = getCellAtPos(new Vector2(grid.x + xOffset, grid.y -1));
    	if (neighbor != null) neighbors.add(neighbor);
    	
    	neighbor = getCellAtPos(new Vector2(grid.x - 1, grid.y));
    	if (neighbor != null) neighbors.add(neighbor);
        neighbor = getCellAtPos(new Vector2(grid.x + 1, grid.y));
    	if (neighbor != null) neighbors.add(neighbor);
    	
    	neighbor = getCellAtPos(new Vector2(grid.x -1 + xOffset, grid.y +1));
    	if (neighbor != null) neighbors.add(neighbor);
        neighbor = getCellAtPos(new Vector2(grid.x + xOffset, grid.y +1));
    	if (neighbor != null) neighbors.add(neighbor);
    	
    	return neighbors;
    }

    @Override
    public void draw(SpriteBatch batch) {
    	batch.setColor(.3f,.3f,.3f,1);
    	batch.draw(Assets.squareTex, gameBounds.x, gameBounds.y, gameBounds.width, gameBounds.height);
    	batch.setColor(Color.WHITE);
    	for (int i = 0; i < cells.size(); i++){
    		cells.get(i).draw(batch);
    	}
    	if (!cellsMoving()){
    		spawnCell.draw(batch);
    	}
    }
}
