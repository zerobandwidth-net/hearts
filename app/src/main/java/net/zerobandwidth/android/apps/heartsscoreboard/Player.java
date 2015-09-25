package net.zerobandwidth.android.apps.heartsscoreboard;

import java.util.GregorianCalendar;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Player
 implements Parcelable
{
	
	// Constant data keys for packing a Player as a Bundle
	public static final String S_UID =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Player.sUID" ;
	public static final String S_NAME =
	 "net.zerobandwidth.android.apps.heartsscoreboard.Player.sName" ;
	
	public static final Parcelable.Creator<Player> CREATOR
	 = new Parcelable.Creator<Player>()
	{
		public Player createFromParcel( Parcel oParcel )
		{ return new Player(oParcel) ; }
		
		public Player[] newArray( int zSize )
		{ return new Player[zSize] ; }
	};
	
	/** Uniquely identifies the player object.  Created by generateUniqueIdentifier(). */
	private String m_sUID ;
	/** The player's name. */
	private String m_sName ;
	
	/**
	 * Constructor, which sets the default name to "Unnamed Player".
	 */
	public Player()
	{
		m_sUID = generateUniqueIdentifier() ;
		m_sName = "" ;
	}
	
	/**
	 * Constructor allowing the caller to set the player's name.
	 * @param sName The player's name.
	 */
	public Player( String sName )
	{
		m_sUID = generateUniqueIdentifier() ;
		m_sName = sName ;
	}
	
	/**
	 * Reconstructs a Player object based on keys found in an Android Bundle
	 * object.  This constructor assumes that the Bundle was created by
	 * the toBundle() method of the Player class.
	 * @param oBundle the Bundle from which to extract the data.
	 */
	public Player( Bundle oBundle )
	{
		if( oBundle.containsKey(S_UID) )
			m_sUID = oBundle.getString(S_UID) ;
		else m_sUID = generateUniqueIdentifier() ;
		
		m_sName = oBundle.getString(S_NAME) ;
	}
	
	/**
	 * Reconstructs a Player object based on data found in an Android Parcel.
	 * The constructor assumes that the Parcel was created by the
	 * writeToParcel() method of the Player class.
	 * @param oParcel the Parcel from which to extract the data.
	 */
	public Player( Parcel oParcel )
	{
		m_sUID = oParcel.readString() ;
		m_sName = oParcel.readString() ;
	}
	
	/** (Parcelable) */
	public int describeContents()
	{ return 0 ; }
	
	/**
	 * Accessor for the Player object's unique identifier.
	 * @return the Player object's unique identifier.
	 */
	public String getID() { return m_sUID ; }
	
	/**
	 * Mutator for the Player object's unique identifier.  This method calls
	 * generateUniqueIdentifier() to regenerate a new ID in whatever standard
	 * format is dictated by that function. 
	 * @return the Player object itself
	 */
	public Player setID()
	{
		m_sUID = generateUniqueIdentifier() ;
		return this ;
	}
	
	/**
	 * Accessor for the player's name.
	 * @return the player's name.
	 */
	public String getName() { return m_sName ; }
	
	/**
	 * Sets the player's name.  Sanitizes out colon characters (':') because
	 * those are used as delimiters when serializing the object via the
	 * toString() method.
	 * @param name The proposed name.
	 * @return the Player object itself
	 */
	public Player setName( String name ) throws IllegalArgumentException
	{
		if( name.contains(":") )
			throw new IllegalArgumentException( "Player names cannot contain colons." ) ;
		
		m_sName = name ;
		return this ;
	}
	
	/**
	 * Standard toString() method.
	 */
	public String toString()
	{
		return new String( m_sUID + ":" + m_sName ) ;
	}
	
	/**
	 * Translates this object into an Android Bundle.
	 * @param oPlayerBundle A pre-existing Bundle object to be overwritten, or
	 * a null reference, which will trigger creation of a new Bundle.
	 * @return A Bundle that is equivalent to this Player object.
	 */
	public Bundle toBundle( Bundle oPlayerBundle )
	{
		if( oPlayerBundle == null ) oPlayerBundle = new Bundle() ;
		else oPlayerBundle.clear() ;
		
		oPlayerBundle.putString( Player.S_UID,  m_sUID ) ;
		oPlayerBundle.putString( Player.S_NAME,  m_sName ) ;
		
		return oPlayerBundle ;
	}
	
	/**
	 * Translates this object into an Android Parcel.
	 * @param oParcel the Parcel object supplied by the platform
	 * @param zFlags ignored
	 */
	public void writeToParcel( Parcel oParcel, int zFlags )
	{
		if( oParcel == null )
			throw new NullPointerException( "Can't use null reference." ) ;
		
		oParcel.writeString(m_sUID) ;
		oParcel.writeString(m_sName) ;
		
		return ;
	}
	
	/**
	 * Generates a new unique identifier string for a Player object.
	 * @return a string usable as the unique identifier for a Player object.
	 */
	public static String generateUniqueIdentifier()
	{
		GregorianCalendar creationDate = new GregorianCalendar() ;
		return new String( "Player:" + Long.toString(creationDate.getTimeInMillis()) ) ;
	}
}
