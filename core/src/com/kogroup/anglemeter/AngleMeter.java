package com.kogroup.anglemeter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

//@author KOgroup

public class AngleMeter extends ApplicationAdapter {

	private SpriteBatch batch;
	private String text = "";
	private float width, height;
	private TextButton mainButton, wLength, hLength, bubbleText;
	private Image area, ruler, ruler2;
	private Slider horizontalBubble, verticalBubble;
	private Window noteWindow;
	private ScrollPane pane;
	private Label helpLabel;
	private Sprite bg;

	private List<String> labelList;

	private int count, state, state2;

	private Stage stage;
	private Skin skin;

	private final int ANGLE = 1, RULER = 2, WATER = 3, XY = 1, YZ = 2, AZIMUTH = 3;
	private float PIXELPERCM, value, zeroValue, gValue;

	private final float BUTTONSIZE = 60, TEXTSIZE = 70, DEFWIDTH = 480;

	private boolean isCm, isCalibrating, isRad, isHorizontal;

	private Array<Button> buttons;

	@Override
	public void create() {
		Gdx.gl.glClearColor(56 / 255f, 57 / 255f, 67 / 255f, 1);

		SavedData.setPrefs();
		isCm = SavedData.getUnitCm();
		isRad = SavedData.getUnitRad();
		gValue = SavedData.getG();
		if (gValue < 8f)
			gValue = 9.81f;

		System.out.println(gValue);
		isHorizontal = true;
		state = -1;
		state2 = 0;
		isCalibrating = false;
		PIXELPERCM = Gdx.graphics.getPpcY();

		batch = new SpriteBatch();
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		setFont();

		noteWindow();
		setStage();
		onOff();

		// table.debug();

		Gdx.input.setInputProcessor(stage);

		count = 0;
	}

	private void setFont() {
		float ratio = width / DEFWIDTH;

		skin = new Skin();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Roboto1.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = Math.round(TEXTSIZE * ratio);

		BitmapFont font = generator.generateFont(parameter);

		parameter.size = Math.round(TEXTSIZE * ratio * 0.4f);
		BitmapFont font2 = generator.generateFont(parameter);

		parameter.size = Math.round(TEXTSIZE * ratio * 0.5f);
		BitmapFont font3 = generator.generateFont(parameter);
		generator.dispose();

		skin.addRegions(new TextureAtlas("atlas.pack"));
		skin.add("myFont", font);
		skin.add("small", font2);
		skin.add("medium", font3);
		skin.load(Gdx.files.internal("skin.json"));

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		bg.draw(batch);
		batch.end();
		switch (state) {
		case ANGLE:
			count++;

			// updates every 0.5 seconds or so
			
			if (count >= 25) {
				count = 0;
				text = "";
				// float accelY = Gdx.input.getAccelerometerY();
				// if (accelY > maxY) {
				// accelY = maxY;
				// } else if (Math.abs(accelY) > maxY && accelY < 0)
				// accelY = -maxY;

				float x = Gdx.input.getAccelerometerX();
				float y = Gdx.input.getAccelerometerY();
				float z = Gdx.input.getAccelerometerZ();

				if (state2 == XY) {
					value = (float) Math.toDegrees(Math.atan2(y, x));
					mainButton.setRotation(-90 + value);
					if (value > 90)
						value = 180 - value;
					else if (value < -90)
						value = -180 - value;

				} else if (state2 == YZ) {
					value = (float) Math.toDegrees(Math.atan2(y, z));

					if (value > 90)
						value = 180 - value;
					else if (value < -90)
						value = -180 - value;
				}

				else if (state2 == AZIMUTH) {
					value = Math.abs(Gdx.input.getAzimuth() - zeroValue);
				}

				if (!isCalibrating) {
					if (isRad) {
						value = Math.round(MathUtils.degRad * value * 100);

						text = "" + (value) / 100;
						mainButton.setText(text + " rad");
					} else {
						text = "" + Math.round(value);
						mainButton.setText(text + "\u00b0");
					}

					// if (state2==AZIMUTH)
					// text+="\n"+ Gdx.input.getAzimuth()+ "\n" + zeroValue;

				}
			}

			break;

		case RULER:
			//everything happens within stage
		
			break;

		case WATER:
			count++;
			if (count>=25){
				count=0;
			
			float x = Gdx.input.getAccelerometerX();
			float y = Gdx.input.getAccelerometerY();

			if (isHorizontal) {
				if (x > gValue)
					x = gValue;
				value = x / gValue * 90;
				horizontalBubble.setValue((int) value);
				value=Math.round(value/0.9f);
				bubbleText.setText((int)value+"%");
			} else {
				if (y > gValue)
					y = gValue;
				value = y / gValue * 90;
				value=Math.round(value/0.9f);
				bubbleText.setText((int)value+"%");
				verticalBubble.setValue((int) value);
			}
			}
			break;
		default:
			break;
		}

		stage.act();
		stage.draw();

		// restart
		if (Gdx.input.isKeyPressed(Keys.R))
			create();

	}

	private void setStage() {

		float ratio = width / DEFWIDTH;

		bg = new Sprite(new Texture("bg2.png"));
		bg.setSize(width, width * 16 / 9);

		stage = new Stage(new StretchViewport(width, height), batch);

		Table table = new Table(skin);
		table.setFillParent(true);

		final Label deleteArea = new Label(" Drag and drop\n\n here to\n\n remove value", skin, "note");
		deleteArea.setVisible(false);
		deleteArea.setSize(width * 2 / 3, height);

		final ImageButton files = new ImageButton(skin, "files");
		files.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				slideIn(noteWindow, width, width - noteWindow.getWidth());
				
				//to get it up front
				deleteArea.setZIndex(15);
				deleteArea.setVisible(true);
				labelList.invalidate();
				pane.invalidate();

			}
		});

		files.setTransform(true);
		files.setOrigin(BUTTONSIZE * ratio / 2, BUTTONSIZE * ratio / 2);

		mainButton = new TextButton("", skin);
		mainButton.setTransform(true);

		mainButton.addListener(new ActorGestureListener(){
			
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				if (isCalibrating || mainButton.getText().length() < 2) {
					// TODO
					if (state2 == AZIMUTH) {
						zeroValue = Gdx.input.getAzimuth();
						isCalibrating = false;
					}

				}

				else {

					if (mainButton.getText().toString().matches(".*\\d+.*")) {
						labelList.getItems().add("\u00b7" + mainButton.getText());

					}

				}
				super.tap(event, x, y, count, button);
			}
			
			@Override
			public boolean longPress(Actor actor, float x, float y) {
				
				if (state==ANGLE && state2!=AZIMUTH){
				float xg = Math.abs(Gdx.input.getAccelerometerX());
				float yg = Math.abs(Gdx.input.getAccelerometerY());
				
				//only saves if the result is logical
				if (xg>9f || yg>9f){
				SavedData.setG((xg>yg? xg:yg));
				gValue=SavedData.getG();
				System.out.println(gValue);
				Gdx.input.vibrate(300);
				}
				}
				return super.longPress(actor, x, y);
			}
			
			
			
		});

		ImageButton menuButton = new ImageButton(skin, "red");
		menuButton.setTransform(true);
		menuButton.setOrigin(BUTTONSIZE * ratio / 2, BUTTONSIZE * ratio / 2);

		menuButton.addListener(new ActorGestureListener() {

			@Override
			public boolean longPress(Actor actor, float x, float y) {
				if (state == RULER) {
					isCm = !isCm;
					SavedData.setUnitCm(isCm);

					float cm = (float) Math.round((area.getHeight() * 100) / PIXELPERCM) / 100;
					float inch = (float) Math.round((Math.round(cm * 100) / 2.54f)) / 100;

					float cm2 = (float) Math.round((area.getWidth() * 100) / PIXELPERCM) / 100;
					float inch2 = (float) Math.round((Math.round(cm2 * 100) / 2.54f)) / 100;
					if (isCm) {
						hLength.setText(cm + " cm");
						wLength.setText(cm2 + " cm");
					} else {
						hLength.setText(inch + " in");
						wLength.setText(inch2 + " in");
					}

				} else if (state == ANGLE) {
					isRad = !isRad;
					SavedData.setUnitRad(isRad);
				} else if (state==WATER){
					isHorizontal=!isHorizontal;
					verticalBubble.setVisible(!isHorizontal);
					horizontalBubble.setVisible(isHorizontal);
					if (isHorizontal){
						bubbleText.setRotation(0);
						bubbleText.setPosition(width / 2 - bubbleText.getWidth() / 2, height*0.1f);
						
					}
					else{
						bubbleText.setRotation(-90);
						bubbleText.setPosition(width*0.05f, height/2 - bubbleText.getWidth()/2);

					}
				}
				return true;
			}

			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				int i = 0;
				if (!buttons.peek().hasActions()) {
					for (Button b : buttons) {
						blink(b, 0.1f * i);
						i++;
					}
				}
				super.tap(event, x, y, count, button);
			}
			
			

		});

		
		helpLabel = new Label("", skin, "small");

		helpLabel.setWrap(true);
		helpLabel.setAlignment(Align.center);
		float p = width * 0.7f;

		mainButton.setOrigin(p / 2, p / 2);

	

		// TODO
		// verticalBubble.setVisualInterpolation(Interpolation.sine);

		table.add(files).right().top().expand().width(BUTTONSIZE * ratio).height(BUTTONSIZE * ratio)
				.pad(width / 40, 0, 0, width / 40).row();
		table.add(mainButton).expandX().center().width(p).height(p).padTop(p * 0.1f).padBottom(p * 0.1f).row();
		table.add(menuButton).width(BUTTONSIZE * ratio).height(BUTTONSIZE * ratio).center().expand().top()
				.padBottom(width / 40).row();

		dragAndDrop(deleteArea);

		// index O is delete area
		stage.addActor(deleteArea);
//		deleteArea.debug();
		setRULER();
		stage.addActor(table);
		stage.addActor(helpLabel);
		helpLabel.setPosition(width / 2, height * 0.8f);
		// table.debug();
		setWaterLevel(p);

		

		setButtonTable(p);
		stage.addActor(noteWindow);
		deleteArea.setPosition(0, 0);
		// deleteArea.debug();

		stage.addListener(new ClickListener() {
			float X, Y;
			boolean measure = false;

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (noteWindow.isVisible() && x < width - noteWindow.getWidth() && !noteWindow.hasActions()) {
					slideOut(noteWindow, 0, width);
					deleteArea.setZIndex(0);
					deleteArea.setVisible(false);
				}
				measure = false;

				if (state == RULER && x <= area.getWidth() && y <= area.getHeight()) {
					X = x;
					Y = y;
					measure = true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {

				if (measure) {
					area.setSize(area.getWidth() + x - X, area.getHeight() + y - Y);
					X = x;
					Y = y;

					if (area.getWidth() > width)
						area.setWidth(width);
					if (area.getHeight() > height)
						area.setHeight(height);

					float cm = (float) Math.round((area.getHeight() * 100) / PIXELPERCM) / 100;
					float inch = (float) Math.round((Math.round(cm * 100) / 2.54f)) / 100;

					float cm2 = (float) Math.round((area.getWidth() * 100) / PIXELPERCM) / 100;
					float inch2 = (float) Math.round((Math.round(cm2 * 100) / 2.54f)) / 100;

					if (isCm) {
						hLength.setText(cm + " cm");
						wLength.setText(cm2 + " cm");
					} else {
						hLength.setText(inch + " in");
						wLength.setText(inch2 + " in");
					}

					float w = area.getWidth() / 2 - wLength.getWidth() / 2;
					float h = area.getHeight() + wLength.getHeight();
					wLength.setPosition((w >= 0 ? w : width * 0.05f),
							(h <= height) ? area.getHeight() : height - wLength.getHeight());

					// due to rotation width and height of label are switched
					w = area.getWidth() + hLength.getHeight();
					h = area.getHeight() / 2 + hLength.getWidth() / 2;
					hLength.setPosition((w <= width ? area.getWidth() : area.getWidth() - w + width),
							(h >= hLength.getWidth()) ? h : hLength.getWidth());
					if (area.getWidth() < wLength.getWidth() && area.getHeight() < hLength.getWidth())
						hLength.setX(wLength.getX() + wLength.getWidth());
				}

				super.touchDragged(event, x, y, pointer);
			}

		});

	}

	private void setButtonTable(float a) {
		buttons = new Array<Button>();
		Table table = new Table(skin);
		table.setFillParent(true);
		

		ImageButton angle1 = new ImageButton(skin, "b1");
		angle1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				helpLabel.setText(
						"\u00b7Click big button to save results\n\u00b7Hold small button to change units\n\u00b7Position phone \u00c0 or \u00c1 and hold\nbig button to calibrate");
				isCalibrating = false;
				state = ANGLE;
				state2 = XY;
				onOff();
			}

		});
		ImageButton angle2 = new ImageButton(skin, "b2");
		angle2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				helpLabel.setText(
						"\u00b7Click big button to save results\n\u00b7Hold small button to change units\n\u00b7Position phone \u00c0 or \u00c1 and hold\nbig button to calibrate");

				isCalibrating = false;
				state = ANGLE;
				state2 = YZ;
				onOff();
			}

		});
		ImageButton angle3 = new ImageButton(skin, "b3");
		angle3.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				helpLabel.setText(
						"\u00b7Click big button to save results\n\u00b7Hold small button to change units\n\u00b7Bottom-left corner of the phone\nshould be fixed to get\ncorrect measurements");
				state = ANGLE;
				state2 = AZIMUTH;
				onOff();

				mainButton.setText("Set zero");
				if (!Gdx.input.isPeripheralAvailable(Peripheral.Compass))
					mainButton.setText("Insufficient\nhardware");
				isCalibrating = true;

			}

		});
		ImageButton ruler = new ImageButton(skin, "b4");
		ruler.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				helpLabel.setText(
						"\u00b7Drag area to change width/height\n\u00b7Click buttons to save results\n\u00b7Hold small button to change units");
				isCalibrating = false;
				state = RULER;
				onOff();
			}

		});
		final ImageButton water = new ImageButton(skin, "b4");
		water.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				helpLabel.setText("\u00b7Hold small button to change\norientation");
				isCalibrating = false;

				state = WATER;
				onOff();
				if (horizontalBubble.getX() <= 10) {
					Vector2 zeroLoc = mainButton.localToStageCoordinates(new Vector2());
					horizontalBubble.setPosition(zeroLoc.x, zeroLoc.y);
					verticalBubble.setPosition(zeroLoc.x, zeroLoc.y);
					bubbleText.setPosition(width / 2 - bubbleText.getWidth() / 2, height*0.1f);

				}

			}

		});
		final ImageButton help = new ImageButton(skin, "b6");
		help.setChecked(!SavedData.getNoHelp());
		help.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SavedData.setHelp(!SavedData.getNoHelp());
				helpLabel.setVisible(!SavedData.getNoHelp());
				help.setChecked(!SavedData.getNoHelp());
			}

		});

		buttons.add(angle1);
		buttons.add(angle2);
		buttons.add(angle3);
		buttons.add(ruler);
		buttons.add(water);
		buttons.add(help);

		float p = a * 0.4f;

		a /= 2;
		for (Button b : buttons) {
			b.setTransform(true);
			b.setOrigin(p / 2, p / 2);
			// b.setVisible(false);
		}

		table.add(angle1).expand().width(p).height(p).padBottom(a * 0.8f).bottom();
		table.add(angle2).expand().width(p).height(p).padBottom(a * 1.1f).bottom();
		table.add(angle3).expand().width(p).height(p).padBottom(a * 0.8f).bottom().row();
		table.add(ruler).expand().width(p).height(p).padTop(a * 0.8f).top();
		table.add(water).expand().width(p).height(p).padTop(a * 1.1f).top();
		table.add(help).expand().width(p).height(p).padTop(a * 0.8f).top().row();

		
		table.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				int i = 0;
				if (state!=-1){
				for (Button b : buttons) {
					if (b.hasActions())
						b.removeAction(b.getActions().first());
					blink(b, 0.2f * i);
					i++;
				}
				}

			}

		});
		// table.debug();
		stage.addActor(table);

	}

	private void setWaterLevel(float p) {
		// edge values are bigger to compensate edge of the picture
		horizontalBubble = new Slider(-154, 154, 3, false, skin, "horizontal");
		horizontalBubble.setAnimateDuration(0.1f);

		horizontalBubble.setValue(0);

		verticalBubble = new Slider(-154, 154, 3, true, skin, "vertical");
		verticalBubble.setAnimateDuration(0.1f);

		verticalBubble.setValue(0);

		horizontalBubble.setAnimateInterpolation(Interpolation.sine);
		verticalBubble.setAnimateInterpolation(Interpolation.sine);

		horizontalBubble.setTouchable(Touchable.disabled);
		verticalBubble.setTouchable(Touchable.disabled);

		// TODO

		horizontalBubble.setVisible(false);
		verticalBubble.setVisible(false);

		horizontalBubble.getStyle().background.setMinHeight(p);
		verticalBubble.getStyle().background.setMinWidth(p);

		verticalBubble.getStyle().knob.setMinWidth(166 * p / 800);
		verticalBubble.getStyle().knob.setMinHeight(144 * p / 800);

		horizontalBubble.getStyle().knob.setMinWidth(144 * p / 800);
		horizontalBubble.getStyle().knob.setMinHeight(166 * p / 800);

		horizontalBubble.setSize(p, p);
		verticalBubble.setSize(p, p);
		
		
		bubbleText = new TextButton("00%", skin, "bubble");

		bubbleText.setVisible(false);

		stage.addActor(horizontalBubble);
		stage.addActor(verticalBubble);

		bubbleText.setOrigin(bubbleText.getWidth() / 2, bubbleText.getHeight() / 2);
		bubbleText.setTransform(true);
		

		stage.addActor(bubbleText);

	}

	private void setRULER() {
		// TODO
		float px = 40;

		float r = PIXELPERCM / px;

		ruler = new Image(skin.getDrawable("ruler"));
		ruler2 = new Image(skin.getDrawable("ruler2"));

		ruler.setSize(ruler.getPrefWidth() * (r * 0.5f), ruler.getPrefHeight() * r);
		ruler.setPosition(0, 0);

		ruler2.setSize(ruler2.getPrefWidth() * r, ruler2.getPrefHeight() * r * 0.5f);
		ruler2.setPosition(0, 0);

		stage.addActor(ruler);
		stage.addActor(ruler2);

		area = new Image(skin.getDrawable("area"));
		stage.addActor(area);

		area.setPosition(0, 0);
		area.setSize(width * 0.6f, height / 2);

		wLength = new TextButton("00.00 cm", skin, "small");

		hLength = new TextButton("00.00 cm", skin, "small2");
		hLength.setTransform(true);
		hLength.setOrigin(hLength.getWidth() / 2, hLength.getHeight() / 2);
		hLength.setRotation(-90);

		float cm = (float) Math.round((area.getHeight() * 100) / PIXELPERCM) / 100;
		float inch = (float) Math.round((Math.round(cm * 100) / 2.54f)) / 100;

		float cm2 = (float) Math.round((area.getWidth() * 100) / PIXELPERCM) / 100;
		float inch2 = (float) Math.round((Math.round(cm2 * 100) / 2.54f)) / 100;

		if (isCm) {
			hLength.setText(cm + " cm");
			wLength.setText(cm2 + " cm");
		} else {
			hLength.setText(inch + " in");
			wLength.setText(inch2 + " in");
		}
		stage.addActor(wLength);
		wLength.setPosition(area.getWidth() / 2 - wLength.getWidth() / 2, area.getHeight());
		// hLength.debug();

		stage.addActor(hLength);
		hLength.setOrigin(0, 0);
		hLength.setPosition(area.getWidth(), area.getHeight() / 2 + hLength.getWidth() / 2);

		wLength.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				labelList.getItems().add("\u00b7" + wLength.getText());
			}
		});

		hLength.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				labelList.getItems().add("\u00b7" + hLength.getText());
			}
		});

	}

	private void noteWindow() {

		labelList = new List<String>(skin);

		String[] values = SavedData.getResults().split("//");
		if (SavedData.getResults() != "") {

			labelList.setItems(values);

		}

		pane = new ScrollPane(labelList, skin);

		noteWindow = new Window("", skin, "note");
		noteWindow.setSize(width / 3, height);
		noteWindow.setPosition(width, 0);
		noteWindow.setVisible(false);
		noteWindow.setKeepWithinStage(false);

		pane.setTransform(true);

		ImageButton deleteAll = new ImageButton(skin, "deleteall");
		deleteAll.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				labelList.getItems().truncate(0);
				labelList.invalidate();
				pane.invalidate();

			}

		});

		noteWindow.add(pane).width(noteWindow.getWidth())
				.height(noteWindow.getHeight() - noteWindow.getWidth() / 4 - height / 40)
				.pad(height / 80, 0, height / 80, 0).row();
		noteWindow.add(deleteAll).width(noteWindow.getWidth()).height(noteWindow.getWidth() / 4);
		// noteWindow.debug();

	}

	private void dragAndDrop(final Actor d) {
		final DragAndDrop dragDrop = new DragAndDrop();
		dragDrop.setDragActorPosition(-30, 20);
		dragDrop.addSource(new Source(labelList) {
			final Payload payload = new Payload();

			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				// TODO secoind parameter is viable
				if (canDrop() && x < noteWindow.getWidth() * 0.77f) {
					String item = labelList.getSelected();
					payload.setObject(item);

					labelList.getItems().removeIndex(labelList.getSelectedIndex());
					// payload.setValidDragActor(new Label(item + " \"delete\"",
					// skin, ""));
					payload.setDragActor(new Label(item, skin, "note"));

					return payload;
				}

				return null;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
				if (target == null) {
					labelList.getItems().add((String) payload.getObject());
					labelList.getItems().sort();
				}
			}
		});
		dragDrop.addTarget(new Target(d) {

			@Override
			public void drop(Source source, Payload payload, float x, float y, int pointer) {

				labelList.invalidate();
				pane.invalidate();
			}

			@Override
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {

				return true;
			}
		});
	}

	private void onOff() {
		boolean is = state == RULER;
		// System.out.println(is);

		ruler.setVisible(is);
		ruler2.setVisible(is);
		area.setVisible(is);
		hLength.setVisible(is);
		wLength.setVisible(is);
		mainButton.setVisible(!is);

		is = state == WATER;
		if (!is) {
			horizontalBubble.setVisible(is);
			verticalBubble.setVisible(is);
			bubbleText.setVisible(is);
			;
		} else {
			horizontalBubble.setVisible(isHorizontal);
			verticalBubble.setVisible(!isHorizontal);
			bubbleText.setVisible(is);
		}
		if (state != RULER)
			mainButton.setVisible(!is);

		mainButton.setRotation(0);
	}

	private void blink(Actor a, float d) {
		if (!a.isVisible()) {
			a.setVisible(true);
			a.setScale(0);
			a.addAction(Actions.sequence(Actions.delay(d), Actions.scaleTo(1, 1, 1f, Interpolation.elasticOut)));
		} else {
			a.addAction(Actions.sequence(Actions.delay(d), Actions.scaleTo(0, 0, 1f, Interpolation.elasticOut),
					Actions.visible(false)));

		}

	}

	private void slideIn(Actor a, float pos, float dis) {
		a.setVisible(true);
		a.setPosition(pos, a.getY());
		if (a.hasActions())
			a.removeAction(a.getActions().first());
		a.addAction(Actions.moveTo(dis, a.getY(), 1f, Interpolation.sine));
	}

	private void slideOut(Actor a, float d, float dis) {
		a.addAction(Actions.sequence(Actions.delay(d), Actions.moveTo(dis, a.getY(), 1f, Interpolation.sine),
				Actions.visible(false)));
	}

	private boolean canDrop() {
		return labelList.getItems().size > 0;
	}

	@Override
	public void resize(int width, int height) {
		// batch.dispose();
		// batch = new SpriteBatch();
		// stage.setViewport(new StretchViewport(width, height));

	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();

	}

	@Override
	public void pause() {
		String res = "";
		for (int i = 0; i < labelList.getItems().size; i++) {
			res += labelList.getItems().get(i);
			if (i < labelList.getItems().size - 1)
				res += "//";

		}

		SavedData.setResults(res);

		super.pause();
	}

	@Override
	public void resume() {
	}

}