package net.zerobandwidth.android.apps.heartsscoreboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Provides static methods that return table cells for the scoring table.
 * @author Pasha
 *
 */
public class ScoreTableCellFactory
{
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	/**
	 * Provides one of the header cells with the specified string.
	 * @param oContext Context to which the cell should be added.
	 * @param sCellContents The string to be shown in the cell.
	 * @return A table cell formatted as a header for the score table.
	 */
	public static TextView getHeaderCell( Context oContext, String sContents )
	{
		TextView oCell = new TextView(oContext) ;
		
		oCell.setBackgroundColor(Color.BLACK) ;
		oCell.setClickable(false) ;
		oCell.setEnabled(false) ;
		oCell.setPadding( 10, 5, 10, 5 ) ;
		oCell.setText(sContents) ;
		oCell.setGravity( Gravity.CENTER | Gravity.FILL_VERTICAL ) ; 
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 )
			oCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER) ;
		oCell.setTextColor(Color.WHITE) ;
		oCell.setTypeface(Typeface.DEFAULT_BOLD) ;
		
		return oCell ;
	}
	
	/**
	 * Provides one of the left-column cells displaying round# and pass scheme.
	 * @param oContext Context to which the cell should be added.
	 * @param zIndex Round index for this row. (Round.getIndex())
	 * @param sPassScheme Pass scheme for this row (Round.getPassScheme() or
	 * Round.getPassSchemeShorthand())
	 * @return A table cell for the leftmost column, showing the round's index
	 * and pass scheme.
	 */
	public static TextView getPassSchemeCell( Context oContext, int zIndex, String sPassScheme )
	{
		TextView oCell = new TextView(oContext) ;
		
		oCell.setBackgroundColor(Color.BLACK) ;
		oCell.setClickable(false) ;
		oCell.setEnabled(false) ;
		oCell.setPadding( 10, 10, 10, 10 ) ;
		oCell.setGravity( Gravity.CENTER | Gravity.FILL_VERTICAL ) ;
		oCell.setTextColor(Color.WHITE) ;
		oCell.setTypeface(Typeface.DEFAULT_BOLD) ;
		
		StringBuffer sbText = new StringBuffer() ;
		sbText.append( "#" ) ;
		sbText.append( Integer.toString(zIndex) ) ;
		sbText.append( " : " ) ;
		sbText.append( sPassScheme ) ;
		oCell.setText( sbText.toString() ) ;
		
		return oCell ;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	/**
	 * Provides a cell to display a player's box score (but not scoring marks).
	 * @param oContext Context to which the cell should be added.
	 * @param zScore The player's box score.
	 * @param bDoubleDeck indicates whether the score is for a double-deck game
	 * @return A table cell containing a player's box score.
	 */
	public static TextView getBoxScoreCell( Context oContext, BoxScore oScore, boolean bDoubleDeck )
	{
		TextView oCell = new TextView(oContext) ;
		
		oCell.setBackgroundColor(Color.parseColor("#e8e8e8")) ;
		oCell.setClickable(true) ;
		oCell.setEnabled(true) ;
		oCell.setPadding( 10, 10, 10, 10 ) ;
		oCell.setGravity( Gravity.RIGHT | Gravity.FILL_VERTICAL ) ; 
		oCell.setText( Integer.toString(oScore.getScore(bDoubleDeck)) ) ;
		oCell.setTextColor(Color.BLACK) ;
		oCell.setTag(oScore) ;
		
		return oCell ;
	}
	
	/**
	 * Provides a table cell to display a player's box score marks.
	 * @param oContext Context to which the cell should be added.
	 * @param oScore the BoxScore for this cell
	 * @param bIsHTML Indicates whether the string contains HTML and should
	 * thus be parsed with Html.fromHtml().
	 * @return A table cell containing a player's box score marks.
	 */
	public static TextView getBoxScoreMarksCell( Context oContext, BoxScore oScore, boolean bIsHTML )
	{
		TextView oCell = new TextView(oContext) ;
		
		oCell.setBackgroundColor(Color.parseColor("#e8e8e8")) ;
		oCell.setClickable(true) ;
		oCell.setEnabled(true) ;
		oCell.setPadding( 10, 10, 10, 10 ) ;
		oCell.setGravity( Gravity.CENTER | Gravity.FILL_VERTICAL ) ; 
		oCell.setText( bIsHTML ? Html.fromHtml(oScore.getMarksHTML()) : oScore.getMarksString() ) ;
		oCell.setTextColor(Color.BLACK) ;
		oCell.setTag(oScore) ;
		
		return oCell ;
	}
	
	/**
	 * Provides a table cell displaying a validation indicator for a table row.
	 * @param oContext Context to which the cell should be added.
	 * @param zCrumbs margin of error across the current hand
	 * @return A table cell indicating the validity of a table row's scores.
	 */
	public static TextView getRowValidatorCell( Context oContext, int zCrumbs )
	{
		TextView oCell = new TextView(oContext) ;
		
		oCell.setClickable(false) ;
		oCell.setEnabled(false) ;
		oCell.setGravity( Gravity.CENTER | Gravity.FILL_VERTICAL ) ;
		oCell.setPadding( 5, 10, 5, 10 ) ;

		if( zCrumbs == 0 )
		{
			oCell.setText( "OK" ) ;
			oCell.setBackgroundColor(Color.GREEN) ;
			oCell.setTextColor(Color.BLACK) ; 
		}
		else
		{
			oCell.setText( zCrumbs > 0 ? "+" + Integer.toString(zCrumbs) : Integer.toString(zCrumbs) ) ;
			oCell.setBackgroundColor(Color.RED) ;
			oCell.setTextColor(Color.WHITE) ; 
		}

		return oCell ;
	}
	
	/**
	 * Calculates a background and text color for a box score total cell.
	 * @param zScore The player's total score.
	 * @param zCeiling The game's score ceiling.
	 * @return An array of six integers: RGB for background and foreground.
	 */
	public static int[] getScoreCellColors( int zScore, int zCeiling )
	{
		int azColors[] = { 232, 232, 232, 0, 0, 0 } ; // Default black on gray.
		
		if( zScore < -100 )
		{ // Hey, it could happen.
			azColors[1] = 255 ;
			azColors[0] = azColors[2] = 0 ;
			// Leave text black.
		}
		else if( zScore < 0 )
		{ // Use a shade of green.
			azColors[1] = 255 ;
			
			if( zCeiling + zScore < 0 )
				azColors[0] = azColors[2] = 0 ;
			else
				azColors[0] = azColors[2] = ((255*(zCeiling+zScore))/zCeiling) ;
			
			// Leave text black.
		}
		else if( zScore < zCeiling )
		{ // Provide a shade of gray that darkens from white at 0 to black
		  // at zCeiling.
			azColors[0] = azColors[1] = azColors[2]
			  = ((255*(zCeiling-zScore))/zCeiling) ;
			
			if( zScore >= (zCeiling-26) )
			{
				azColors[3] = azColors[4] = 255 ;
				azColors[5] = 0 ;
			}
			else if( zScore >= (int)(4*zCeiling/10) )
			{
				azColors[3] = azColors[4] = azColors[5] = 255 ;
			}
			// else leave text black.
		}
		else // zScore >= zCeiling
		{ // Use a shade of red in the background, and yellow text.
			azColors[0] = 128 ;
			azColors[1] = azColors[2] = 0 ;
			azColors[3] = azColors[4] = 255 ;
			azColors[5] = 0 ;
		}
		
		return azColors ;
	}
	
	/**
	 * As getScoreCellColors(), but returns two HTML color codes.
	 * The original implementation tried to take each color value, multiply it
	 * by 256, and add the next color value, to build up an integer that would
	 * be converted to the six-digit string. However, this strategy fails when
	 * leading segments are set to a single-hex-digit value, particularly zero.
	 * Thus each color code must be built up and concatenated to a string.
	 * @param zScore The player's total score.
	 * @param zCeiling The game's score ceiling.
	 * @return
	 */
	public static String[] getScoreCellColorCodes( int zScore, int zCeiling )
	{
		int azColors[] = getScoreCellColors( zScore, zCeiling ) ;
		String asCodes[] = new String[2] ;
		
		// Create the string for each color code.
		for( int c = 0 ; c < 2 ; c++ )
		{
			StringBuffer sbCode = new StringBuffer("#") ;
			for( int i = 0 ; i < 3 ; i++ )
			{ // Pad values that are less than 16 with leading zeroes.
				int zColor = azColors[ (3*c) + i ] ;   // Array index trickery.
				
				if( zColor == 0 )
					sbCode.append( "00" ) ;
				else if( zColor < 16 )
				{
					sbCode.append( "0" ) ;
					sbCode.append( Integer.toHexString(zColor) ) ;
				}
				else // zColor > 16
					sbCode.append( Integer.toHexString(zColor) ) ;
			}
			asCodes[c] = sbCode.toString() ;
		}
		
		return asCodes ;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	/**
	 * Provides a table cell displaying a player's total score for the game.
	 * The cell's background color and text color are adjusted to show how the
	 * player's score relates to the game's score ceiling.  The caller is
	 * responsible for precalculating the total score.
	 * @param oContext Context to which the cell should be added.
	 * @param zScore The player's total score.
	 * @param zCeiling The game's score ceiling.
	 * @return A table cell showing a player's total score.
	 */
	public static TextView getScoreTotalCell( Context oContext, int zScore, int zCeiling )
	{
		TextView oCell = new TextView(oContext) ;
		
		oCell.setClickable(false) ;
		oCell.setEnabled(false) ;
		oCell.setPadding( 10, 10, 10, 10 ) ;
		if( Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 )
			oCell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER) ;
		oCell.setGravity( Gravity.CENTER | Gravity.FILL_VERTICAL ) ;
		oCell.setText( Integer.toString(zScore) ) ;
		oCell.setTextSize(36) ;
		oCell.setTypeface(Typeface.DEFAULT_BOLD) ;
		
		// Calculate a fancy background color.
		// This will be darker as it approaches the game's point ceiling.
		int azColors[] = ScoreTableCellFactory.getScoreCellColors( zScore,  zCeiling ) ;
		
		oCell.setBackgroundColor( Color.rgb( azColors[0], azColors[1], azColors[2] ) ) ;
		oCell.setTextColor( Color.rgb( azColors[3], azColors[4], azColors[5] ) ) ;
		
		return oCell ;	
	}
	
	/**
	 * Returns a cell similar to the total score cell, but as a "running total"
	 * replacement for a box score cell.
	 * @param oContext The context to which the cell is added.
	 * @param oScore The box score bound to this cell.
	 * @param zScore The player's total score at this point in the game.
	 * @param zCeiling The game's score ceiling.
	 * @return A table cell formatted to show a player's running total.
	 */
	public static TextView getRunningTotalCell( Context oContext, BoxScore oScore, int zScore, int zCeiling )
	{
		TextView oCell = getScoreTotalCell( oContext, zScore, zCeiling ) ;
		oCell.setClickable(true) ;
		oCell.setEnabled(true) ;
		oCell.setTextSize(18) ;
		oCell.setGravity( Gravity.RIGHT | Gravity.FILL_VERTICAL ) ;
		oCell.setTag(oScore) ;
		
		return oCell ;
	}
	
	/**
	 * Lonely, unused constructor.
	 */
	public ScoreTableCellFactory()
	{
		// This constructor intentionally left blank.   =^.^=
	}

}
