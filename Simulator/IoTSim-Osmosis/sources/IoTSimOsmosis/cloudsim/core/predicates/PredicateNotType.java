/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package IoTSimOsmosis.cloudsim.core.predicates;

import IoTSimOsmosis.cloudsim.core.SimEvent;

/**
 * A predicate to select events that don't match specific tags.
 * 
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 * @see PredicateType
 * @see Predicate
 */
public class PredicateNotType extends Predicate {

	/** The tags. */
	private final int[] tags;

	/**
	 * Constructor used to select events whose tags do not match a given tag.
	 * 
	 * @param tag An event tag value
	 */
	public PredicateNotType(int tag) {
		tags = new int[] { tag };
	}

	/**
	 * Constructor used to select events whose tag values do not match any of the given tags.
	 * 
	 * @param tags the list of tags
	 */
	public PredicateNotType(int[] tags) {
		this.tags = tags.clone();
	}

	/**
	 * The match function called by {@link Simulation}, not used directly by the user.
	 * 
	 * @param ev the ev
	 * @return true, if match
	 */
	@Override
	public boolean match(SimEvent ev) {
		int tag = ev.getTag();
		for (int tag2 : tags) {
			if (tag == tag2) {
				return false;
			}
		}
		return true;
	}

}