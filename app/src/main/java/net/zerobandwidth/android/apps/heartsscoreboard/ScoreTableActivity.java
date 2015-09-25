package net.zerobandwidth.android.apps.heartsscoreboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreTableActivity
 extends Activity
 implements OnClickListener, BoxScoreEditorDialogFragment.Listener,
  ScoreTableBackAlertDialog.Listener
{
	/** Logging tag. */
	public static final String LTAG = "ScoreTableActivity" ;
	
	/** A constant name for the activity's display mode. */
	public static final String Z_DISPLAYMODE =
	 "net.zerobandwidth.android.apps.heartsscoreboard.ScoreTableActivity.zDisplayMode" ;
	
	/**
	 * The game being tracked by the activity.
	 * This is initialized as null explicitly, because the activity's behavior
	 * depends on it.
	 */
	private Game m_oGame = null ;
	
	/**
	 * Specifies the current display mode of the activity.  BOXSCORES mode will
	 * show each box score's individual value.  TOTALS will instead show the
	 * player's running total in each round.
	 */
	private Game.DisplayMode m_eDisplayMode ;
	
	@Override
	protected void onCreate( Bundle oState )
	{
		super.onCreate(oState) ;
		setContentView(R.layout.activity_score_table) ;
		// Show the Up button in the action bar.
		setupActionBar() ;
		
		if( m_oGame == null )
        { // Game to be displayed has not yet been loaded.
			m_oGame = GameSingleton.getInstance() ;
        	this.setTitle( "Scoreboard for " + m_oGame.getID() ) ;
        }
		
		if( oState != null && oState.containsKey(Z_DISPLAYMODE) )
			m_eDisplayMode = Game.DisplayMode.VALUES[oState.getInt(Z_DISPLAYMODE)] ;
		else
			m_eDisplayMode = Game.DisplayMode.BOXSCORES ;
		
		drawScoreTable() ;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{
		getActionBar().setDisplayHomeAsUpEnabled(true) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score_table, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem oItem )
	{
		switch( oItem.getItemId() )
		{
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this) ;
			return true ;
		case R.id.action_add_round:
			this.onAddRoundActionClicked() ;
			break ;
		case R.id.action_dump_html:
			this.dumpToHTML() ;
			break ;
		case R.id.action_switch_table_mode:
			if( m_eDisplayMode == Game.DisplayMode.BOXSCORES )
			{ // toggle to total display mode
				m_eDisplayMode = Game.DisplayMode.TOTALS ;
				oItem.setIcon(R.drawable.icon_table_sum) ;
				oItem.setTitle(getString(R.string.act_TableMode_Sum)) ;
			}
			else // m_ezDisplayMode == DisplayMode.TOTALS
			{ // toggle to box score mode
				m_eDisplayMode = Game.DisplayMode.BOXSCORES ;
				oItem.setIcon(R.drawable.icon_table_row) ;
				oItem.setTitle(getString(R.string.act_TableMode_Box)) ; 
			}
			((TableLayout)(findViewById(R.id.tbl_ScoreTableLayout))).removeAllViews() ;
			drawScoreTable() ;
			break ;
		}
		return super.onOptionsItemSelected(oItem) ;
	}
	
	protected void onSaveInstanceState( Bundle oOutboundBundle )
	{
		super.onSaveInstanceState(oOutboundBundle) ;
		if( GameSingleton.hasInstance() )
			GameSingleton.clearInstance() ;
		GameSingleton.setInstance(m_oGame) ;
		oOutboundBundle.putInt( Z_DISPLAYMODE, m_eDisplayMode.value() ) ;
	}
	
	protected void onRestoreInstanceState( Bundle oSavedBundle )
	{
		super.onRestoreInstanceState(oSavedBundle) ;
		if( m_oGame == null )
			m_oGame = GameSingleton.getInstance() ;
		m_eDisplayMode = Game.DisplayMode.VALUES[oSavedBundle.getInt(Z_DISPLAYMODE)] ;
	}
	
	/**
	 * Intercept the Back button and prevent it from killing the current game.
	 */
	@Override
	public void onBackPressed()
	{
		ScoreTableBackAlertDialog d = new ScoreTableBackAlertDialog() ;
		d.show( getFragmentManager(), "ScoreTableBackAlertDialog" ) ;
	}
	
	/**
	 * Handles a click on the "add round" action in the menu bar.
	 * Adds a new round to the current game, and requests a redraw from the
	 * display adapter. 
	 */
	private void onAddRoundActionClicked()
	{
		m_oGame.addRound( new Round( m_oGame.getPlayerCount(), m_oGame.getRoundCount() ) ) ;
		TableLayout oLayout = (TableLayout)(findViewById(R.id.tbl_ScoreTableLayout)) ;
		// TODO Surely, there is a better way to redraw the screen than this.
		oLayout.removeAllViews() ;
		this.drawScoreTable() ;
		return ;
	}
	
	/**
	 * Draws the entire TableView, showing all the various scores and marks.
	 */
	private void drawScoreTable()
	{
		TableLayout tblScoreTable = (TableLayout)(findViewById(R.id.tbl_ScoreTableLayout)) ;
		
		// Prepare to total up the players' scores for the final row draw.
		int azPlayerScores[] = new int[m_oGame.getPlayerCount()] ;

		// This will be handy.
		boolean bDoubleDeck = m_oGame.getRules().getRule(Game.OptionalRules.DOUBLEDECK) ;

		// This will be handy for repetitive tasks later.
		TableRow.LayoutParams oDoubleCellParams = new TableRow.LayoutParams() ;
		oDoubleCellParams.span = 2 ;
		
		// Draw the top row, which includes a blank cell, player names,
		// and then another blank cell.
		
		TableRow rowNames = new TableRow(this) ;
		
		rowNames.addView( ScoreTableCellFactory.getHeaderCell(this,"") ) ;
		
		for( int i = 0 ; i < m_oGame.getPlayerCount() ; i++ )
		{
			rowNames.addView(
					ScoreTableCellFactory.getHeaderCell( this, m_oGame.getPlayer(i).getName() ),
					oDoubleCellParams ) ;
		}
		
		rowNames.addView( ScoreTableCellFactory.getHeaderCell(this,"") ) ;
		
		tblScoreTable.addView( rowNames ) ;

		// Draw each of the rows corresponding to rounds of the game.
		
		for( int zRound = 0 ; zRound < m_oGame.getRoundCount() ; zRound++ )
		{
			TableRow rowRound = new TableRow(this) ;
			Round oRound = m_oGame.getRound(zRound) ;
			
			// Draw the row header, which includes the round number and pass
			// scheme indicator.
			rowRound.addView( ScoreTableCellFactory.getPassSchemeCell( this, zRound + 1, m_oGame.getRound(zRound).getPassScheme() ) ) ;
			
			// Draw each of the box score cell pairs.
			
			for( int zBox = 0 ; zBox < oRound.getBoxScoreCount() ; zBox++ )
			{
				BoxScore oScore = oRound.getBoxScore(zBox) ;
				azPlayerScores[zBox] += oScore.getScore(bDoubleDeck) ;
				
				TextView txtScore ;
				
				// Choose the factory method to use for this cell based on the
				// table's current display mode.
				if( m_eDisplayMode == Game.DisplayMode.BOXSCORES )
					txtScore = ScoreTableCellFactory.getBoxScoreCell( this, oScore, bDoubleDeck ) ;
				else // m_eDisplayMode == Game.DisplayMode.TOTALS
					txtScore = ScoreTableCellFactory.getRunningTotalCell( this, oScore, azPlayerScores[zBox], ( bDoubleDeck ? 150 : 100 ) ) ;
				
				txtScore.setOnClickListener(this) ;				
				TextView txtMarks = ScoreTableCellFactory.getBoxScoreMarksCell( this, oScore, true ) ;
				txtMarks.setOnClickListener(this) ;
				
				rowRound.addView( txtScore ) ;
				rowRound.addView( txtMarks ) ;
			}
			
			// Draw the validation cell at the end of the row.
			rowRound.addView( ScoreTableCellFactory.getRowValidatorCell( this, oRound.validateTotal(m_oGame.getPointsPerRound()) ) ) ;
			
			tblScoreTable.addView( rowRound ) ;
		}
		
		// Draw the bottom row, which includes a cell marked "TOTAL",
		// cells for each player's total score, and a final blank cell.
		
		TableRow rowTotals = new TableRow(this) ;
		
		rowTotals.addView( ScoreTableCellFactory.getHeaderCell( this, "TOTALS" ) ) ;
		
		for( int i = 0 ; i < m_oGame.getPlayerCount() ; i++ )
		{
			rowTotals.addView(
					ScoreTableCellFactory.getScoreTotalCell( this,  azPlayerScores[i],  ( bDoubleDeck ? 150 : 100 ) ),
					oDoubleCellParams ) ;
		}
		
		rowTotals.addView( ScoreTableCellFactory.getHeaderCell( this, "" ) ) ;

		tblScoreTable.addView( rowTotals ) ;
		
		Log.d( LTAG, "drawScoreTable() dumping game to logs" ) ;
		m_oGame.writeToLogs(Log.DEBUG) ; 
		
		return ;
	}
	
	/**
	 * Dumps the currently-displayed table to an HTML file with a name
	 * corresponding to the game's ID and activity's current display mode.
	 */
	private void dumpToHTML()
	{
		String sFilepath = Environment.getExternalStoragePublicDirectory("Documents").getPath() + "/HeartsScoreboard/" ;
		String sFilename = m_oGame.getID().substring(5) + "-" + ( m_eDisplayMode == Game.DisplayMode.BOXSCORES ? "box" : "totals" ) + ".html" ;
		String sStatus = "" ;
		boolean bFileExisted = false ;
		File fOutputFile ;
		
		try
		{ // to create the save file directory if it doesn't exist.
			File fPath = new File( sFilepath ) ;
			if( ! fPath.exists() )
				if( ! fPath.mkdirs() )
					throw new IOException() ;
		}
		catch( Exception e )
		{
			sStatus = "Error: Couldn't create directory " + sFilepath ;
			Toast.makeText(this, sStatus, Toast.LENGTH_LONG).show() ;
			return ;
		}
		
		try
		{ // to delete any existing dump file of the same name.
			fOutputFile = new File( sFilepath + sFilename ) ;
			if( fOutputFile.exists() )
			{
				bFileExisted = true ;
				fOutputFile.delete() ;
			}
		}
		catch( Exception e )
		{
			sStatus = "Error: Couldn't delete existing file " + sFilepath + sFilename ;
			Toast.makeText(this, sStatus, Toast.LENGTH_LONG).show() ;
			return ;
		}
		
		StringBuffer sbTable = new StringBuffer() ;
		sbTable.append( "<html>\n<body>\n" ) ;
		sbTable.append( m_oGame.toHTMLTable(m_eDisplayMode) ) ;
		sbTable.append( "</body>\n</html>\n" ) ;
		
		try { fOutputFile.createNewFile() ; }
		catch( Exception e )
		{
			sStatus = "Error: Couldn't create file at " + sFilepath + sFilename + "\n" + e.toString() ;
			Toast.makeText(this, sStatus, Toast.LENGTH_LONG).show() ;
			return ;
		}
		
		FileWriter fwOut ;
		try { fwOut = new FileWriter( fOutputFile ) ; }
		catch( Exception e )
		{
			sStatus = "Error: Couldn't open file stream at " + sFilepath + sFilename + ".\n" + e.toString() ;
			Toast.makeText(this, sStatus, Toast.LENGTH_LONG).show() ;
			return ;
		}
		
		try { fwOut.write( sbTable.toString() ) ; fwOut.close() ; }
		catch( Exception e )
		{
			sStatus = "Error: Couldn't write to file at " + sFilepath + sFilename + ".\n" + e.toString() ;
			Toast.makeText(this, sStatus, Toast.LENGTH_LONG).show() ;
			return ;			
		}
		
		try { fwOut.close() ; }
		catch( Exception e )
		{
			sStatus = "Error: Couldn't close file stream at " + sFilepath + sFilename + ".\n" + e.toString() ;
			Toast.makeText(this, sStatus, Toast.LENGTH_LONG).show() ;
			return ;			
		}
		
		if( bFileExisted )
			sStatus = "Game saved over " + sFilepath + sFilename ;
		else
			sStatus = "Game saved to " + sFilepath + sFilename ;
		
		Toast.makeText(this, sStatus, Toast.LENGTH_SHORT).show() ;
		
		return ;
	}
	
	/**
	 * Catches events from clicked box score cells.
	 */
	public void onClick( View oClicked )
	{
		BoxScore oScore = (BoxScore)oClicked.getTag() ;
		BoxScoreEditorDialogFragment fragBoxScoreDialog = new BoxScoreEditorDialogFragment() ;
		fragBoxScoreDialog.setBoxScore(oScore) ;
		fragBoxScoreDialog.setRules(m_oGame.getRules()) ;
		fragBoxScoreDialog.show(getFragmentManager(), "fragBoxScore") ;
		// The post-dialog updates are handled by onScoreChanged() below.
	}
	
	/**
	 * Catches the event passed back from the box score editor dialog.
	 */
	public void onScoreChanged( BoxScoreEditorDialogFragment fragDialog )
	{
		// There are some sophisticated things we can do with score validation
		// when the editor comes back, based on the marks that are set in the
		// new box score.
		
		BoxScore oScore = fragDialog.getBoxScore() ;
		if( oScore.getMark(BoxScore.Mark.FULLMOON) )
		{ // Each other score in that row should be marked as a full moon victim.
			Round oRound = m_oGame.getRoundOf(oScore) ;
			if( oRound != null )
			{ // Set the "Full Moon Victim" mark in every other box score.
				for( int i = 0 ; i < oRound.getBoxScoreCount() ; i++ )
				{
					BoxScore oOtherScore = oRound.getBoxScore(i) ;
					if( oOtherScore == oScore ) continue ;
					else oOtherScore.setMark( BoxScore.Mark.FULLMOONVICTIM, true ) ;
				}
			}
		}
		else if( oScore.getMark(BoxScore.Mark.HALFMOON) )
		{ // Each other score in that row should be marked as a half moon victim.
			Round oRound = m_oGame.getRoundOf(oScore) ;
			if( oRound != null )
			{ // Set the "Half Moon Victim" mark in every other box score.
				for( int i = 0 ; i < oRound.getBoxScoreCount() ; i++ )
				{
					BoxScore oOtherScore = oRound.getBoxScore(i) ;
					if( oOtherScore == oScore ) continue ;
					else oOtherScore.setMark( BoxScore.Mark.HALFMOONVICTIM, true ) ;
				}
			}
		}
		
		// So I guess this is why people say that Java is inefficient.  Here
		// we are having to destroy the entire view and recreate it just to get
		// anything to update on the screen in real time.
		TableLayout tblLayout = (TableLayout)findViewById(R.id.tbl_ScoreTableLayout) ;
		tblLayout.removeAllViews() ;
		drawScoreTable() ;
	}
	
	/**
	 * Catches the event passed back from the back button catcher.
	 */
	public void onBackButtonConfirmed()
	{
		super.onBackPressed() ;
	}
}
