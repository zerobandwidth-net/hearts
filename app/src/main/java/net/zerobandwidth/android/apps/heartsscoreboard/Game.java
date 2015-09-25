package net.zerobandwidth.android.apps.heartsscoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

//import android.os.Bundle;
//import android.os.Parcel;
//import android.os.Parcelable;
import android.util.Log;

public class Game
// implements Parcelable
{
	public static final String LTAG = "Game" ;
	
	// Constant data keys for packing a Game as a Bundle
/*	public static final String S_UID =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Game.sUID" ;
	public static final String Z_PLAYERS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Game.zPlayers" ;
	public static final String AO_PLAYERS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Game.aoPlayers" ;
	public static final String Z_ROUNDS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Game.zRounds" ;
	public static final String AO_ROUNDS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Game.aoRounds" ;
	public static final String AB_RULES =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Game.abRules" ;
*/
	
	/**
	 * Parcel creator for Game.
	 */
/*	public static final Parcelable.Creator<Game> CREATOR
		= new Parcelable.Creator<Game>()
	{
		public Game createFromParcel( Parcel oParcel )
		{ return new Game(oParcel) ; }
		
		public Game[] newArray( int zSize )
		{ return new Game[zSize] ; }
	};
*/	
	/**
	 * Enumeration of various Boolean tags representing optional rules.
	 */
	public enum OptionalRules
	{
		DOUBLEDECK(0), OMNIBUS(1), HOOLIGAN(2) ;
	
		/**
		 * Reference for the size of the enumeration.
		 * @return The number of values in the enumeration.
		 */
		public static int size() { return 3 ; }
	
		/**
		 * Textual labels for various elements that might display or refer to
		 * the rules in question.
		 */
		public static final String RULELABELS[] =
		{ "Double Deck", "Omnibus", "Hooligan" } ;
		
		/**
		 * Help text for each rule.
		 */
		public static final String RULEHELP[] =
		{
			"Two decks of cards are used instead of one.",
			"Taking the Jack of Diamonds grants a -10 bonus.",
			"Taking the Seven of Clubs imposes a +7 penalty."
		} ;
	
		/**
		 * The integer value of the enumeration term.
		 */
		private int m_zValue ;
		
		/**
		 * Implicit constructor for enumeration type.
		 * @param value the value to be set
		 */
		OptionalRules( int value ) { m_zValue = value ; }
		
		/**
		 * Converts the enumeration term to an integer primitive.
		 * @return The integer value of the enumeration term.
		 */
		public int value() { return m_zValue ; }
		
		/**
		 * Returns the label text for this mark type.
		 */
		public String toString() { return RULELABELS[m_zValue] ; }
		
		/**
		 * Returns the help text for this mark type.
		 * @return the help text for this mark type.
		 */
		public String getHelpText() { return RULEHELP[m_zValue] ; }
	}
	
	/**
	 * Enumeration providing semantically-meaningful names for the various
	 * display modes supported by both scoreboard activities and the Game's own
	 * HTML rendering functions. 
	 */
	public enum DisplayMode
	{
		BOXSCORES(0), TOTALS(1) ;
		
		public static int size() { return 2 ; }
		
		private int m_zValue ;
		
		DisplayMode( int z ) { m_zValue = z ; }
		
		public int value() { return m_zValue ; }
		
		// This is cached for efficiency.
		public static final DisplayMode[] VALUES = DisplayMode.values() ;
		
		public String toString() { return Integer.toString(m_zValue) ; }
	}
		
	/**
	 * This static method creates an array of Booleans that is the same
	 * size as the OptionalRules enumeration.  The caller would invoke this
	 * method in order to construct its own array of optional rules in
	 * preparation for updating this object.
	 * @return an array of booleans that is the same size as OptionalRules.
	 */
	public static boolean[] getOptionalRuleArray()
	{ return new boolean[Game.OptionalRules.size()] ; }

	/**
	 * Inner class for a game's ruleset.  Defining the ruleset in this way
	 * allows other classes to pass only rulesets around without passing
	 * around the entire game object.
	 */
	public static class RuleSet
	{
		public static final String LTAG = "Game.Ruleset" ;
		/**
		 * An array of rule flags.  The size and indices of this array are
		 * controlled by the definition of the Game.OptionalRules enumeration.
		 */
		private boolean m_abRules[] ;
		
		/**
		 * Constructor.
		 */
		public RuleSet()
		{
			m_abRules = new boolean[Game.OptionalRules.size()] ;
			for( int i = 0 ; i < Game.OptionalRules.size() ; i++ )
				m_abRules[i] = false ;
		}
		
		/**
		 * Copy constructor.
		 * @param that the other RuleSet to be copied
		 */
		public RuleSet( Game.RuleSet that )
		{
			m_abRules = new boolean[Game.OptionalRules.size()] ;
			System.arraycopy( that.m_abRules, 0, this.m_abRules, 0, Game.OptionalRules.size() ) ;
		}
		
		/**
		 * Constructor which copies values from an array of booleans.  Used to
		 * reconstruct a RuleSet object from a Parcel or Bundle.
		 * @param abRules the source array to be copied
		 */
		public RuleSet( boolean abRules[] )
		{
			m_abRules = new boolean[Game.OptionalRules.size()] ;
			System.arraycopy( abRules, 0, m_abRules, 0, Game.OptionalRules.size() ) ;
		}
		
		/**
		 * Accessor for a particular rule.
		 * @param ezRuleId the enumerated tag identifying the sought rule
		 * @return true if the rule is in effect
		 */
		public boolean getRule( Game.OptionalRules ezRuleId )
		{ return m_abRules[ezRuleId.value()] ; }

		/**
		 * Accessor for a particular rule.
		 * @param zRuleId the integer ID of the sought rule, corresponding
		 * to exactly one term in the Game.OptionalRules enumeration
		 * @return true if the rule is in effect
		 */
		public boolean getRule( int zRuleId )
		{ return m_abRules[zRuleId] ; }
		
		/**
		 * Mutator for a particular rule.
		 * @param ezRuleId the enumerated tag identifying the sought rule
		 * @param bValue the new value of the rule flag
		 * @return the new value of the rule flag
		 */
		public boolean setRule( Game.OptionalRules ezRuleId, boolean bValue )
		{
			m_abRules[ezRuleId.value()] = bValue ;
			return m_abRules[ezRuleId.value()] ;
		}
		
		/**
		 * Converts the object's rule flags to a boolean array.  Used to
		 * marshal the RuleSet object into a Parcel or Bundle.  This could
		 * simply return the member array but it should be clear why this
		 * would be a bad idea.
		 * @return a copy of the set of rule flags stored in the object
		 */
		public boolean[] toArray()
		{
			boolean abRuleCopy[] = new boolean[Game.OptionalRules.size()] ;
			System.arraycopy( this.m_abRules, 0, abRuleCopy, 0, Game.OptionalRules.size() ) ;
			return abRuleCopy ;
		}
	}	

	/** Unique identifier created by the generateUniqueIdentifier() method */
	private String m_sUID ;
	/** Vector of Player objects */
	private Vector<Player> m_voPlayers ;
	/** Vector of Rounds */
	private Vector<Round> m_voRounds ;
	/** Tracks rules that may or may not be in effect for this game. */
	private RuleSet m_oRules ;
	
	/**
	 * Default constructor; not intended to be used explicitly.
	 */
	public Game() { this.init() ; }
	
	/**
	 * Constructor which takes a vector of players.
	 * It takes a guess about double-deck status based on the number of
	 * players; for six players, it always assumes double-deck.
	 * @param voPlayers The number of players in the game.
	 */
	public Game( Vector<Player> voPlayers )
	{ this.init().constructWithData( voPlayers, ( voPlayers.size() > 6 ) ) ; }
	
	/**
	 * Constructor
	 * @param voPlayers Pre-existing player vector created/managed by caller.
	 * @param bDoubleDeck Specifies whether the game uses two decks.
	 */
	public Game( Vector<Player> voPlayers, boolean bDoubleDeck )
	{ this.init().constructWithData(voPlayers, bDoubleDeck) ; }

	/**
	 * Private template function for constructors.
	 */
	private Game init()
	{
		Log.d(LTAG, "init() called") ;
		m_voPlayers = new Vector<Player>() ;
		m_oRules = new RuleSet() ;
		return this.setID().initRounds() ;
	}

	private Game constructWithData( Vector<Player> voPlayers, boolean bDoubleDeck )
	{
		m_voPlayers = voPlayers ;
		m_oRules.setRule( OptionalRules.DOUBLEDECK, bDoubleDeck ) ;
		return this ;
	}

	private Game initRounds()
	{
		m_voRounds = new Vector<Round>( m_voPlayers.size() * 2, m_voPlayers.size() ) ;
		return this ;
	}

	/**
	 * Reconstructs a Game object based on keys found in a Bundle object.
	 * This constructor assumes that the Bundle object was constructed by
	 * using the toBundle() method of the Game class.
	 * @param oGameBundle The Bundle from which to extract the data.
	 */
/*	public Game( Bundle oBundle )
	{
		this.init() ;
		
		if( oBundle.containsKey(Game.S_UID) )
			m_sUID = oBundle.getString(Game.S_UID) ;
		else
			m_sUID = generateUniqueIdentifier() ;
		
		int zPlayers = oBundle.getInt(Game.Z_PLAYERS) ;
		
		if( zPlayers > 0 && oBundle.containsKey(Game.AO_PLAYERS) )
		{ // Game might not have player data, so don't bother if it's not there.
			m_voPlayers = new Vector<Player>( zPlayers ) ;
			Parcelable aoPlayers[] = oBundle.getParcelableArray(Game.AO_PLAYERS) ;
			for( int i = 0 ; i < zPlayers ; i++ )
				m_voPlayers.add( i, (Player)(aoPlayers[i]) ) ;
		}
		
		int zRounds = oBundle.getInt(Game.Z_ROUNDS) ;
		if( zRounds > 0 && oBundle.containsKey(Game.AO_ROUNDS) )
		{ // Game might not have had rounds data, so don't bother if it's not there.
			m_voRounds = new Vector<Round>( zRounds, zPlayers ) ;
			Parcelable aoRounds[] = oBundle.getParcelableArray(Game.AO_ROUNDS) ;
			for( int i = 0 ; i < zRounds ; i++ )
				m_voRounds.add( i, (Round)(aoRounds[i]) ) ;
		}
		
		m_oRules = new RuleSet( oBundle.getBooleanArray(Game.AB_RULES) ) ;
	}
*/	
	/**
	 * Reconstructs a Game object based on data found in an Android Parcel.
	 * The constructor assumes that the Parcel was created by the
	 * writeToParcel() method of the Game class.
	 * @param oParcel the Parcel, supplied by the platform
	 */
/*	public Game( Parcel oParcel )
	{
		this.init() ;
		
		m_sUID = oParcel.readString() ;
		
		int zPlayers = oParcel.readInt() ;
		m_voPlayers = new Vector<Player>( zPlayers ) ;
		if( zPlayers > 0 )
		{
			Player aoPlayers[] = new Player[zPlayers] ;
			oParcel.readTypedArray( aoPlayers, Player.CREATOR ) ;
			for( int i = 0 ; i < zPlayers ; i++ )
				m_voPlayers.add( i, aoPlayers[i] ) ;
		}
		
		int zRounds = oParcel.readInt() ;
		if( zRounds > 0 )
		{
			m_voRounds = new Vector<Round>( zRounds ) ;
			Round aoRounds[] = new Round[zRounds] ;
			oParcel.readTypedArray( aoRounds, Round.CREATOR ) ;
			for( int i = 0 ; i < zRounds ; i++ )
				m_voRounds.add( i, aoRounds[i] ) ;
		}

		boolean abRules[] = new boolean[Game.OptionalRules.size()] ;
		oParcel.readBooleanArray(abRules) ;
		m_oRules = new RuleSet(abRules) ;
	}
*/	
	/** (Parcelable) */
/*	public int describeContents()
	{ return 0 ; }
*/		
	/**
	 * Accessor for the Game object's unique identifier.
	 * @return the Game object's unique identifier.
	 */
	public String getID()
	{ return m_sUID ; }
	
	/**
	 * Mutator for the Game object's unique identifier.
	 * This method calls generateUniqueIdentifier() to regenerate a new ID in
	 * whatever standard format is dictated by that function. 
	 * @return the Game itself, for chained invocation
	 */
	public Game setID()
	{
		m_sUID = generateUniqueIdentifier() ;
		return this ;
	}
	
	/**
	 * Accessor for the number of players in this game.
	 * @return number of players in the game
	 */
	public int getPlayerCount()
	{
		if( m_voPlayers != null ) return m_voPlayers.size() ;
		else return 0 ;
	}
	
	/**
	 * Accessor for a particular player.
	 * @param zIndex The index of the player in the vector.
	 * @return The player at that index in the vector.
	 */
	public Player getPlayer( int zIndex )
	{
		if( m_voPlayers == null ) return null ;
		else return m_voPlayers.get(zIndex) ;
	}
	
	/**
	 * Accessor for the number of rounds played in the current game.
	 * @return number of rounds in the game
	 */
	public int getRoundCount()
	{
		if( m_voRounds != null ) return m_voRounds.size() ;
		else return 0 ;
	}
	
	/**
	 * Adds a new round to the game.  
	 * It is assumed that the caller has already created and validated the
	 * round's scores prior to adding it to the game.
	 * @param oNewRound The new round to be added.
	 * @return whether the vector was changed by the operation (as Collection.add())
	 */
	public boolean addRound( Round oNewRound )
	{
		if( m_voRounds == null )
			m_voRounds = new Vector<Round>( m_voPlayers.size() * 2, m_voPlayers.size() ) ;
		return m_voRounds.add( oNewRound ) ;
	}
	
	/**
	 * Accessor for a particular round of the game.
	 * @param zIndex The index of the round in the current game.
	 * @return the Round object representing that round of the game.
	 */
	public Round getRound( int zIndex )
	{
		if( m_voRounds == null ) return null ;
		else return m_voRounds.get(zIndex) ;
	}
	
	/**
	 * Return the round that contains the given box score.
	 * @param oScore The score whose parent round is sought.
	 * @return the Round that contains that score.
	 */
	public Round getRoundOf( BoxScore oScore )
	{
		for( Round oRound : m_voRounds )
			if( oRound.contains(oScore) )
				return oRound ;
		
		return null ;
	}
	
	/**
	 * Accessor for the entire ruleset.
	 * @return the RuleSet object tracking this game's rules
	 */
	public RuleSet getRules()
	{
		return m_oRules ;
	}
	
	/**
	 * "Is this game double-deck?" is such a frequent question that it warrants
	 * its own accessor method.
	 */
	public boolean isDoubleDeck()
	{
		return m_oRules.getRule(OptionalRules.DOUBLEDECK) ; 
	}
	
	/**
	 * Calculates the number of points that should be taken each round based
	 * on the number of players and the optional rules that are in place for
	 * this game.
	 * @return the number of points that would be taken each round.
	 */
	public int getPointsPerRound()
	{
		int zTotal = ( m_oRules.getRule(OptionalRules.DOUBLEDECK) ? 52 : 26 ) ;
		
		if( m_oRules.getRule(OptionalRules.OMNIBUS) )
			zTotal -= ( m_oRules.getRule(OptionalRules.DOUBLEDECK) ? 20 : 10 ) ;

		if( m_oRules.getRule(OptionalRules.HOOLIGAN) )
			zTotal += ( m_oRules.getRule(OptionalRules.DOUBLEDECK) ? 14 : 7 ) ;

		return zTotal ;
	}
	
	/**
	 * Writes the contents of the Game object into the JVM logger.
	 * @param zPriority the desired logging level; this should be Log.DEBUG.
	 * @return the total number of bytes written (like the Log class methods)
	 */
	public int writeToLogs( int zPriority )
	{
		int zBytes = 0 ;
		final String sTag = LTAG + " (dump)" ;
		
		zBytes += Log.println( zPriority, sTag,
				"BEGIN DUMP OF GAME ID " + m_sUID ) ;
		if( m_voPlayers != null )
		{
			for ( int p = 0 ; p < m_voPlayers.size() ; p++ )
				zBytes += Log.println( zPriority, sTag,
				 " Player " + Integer.toString(p) + ": " + m_voPlayers.get(p).getName() ) ;
			
			if( m_voRounds != null )
			{
				for( int r = 0 ; r < m_voRounds.size() ; r++ )
				{
					zBytes += Log.println( zPriority, sTag,
							" Round #" + Integer.toString(r) ) ;
					
					for( int s = 0 ; s < m_voPlayers.size() ; s++ )
					{
						zBytes += Log.println( zPriority, sTag,
								"  " + m_voPlayers.get(s).getName()
								+ " "+ m_voRounds.get(r).getBoxScore(s).toString(m_oRules.getRule(OptionalRules.DOUBLEDECK))
								) ;
					}
				}
			}
			else
			{
				zBytes += Log.println( zPriority, sTag,
						" No rounds registered." ) ;
			}
		}
		else
		{
			zBytes += Log.println( zPriority, sTag, "No players found." ) ;
		}
		zBytes += Log.println( zPriority, sTag,
				" Rules in Play:" ) ;
		for( int r = 0 ; r < Game.OptionalRules.size() ; r++ )
		{
			zBytes += Log.println( zPriority, sTag,
					"  " + ( m_oRules.getRule(r) ? "(X)" : "(_)" )
					+ Game.OptionalRules.RULELABELS[r] 
					) ;
		}
		zBytes += Log.println( zPriority, sTag,
				"END DUMP OF GAME ID " + m_sUID ) ;
		
		return zBytes ;
	}
	
	/**
	 * Formats the data in the Game object as an HTML table.
	 * @return an HTML table displaying the game's data
	 */
	public String toHTMLTable( DisplayMode eDisplayMode )
	{
		if( m_voPlayers == null ) return "" ;
		
		StringBuffer sbValue = new StringBuffer() ;
		int azRunningTotals[] = new int[m_voPlayers.size()] ;

		sbValue.append( "<table style=\"border:none;margin-left:auto;margin-right:auto;\">\n" ) ;
		sbValue.append( " <tr>\n" ) ;
		sbValue.append( "  <th style=\"border:none;background-color:#000000;\">&nbsp;</th>\n" ) ;
		for( int i = 0 ; i < m_voPlayers.size() ; i++ )
		{
			sbValue.append( "  <th colspan=\"2\"" )
				.append( " style=\"border:none;background-color:#000000;color:#ffffff;font-weight:bold;padding:2px 1em;text-align:center;\">" )
				.append( m_voPlayers.get(i).getName() )
				.append( "</th>\n" )
				;
		}
		sbValue.append( " </tr>\n" ) ;
				
		if( m_voRounds != null )
		{ // Draw all the game's rounds.
			for( int i = 0 ; i < m_voRounds.size() ; i++ )
			{
				sbValue.append( m_voRounds.get(i).toHTMLTableRow(
						this.isDoubleDeck(), eDisplayMode, azRunningTotals ) ) ;
			}
		}
		
		this.appendFinalScoresToHTML( sbValue, azRunningTotals ) ;
		
		sbValue.append( "</table>\n" ) ;
		
		return sbValue.toString() ;
	}
	
	/**
	 * Paragraph of toHTMLTable(): Appends the table row containing the game's
	 * final scores for each player.
	 * @param sbTable The existing StringBuffer holding most of the HTML table.
	 * @param azTotals The array of total scores for each player.
	 */
	private void appendFinalScoresToHTML( StringBuffer sbTable, int azTotals[] )
	{
		sbTable.append( " <tr>\n" ) ;
		sbTable.append( "  <th style=\"border:none;background-color:#000000;\">&nbsp;</th>\n" ) ;
		for( int zTotal : azTotals )
		{
			String asColors[] = ScoreTableCellFactory.getScoreCellColorCodes(
					zTotal, ( this.isDoubleDeck() ? 150 : 100 ) ) ;
			sbTable.append( "  <td colspan=\"2\" style=\"border:none;" )
				.append( "background-color:" )
				.append( asColors[0] )
				.append( ";color:" )
				.append( asColors[1] )
				.append( ";font-size:large;font-weight:bold;padding:2px 1em;text-align:center;\">" )
				.append( Integer.toString(zTotal) )
				.append( "</td>\n" )
				;
		}
		sbTable.append( " </tr>\n" ) ;
	}

	/**
	 * Translates the Game object into an Android Bundle.
	 * @param oGameBundle A pre-existing Bundle object to be overwritten, or
	 * a null reference which will trigger creation of a new Bundle.
	 * @return A Bundle that is equivalent to this Game object.
	 */
/*	public Bundle toBundle( Bundle oBundle )
	{
		if( oBundle == null )
			oBundle = new Bundle() ;
		else
			oBundle.clear() ;
		
		oBundle.putString( Game.S_UID, m_sUID ) ;
		
		int zPlayers = m_voPlayers.size() ;
		oBundle.putInt( Game.Z_PLAYERS, zPlayers ) ;
		if( zPlayers > 0 )
		{ // Don't bother doing this unless there is player data.
			Player aoPlayers[] = new Player[zPlayers] ;
			m_voPlayers.toArray(aoPlayers) ;
			oBundle.putParcelableArray( Game.AO_PLAYERS, aoPlayers ) ;
		}
		
		int zRounds = m_voRounds.size() ;
		oBundle.putInt( Game.Z_ROUNDS, zRounds ) ;
		if( zRounds > 0 )
		{ // Don't bother doing this unless there is game data.
			Round aoRounds[] = new Round[zRounds] ;
			m_voRounds.toArray(aoRounds) ;
			oBundle.putParcelableArray( Game.AO_ROUNDS,  aoRounds ) ;
		}
		
		oBundle.putBooleanArray( Game.AB_RULES, m_oRules.toArray() ) ;
		
		return oBundle ;
	}
*/	
	/**
	 * Translates the Game object into an Android Parcel.
	 * @param oParcel the Parcel to be updated, supplied by the platform
	 * @param zFlags ignored
	 */
/*	public void writeToParcel( Parcel oParcel, int zFlags )
	{
		if( oParcel == null )
			throw new NullPointerException( "Can't use null reference." ) ;
		
		oParcel.writeString(m_sUID) ;
		
		int zPlayers = m_voPlayers.size() ;
		oParcel.writeInt(zPlayers) ;
		if( zPlayers > 0 )
		{
			Player aoPlayers[] = new Player[zPlayers] ;
			m_voPlayers.toArray(aoPlayers) ;
			oParcel.writeTypedArray( aoPlayers, 0 ) ;
		}
		
		int zRounds = m_voRounds.size() ;
		oParcel.writeInt(zRounds) ;
		if( zRounds > 0 )
		{
			Round aoRounds[] = new Round[zRounds] ;
			m_voRounds.toArray(aoRounds) ;
			oParcel.writeTypedArray( aoRounds, 0 ) ;
		}
		
		oParcel.writeBooleanArray( m_oRules.toArray() ) ;
		
		return ;
	}
*/	
	/**
	 * Static method which can generate a unique identifier field for a Game.
	 * @return A string which uniquely identifies the Game object.
	 */
	public static String generateUniqueIdentifier()
	{
		StringBuffer sbGameID = new StringBuffer( "Game:" ) ;
		SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd-HHmmss-SSS" ) ;
		sbGameID.append(df.format(new Date())) ;
		return sbGameID.toString() ;
	}
}
