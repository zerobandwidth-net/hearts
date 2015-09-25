package net.zerobandwidth.android.apps.heartsscoreboard ;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ScoreTableBackAlertDialog
 extends DialogFragment
{
	/**
	 * The activity to which this dialog is attached must implement this
	 * interface to catch the confirmation event. 
	 */
	public interface Listener
	{
		public void onBackButtonConfirmed() ;
	}
	
	/**
	 * Binds to the dialog's creator in onAttach().
	 */
	private ScoreTableBackAlertDialog.Listener m_oListener ;
	
	public ScoreTableBackAlertDialog()
	{
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Attaches dialog to its creator and registers it as a listener.
	 */
	@Override
	public void onAttach( Activity oCreator )
	{
		super.onAttach(oCreator) ;
		try { m_oListener = (ScoreTableBackAlertDialog.Listener)oCreator ; }
		catch( ClassCastException e ) { throw e ; }
	}
	
	@Override
	public Dialog onCreateDialog( Bundle oState )
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this.getActivity()) ;
		adb.setIcon( R.drawable.icon_exclamation ) ;
		adb.setTitle( R.string.title_dialog_ScoreTableBackAlert ) ; 
		adb.setMessage( R.string.txt_ScoreTableBackAlert ) ;
		adb.setPositiveButton( "Yes",
		  new ScoreTableBackAlertDialog.PositiveButtonClickListener() ) ;
		adb.setNegativeButton( "No",
		  new ScoreTableBackAlertDialog.NegativeButtonClickListener() ) ;
		return adb.create() ;
	}
	
	/**
	 * I dislike inline classes.
	 */
	private class PositiveButtonClickListener
	 implements DialogInterface.OnClickListener
	{
		@Override
		public void onClick( DialogInterface d, int n )
		{
			m_oListener.onBackButtonConfirmed() ;
		}
	}

	/**
	 * Yep, still dislike inline classes.
	 */
	private class NegativeButtonClickListener
	 implements DialogInterface.OnClickListener
	{
		@Override
		public void onClick( DialogInterface d, int n )
		{
			// Do nothing.
		}
	}
}
