package net.zerobandwidth.android.apps.heartsscoreboard;

import java.util.Vector;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Round
 implements Parcelable
{
	
	// Constant data keys for packing a Game as a Bundle
	public static final String Z_BOXSCORES =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Round.zBoxScores" ;
	public static final String AO_BOXSCORES =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Round.aoBoxScores" ;
	public static final String Z_INDEX =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Round.zIndex" ;
	public static final String Z_SIZE =
	 "net.zerobanwdidth.android.apps.heartsscoreboard.Round.zSize" ;
	
	public static final Parcelable.Creator<Round> CREATOR
	 = new Parcelable.Creator<Round>()
	{
		public Round createFromParcel( Parcel oParcel )
		{ return new Round(oParcel) ; }
		
		public Round[] newArray( int zSize )
		{ return new Round[zSize] ; }
	};
		
	/** Index of the round within the game. */
	private int m_zIndex ;
	
	/**
	 * Intended number of players.  This is different from m_voBoxScores.size()
	 * because we might not have actually created the box score objects yet.
	 */
	private int m_zSize ;
	
	/** Vector of box scores.  Dim equals number of players. */
	private Vector<BoxScore> m_voBoxScores ;
	
	/**
	 * Constructor
	 * @param zSize the number of players
	 * @param zIndex the index of the round within a Game
	 */
	public Round( int zSize, int zIndex )
	{
		m_zSize = zSize ;
		m_voBoxScores = new Vector<BoxScore>(zSize) ;
		// Saving space on the front end just got too complicated, so the
		// constructor just creates a bunch of blank box scores upfront when
		// the Round object is created.
		for( int i = 0 ; i < zSize ; i++ )
			m_voBoxScores.add( new BoxScore() ) ;
		m_zIndex = zIndex ;		
	}
	
	/**
	 * Reconstructs a Round object based on keys found in an Android Bundle
	 * object.  This constructor assumes that the Bundle was created using the
	 * toBundle() method of the Round class.
	 * @param oBundle the Bundle from which to extract the data.
	 */
	public Round( Bundle oBundle )
	{
		m_zIndex = oBundle.getInt(Round.Z_INDEX) ; 
		m_zSize = oBundle.getInt(Round.Z_SIZE) ;
		
		int zBoxScores = oBundle.getInt(Round.Z_BOXSCORES) ;
		if( zBoxScores > 0 && oBundle.containsKey(Round.AO_BOXSCORES) )
		{
			m_voBoxScores = new Vector<BoxScore>( zBoxScores ) ;
			Parcelable aoBoxScores[] = oBundle.getParcelableArray(Round.AO_BOXSCORES) ;
			for( int i = 0 ; i < zBoxScores ; i++ )
				m_voBoxScores.add( i, (BoxScore)(aoBoxScores[i]) ) ;
		}
	}
	
	/**
	 * Reconstructs a Round object based on data found in an Android Parcel.
	 * The constructor assumes that the Parcel was created using the
	 * writeToParcel() method of the Round class.
	 * @param oParcel the Parcel from which to extract the data
	 */
	public Round( Parcel oParcel )
	{
		m_zIndex = oParcel.readInt() ;
		m_zSize = oParcel.readInt() ;
		
		int zBoxScores = oParcel.readInt() ;
		if( zBoxScores > 0 )
		{
			BoxScore aoBoxScores[] = new BoxScore[zBoxScores] ;
			oParcel.readTypedArray( aoBoxScores,  BoxScore.CREATOR ) ;
			m_voBoxScores = new Vector<BoxScore>( zBoxScores ) ;
			for( int i = 0 ; i < zBoxScores ; i++ )
				m_voBoxScores.add( i, aoBoxScores[i] ) ;
		}
	}
	
	/**
	 * Indicates that the given box score belongs to this round.
	 * @param oSoughtScore The sought box score object.
	 * @return True if the object is found in this round.
	 */
	public boolean contains( BoxScore oSoughtScore )
	{
		for( BoxScore oRoundScore : m_voBoxScores )
		{
			if( oRoundScore == oSoughtScore )
				return true ;
		}
		
		return false ;
	}
	
	/** (Parcelable) */
	public int describeContents() { return 0 ; }
	
	/**
	 * Accessor for the round's index within the current game.  This should be
	 * in sync with the round's index within the m_voRounds member of the Game
	 * object.
	 * @return The index of the round within a Game.
	 */
	public int getIndex() { return m_zIndex ; }
	
	/**
	 * Mutator to set the round's index in the current game.
	 * @param zIndex The index of the round within a Game.
	 * @return The index that was just set.
	 */
	public int setIndex( int zIndex )
	{
		m_zIndex = zIndex ;
		return m_zIndex ;
	}
	
	/**
	 * Accessor for the intended size of the round, i.e., the intended player
	 * count.  This may be different from the size of the vector of box scores.
	 * @return the intended number of players in this round.
	 */
	public int getSize()
	{
		return m_zSize ;
	}
	
	/**
	 * Accessor for the length of the box score vector.
	 * This can be used to verify that the round actually contains box scores,
	 * before trying (and possibly failing) to access them.  
	 * @return the number of box scores actually contained in the vector, or -1
	 * if no box scores have been added.
	 */
	public int getBoxScoreCount()
	{
		if( m_voBoxScores == null ) return -1 ;
		else return m_voBoxScores.size() ;
	}
	
	/**
	 * Returns the box score at the given index.
	 * @param zIndex The index of the player within the vector whose score
	 * should be fetched.
	 * @return a box score object.
	 */
	public BoxScore getBoxScore( int zIndex )
	{
		if( zIndex < m_voBoxScores.size() )
			return m_voBoxScores.get(zIndex) ;
		else
			throw new ArrayIndexOutOfBoundsException( "Can't get box score " + Integer.toString(zIndex) ) ;
	}
	
	/**
	 * Writes a box score at the specified index.
	 * @param zIndex The column in this round in which to record the box score.
	 * @param oScore The BoxScore object, representing the score itself and 
	 * all scoring marks (such as "took the queen", "shot the moon", etc.
	 * @return the box score of the specified player in the vector
	 */
	public BoxScore setBoxScore( int zIndex, BoxScore oScore )
	{
		m_voBoxScores.set( zIndex, oScore ) ;
		return m_voBoxScores.get(zIndex) ;
	}
	
	/**
	 * Accessor for the number of players in this round of play.
	 * @return a count of the players in this round.
	 */
	public int getPlayerCount() { return m_zSize ; }
	
	/**
	 * Returns a string indicating the pass scheme for the round, based on the
	 * number of players and the round's index in the current game.
	 * @return a human-readable string describing the pass scheme.
	 */
	public String getPassScheme()
	{ // Note that m_zIndex needs a +1 because the vector's indices are 0-based.
		int zMod = (m_zIndex+1) % m_zSize ;
		
		if( zMod == 0 ) return "Hold" ;
		else if( m_zSize % 2 == 0 && zMod == m_zSize - 1 )
			return "Across" ;
		else if( zMod % 2 == 1 ) // odd hands pass left
			return new String( "Left " + Integer.toString( (zMod/2) + 1 ) ) ;
		else // zMod % 2 == 0 // even hands pass right
			return new String( "Right " + Integer.toString( zMod/2 ) ) ;
	}
	
	/**
	 * Like getPassScheme(), except that this method returns a shorthand
	 * indication of the pass scheme using HTML entities.
	 * @return a string containing a shorthand notation for the pass scheme.
	 */
	public String getPassSchemeShorthand()
	{ // Note that m_zIndex needs a +1 because the vector's indices are 0-based.
		int zMod = (m_zIndex+1) % m_zSize ;
		
		if( zMod == 0 ) return "&mdash;" ;
		else if( m_zSize % 2 == 0 && zMod == m_zSize - 1 )
			return "&uarr;&darr;" ;
		else if( zMod % 2 == 1 ) // odd hands pass left
			return new String( "&larr;" + Integer.toString( (zMod/2) + 1 ) ) ;
		else // zMod % 2 == 0 // even hands pass right
			return new String( Integer.toString( zMod/2 ) + "&rarr;" ) ;		
	}

	/**
	 * Validates whether the sum of box scores in this round is equal to an
	 * even multiple of the indicated point total.  The method expects its
	 * caller to figure out what the valid point total should be.  This is
	 * generally based on the number of players and whether point cards had
	 * to be removed from the deck to make the player count work.
	 * @param zTarget the expected number of points in a given round
	 * @return true if the value is on target, or false otherwise
	 */
	public int validateTotal( int zTarget )
	{
		int zSum = 0 ;
		int zPlayers = m_voBoxScores.size() ;
		
		for( int i = 0 ; i < zPlayers ; i++ )
		{
			// If any of the cells has a moonshot mark in it, then we will
			// assume that the rest of the app has properly set the moonshot
			// values, and we'll just return a zero.
			
			BoxScore oScore = m_voBoxScores.get(i) ;
			
			if( oScore.getMark(BoxScore.Mark.FULLMOON)
			 || oScore.getMark(BoxScore.Mark.NEWMOON)
			 || oScore.getMark(BoxScore.Mark.HALFMOON) )
				return 0 ;
			
			zSum += oScore.getScore() ;
		}
		
		int zCrumbs = zSum % zTarget ;
		
		// If the sum has not yet reached the target, or the remainder is more
		// than half of the target, then we indicate that we are SHORT some
		// number of points, by returning a negative number.
		if( zSum < zTarget || zCrumbs > zTarget / 2 )
			return zCrumbs - zTarget ;
		else // return a positive number, indicating an excess.
			return zCrumbs ;
	}
	
	/**
	 * Standard toString() method.
	 */
	public String toString()
	{
		return "I haven't done this yet!" ;
	}
	
	/**
	 * Outputs the contents of this round of the game as a row in an HTML
	 * table.  The first cell describes the passing scheme; the subsequent
	 * cells show the score and scoring marks for each player.
	 * @param bDoubleDeck indicates whether the game is a double-deck game.
	 * @param eDisplayMode the current display mode
	 * @param azRunningTotals Supplies running totals for the previous
	 * round. This method will update these totals so that the parent method
	 * can also render a row of total scores.
	 * @return a String containing a fully-formatted HTML table row showing the
	 * statistics for this round of the game.
	 */
	public String toHTMLTableRow( boolean bDoubleDeck, Game.DisplayMode eDisplayMode, int azRunningTotals[] )
	{
		StringBuffer sbValue = new StringBuffer() ;
		sbValue.append( " <tr>\n" ) ;
		sbValue.append( this.getPassSchemeTableCell() ) ;
		for( int i = 0 ; i < m_zSize ; i++ )
		{
			if( i >= m_voBoxScores.size() )
			{ // Set a blank box score into the vector before rendering.
				m_voBoxScores.add( i, new BoxScore() ) ;
			}

			BoxScore oScore = m_voBoxScores.get(i) ;
			
			if( eDisplayMode == Game.DisplayMode.BOXSCORES )
				sbValue.append( oScore.toHTMLBoxScore(bDoubleDeck) ) ;
			else if( eDisplayMode == Game.DisplayMode.TOTALS )
				sbValue.append( oScore.toHTMLRunningTotal( bDoubleDeck, azRunningTotals[i] ) ) ;
			else throw new IllegalArgumentException( "HOW HAPPEN?!" ) ;
			
			azRunningTotals[i] += oScore.getScore(bDoubleDeck) ;
		}
		sbValue.append( " </tr>" ) ;
		
		return sbValue.toString() ;
	}
	
	/**
	 * Generates the pass scheme cell for HTML output.
	 */
	private String getPassSchemeTableCell()
	{
		StringBuffer sbValue = new StringBuffer() ;
		sbValue.append( "  <th style=\"border:none;background-color:#000000;color:#ffffff;font-weight:bold;padding:2px 1em;text-align:center;\">" ) ;
		sbValue.append( this.getPassSchemeShorthand() ) ;
		sbValue.append( "</th>\n" ) ;
		return sbValue.toString() ;
	}
	
	/**
	 * Translates a Round into an Android Bundle.
	 * @param oBundle An existing Bundle object, or a null reference which
	 * will trigger creation of a new Bundle.
	 * @return A Bundle that is equivalent to this Round.
	 */
	public Bundle toBundle( Bundle oBundle )
	{
		if( oBundle == null ) oBundle = new Bundle() ;
		else oBundle.clear() ;
		
		oBundle.putInt( Round.Z_INDEX,  m_zIndex ) ;
		
		oBundle.putInt( Round.Z_SIZE, m_zSize ) ;
		
		int zBoxScores = m_voBoxScores.size() ;
		oBundle.putInt( Round.Z_BOXSCORES, m_voBoxScores.size() ) ;
		
		if( zBoxScores > 0 )
		{
			BoxScore aoBoxScores[] = new BoxScore[zBoxScores] ;
			m_voBoxScores.toArray(aoBoxScores) ;
			oBundle.putParcelableArray( Round.AO_BOXSCORES, aoBoxScores ) ;
		}
		
		return oBundle ;
	}
	
	/**
	 * Translates a Round into an Android Parcel.
	 * @param oParcel a reference to the Parcel object provided by the platform.
	 * @param zFlags ignored
	 */
	public void writeToParcel( Parcel oParcel, int zFlags )
	{
		if( oParcel == null )
			throw new NullPointerException( "Can't use null reference." ) ;
		
		oParcel.writeInt(m_zIndex) ;
		oParcel.writeInt(m_zSize) ;
		
		int zBoxScores = m_voBoxScores.size() ; 
		oParcel.writeInt(zBoxScores) ;
		if( zBoxScores > 0 )
		{
			BoxScore aoBoxScores[] = new BoxScore[zBoxScores] ;
			m_voBoxScores.toArray(aoBoxScores) ;
			oParcel.writeTypedArray( aoBoxScores, 0 ) ;
		}
		
		return ;
	}
}
