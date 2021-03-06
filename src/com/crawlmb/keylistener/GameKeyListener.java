package com.crawlmb.keylistener;

import android.os.Handler;

import android.view.KeyEvent;

import com.crawlmb.GameThread;
import com.crawlmb.KeyBuffer;
import com.crawlmb.NativeWrapper;
import com.crawlmb.view.TermView;

public class GameKeyListener implements KeyListener
{
	public static final int KEY_A1 = 0534; /* upper left of keypad */
	public static final int KEY_A3 = 0535; /* upper right of keypad */
	public static final int KEY_B2 = 0536; /* center of keypad */
	public static final int KEY_C1 = 0537; /* lower left of keypad */
	public static final int KEY_C3 = 0540; /* lower right of keypad */
	public static final int KEY_DOWN = 0402; /* down-arrow key */
	public static final int KEY_UP = 0403; /* up-arrow key */
	public static final int KEY_LEFT = 0404; /* left-arrow key */
	public static final int KEY_RIGHT = 0405; /* right-arrow key */
	/* screen state */
	public int termWinNext = 0;

	/* alert dialog state */
	public boolean fatalError = false;
	public String fatalMessage = "";

	/* progress dialog state */
	public static final String progress_lock = "lock";

	/* keybuffer */
	private KeyBuffer keyBuffer = null;

	/* native crawl library interface */
	public NativeWrapper nativew = null;

	/* game thread */
	public GameThread gameThread = null;

	/* game thread */
	public Handler handler = null;

	public GameKeyListener()
	{
		nativew = new NativeWrapper(this);
		gameThread = new GameThread(this, nativew);

		keyBuffer = new KeyBuffer(this);
	}

	public void link(TermView t, Handler h)
	{
		handler = h;
		nativew.link(t);
	}

	public String getFatalError()
	{
		return "Crawl quit with the following error: " + fatalMessage;
	}

	public int getKeyUp()
	{
		return KEY_UP;
	}

	public int getKeyDown()
	{
		return KEY_DOWN;
	}

	public int getKeyLeft()
	{
		return KEY_LEFT;
	}

	public int getKeyRight()
	{
		return KEY_RIGHT;
	}

	public int getKeyEnter()
	{
		return 13;
	}

	public int getKeyEsc()
	{
		return 0x1B;
	}

	public int getKeyTab()
	{
		return '\t';
	}

	public int getKeyBackspace()
	{
		return 0x9F;
	}

	public int getKeyDelete()
	{
		return 0x9E;
	}

	public void resetKeyBuffer()
	{
		this.keyBuffer = new KeyBuffer(this);
	}

    @Override
	public void addKey(int key, int keyIndex)
	{
		if (this.keyBuffer != null)
			this.keyBuffer.add(key);
	}

	public int getKey(int v)
	{
		if (this.keyBuffer != null)
		{
			return keyBuffer.get(v);
		}
		else
		{
			return 0;
		}
	}

    @Override
	public void addDirectionKey(int k)
	{
		if (this.keyBuffer != null)
			this.keyBuffer.addDirection(k);
	}

	public void signalGameExit()
	{
		if (this.keyBuffer != null)
			this.keyBuffer.signalGameExit();
	}

	public boolean getSignalGameExit() {
		return this.keyBuffer != null && this.keyBuffer.getSignalGameExit();
	}

	public void signalSave()
	{
		if (this.keyBuffer != null)
			this.keyBuffer.signalSave();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return this.keyBuffer != null && this.keyBuffer.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return this.keyBuffer != null && this.keyBuffer.onKeyUp(keyCode, event);
	}
}
