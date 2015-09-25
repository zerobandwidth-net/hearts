/**
 * 
 */
package net.zerobandwidth.android.apps.heartsscoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Dialog for editing a box score.
 * Spawned by ScoreTableActivity.
 */
public class BoxScoreEditorDialogFragment
 extends DialogFragment
 implements View.OnClickListener, NumberPicker.OnValueChangeListener
{
	/** Logging tag. */
	private static final String LTAG = "BoxScoreEditor" ;
	
	/** Used to address the Intent extra that contains the BoxScore to be edited. */
	public static final String X_SCOREINPUT =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScoreEditorDialogFragment.oScore" ;
	
	public static final String O_NEWSCORE =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScoreEditorDialogFragment.oNewScore" ;
	
	public static final String AB_RULESET =
	 "net.zerobandwidth.android.apps.heartsscoreboard.BoxScoreEditorDialogFragment.abRuleSet" ;
	
	/** The box score to be edited by this dialog, in its original form. */
	private BoxScore m_oScore ;
	
	/** The update box score to be substituted in when we return. */
	private BoxScore m_oNewScore ;
	
	/** The rules applying to this score, fetched from the overall Game object. */
	private Game.RuleSet m_oRules ;
	
	/**
	 * Life is simply easier if this is accessible across the whole object.
	 */
	private View m_oDialogLayout ;
	
	/**
	 * A reference to the activity that spawned this event, cast as a
	 * listener.  The binding is established in onAttach().
	 */
	private BoxScoreEditorDialogFragment.Listener m_oListener ;
	
	/**
	 * Callers of this dialog must implement this interface to catch the events
	 * that are returned from it.
	 */
	public interface Listener
	{
		public void onScoreChanged( BoxScoreEditorDialogFragment fragDialog ) ;
	}
	
	/**
	 * Default constructor. 
	 */
	public BoxScoreEditorDialogFragment() {
		m_oScore = null ;
	}
	
	/**
	 * Returns the box score that is considered "current" by the dialog. Until
	 * the user clicks the "Update" button, the old score is still considered
	 * current. As soon as the score is updated, however, the new score will
	 * be returned.
	 * @return The "current" box score.
	 */
	public BoxScore getBoxScore()
	{
		return m_oScore ;
	}
	
	/**
	 * When the dialog is attached to an activity, we form a binding back to
	 * the calling activity as a listener for dialog events.
	 */
	public void onAttach( Activity oCaller )
	{
		super.onAttach(oCaller) ;
		try { m_oListener = (BoxScoreEditorDialogFragment.Listener)oCaller ; }
		catch( ClassCastException e ) { throw e ; }
	}
	
	/**
	 * Builds the dialog's layout and sets up the exit event handlers.
	 */
	public Dialog onCreateDialog( Bundle oState )
	{
		Activity actCaller = this.getActivity() ; // We need this more than once.
		AlertDialog.Builder oBuilder = new AlertDialog.Builder( actCaller ) ;
		
		oBuilder.setTitle( R.string.title_activity_BoxScoreEditor ) ;
		
		LayoutInflater oInflater = actCaller.getLayoutInflater() ;
		m_oDialogLayout = oInflater.inflate(R.layout.dialog_box_score_editor,null) ;
		oBuilder.setView(m_oDialogLayout) ;
		
		// If we're rebuilding from a saved state (as if the device has flipped
		// orientation, for example), then rebuild that state from the supplied
		// bundle.
		if( oState != null )
		{
			m_oScore = (BoxScore)(oState.getParcelable(X_SCOREINPUT)) ;
			m_oNewScore = (BoxScore)(oState.getParcelable(O_NEWSCORE)) ;
			m_oRules = new Game.RuleSet(oState.getBooleanArray(AB_RULESET)) ;
		}
		
		initialize() ;
		
		oBuilder.setPositiveButton( "Update", 
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface oDialog, int zID )
				{
					m_oScore.set(m_oNewScore) ;
					m_oListener.onScoreChanged(BoxScoreEditorDialogFragment.this) ;
				}
			}
		);
		
		oBuilder.setNegativeButton( "Cancel",
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface oDialog, int zID )
				{
					// Do nothing.
				}
			}
		);
		
		return oBuilder.create() ;
	}

	public void onSaveInstanceState( Bundle oState )
	{
		super.onSaveInstanceState(oState) ;
		oState.putParcelable( X_SCOREINPUT, m_oScore ) ;
		oState.putParcelable( O_NEWSCORE, m_oNewScore ) ;
		oState.putBooleanArray( AB_RULESET, m_oRules.toArray() ) ;
	}
	
	/**
	 *  Catches box score data from scoreboard.  Whenever this is called, the
	 *  temporary "new" box score is also reset.
	 */ 
	public void setBoxScore( BoxScore oScore )
	{
		m_oScore = oScore ;
		m_oNewScore = new BoxScore(oScore) ;
		return ;
	}
	
	/** Catches rule data from scoreboard. */
	public void setRules( Game.RuleSet oRules )
	{
		m_oRules = oRules ;
		return ;
	}
	
	/**
	 * Initializes the dialog's UI elements based on the values in the box
	 * score member object. 
	 */
	private void initialize()
	{
		View oElement ; // placeholder to be reused and recast repeatedly
		
		// The following shorthand became necessary for sanity.
		boolean bDoubleDeck = m_oRules.getRule(Game.OptionalRules.DOUBLEDECK) ;
		
		// Set up all the normal controls.
		
		oElement = m_oDialogLayout.findViewById(R.id.txt_OldScore) ;
		((TextView)oElement).setText( getString(R.string.txt_OldScore)
				+ Integer.toString(m_oScore.getScore(bDoubleDeck)) ) ;
		
		oElement = m_oDialogLayout.findViewById(R.id.txt_NewScore) ;
		((TextView)oElement).setText( getString(R.string.txt_NewScore)
				+ Integer.toString(m_oNewScore.getScore(bDoubleDeck)) ) ;
		
		oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ;
		NumberPicker npkHearts = (NumberPicker)oElement ;
		npkHearts.setMinValue(0) ;
		npkHearts.setMaxValue( bDoubleDeck ? 26 : 13 ) ;
		npkHearts.setValue( m_oScore.getHearts() ) ;
		npkHearts.setOnValueChangedListener(this) ;
		
		oElement = m_oDialogLayout.findViewById(R.id.chk_Queen1) ;
		oElement.setOnClickListener(this) ;
		((CheckBox)oElement).setChecked( m_oScore.getQueens() > 0 ) ;
		if( bDoubleDeck )
		{
			oElement = m_oDialogLayout.findViewById(R.id.chk_Queen2) ;
			oElement.setOnClickListener(this) ;
			switch( m_oScore.getQueens() )
			{
			case 0:
				hideControl(oElement,false) ;
				break ;
			case 1:
				showControl(oElement,false) ;
				break ;
			case 2:
				showControl(oElement,true) ;
				break ;
			default:;
			}
		}
		
		if( m_oRules.getRule(Game.OptionalRules.OMNIBUS) )
		{
			oElement = m_oDialogLayout.findViewById(R.id.chk_Omnibus1) ;
			oElement.setOnClickListener(this) ;
			oElement.setVisibility(View.VISIBLE) ;
			((CheckBox)oElement).setChecked( m_oScore.getOmnibus() > 0 ) ;
			if( bDoubleDeck )
			{
				oElement = m_oDialogLayout.findViewById(R.id.chk_Omnibus2) ;
				oElement.setOnClickListener(this) ;
				switch( m_oScore.getOmnibus() )
				{
				case 0:
					hideControl(oElement,false) ;
					break ;
				case 1:
					showControl(oElement,false) ;
					break ;
				case 2:
					showControl(oElement,true) ;
					break ;
				default:;
				}
			}
		}
		
		if( m_oRules.getRule(Game.OptionalRules.HOOLIGAN) )
		{
			oElement = m_oDialogLayout.findViewById(R.id.chk_Hooligan1) ;
			oElement.setVisibility(View.VISIBLE) ; 
			oElement.setOnClickListener(this) ;
			((CheckBox)oElement).setChecked( m_oScore.getHooligan() > 0 ) ;
			if( bDoubleDeck )
			{
				oElement = m_oDialogLayout.findViewById(R.id.chk_Hooligan2) ;
				oElement.setOnClickListener(this) ;
				switch( m_oScore.getHooligan() )
				{
				case 0:
					hideControl(oElement,false) ;
					break ;
				case 1:
					showControl(oElement,false) ;
					break ;
				case 2:
					showControl(oElement,true) ;
					break ;
				default:;
				}
			}
		}

		// Now the check boxes for the esoteric gameplay marks.
		
		oElement = m_oDialogLayout.findViewById(R.id.chk_Angel) ;
		oElement.setOnClickListener(this) ;
		((CheckBox)oElement).setChecked(m_oScore.getMark(BoxScore.Mark.ANGEL)) ;

		oElement = m_oDialogLayout.findViewById(R.id.chk_Dagger) ;
		oElement.setOnClickListener(this) ;
		((CheckBox)oElement).setChecked(m_oScore.getMark(BoxScore.Mark.DAGGER)) ;
		
		oElement = m_oDialogLayout.findViewById(R.id.chk_Fisher) ;
		oElement.setOnClickListener(this) ;
		((CheckBox)oElement).setChecked(m_oScore.getMark(BoxScore.Mark.FISHERMAN)) ;

		oElement = m_oDialogLayout.findViewById(R.id.chk_Poisoner) ;
		oElement.setOnClickListener(this) ;
		((CheckBox)oElement).setChecked(m_oScore.getMark(BoxScore.Mark.POISONER)) ;
		
		// Now set up the score override controls.
		
		oElement = m_oDialogLayout.findViewById(R.id.rad_NewMoon) ;
		oElement.setOnClickListener(this) ;
		((RadioButton)oElement).setText(
				getString(R.string.rad_NewMoon)
				+ ( bDoubleDeck ? "(-52)" : "(-26)" ) ) ;
		
		oElement = m_oDialogLayout.findViewById(R.id.rad_FullMoon) ;
		oElement.setOnClickListener(this) ;
		((RadioButton)oElement).setText(
				getString(R.string.rad_FullMoon)
				+ ( bDoubleDeck ? "(+52)" : "(+26)" ) ) ;
		
		oElement = m_oDialogLayout.findViewById(R.id.rad_FullMoonVictim) ;
		oElement.setOnClickListener(this) ;
		((RadioButton)oElement).setText(
				getString(R.string.rad_FullMoonVictim)
				+ ( bDoubleDeck ? "(+52)" : "(+26)" ) ) ;
		
		if( bDoubleDeck )
		{ // Set up half-moon-related items.
			oElement = m_oDialogLayout.findViewById(R.id.rad_HalfMoon) ;
			oElement.setVisibility(View.VISIBLE) ;
			oElement.setOnClickListener(this) ;
			oElement = m_oDialogLayout.findViewById(R.id.rad_HalfMoonVictim) ;
			oElement.setVisibility(View.VISIBLE) ;
			oElement.setOnClickListener(this) ;
		}
		
		m_oDialogLayout.findViewById(R.id.rad_NormalHand).setOnClickListener(this) ;
		m_oDialogLayout.findViewById(R.id.chk_Failed).setOnClickListener(this) ;
		m_oDialogLayout.findViewById(R.id.chk_NullHand).setOnClickListener(this) ;
		
		// Manage complex interactions between controls caused by overrides.
		
		if( m_oScore.getMark(BoxScore.Mark.NEWMOON) )
		{ // set up a new moon
			((RadioButton)(m_oDialogLayout.findViewById(R.id.rad_NewMoon))).setChecked(true) ;
			
			hideControl(R.id.chk_Failed,false) ;
			hideControl(R.id.chk_NullHand,false) ;
			
			m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(false) ;
			
			hideControl(R.id.chk_Queen1,true) ;
			if( bDoubleDeck )
				hideControl(R.id.chk_Queen2,true) ;
		}
		else if( m_oScore.getMark(BoxScore.Mark.FULLMOON) )
		{ // set up a full moon
			((RadioButton)(m_oDialogLayout.findViewById(R.id.rad_FullMoon))).setChecked(true) ;

			hideControl(R.id.chk_Failed,false) ;
			hideControl(R.id.chk_NullHand,false) ;
			
			m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(false) ;
			
			hideControl(R.id.chk_Queen1,true) ;
			if( bDoubleDeck )
				hideControl(R.id.chk_Queen2,true) ;
		}
		else if( m_oScore.getMark(BoxScore.Mark.FULLMOONVICTIM) )
		{ // set up a full moon victim
			((RadioButton)(m_oDialogLayout.findViewById(R.id.rad_FullMoonVictim))).setChecked(true) ;
			
			m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(false) ;
			
			hideControl(R.id.chk_Queen1,false) ;
			if( bDoubleDeck )
				hideControl(R.id.chk_Queen2,false) ;
		}
		else if( m_oScore.getMark(BoxScore.Mark.HALFMOON) )
		{ // set up a half-moon
			((RadioButton)(m_oDialogLayout.findViewById(R.id.rad_HalfMoon))).setChecked(true) ;

			hideControl(R.id.chk_Failed,false) ;
			hideControl(R.id.chk_NullHand,false) ;
			
			m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(false) ;

			hideControl(R.id.chk_Queen1,true) ;
			if( bDoubleDeck )
				hideControl(R.id.chk_Queen2,true) ;
		}
		else if( m_oScore.getMark(BoxScore.Mark.HALFMOONVICTIM) )
		{ // set up a half-moon victim
			((RadioButton)(m_oDialogLayout.findViewById(R.id.rad_HalfMoonVictim))).setChecked(true) ;

			m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(false) ;

			hideControl(R.id.chk_Queen1,false) ;
			if( bDoubleDeck )
				hideControl(R.id.chk_Queen2,false) ;
		}
		else
		{ // set up a normal hand
			((RadioButton)(m_oDialogLayout.findViewById(R.id.rad_NormalHand))).setChecked(true) ;
		}
		
		if( m_oScore.getMark(BoxScore.Mark.NULLHAND) )
		{ // set up a null hand
			((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_NullHand))).setChecked(true) ;
			
			hideControl(R.id.rad_NewMoon,false) ;
			hideControl(R.id.rad_FullMoon,false) ;
			hideControl(R.id.rad_HalfMoon,false) ;
			
			m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(false) ;
			
			hideControl(R.id.chk_Queen1,false) ;
			hideControl(R.id.chk_Omnibus1,false) ;
			hideControl(R.id.chk_Hooligan1,false) ;
			if( bDoubleDeck )
			{
				hideControl(R.id.chk_Queen2,false) ;
				hideControl(R.id.chk_Omnibus2,false) ;
				hideControl(R.id.chk_Hooligan2,false) ;
			}
		}
		
		if( m_oScore.getMark(BoxScore.Mark.FAILURE) )
		{ // set up a moonshot failure
			((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Failed))).setChecked(true) ;
			
			hideControl(R.id.rad_NewMoon,false) ;
			hideControl(R.id.rad_FullMoon,false) ;
			hideControl(R.id.rad_HalfMoon,false) ;
		}
	}
	
	/**
	 * (View.OnClickListener) Catches events from all of the checkboxes and
	 * radio buttons.  The method sorts out the routing to the appropriate
	 * dialog-scope handler method.
	 * @param oView The checkbox or radio button that was clicked.
	 */
	public void onClick( View oView )
	{
		switch( oView.getId() )
		{ // Check the scoring marks that have overriding score effects.
		case R.id.rad_NewMoon:
		case R.id.rad_FullMoon:
		case R.id.rad_FullMoonVictim:
		case R.id.rad_HalfMoon:
		case R.id.rad_HalfMoonVictim:
		case R.id.chk_Failed:
		case R.id.chk_NullHand:
		case R.id.rad_NormalHand:
			this.onScoreOverrideClicked(oView) ;
			break ;
		case R.id.chk_Angel:
			m_oNewScore.setMark( BoxScore.Mark.ANGEL, ((CheckBox)oView).isChecked() ) ;
			break ;
		case R.id.chk_Dagger:
			m_oNewScore.setMark( BoxScore.Mark.DAGGER, ((CheckBox)oView).isChecked() ) ;
			break ;
		case R.id.chk_Fisher:
			m_oNewScore.setMark( BoxScore.Mark.FISHERMAN, ((CheckBox)oView).isChecked() ) ;
			break ;
		case R.id.chk_Poisoner:
			m_oNewScore.setMark( BoxScore.Mark.POISONER, ((CheckBox)oView).isChecked() ) ;
			break ;
		case R.id.chk_Queen1:
			if( m_oRules.getRule(Game.OptionalRules.DOUBLEDECK) )
			{ // adjust status of "took both queens" checkbox
				if( ((CheckBox)oView).isChecked() )
					showControl( R.id.chk_Queen2, m_oNewScore.getQueens() == 2 ) ;
			    else // not checked
			    	hideControl( R.id.chk_Queen2, false ) ;
			}
			this.onScoreChange() ;
			break ;
		case R.id.chk_Omnibus1:
			if( m_oRules.getRule(Game.OptionalRules.DOUBLEDECK) )
			{ // adjust status of "took both jacks" checkbox
				if( ((CheckBox)oView).isChecked() )
					showControl( R.id.chk_Omnibus2, m_oNewScore.getOmnibus() == 2 ) ;
			    else // not checked
			    	hideControl( R.id.chk_Omnibus2, false ) ;
			}
			this.onScoreChange() ;
			break ;
		case R.id.chk_Hooligan1:
			if( m_oRules.getRule(Game.OptionalRules.DOUBLEDECK) )
			{ // adjust status of "took both hooligans" checkbox
				if( ((CheckBox)oView).isChecked() )
					showControl( R.id.chk_Hooligan2, m_oNewScore.getHooligan() == 2 ) ;
			    else // not checked
			    	hideControl( R.id.chk_Hooligan2, false ) ;
			}
			this.onScoreChange() ;
			break ;
		default:;
			this.onScoreChange() ;
			break ;
		}
		
		return ;
	}
	
	/**
	 * Catches the change event from the number picker.
	 * @param npkPicker The number picker.
	 * @param zOld The picker's old value.
	 * @param zNew The picker's new value.
	 */
	public void onValueChange( NumberPicker npkPicker, int zOld, int zNew )
	{
		this.onScoreChange() ;
	}
	
	/**
	 * The click listeners for the checkboxes, and the change listener for
	 * the number picker, all come here.  This method is also called after
	 * onMarksRadioClicked() to calculate the normal part of a box score
	 * that may or may not be under the influence of special marks like a
	 * moonshot or a null hand.
	 */
	private void onScoreChange()
	{
		boolean bDoubleDeck = m_oRules.getRule(Game.OptionalRules.DOUBLEDECK) ;

		m_oNewScore.setHearts( ((NumberPicker)(m_oDialogLayout.findViewById(R.id.npk_HeartCount))).getValue() ) ;

		int z = 0 ;
		if( ((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Queen1))).isChecked() )
			++z ;
		if( bDoubleDeck && ((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Queen2))).isChecked() )
			++z ;
		m_oNewScore.setQueens(z) ;
		
		if( m_oRules.getRule(Game.OptionalRules.OMNIBUS) )
		{
		    z = 0 ;
		    if( ((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Omnibus1))).isChecked() )
    			++z ;
		    if( bDoubleDeck && ((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Omnibus2))).isChecked() )
		    	++z ;
		    m_oNewScore.setOmnibus(z) ;
		}
		
		if( m_oRules.getRule(Game.OptionalRules.HOOLIGAN) )
		{
			z = 0 ;
			if( ((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Hooligan1))).isChecked() )
				++z ;
			if( bDoubleDeck && ((CheckBox)(m_oDialogLayout.findViewById(R.id.chk_Hooligan2))).isChecked() )
				++z ;
			m_oNewScore.setHooligan(z) ;
		}
		
		// Now we can set the score display.
		
		((TextView)(m_oDialogLayout.findViewById(R.id.txt_NewScore))).setText( 
				getString(R.string.txt_NewScore)
				+ Integer.toString(m_oNewScore.getScore(bDoubleDeck)) ) ;
		
		return ;
	}
	
	/**
	 * The click handler for the radio buttons in the dialog.  This will also
	 * set the scoring marks in the box score.  The setMark() method in
	 * BoxScore is smart enough to handle mutual exclusivity among the marks
	 * themselves, and the renderer will handle exclusivity among the radio
	 * buttons. 
	 * @param oView The radio button that was clicked.
	 */
	private void onScoreOverrideClicked( View oView )
	{
		View oElement ; // placeholder to be reused and recast repeatedly
		boolean bChecked = ((CompoundButton)oView).isChecked() ;
		boolean bDoubleDeck = m_oRules.getRule(Game.OptionalRules.DOUBLEDECK) ;
		
		switch( oView.getId() )
		{
		case R.id.rad_NewMoon:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.NEWMOON, true ) ;

			    hideControl(R.id.chk_Failed,false) ;
			    hideControl(R.id.chk_NullHand,false) ; 
			    
				oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ;
			    ((NumberPicker)oElement).setValue( bDoubleDeck ? 26 : 13 ) ;
			    oElement.setEnabled(false) ;
				
				hideControl(R.id.chk_Queen1,true) ;
				if( bDoubleDeck )
					hideControl(R.id.chk_Queen2,true) ;
			}
			break ;
		case R.id.rad_FullMoon:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.FULLMOON, true ) ;
				
				hideControl(R.id.chk_Failed,false) ;
				hideControl(R.id.chk_NullHand,false) ;
				
			    oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ;
			    ((NumberPicker)oElement).setValue( bDoubleDeck ? 26 : 13 ) ;
			    oElement.setEnabled(false) ;
			    
			    hideControl(R.id.chk_Queen1,true) ;
			    if( bDoubleDeck )
			    	hideControl(R.id.chk_Queen2,true) ;
			}
			break ;
		case R.id.rad_FullMoonVictim:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.FULLMOONVICTIM, true ) ;
				
				showControl(R.id.chk_Failed,m_oNewScore.getMark(BoxScore.Mark.FAILURE)) ;
				showControl(R.id.chk_NullHand,m_oNewScore.getMark(BoxScore.Mark.NULLHAND)) ; 
				
			    oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ;
			    ((NumberPicker)oElement).setValue(0) ;
			    oElement.setEnabled(false) ;
			    
			    hideControl(R.id.chk_Queen1,false) ;
			    if( bDoubleDeck )
			    	hideControl(R.id.chk_Queen2,false) ;
			}
			break ;
		case R.id.rad_HalfMoon:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.HALFMOON, true ) ;
				
				hideControl(R.id.chk_Failed,false) ;
				hideControl(R.id.chk_NullHand,false) ;
				
			    oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ;
			    ((NumberPicker)oElement).setValue(26) ;
			    oElement.setEnabled(false) ;				

			    hideControl(R.id.chk_Queen1,true) ;
			    if( bDoubleDeck )
			    	hideControl(R.id.chk_Queen2,true) ;
			}
			break ;
		case R.id.rad_HalfMoonVictim:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.HALFMOONVICTIM, true ) ;
				
				showControl(R.id.chk_Failed,m_oNewScore.getMark(BoxScore.Mark.FAILURE)) ;
				showControl(R.id.chk_NullHand,m_oNewScore.getMark(BoxScore.Mark.NULLHAND)) ; 
				
			    oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ;
			    ((NumberPicker)oElement).setValue(0) ;
			    oElement.setEnabled(false) ;

			    hideControl(R.id.chk_Queen1,false) ;
			    if( bDoubleDeck )
			    	hideControl(R.id.chk_Queen2,false) ;
			}
			break ;
		case R.id.rad_NormalHand:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.NEWMOON, false ) ;
				m_oNewScore.setMark( BoxScore.Mark.FULLMOON, false ) ;
				m_oNewScore.setMark( BoxScore.Mark.FULLMOONVICTIM, false ) ;
				m_oNewScore.setMark( BoxScore.Mark.HALFMOON, false ) ;
				m_oNewScore.setMark( BoxScore.Mark.HALFMOONVICTIM, false ) ;
				
				m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(true) ;
				
				showControl( R.id.chk_Failed, m_oNewScore.getMark(BoxScore.Mark.FAILURE) ) ;
				showControl( R.id.chk_NullHand, m_oNewScore.getMark(BoxScore.Mark.NULLHAND) ) ;
				
				showControl( R.id.chk_Queen1, m_oNewScore.getQueens() > 0 ) ;
				if( bDoubleDeck && m_oNewScore.getQueens() > 0 )
					showControl( R.id.chk_Queen2, m_oNewScore.getQueens() == 2 ) ;
				
				if( m_oRules.getRule(Game.OptionalRules.OMNIBUS) )
				{
					showControl( R.id.chk_Omnibus1, m_oNewScore.getOmnibus() > 0 ) ;
					if( bDoubleDeck && m_oNewScore.getOmnibus() > 0 )
						showControl( R.id.chk_Omnibus2, m_oNewScore.getOmnibus() == 2 ) ;
				}
				
				if( m_oRules.getRule(Game.OptionalRules.HOOLIGAN) )
				{
					showControl( R.id.chk_Hooligan1, m_oNewScore.getHooligan() > 0 ) ;
					if( bDoubleDeck && m_oNewScore.getHooligan() > 0 )
						showControl( R.id.chk_Hooligan2, m_oNewScore.getHooligan() == 2 ) ;
				}
			}
			break ;
		case R.id.chk_Failed:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.FAILURE, true ) ;
				
				hideControl(R.id.rad_NewMoon,false) ;
				hideControl(R.id.rad_FullMoon,false) ;
				if( bDoubleDeck )
					hideControl(R.id.rad_HalfMoon,false) ;
			}
			else // unchecked
			{
				m_oNewScore.setMark( BoxScore.Mark.FAILURE, false ) ;
				
				showControl( R.id.rad_NewMoon, m_oNewScore.getMark(BoxScore.Mark.NEWMOON) ) ;
				showControl( R.id.rad_FullMoon, m_oNewScore.getMark(BoxScore.Mark.FULLMOON) ) ;
				if( bDoubleDeck )
					showControl( R.id.rad_HalfMoon, m_oNewScore.getMark(BoxScore.Mark.HALFMOON) ) ;
			}
			break ;
		case R.id.chk_NullHand:
			if( bChecked )
			{
				m_oNewScore.setMark( BoxScore.Mark.NULLHAND, true ) ;
				
				hideControl(R.id.rad_NewMoon,false) ;
				hideControl(R.id.rad_FullMoon,false) ;
				hideControl(R.id.rad_HalfMoon,false) ;
				
				oElement = m_oDialogLayout.findViewById(R.id.npk_HeartCount) ; 
				((NumberPicker)oElement).setEnabled(false) ;
				((NumberPicker)oElement).setValue(0) ;
				
				hideControl(R.id.chk_Queen1,false) ;
				if( bDoubleDeck )
					hideControl(R.id.chk_Queen2,false) ;
				
				if( m_oRules.getRule(Game.OptionalRules.OMNIBUS) )
				{
					hideControl(R.id.chk_Omnibus1,false) ;
					if( bDoubleDeck )
						hideControl(R.id.chk_Omnibus2,false) ;
				}
				
				if( m_oRules.getRule(Game.OptionalRules.HOOLIGAN) )
				{
					hideControl(R.id.chk_Hooligan1,false) ;
					if( bDoubleDeck )
						hideControl(R.id.chk_Hooligan2,false) ;
				}
			}
			else // unchecked
			{
				m_oNewScore.setMark( BoxScore.Mark.NULLHAND, false ) ;
				
				showControl( R.id.rad_NewMoon, m_oNewScore.getMark(BoxScore.Mark.NEWMOON) ) ;
				showControl( R.id.rad_FullMoon, m_oNewScore.getMark(BoxScore.Mark.FULLMOON) ) ;
				
				if( bDoubleDeck )
					showControl( R.id.rad_HalfMoon, m_oNewScore.getMark(BoxScore.Mark.HALFMOON) ) ;
				
				m_oDialogLayout.findViewById(R.id.npk_HeartCount).setEnabled(true) ;
				
				showControl( R.id.chk_Queen1, m_oNewScore.getQueens() > 0 ) ;
				if( bDoubleDeck && m_oNewScore.getQueens() > 0 )
					showControl( R.id.chk_Queen2, m_oNewScore.getQueens() == 2 ) ;
				
				if( m_oRules.getRule(Game.OptionalRules.OMNIBUS) )
				{
					showControl( R.id.chk_Omnibus1, m_oNewScore.getOmnibus() > 0 ) ;
					if( bDoubleDeck && m_oNewScore.getOmnibus() > 0 )
						showControl( R.id.chk_Omnibus2, m_oNewScore.getOmnibus() == 2 ) ;
				}
				
				if( m_oRules.getRule(Game.OptionalRules.HOOLIGAN) )
				{
					showControl( R.id.chk_Hooligan1, m_oNewScore.getHooligan() > 0 ) ;
					if( bDoubleDeck && m_oNewScore.getHooligan() > 0 )
						showControl( R.id.chk_Hooligan2, m_oNewScore.getHooligan() == 2 ) ;
				}
			}
			break ;
		default : break ;
		}
		
		this.onScoreChange() ;
		
		return ;
	}
	
	/**
	 * Enables and reveals one of the radio buttons or check boxes.
	 * @param oView points to the control that is being revealed
	 * @param bChecked indicates whether the control should appear checked
	 */
	private void showControl( View oView, boolean bChecked )
	{
		oView.setEnabled(true) ;
		oView.setVisibility(View.VISIBLE) ;
		
		try { ((CompoundButton)oView).setChecked(bChecked) ; }
		catch( ClassCastException e )
		{ Log.w(LTAG, "showControl() couldn't cast View to CompoundButton.", e) ; }
		
		return ;
	}
	
	/**
	 * Enables and reveals one of the radio buttons or check boxes.
	 * @param zViewId provides the ID to be sought in the layout
	 * @param bChecked indicates whether the control should appear checked
	 */
	private void showControl( int zViewId, boolean bChecked )
	{
		showControl( m_oDialogLayout.findViewById(zViewId), bChecked ) ;
		return ;
	}
	
	/**
	 * Disables and conceals one of the radio buttons or check boxes.
	 * @param oView points to the control that is being concealed
	 * @param bChecked indicates whether the control should be checked.
	 */
	private void hideControl( View oView, boolean bChecked )
	{
		oView.setEnabled(false) ;
		oView.setVisibility(View.GONE) ;
		
		try { ((CompoundButton)oView).setChecked(bChecked) ; }
		catch( ClassCastException e )
		{ Log.w(LTAG, "hideControl() couldn't cast View to CompoundButton.", e) ; }
		
		return ;
	}
	
	/**
	 * Disables and conceals one of the radio buttons or check boxes.
	 * @param zViewId provides the ID to be sought in the layout
	 * @param bChecked indicates whether the control should appear checked
	 */
	private void hideControl( int zViewId, boolean bChecked )
	{
		hideControl( m_oDialogLayout.findViewById(zViewId), bChecked ) ;
		return ;
	}
}
