package net.zerobandwidth.android.apps.heartsscoreboard;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class BoxScore
 implements Parcelable
{

	// Constant data keys for packing a BoxScore as a Bundle
	public static final String Z_HEARTS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScore.zHearts" ;
//	public static final String B_ITOOKTHEREST =
//	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScore.bITookTheRest" ;
	public static final String Z_QUEENS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScore.zQueens" ;
	public static final String Z_OMNIBUS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScore.zOmnibus" ;
	public static final String Z_HOOLIGAN =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScore.zHooligan" ;
	public static final String AB_MARKS =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScore.abMarks" ;

	/**
	 * Enumeration of various Boolean scoring marks.  Some of these correspond
	 * to standard rules of Hearts; others are esoteric custom flags that are
	 * simply interesting to the obsessive statistician in all of us...
	 * @author Pasha
	 */
	public static enum Mark
	{
		NEWMOON(0), FULLMOON(1), MINIMOON(2), FAILURE(3), NULLHAND(4),
		ANGEL(5), DAGGER(6), FISHERMAN(7), FISH(8), POISONER(9),
		DOWNHEART(10), FULLMOONVICTIM(11), HALFMOON(12), HALFMOONVICTIM(13) ;
	
		/**
		 * Reference for the size of the enumeration.
		 * @return The number of values in the enumeration.
		 */
		public static final int size() { return 14 ; }
	
		/**
		 * Shorthand text to be displayed for each mark type.
		 */
		public static final String MARKTEXT[] =
			{ "M-", "M+", "MM", "F",  "0", "An", "Dg",
			  "Fm", "Fs", "Ps", "dH", "",  "HM", ""    } ;
		
		/**
		 * HTML shorthand to be displayed for each mark type.
		 */
		public static final String MARKHTML[] =
			{
				"&darr;M", "&uarr;M", "&hearts;M", "F", "&empty;",
				"An", "&dagger;", "&Dagger;", "&alpha;", "P&dagger;",
				"&hearts;&darr;", "", "&frac12;M", ""
			} ;
	
		/**
		 * The integer value of the enumeration term.
		 */
		private int m_zValue ;
		
		/**
		 * Implicit constructor for enumeration type.
		 * @param value
		 */
		Mark( int value ) { m_zValue = value ; }
		
		/**
		 * Converts the enumeration term to an integer primitive.
		 * @return The integer value of the enumeration term.
		 */
		public int value() { return m_zValue ; }
		
		/**
		 * Returns the shorthand text for this mark type.
		 */
		public String toString() { return MARKTEXT[m_zValue] ; }
		
		/**
		 * Returns the HTML text for this mark type.
		 * @return the HTML text for this mark type.
		 */
		public String toHTML() { return MARKHTML[m_zValue] ; }
	}
	
	public static final Parcelable.Creator<BoxScore> CREATOR
	 = new Parcelable.Creator<BoxScore>() 
	{
		public BoxScore createFromParcel( Parcel oParcel )
		{
			return new BoxScore( oParcel ) ;
		}
		
		public BoxScore[] newArray( int zSize )
		{
			return new BoxScore[zSize] ;
		}
	};
	
	/**
	 * (Parcelable)
	 */
	public int describeContents() { return 0 ; }
	
	/** The number of hearts this player scored in this round of the game. */
	private int m_zHearts ;
	/** Is this box score being reserved for "the rest" of the points in this round? */
//	private boolean m_bITookTheRest ;
	
	/**
	 * Did the player take the Queen of Spades?  This is an integer because,
	 * in double-deck games, the player might have taken more than one.
	 */
	private int m_zQueens ;
	/** Did the player claim any "omnibus" cards (10D or JD)? */
	private int m_zOmnibus ;
	/** Did the player claim any "hooligan" cards (7C)? */
	private int m_zHooligan ;
	
	/**
	 * This array signifies several scoring marks.  It is created with a number
	 * of Boolean fields equal to Marks.size().
	 */
	private boolean m_abMarks[] ;
	
	/**
	 * Default constructor.
	 */
	public BoxScore()
	{
		this.init() ;
	}
	
	/**
	 * Copy constructor
	 * @param that the object to be copied
	 */
	public BoxScore( BoxScore that )
	{
		this.set(that) ;
	}
	
	/**
	 * Constructor to be used when "I took the rest" is the expected result.
	 * @param bTookTheRest Indicates which mode to use.
	 */
/*	public BoxScore( boolean bTookTheRest )
	{
		this.init() ;
		if( bTookTheRest )
		{
			m_bITookTheRest = true ;
			m_abMarks[Mark.NULLHAND.value()] = false ;
		}
	}
*/	
	/**
	 * Reconstructs a BoxScore object based on keys found in an Android Bundle.
	 * This constructor assumes that the Bundle was created using the toBundle()
	 * method of the BoxScore class.
	 * @param oBundle the Bundle from which to extract the data.
	 */
	public BoxScore( Bundle oBundle )
	{
		m_zHearts = oBundle.getInt(BoxScore.Z_HEARTS) ;
//		m_bITookTheRest = oBundle.getBoolean(BoxScore.B_ITOOKTHEREST) ;
		m_zQueens = oBundle.getInt(BoxScore.Z_QUEENS) ;
		m_zOmnibus = oBundle.getInt(BoxScore.Z_OMNIBUS) ;
		m_zHooligan = oBundle.getInt(BoxScore.Z_HOOLIGAN) ;
		m_abMarks = oBundle.getBooleanArray(BoxScore.AB_MARKS) ;
	}
	
	/**
	 * Reconstructs a BoxScore object based on the data found in an Android
	 * Parcel.  The constructor assumes that the Parcel was created by the
	 * the writeToParcel() method.
	 * @param oParcel the Parcel from which to extract the data.
	 */
	public BoxScore( Parcel oParcel )
	{
		m_zHearts = oParcel.readInt() ;
//		if( oParcel.readInt() == 1 )
//			m_bITookTheRest = true ;
//		else m_bITookTheRest = false ;
		m_zQueens = oParcel.readInt() ;
		m_zOmnibus = oParcel.readInt() ;
		m_zHooligan = oParcel.readInt() ;
		m_abMarks = new boolean[Mark.size()] ;
		oParcel.readBooleanArray(m_abMarks) ;
	}
	
	// Accessors and mutators...
	
	/**
	 * Returns the players box score based on the point card count and other
	 * scoring marks that have been set in this box score.
	 * @return The final integer score after all considerations have been
	 * factored in.
	 */
	public int getScore()
	{
		return this.getScore(false) ;
	}
	
	/**
	 * Same as normal getScore, but with an additional argument to indicate
	 * whether the score is part of a double-deck game.
	 * @param bDoubleDeck Indicates whether the score is part of a double-deck
	 * game.
	 * @return The final integer score after all considerations have been
	 * factored in.
	 */
	public int getScore( boolean bDoubleDeck )
	{
		int zScore = 0 ;
		
		if( m_abMarks[Mark.NEWMOON.value()] )
			zScore = ( bDoubleDeck ? -52 : -26 ) ;
		else if( m_abMarks[Mark.FULLMOON.value()] )
			zScore = 0 ;
		else if( m_abMarks[Mark.FULLMOONVICTIM.value()] )
			zScore = ( bDoubleDeck ? 52 : 26 ) ;
		else if( m_abMarks[Mark.HALFMOON.value()] )
			zScore = -26 ;
		else if( m_abMarks[Mark.HALFMOONVICTIM.value()] )
			zScore = 26 ;
		else // process hearts and queens normally
			zScore = m_zHearts + ( 13 * m_zQueens ) ;
		
		zScore -= 10 * m_zOmnibus ;
		zScore += 7 * m_zHooligan ;
		return zScore ;		
	}
	
	public int getHearts()
	{ return m_zHearts ; }
	
	public int setHearts( int zHearts )
	{
		m_zHearts = zHearts ;
		return m_zHearts ;
	}
	
//	public boolean tookTheRest() { return m_bITookTheRest ; }
//	public boolean tookTheRest( boolean bTookTheRest )
//	{ m_bITookTheRest = bTookTheRest ; return m_bITookTheRest ; }
	
	public int getQueens() { return m_zQueens ; }
	public int setQueens( int zQueens )
	{ m_zQueens = zQueens ; return m_zQueens ; }
	
	public int getOmnibus() { return m_zOmnibus ; }
	public int setOmnibus( int zOmnibus )
	{ m_zOmnibus = zOmnibus ; return m_zOmnibus ; }
	
	public int getHooligan() { return m_zHooligan ; }
	public int setHooligan( int zHooligan )
	{ m_zHooligan = zHooligan ; return m_zHooligan ; }
	
	public boolean getMark( Mark ezMark )
	{
		return m_abMarks[ezMark.value()] ;
	}
	
	/**
	 * Sets the value of a scoring mark. Some marks are mutually exclusive;
	 * this method will enforce that exclusivity.
	 * @param ezMark The mark whose value is being set.
	 * @param bMarkValue The new mark value.
	 * @return The post-state of the mark in the object.
	 */
	public boolean setMark( Mark ezMark, boolean bMarkValue )
	{
		m_abMarks[ezMark.value()] = bMarkValue ;
		
		if( bMarkValue = true )
		{
			// Handle mutual exclusivity of marks by unsetting all the marks
			// which can no longer be true.
			switch(ezMark)
			{
			case NEWMOON:
				// This is a new moon.
				m_abMarks[Mark.FULLMOON.value()] = false ;
				m_abMarks[Mark.MINIMOON.value()] = false ;
				m_abMarks[Mark.FAILURE.value()] = false ;
				m_abMarks[Mark.NULLHAND.value()] = false ;
				m_abMarks[Mark.FULLMOONVICTIM.value()] = false ;
				m_abMarks[Mark.HALFMOON.value()] = false ;
				m_abMarks[Mark.HALFMOONVICTIM.value()] = false ;
				break ;
			case FULLMOON:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				// This is a full moon.
				m_abMarks[Mark.MINIMOON.value()] = false ;
				m_abMarks[Mark.FAILURE.value()] = false ;
				m_abMarks[Mark.NULLHAND.value()] = false ;
				m_abMarks[Mark.FULLMOONVICTIM.value()] = false ;
				m_abMarks[Mark.HALFMOON.value()] = false ;
				m_abMarks[Mark.HALFMOONVICTIM.value()] = false ;
				break ;
			case MINIMOON:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				m_abMarks[Mark.FULLMOON.value()] = false ;
				// This is a mini-moon.
				// This might still be a failure.
				m_abMarks[Mark.NULLHAND.value()] = false ;
				m_abMarks[Mark.FULLMOONVICTIM.value()] = false ;
				m_abMarks[Mark.HALFMOON.value()] = false ;
				m_abMarks[Mark.HALFMOONVICTIM.value()] = false ;
				break ;
			case FAILURE:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				m_abMarks[Mark.FULLMOON.value()] = false ;
				// This might still be a minimoon.
				// This is a failure.
				// This might still be a null hand.
				// Might still be a moon victim.
				m_abMarks[Mark.HALFMOON.value()] = false ;
				// Might still be a half-moon victim.
				break ;
			case NULLHAND:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				m_abMarks[Mark.FULLMOON.value()] = false ;
				m_abMarks[Mark.MINIMOON.value()] = false ;
				// This might still be a failure.
				// This is a null hand.
				// Might still be a moon victim.
				m_abMarks[Mark.HALFMOON.value()] = false ;
				// Might still be a half-moon victim.
				break ;
			case FULLMOONVICTIM:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				m_abMarks[Mark.FULLMOON.value()] = false ;
				m_abMarks[Mark.MINIMOON.value()] = false ;
				// This might still be a failure.
				// This might still be a null hand.
				// This is a moon victim.
				m_abMarks[Mark.HALFMOON.value()] = false ;
				m_abMarks[Mark.HALFMOONVICTIM.value()] = false ;
				break ;
			case HALFMOON:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				m_abMarks[Mark.FULLMOON.value()] = false ;
				m_abMarks[Mark.MINIMOON.value()] = false ;
				m_abMarks[Mark.FAILURE.value()] = false ;
				m_abMarks[Mark.NULLHAND.value()] = false ;
				m_abMarks[Mark.FULLMOONVICTIM.value()] = false ;
				// This is a half-moon.
				m_abMarks[Mark.HALFMOONVICTIM.value()] = false ;
				break ;
			case HALFMOONVICTIM:
				m_abMarks[Mark.NEWMOON.value()] = false ;
				m_abMarks[Mark.FULLMOON.value()] = false ;
				m_abMarks[Mark.MINIMOON.value()] = false ;
				// This might still be a failure.
				// This might still be a null hand.
				m_abMarks[Mark.FULLMOONVICTIM.value()] = false ;
				m_abMarks[Mark.HALFMOON.value()] = false ;
				// This is a half-moon victim.
				break ;
			default : // Do nothing.
			}
		}
		
		return m_abMarks[ezMark.value()] ;
	}
	
	/**
	 * Returns a string representing all marks in the box score.
	 * This member function is reused by toString().
	 * @return a string representing all marks in the box score.
	 */
	public String getMarksString()
	{
		StringBuffer sbValue = new StringBuffer() ;
		
		// Show queens only if the player didn't shoot the moon.
		if( ! m_abMarks[Mark.NEWMOON.value()]
				&& ! m_abMarks[Mark.FULLMOON.value()]
				&& ! m_abMarks[Mark.FULLMOONVICTIM.value()]
				&& ! m_abMarks[Mark.HALFMOON.value()]
				&& ! m_abMarks[Mark.HALFMOONVICTIM.value()]
				&& ! m_abMarks[Mark.MINIMOON.value()] )
		{
			for( int i=0 ; i < m_zQueens ; i++ )
				sbValue.append( " Q" ) ;
		}
		for( int i=0 ; i < m_zOmnibus ; i++ ) sbValue.append( " JD" ) ;
		for( int i=0 ; i < m_zHooligan ; i++ ) sbValue.append( " 7C" ) ;
		for( int i=0 ; i < Mark.size() ; i++ )
			if( m_abMarks[i] ) sbValue.append( " " + Mark.MARKTEXT[i] ) ;
		
		return sbValue.toString() ;		
	}
	
	/**
	 * Returns HTML markup representing all marks in the box score.
	 * @return HTML markup representing all marks in the box score.
	 */
	public String getMarksHTML()
	{
		StringBuffer sbValue = new StringBuffer() ;
		
		// Show queens only if the player didn't shoot the moon.
		if( ! m_abMarks[Mark.NEWMOON.value()]
				&& ! m_abMarks[Mark.FULLMOON.value()]
				&& ! m_abMarks[Mark.FULLMOONVICTIM.value()]
				&& ! m_abMarks[Mark.HALFMOON.value()]
				&& ! m_abMarks[Mark.HALFMOONVICTIM.value()]
				&& ! m_abMarks[Mark.MINIMOON.value()] )
		{
			for( int i=0 ; i < m_zQueens ; i++ )
				sbValue.append( " Q&spades;" ) ;
		}
		for( int i=0 ; i < m_zOmnibus ; i++ ) sbValue.append( " J&diams;" ) ;
		for( int i=0 ; i < m_zHooligan ; i++ ) sbValue.append( " 7&clubs;" ) ;
		for( int i=0 ; i < Mark.size() ; i++ )
			if( m_abMarks[i] ) sbValue.append( " " + Mark.MARKHTML[i] ) ;
		
		return sbValue.toString() ;		
	}
	
	/**
	 * Copy mutator.
	 * @param that
	 */
	public void set( BoxScore that )
	{
		m_zHearts = that.m_zHearts ;
//		m_bITookTheRest = that.m_bITookTheRest ;
		m_zQueens = that.m_zQueens ;
		m_zOmnibus = that.m_zOmnibus ;
		m_zHooligan = that.m_zHooligan ;
		
		m_abMarks = new boolean[Mark.size()] ;
		for( int i = 0 ; i < Mark.size() ; i++ )
			m_abMarks[i] = that.m_abMarks[i] ;
	}
	
	/**
	 * Renders the box score and scoring marks in plain text.
	 */
	public String toString()
	{
		StringBuffer sbValue = new StringBuffer() ;
		
		sbValue.append( Integer.toString( this.getScore() ) ) ;
		sbValue.append( ":" ) ;
		sbValue.append( this.getMarksString() ) ;
		
		return sbValue.toString() ;
	}
	
	/**
	 * Overloaded toString() method.
	 * Oh, that darned double-deck rule, messing up the scoring.
	 * @param bDoubleDeck indicates whether the score is part of a double-deck
	 * game
	 * @return a string representing the box score
	 */
	public String toString( boolean bDoubleDeck )
	{
		StringBuffer sbValue = new StringBuffer() ;
		
		sbValue.append( Integer.toString( this.getScore(bDoubleDeck) ) ) ;
		sbValue.append( ":" ) ;
		sbValue.append( this.getMarksString() ) ;
		
		return sbValue.toString() ;
	}
	
	/**
	 * Renders the box score as a pair of table cells in HTML.
	 * @return
	 */
	public String toHTMLBoxScore()
	{
		return this.toHTMLBoxScore(false) ;
	}
	
	/**
	 * Renders the box score as a pair of table cells in HTML.
	 * @return
	 */
	public String toHTMLBoxScore( boolean bDoubleDeck )
	{
		StringBuffer sbValue = new StringBuffer() ;
		
		sbValue.append( "  <td style=\"border:none;background-color:#e8e8e8;padding:2px 1em;text-align:right;\">" ) ;
		sbValue.append( Integer.toString( this.getScore(bDoubleDeck) ) ) ;
		sbValue.append( "</td>\n  <td style=\"border:none;background-color:#e8e8e8;padding:2px 1em;text-align:center;\">" ) ;
		sbValue.append( this.getMarksHTML() ) ;
		sbValue.append( "</td>\n" ) ;
		
		return sbValue.toString() ;
	}
	
	/**
	 * Renders the box score as a pair of table cells in HTML, showing the
	 * player's running total as calculated based on the given previous total.
	 */
	public String toHTMLRunningTotal( boolean bDoubleDeck, int zOldTotal )
	{
		int zNewTotal = zOldTotal + this.getScore(bDoubleDeck) ;
		StringBuffer sbValue = new StringBuffer() ;
		String asColors[] = ScoreTableCellFactory.getScoreCellColorCodes( zNewTotal, (bDoubleDeck ? 150 : 100) ) ;
		
		sbValue.append( "  <td style=\"border:none;") ;
		sbValue.append( "background-color:" ) ;
		sbValue.append( asColors[0] ) ;
		sbValue.append( ";color:" ) ;
		sbValue.append( asColors[1] ) ;
		sbValue.append( ";padding:2px 1em;text-align:right;\">" ) ;
		sbValue.append( Integer.toString(zNewTotal) ) ;
		sbValue.append( "</td>\n" ) ;
		
		sbValue.append( "  <td style=\"background-color:" ) ;
		sbValue.append( asColors[0] ) ;
		sbValue.append( ";color:" ) ;
		sbValue.append( asColors[1] ) ;
		sbValue.append( ";padding:2px 1em;text-align:center;\">" ) ;
		sbValue.append( this.getMarksHTML() ) ;
		sbValue.append( "</td>\n" ) ;
		
		return sbValue.toString() ;
	}
	
	/**
	 * Translates a BoxScore object into an Android Bundle.
	 * @param oBundle An existing Bundle object to be overwritten, or
	 * a null reference which will trigger creation of a new Bundle.
	 * @return A Bundle that is equivalent to this BoxScore object.
	 */
	public Bundle toBundle( Bundle oBundle )
	{
		if( oBundle == null ) oBundle = new Bundle() ;
		else oBundle.clear() ;
		
		oBundle.putInt( BoxScore.Z_HEARTS, m_zHearts ) ;
//		oBundle.putBoolean( BoxScore.B_ITOOKTHEREST,  m_bITookTheRest ) ;
		oBundle.putInt( BoxScore.Z_QUEENS, m_zQueens ) ;
		oBundle.putInt( BoxScore.Z_OMNIBUS,  m_zOmnibus ) ;
		oBundle.putInt( BoxScore.Z_HOOLIGAN,  m_zHooligan ) ;
		oBundle.putBooleanArray( BoxScore.AB_MARKS,  m_abMarks ) ;
		
		return oBundle ;
	}
	
	/**
	 * Translates a BoxScore into an Android Parcel.
	 * @param oParcel A reference to the Parcel supplied by the platform.
	 */
	public void writeToParcel( Parcel oParcel, int zFlags )
	{
		if( oParcel == null )
			throw new NullPointerException( "Can't use null reference here." ) ;
		
		oParcel.writeInt(m_zHearts) ;
//		oParcel.writeInt( m_bITookTheRest ? 1 : 0 ) ;
		oParcel.writeInt(m_zQueens) ;
		oParcel.writeInt(m_zOmnibus) ;
		oParcel.writeInt(m_zHooligan) ;
		oParcel.writeBooleanArray(m_abMarks) ;
		
		return ;
	}
	
    /**
     * Private template function for constructors.  Initializes all members
     * with default attributes.
     */
	private void init()
	{
		m_zHearts = 0 ;
//		m_bITookTheRest = false ;
		m_zQueens = 0 ;
		m_zOmnibus = 0 ;
		m_zHooligan = 0 ;
		
		m_abMarks = new boolean[BoxScore.Mark.size()] ;
		for( int i = 0 ; i < BoxScore.Mark.size() ; i++ )
			m_abMarks[i] = false ;
	}
}
