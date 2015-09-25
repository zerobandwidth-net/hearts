package net.zerobandwidth.android.apps.heartsscoreboard;

public class GameSingleton
{
	private static Game m_oGame = null ;
	
	public static boolean hasInstance()
	{
		return ( m_oGame != null ) ;
	}
	
	public static Game getInstance()
	{
		if( m_oGame == null )
			m_oGame = new Game() ;
		
		return m_oGame ;
	}
	
	public static Game setInstance( Game oGame )
	 throws IllegalStateException
	{
		if( m_oGame == null )
		{
			m_oGame = oGame ;
			return m_oGame ;
		}
		else throw new IllegalStateException(
		 "A game is already bound to the factory." ) ;
	}
	
	public static Game clearInstance()
	{
		Game oGame = m_oGame ;
		m_oGame = null ;
		return oGame ;
	}
}
