package ise.antelope.tasks;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.Available;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.UpToDate;
import org.apache.tools.ant.taskdefs.condition.*;

/**
 * Extends ConditionBase so I can get access to the condition count and the
 * first condition. This is the class that the BooleanConditionTask is proxy
 * for.
 *
 * @author   danson
 */
public class BooleanConditionBase extends ProjectComponent {

    private Vector conditions = new Vector();


    /**
     * Gets the conditionCount attribute of the BooleanConditionBase object
     *
     * @return   The conditionCount value
     */
    public int getConditionCount() {
        return countConditions();
    }


    /**
     * Gets the firstCondition attribute of the BooleanConditionBase object
     *
     * @return   The firstCondition value
     */
    public Condition getFirstCondition() {
        return ( Condition ) getConditions().nextElement();
    }

    /**
     * Count the conditions.
     *
     * @return the number of conditions in the container
     * @since 1.1
     */
    public int countConditions() {
        return conditions.size();
    }

    /**
     * Iterate through all conditions.
     *
     * @return an enumeration to use for iteration
     * @since 1.1
     */
    public final Enumeration getConditions() {
        return conditions.elements();
    }

    public void addAvailable( Available a ) {
        add( a );
    }

    public void addChecksum( Checksum c ) {
        add( c );
    }

    public void addUptodate( UpToDate u ) {
        add( u );
    }

    public void addNot( Not n ) {
        add( n );
    }

    public void addAnd( And a ) {
        add( a );
    }

    public void addOr( Or o ) {
        add( o );
    }

    public void addEquals( Equals e ) {
        add( e );
    }

    public void addOs( Os o ) {
        add( o );
    }

    public void addIsSet( IsSet i ) {
        add( i );
    }

    public void addHttp( Http h ) {
        add( h );
    }

    public void addSocket( Socket s ) {
        add( s );
    }

    public void addFilesMatch( FilesMatch test ) {
        add( test );
    }

    public void addContains( Contains test ) {
        add( test );
    }

    public void addIsTrue( IsTrue test ) {
        add( test );
    }

    public void addIsFalse( IsFalse test ) {
        add( test );
    }

    public void addIsReference( IsReference i ) {
        add( i );
    }
    
    public void addIsPropertyTrue( IsPropertyTrue i ) {
        add( i );
    }

    public void addIsPropertyFalse( IsPropertyFalse i ) {
        add( i );
    }

    public void addIsGreaterThan( IsGreaterThan i ) {
        add( i );
    }

    public void addIsLessThan( IsLessThan i ) {
        add( i );
    }

    public void addMathEquals( MathEquals i ) {
        add( i );
    }

    public void addStartsWith( StartsWith i ) {
        add( i );
    }
    
    public void addEndsWith( EndsWith i ) {
        add( i );
    }

    public void addDateDifference(DateTimeDifference i) {
        add(i);   
    }
    
    public void addTimeDifference(DateTimeDifference i) {
        add(i);   
    }
    
    public void addDateBefore(DateTimeBefore i) {
        add(i);   
    }
    
    public void addTimeBefore(DateTimeBefore i) {
        add(i);   
    }

    /**
     * Add an arbitrary condition -- this doesn't work, it is copied from 
     * ConditionBase in Ant, and it doesn't work there either.
     * @param c a  condition
     * @since Ant 1.6
     */
    public void add( Condition c ) {
        conditions.addElement( c );
    }

}

