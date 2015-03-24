package com.crawlmb;

import android.graphics.Color;

import com.crawlmb.view.TermView;

public class NativeWrapper
{
	// Load native library
	static
	{
        System.loadLibrary("crystax");
		System.loadLibrary("crawl");
	}

	private TermView term = null;
	private StateManager stateManager = null;

	private String display_lock = "lock";
	private static final String TAG = NativeWrapper.class.getCanonicalName();

	public void gameStart()
	{
		initGame(term.getContext().getFilesDir().getPath());

	}

	private void showLoadingMessage()
	{
		synchronized (display_lock)
		{
			String launchingGame1 = term.getResources().getString(R.string.launching_game_line_1);
			String launchingGame2 = term.getResources().getString(R.string.launching_game_line_2);

			for (int i = 0; i < launchingGame1.length(); i++)
			{
				term.drawPoint(0, i, launchingGame1.charAt(i), Color.WHITE, Color.BLACK, false);
			}
			for (int i = 0; i < launchingGame2.length(); i++)
			{
				term.drawPoint(1, i, launchingGame2.charAt(i), Color.WHITE, Color.BLACK, false);
			}
			term.invalidate();
		}
	}

	public native String initGame(String initFileLocation);

	public NativeWrapper(StateManager s)
	{
		stateManager = s;
	}

	public int getch(final int v)
	{
		stateManager.gameThread.setFullyInitialized();
		int key = stateManager.getKey(v);
		return key;
	}

	// this is called from native thread just before exiting
	public void onGameExit()
	{
		stateManager.handler.sendEmptyMessage(CrawlDialog.Action.OnGameExit.ordinal());
		// Log.d(TAG, "onGameExit()");
	}

	private native void refreshTerminal();

	public boolean onGameStart()
	{
		// Log.d(TAG, "onGameStart()");
		synchronized (display_lock)
		{
			boolean result = term.onGameStart();
			showLoadingMessage();
			return result;
		}
	}

	public void increaseFontSize()
	{
		// Log.d(TAG, "increaseFontSzie()");
		synchronized (display_lock)
		{
			term.increaseFontSize();
			resize();
		}
	}

	public void link(TermView t)
	{
		synchronized (display_lock)
		{
			term = t;
		}
	}

	public void decreaseFontSize()
	{
		// Log.d(TAG, "decreaseFontSize()");
		synchronized (display_lock)
		{
			term.decreaseFontSize();
			resize();
		}
	}

	public void fatal(String msg)
	{
		// Log.d(TAG, "fatal("+msg+")");
		synchronized (display_lock)
		{
			stateManager.fatalMessage = msg;
			stateManager.fatalError = true;
			stateManager.handler.sendMessage(stateManager.handler.obtainMessage(
					CrawlDialog.Action.GameFatalAlert.ordinal(), 0, 0, msg));
		}
	}

	public void resize()
	{
		// Log.d(TAG, "resize()");
		synchronized (display_lock)
		{
			term.onGameStart(); // recalcs TermView canvas dimension
			refreshTerminal();
		}
	}

	public void printTerminalChar(int y, int x, char c, int fgcolor, int bgcolor)
	{
		synchronized (display_lock)
		{
			// Formatter fmt = new Formatter();
			// fmt.format("fgcolor:%x bgcolor:%x ", fgcolor, bgcolor);
			// Log.d("Crawl","printingTerminalChar, y is: " + y + ", x is: " + x
			// + ", character is: " + c + ", " + fmt);
			term.drawPoint(y, x, c, fgcolor, bgcolor, false);
		}
	}

	public void invalidateTerminal()
	{
		synchronized (display_lock)
		{
			term.postInvalidate();
		}
	}

}
