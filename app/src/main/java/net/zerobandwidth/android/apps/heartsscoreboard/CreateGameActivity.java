package net.zerobandwidth.android.apps.heartsscoreboard;

import java.util.Stack;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class CreateGameActivity extends Activity
{
	public static final String LTAG = "CreateGameActivity" ;

	/**
	 * Used to define the default value for the number of players in a new game.
	 */
	public static final int INITIAL_PLAYER_COUNT = 4 ;
	
	/**
	 * The UI dynamically maintains a stack of player name EditText controls.
	 */
	private Stack<EditText> m_koPlayerNames ;
	
	/**
	 * The game currently under construction.
	 */
	private Game m_oGame ;
	
	@Override
	/**
	 * Does some wacky initialization of the UI elements.
	 */
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.activity_create_game);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Initialize number picker for player count.
		NumberPicker npkPlayerCount = (NumberPicker)(findViewById(R.id.npk_PlayerCount)) ;
		npkPlayerCount.setMinValue(3) ;
		npkPlayerCount.setMaxValue(8) ;
		npkPlayerCount.setDisplayedValues( new String[] { "3", "4", "5", "6", "7", "8" } ) ;
		npkPlayerCount.setValue(INITIAL_PLAYER_COUNT) ;
		
		// Initialize stack of player name text boxes.
		m_koPlayerNames = new Stack<EditText>() ;
		for( int i = 0 ; i < INITIAL_PLAYER_COUNT ; i++ )
			pushPlayerNameTextBox() ;
				
		// Initialize double deck checkbox.
		CheckBox chkDoubleDeck = (CheckBox)(findViewById(R.id.chk_DoubleDeck)) ;
		chkDoubleDeck.setEnabled(false) ; // Don't enable until npkPlayerCount >= 6
		chkDoubleDeck.setChecked(false) ;
		
		// Tie the two UI elements together.
		npkPlayerCount.setOnValueChangedListener( new NumberPicker.OnValueChangeListener()
		{
			private int newListSize ;
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal)
			{
				this.newListSize = newVal ;
				
				// Adjust the status of the "Double deck" checkbox.
				if( newListSize > 6 )
				{
					CheckBox chkDoubleDeck = (CheckBox)(findViewById(R.id.chk_DoubleDeck)) ;
					chkDoubleDeck.setEnabled(false) ;
					chkDoubleDeck.setChecked(true) ;
				}
				else if( newListSize == 6 )
				{
					CheckBox chkDoubleDeck = (CheckBox)(findViewById(R.id.chk_DoubleDeck)) ;
					chkDoubleDeck.setEnabled(true) ;
					chkDoubleDeck.setChecked(false) ;
				}
				else // newListSize < 6
				{
					CheckBox chkDoubleDeck = (CheckBox)(findViewById(R.id.chk_DoubleDeck)) ;
					chkDoubleDeck.setEnabled(false) ;
					chkDoubleDeck.setChecked(false) ;
				}
				
				if( oldVal < newListSize )
				{ // push more player name text boxes onto the stack
					while( m_koPlayerNames.size() < newListSize )
						pushPlayerNameTextBox() ;
				}
				else if( newListSize < oldVal )
				{ // pop player name text boxes off the stack
					while( m_koPlayerNames.size() > newListSize )
						popPlayerNameTextBox() ;
				}
			}
		}) ;
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar()
	{
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
		{
			try { getActionBar().setDisplayHomeAsUpEnabled(true) ; }
			catch( NullPointerException npx )
			{ Log.e( LTAG, "Couldn't build action bar.", npx ) ; }
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_create_game: // User clicked create game button.
				this.createGameActionClicked() ;
				break ;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Preserves data from the game creation controls.
	 */
	@Override
	protected void onSaveInstanceState( Bundle oOutboundBundle )
	{
		super.onSaveInstanceState(oOutboundBundle) ;
		prepareDataAsGame() ;
		if( GameSingleton.hasInstance() )
			GameSingleton.clearInstance() ;
		GameSingleton.setInstance( m_oGame ) ;
	}
	
	protected void onRestoreInstanceState( Bundle oSavedBundle )
	{
		super.onRestoreInstanceState(oSavedBundle) ;
		m_oGame = GameSingleton.getInstance() ;
		
		int zPlayerCount = m_oGame.getPlayerCount() ;
		
		((NumberPicker)(findViewById(R.id.npk_PlayerCount))).setValue(zPlayerCount) ;
		
		CheckBox chkDoubleDeck = (CheckBox)(findViewById(R.id.chk_DoubleDeck)) ;
		chkDoubleDeck.setChecked(m_oGame.getRules().getRule(Game.OptionalRules.DOUBLEDECK)) ; 
		if( zPlayerCount == 6 ) chkDoubleDeck.setEnabled(true) ;
		else chkDoubleDeck.setEnabled(false) ;
		
		// Exactly zero or one of the following while() loops will execute.
		while( m_koPlayerNames.size() < zPlayerCount ) pushPlayerNameTextBox() ;
		while( m_koPlayerNames.size() > zPlayerCount ) popPlayerNameTextBox() ;
		
		for( int i = 0 ; i < zPlayerCount ; i ++ )
			m_koPlayerNames.get(i).setText( m_oGame.getPlayer(i).getName() ) ;
	}
	
	/**
	 * Dynamically adds an EditText control to the stack.
	 */
	private void pushPlayerNameTextBox()
	{
		EditText txtPlayerName = new EditText(this) ;
		txtPlayerName.setLayoutParams(
			new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) ) ;
		txtPlayerName.setInputType(InputType.TYPE_CLASS_TEXT) ;
		txtPlayerName.setHint( "(player name)" ) ;
		txtPlayerName.setEnabled(true) ;
		txtPlayerName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS) ; 
		txtPlayerName.setVisibility(View.VISIBLE) ;
		LinearLayout layout = (LinearLayout)(findViewById(R.id.lay_CreateGameActivity)) ;
		layout.addView(txtPlayerName) ;
		m_koPlayerNames.push(txtPlayerName) ;
		return ;
	}
	
	/**
	 * Dynamically removes the last EditText control from the stack.
	 * @return the name of the removed player
	 */
	private String popPlayerNameTextBox()
	{
		EditText txtPlayerName = m_koPlayerNames.pop() ;
		txtPlayerName.setVisibility(View.GONE) ;
		//FIGURE OUT HOW TO MAKE THIS SHIT WORK AND PREVENT A MEMORY LEAK
		//LinearLayout layout = (LinearLayout)(findViewById(R.id.lay_CreateGameActivity)) ;
		//layout.removeView(txtPlayerName) ;
		return txtPlayerName.getText().toString() ;
	}
	
	/**
	 * Target for the "Create Game" action.  Pushes game data to the
	 * scoreboard activity.
	 */
	public void createGameActionClicked()
	{
		prepareDataAsGame() ;

		// Sanitize the player names at this point to put default text in for
		// the names that are still empty.
		for( int i = 0 ; i < m_oGame.getPlayerCount() ; i++ )
			if( m_oGame.getPlayer(i).getName().equals("") )
				m_oGame.getPlayer(i).setName( new String( "Player " + Integer.toString(i+1) ) ) ;
		
		Intent oIntent = new Intent( this, ScoreTableActivity.class ) ;

		if( GameSingleton.hasInstance() )
			GameSingleton.clearInstance() ;
		GameSingleton.setInstance(m_oGame) ;
		
		startActivity( oIntent ) ;
				
		return ;
	}
	
	/**
	 * Gathers all information from the UI elements of this activity and packs
	 * them up into a Bundle object, either to save the activity's state, or
	 * push it toward another activity.
	 */
	private void prepareDataAsGame()
	{
		// Get the number of players from the number picker.
		int zPlayerCount = ((NumberPicker)(findViewById(R.id.npk_PlayerCount))).getValue() ;
		
		// Create an initial vector of players.
		Vector<Player> voPlayers = new Vector<Player>( zPlayerCount ) ;
		for( int i = 0 ; i < zPlayerCount ; i++ )
		{
			String sPlayerName = m_koPlayerNames.get(i).getText().toString() ;
			if( sPlayerName.equals(null) || sPlayerName.equals("") )
				voPlayers.add( new Player() ) ;
			else
				voPlayers.add( new Player(sPlayerName) ) ;
		}
		
		m_oGame = new Game(voPlayers) ;

		// Set optional rules based on values of checkboxes.
		m_oGame.getRules().setRule( Game.OptionalRules.DOUBLEDECK,
		      ((CheckBox)(findViewById(R.id.chk_DoubleDeck))).isChecked() ) ;
		m_oGame.getRules().setRule( Game.OptionalRules.OMNIBUS,
		      ((CheckBox)(findViewById(R.id.chk_Omnibus))).isChecked() ) ;
		m_oGame.getRules().setRule( Game.OptionalRules.HOOLIGAN,
		      ((CheckBox)(findViewById(R.id.chk_Hooligan))).isChecked() ) ;

		return ;
	}
}
