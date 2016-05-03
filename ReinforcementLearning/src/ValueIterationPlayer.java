import java.awt.DisplayMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * An agent that uses value iteration to play the game.
 * 
 * @author Mitch Parry
 * @version 2014-03-28
 * 
 */
public class ValueIterationPlayer extends Player
{
	private boolean calculated_optimal_policy = false;
	private boolean calculated_state_utilities = false;
	private boolean state_information_initialized = false;
    /**
     * The constructor takes the name.
     * 
     * @param name
     *            the name of the player.
     */
    public ValueIterationPlayer(String name)
    {
        super(name);
    }
	
	/**
	 * Returns the maximum change in the utility of any state in an iteration. 
	 * @param backup -A previous utility backup to compare to the current state utilities.
	 * @return policy_delta -The maximum change in the utility of any state. 
	 */
	private Double getUtilityDelta(HashMap<State,Double> backup) {
		double policy_delta = 0.0;
		
		for (HashMap.Entry<State,Double> backup_state : backup.entrySet()) {
			//System.out.format("CSV: %d", utility.get(backup_state.getKey()));
			double current_state_value = Math.abs(utility.get(backup_state.getKey()));
			double previous_state_value = Math.abs(backup_state.getValue());
			double current_state_delta = Math.abs(current_state_value - previous_state_value);
			if (current_state_delta > policy_delta) {
				policy_delta = current_state_delta;
			}
		}
		return policy_delta;
	}
	
	/**
	 * Returns a Set of states that are adjacent to the provided state. 
	 * @param start_state -The state from which to determine adjacent neighbors. 
	 * @param mdp -The Markov Decision Process.
	 * @return neighbors -A HashMap<State,Double> of state's and their expected discount utility-
	 * 	as calculated from the start_state.  
	 */
	private Set<State> getNeighboringStates(State start_state, MarkovDecisionProcess mdp) {
		Set<State> neighbors = new HashSet<State>();
		
		for (State state : mdp.getStates()) {
			for (String dir : mdp.getActions()) {
				double trans_prob = mdp.transProb(start_state, dir, state);
				if (trans_prob > 0.0) {
					//if (!neighbors.contains(state) && !state.equals(start_state)) {
					if (!neighbors.contains(state)) {
						neighbors.add(state);
					}
				}
			}
		}
		return neighbors;
	}

	/**
	 * Returns the expected reward/utility of a given state, as described in-
	 * 	AIMA: 17.2.1: The Bellman Equation. 
	 * @param state -The state to calculate the utility for. 
	 * @param mdp -The Markov Decision Process.
	 * @return state_utility -The utility/expected reward of a given state.
	 */
	private Double getStateUtility(State state, MarkovDecisionProcess mdp) {
		//Every state has four directions associated with it. 
		//Each direction is associated with an expected discounted utility. 
		Double state_utility;
		HashMap<String,Double> expected_discount_utility = new HashMap<String,Double>();
		
		for (String dir : mdp.getActions()) {
			double EDU = 0.0;
			for (State neighbor : getNeighboringStates(state, mdp)) {
				EDU += (utility.get(neighbor) * mdp.transProb(state, dir, neighbor));
				//EDU += utility.get(neighbor);
			}
			expected_discount_utility.put(dir, EDU);
		}
		//return the state's utility as defined in AIMA: 17.2.1
		state_utility = (Collections.max(expected_discount_utility.values()) * mdp.getGamma()) + state.reward();
		return state_utility;
	}
	
	/**
	 * Calculates the utility of every state and updates 'Utility'.
	 * @param mdp -The Markov Decision Process
	 */
	private void calculateGlobalUtility(MarkovDecisionProcess mdp) {
		for (State state : mdp.getStates()) {
			//GridWorld.display(mdp.getStates(), mdp.getCurrent(), utility);
			utility.replace(state, getStateUtility(state,mdp));
			//GridWorld.display(mdp.getStates(), mdp.getCurrent(), utility);
		}
	}

	/**
     * Initializes the utility and policy storage structures for the-
     *  ValueIterationPlayer. U(s) is initialized to be 0.0.   
     * @param mdp -The Markov Decision Process.
     */
    private void initializeStateInformation(MarkovDecisionProcess mdp) {
		utility = new HashMap<State,Double>();
	    policy = new HashMap<State,String>();
		//Add every state to utility and policy data structures.
		//Initialize the utility of state to be 0.0, and the policy to null:
		for (State state : mdp.getStates()) {
			//Given a state s, U(s) = 0.0. 
			utility.put(state, 0.0);
			policy.put(state, null);
		}
	}
    
    /**
     * Returns the adjacent cell with the highest utility (as determined via ValueIteration). 
     * @param state -The state from which to determine adjacent utility. 
     * @param mdp -The Markov Decision Process
     * @return best_neighbor -The adjacent state with the most utility. 
     */
    private State getOptimalNeighbor(State state, MarkovDecisionProcess mdp) {
    	Set<State> neighbors = getNeighboringStates(state, mdp);
    	double max_utility = Double.MIN_VALUE;
    	double neighbor_utility;
    	State best_neighbor = null;
    	for (State neighbor : neighbors) {
    		neighbor_utility = utility.get(neighbor);
    		if (neighbor_utility > max_utility) {
    			max_utility = neighbor_utility;
    			best_neighbor = neighbor;
    		}
    	}
    	return best_neighbor;
    }
    
    /**
     * Determines the optimal policy for the provided state. This is determined-
     * 	by finding the action that maximizes the probability of arriving at the optimal neighbor. 
     * @param state -The state to determine an optimal policy for. 
     * @param mdp -The Markov Decision Process.
     * @return optimal_state_policy -The action that an agent in 'state' should take to maximize-
     * 	its probability of reaching the adjacent state with the highest utility. 
     */
    private String calculateStatePolicy(State state, MarkovDecisionProcess mdp) {
    	double max_prob = 0.0;
    	String optimal_state_policy = null;
    	//The optimal_neighbor has the highest adjacent utility:
    	State optimal_neighbor = getOptimalNeighbor(state,mdp);
    	//Keep track of P(s'|s,a) for each direction of movement/action:
    	HashMap<String,Double> state_policies = new HashMap<String,Double>();
    	
    	//Determine the probability of arriving at the optimal neighbor for every action: 
    	for (String dir : mdp.getActions()) {
    		state_policies.put(dir, mdp.transProb(state, dir, optimal_neighbor));
    	}
    	
    	//Determine the action that maximizes the probability of arriving at the optimal neighbor:
    	for (HashMap.Entry<String,Double> policy : state_policies.entrySet()) {
    		double policy_probability = policy.getValue();
    		if (policy_probability > max_prob) {
    			max_prob = policy_probability;
    			optimal_state_policy = policy.getKey();
    		}
    	}
    	return optimal_state_policy;
    }
    
    /**
     * Determines the optimal policy for every state in the world. 
     * @param mdp -The Markov Decision Process.
     */
    private void calculateGlobalPolicy(MarkovDecisionProcess mdp) {
    	//For every state in the world, determine the optimal policy:
    	for (State state : mdp.getStates()) {
    		if (!state.isTerminal()) {
    			policy.replace(state, calculateStatePolicy(state,mdp));
    		}
    	}
    }
    
	/**
     * Plays the game using value iteration to precompute the policy and then
     * applying the policy in future moves. The Abstract Player class has
     * 
     * @param mdp
     *            the MDP.
     * @return the desired action.
     */
    public String play(MarkovDecisionProcess mdp)
    {
        final double EPS = 1e-3;
        double utility_delta = 0.0;
        double termination_coefficient = EPS * ((1 - mdp.getGamma())/mdp.getGamma());
        HashMap<State,Double> backup;
        if (!state_information_initialized) {
        	initializeStateInformation(mdp);
        	state_information_initialized = true;
        }
        if (!calculated_state_utilities) {
	        do {
	        	//GridWorld.display(mdp.getStates(), mdp.getCurrent(), utility);
	        	backup = new HashMap<State,Double>(utility);
	        	calculateGlobalUtility(mdp);
	        	utility_delta = getUtilityDelta(backup);
	        	System.out.format("Utility Delta At %f approaching termination coefficent: %f\n", utility_delta, termination_coefficient);
	        } while (utility_delta > termination_coefficient);
	        calculated_state_utilities = true;
        }
    	//System.out.println("Utility Calculated Successfully. Convergence Criterion Successful.");
    	//TODO: How to stop re-calculating the optimal policy?
    	if (!calculated_optimal_policy) {
    		calculateGlobalPolicy(mdp);
    		calculated_optimal_policy = true;
    	}
    	GridWorld.display(mdp.getStates(), mdp.getCurrent(), utility);
    	GridWorld.display(mdp.getStates(), mdp.getCurrent(), policy);
    	// if the utility hasn't been computed yet
        // use value iteration to compute the utility of each state.
        // use the utility to determine the optimal policy.
        // endif
        //
        // return the action for the current state from the policy.
        return policy.get(mdp.getCurrent());
    }
}
