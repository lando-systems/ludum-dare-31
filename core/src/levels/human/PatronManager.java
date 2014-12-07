package levels.human;

import java.util.ArrayList;

import lando.systems.ld31.Assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PatronManager {
	
	private Texture[] _patronGfx = new Texture[] {
			new Texture(HumanAssets.Patron1),
			new Texture(HumanAssets.Patron2),
			new Texture(HumanAssets.Patron3)			
	};
	
	ArrayList<Patron> _patrons = new ArrayList<Patron>(15);
	
	int[] _barlocations;
	float _patronAddTime = 1f;
	float _addReset = 3f;
	
	public PatronManager(int[] barlocations) {
		_barlocations = barlocations;
		reset();
	}
	
	public void reset() {
		_patronAddTime = 1f;
		_addReset = 3f;
	}
	
	public void update(ArrayList<Glass> glasses, float dt) {
		_patronAddTime -= dt;
			
		for (int i = 0; i < _patrons.size(); i++) {
			_patrons.get(i).check(glasses);
		}
	
		// update
		for (int i = _patrons.size() - 1; i >= 0; i--) {
			Patron patron = _patrons.get(i);
			patron.update(dt);
			if (patron.remove) {
				_patrons.remove(patron);
			}
		}
		
		// add
		if (_patronAddTime < 0) {
			if (_addReset > 1f) { 
				_addReset -= 0.2f;
			}
			
			_patronAddTime = _addReset;
			
			int barLevel = Assets.rand.nextInt(_barlocations.length);
			int y = _barlocations[barLevel] + 60;		
			Patron patron = createPatron(y);			
			patron.level = barLevel;
			_patrons.add(patron);			
		}		
	}
	
	private Patron createPatron(int y) {
		Texture image = _patronGfx[Assets.rand.nextInt(_patronGfx.length)];		
		Patron patron = new Patron(image, 90, -image.getWidth(), y);
		patron.speed = 100 + Assets.rand.nextInt(100);
		return patron;
	}

	public void draw(SpriteBatch batch) {
		for (int i = 0; i < _patrons.size(); i++) {
			_patrons.get(i).draw(batch);
		}
	}
}