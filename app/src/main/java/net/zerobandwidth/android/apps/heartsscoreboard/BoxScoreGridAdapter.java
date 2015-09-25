package net.zerobandwidth.android.apps.heartsscoreboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BoxScoreGridAdapter
 extends BaseAdapter
{
	private Context m_oContext ;
	private Game m_oGame ;
	private int m_zColumnCount ;
	
	// /// BaseAdapter overrides... ///////////////////////
	
	public BoxScoreGridAdapter()
	{
		m_oContext = null ;
		m_oGame = null ;
		m_zColumnCount = 0 ;
	}

	/**
	 * Calculates the number of items that should be in the adapter based on
	 * the size of the game to which it is bound.
	 */
	@Override
	public int getCount()
	{
		if( m_oGame == null ) // display nothing
			return 0 ;
		else if( m_oGame.getRoundCount() == 0 ) // show player names
			return m_oGame.getPlayerCount() + 2 ;
		else // show player names, scores, and total scores
			return (m_zColumnCount * (m_oGame.getRoundCount()+2) ) ;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null ;
	}

	/**
	 * Calculates an item's row number based on its index.
	 */
	@Override
	public long getItemId( int zIndex )
	{
		return (long)( zIndex / m_zColumnCount ) ;
	}

	/**
	 * Creates a view that displays one of the scoreboard table cells.
	 * This method figures out what sort of cell should be created based on its
	 * position within the adapter.  The first row is player names, and the
	 * last row shows total scores.  All rows in between show rounds.  For a
	 * row that shows rounds, the first column is always a pass scheme
	 * indicator, and the last cell indicates whether the scores for that round
	 * are correctly tabulated. 
	 */
	@Override
	public View getView( int zPos, View oOldView, ViewGroup oParent )
	{
		// For now, we're displaying everything as text.  Later, we might
		// change this up to display graphical marks instead, thus producing
		// a mix of text views and image views.
		
		TextView oCellView ;
		if( oOldView == null )
		{
			oCellView = new TextView(m_oContext) ;
		}
		else oCellView = (TextView)oOldView ;
		
		// Figure out what the text should be based on the intended position.
		// The width of the table will be the number of players plus one cell
		// for the pass descriptor and another cell for a round score validity
		// indicator.  The height of the table will be the number of rounds in
		// the game, plus one row for the player names, and another row for the
		// total scores.  This series of stacked logical tests proceeds from
		// position zero up to the maximum.  Note that the docs for the
		// AdapterView class seem to indicate that positions are zero-based.
		
		if( zPos == 0 )
		{ // The position is for the blank space at the top-left.
			oCellView.setText("") ;
			oCellView.setEnabled(false) ;
			oCellView.setBackgroundColor(Color.BLACK) ;
		}
		else if( zPos < m_zColumnCount - 1 )
		{ // The position is one of the player names.
			oCellView.setText( m_oGame.getPlayer(zPos-1).getName() ) ;
			oCellView.setEnabled(false) ;
			oCellView.setBackgroundColor(Color.BLACK) ;
			oCellView.setTextColor(Color.WHITE) ;
			oCellView.setTypeface(Typeface.DEFAULT_BOLD) ;
		}
		else if( zPos == m_zColumnCount - 1 )
		{ // The position is of the blank header above the validation column.
			oCellView.setText("") ;
			oCellView.setEnabled(false) ;
			oCellView.setBackgroundColor(Color.BLACK) ;
		}
		else if( zPos < ( (m_oGame.getRoundCount()+1) * m_zColumnCount ) )
		{ // You're either a pass scheme indicator, a box score, or a
		  // round validity indicator.
			
			// Subtract 1 because we have to ignore the header row.
			int zRound = (int)( (zPos/m_zColumnCount) - 1 ) ;
			
			int zColumn = zPos % m_zColumnCount ;
			
			if( zColumn == 0 )
			{ // You're a pass scheme indicator.

				StringBuffer sbText = new StringBuffer() ;
				sbText.append( "#" ) ;
				sbText.append( Integer.toString( m_oGame.getRound(zRound).getIndex() + 1 ) ) ;
				sbText.append( ": " ) ;
				sbText.append( Html.fromHtml( m_oGame.getRound(zRound).getPassScheme() ) ) ;
				oCellView.setText( sbText.toString() ) ;
				
				oCellView.setEnabled(false) ;
				oCellView.setBackgroundColor(Color.BLACK) ;
				oCellView.setTextColor(Color.WHITE) ;
				oCellView.setTypeface(Typeface.DEFAULT_BOLD) ;
			}
			else if( zColumn < m_zColumnCount - 1 )
			{ // You're a box score.
				
				// Remember to subtract 1 from the column to account for the
				// cell displaying the round's pass scheme.
				oCellView.setText( m_oGame.getRound(zRound).getBoxScore(zColumn-1).toString() ) ;
				oCellView.setEnabled(true) ;
				oCellView.setBackgroundColor(Color.parseColor("#e8e8e8")) ;
				oCellView.setTextColor(Color.BLACK) ;
			}
			else
			{ // You're a score validation indicator.
				if( m_oGame.getRound(zRound).validateTotal(m_oGame.getPointsPerRound()) == 0 )
				{
					oCellView.setText( "OK" ) ;
					oCellView.setEnabled(false) ;
					oCellView.setBackgroundColor(Color.GREEN) ;
				}
				else
				{
					oCellView.setText( "Bad" ) ;
					oCellView.setEnabled(false) ;
					oCellView.setBackgroundColor(Color.RED) ;
					oCellView.setTextColor(Color.WHITE) ; 
				}
			}
		}
		else
		{ // You're one of the cells in the totals column.
			int zColumn = zPos % m_zColumnCount ;
			
			if( zColumn == 0 )
			{ // You're the row heading.
				oCellView.setText("TOTAL") ;
				oCellView.setEnabled(false) ;
				oCellView.setBackgroundColor(Color.BLACK) ;
				oCellView.setTextColor(Color.WHITE) ;
			}
			else if( zColumn < m_zColumnCount - 1 )
			{ // You're a player's total score.
				int zTotal = 0 ;
				for( int i = 0 ; i < m_oGame.getRoundCount() ; i++ )
					zTotal += m_oGame.getRound(i).getBoxScore(zColumn=1).getScore() ;
				
				oCellView.setText( Integer.toString(zTotal) ) ;
				oCellView.setEnabled(false) ;
				oCellView.setBackgroundColor(Color.parseColor("#808080")) ;
				oCellView.setTextColor(Color.WHITE) ;
			}
			else
			{ // You're in the validity indicator column but you have nothing to do.
				oCellView.setText("") ;
				oCellView.setEnabled(false) ;
				oCellView.setBackgroundColor(Color.BLACK) ;
			}
		}
		
		oCellView.setPadding( 2,  10,  2,  10 ) ;
		
		return oCellView ;
	}

	// /// Custom functions... ////////////////////////////
	
	/**
	 * Presets members of the adapter.
	 */
	public BoxScoreGridAdapter( Context oContext, Game oGame )
	{
		m_oContext = oContext ;
		m_oGame = oGame ;
		m_zColumnCount = oGame.getPlayerCount() + 2 ;
	}

	/**
	 * Accessor for the cell's context.
	 * @return the Context object for this view
	 */
	public Context getContext()
	{ return m_oContext ; }
	
	/**
	 * Mutator for the cell's context.
	 * @param oContext The cell's intended context.
	 * @return the newly-updated context
	 */
	public Context setContext( Context oContext )
	{ m_oContext = oContext ; return m_oContext ; }
	
	/**
	 * Accessor for game associated with the adapter.
	 * @return a Game object
	 */
	public Game getGame()
	{ return m_oGame ; }
	
	/**
	 * Mutator for the game associated with the adapter.
	 * This operation forces a recalculation of the adapter's
	 * column count.
	 * @param oGame The new game to which this adapter is bound.
	 * @return the newly-updated Game object
	 */
	public Game setGame( Game oGame )
	{
		m_oGame = oGame ;
		m_zColumnCount = oGame.getPlayerCount() + 2 ;
		return m_oGame ;
	}
	
	/**
	 * Accessor for column count.
	 * @return the column count for the adapter.
	 */
	public int getColumnCount()
	{ return m_zColumnCount ; }
	
	/**
	 * Mutator-ish method for column count.
	 * Because column count is always derived from the number of players in
	 * the game plus a constant, this mutator doesn't allow specification of
	 * a specific value.
	 * @return the new column count
	 */
	public int setColumnCount()
	{
		m_zColumnCount = m_oGame.getPlayerCount() + 2 ;
		return m_zColumnCount ;
	}
}
