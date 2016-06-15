package org.ajm.laforkids.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.ajm.laforkids.Adapter;

public class DesktopLauncher {
	public static void main (String[] arg) {

		//TexturePacker.process("gdx-skins-master\\kenney-pixel\\custom-raw",
		//	"gdx-skins-master\\kenney-pixel\\custom-skin", "skin");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Adapter(), config);
	}
}
