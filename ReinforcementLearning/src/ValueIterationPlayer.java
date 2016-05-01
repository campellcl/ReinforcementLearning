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
     * Returns a list of directly adjacent states for the provided start_state.
     * @param start_state -The state from which to determine adjacent neighbors. 
     * @param mdp -The MarkovDecisionProcess.
     * @return neighbors -A HashMap comprised of adjacent states and-
     * 	the action required to arrive at said state from the start_state.
     */
    /*
    private HashMap<State, String> getNeighboringStates(State start_state, MarkovDecisionProcess mdp) {
    	HashMap<State, String> neighbors = new HashMap<State, String>();
    	State goal_state = getGoalState(mdp);
    	State end_state;
    	
    	if (start_state.equals(goal_state)) {
    		for (int i = 0; i < mdp.getStates().size(); i++) {
    			start_state = mdp.getStates().get(i);
    			for (String dir : mdp.getActions()) {
    				double prob = mdp.transProb(start_state, dir, goal_state);
    				if (prob > 0.0) {
    					neighbors.putIfAbsent(start_state, dir);
    				}
    			}
    		}
    	} else {
    		for (int i = 0; i < mdp.getStates().size(); i++) {
    			end_state = mdp.getStates().get(i);
    			for (String dir : mdp.getActions()) {
    				double prob = mdp.transProb(start_state, dir, end_state);
    				if (prob > 0.0) {
    					neighbors.put(end_state, dir);
    				}
    			}
    		}
    	}
    	return neighbors;
    }
    */
    
    /**
     * Returns a HashMap of neighboring states with their associated expected discount utility.
     * @param start_state -The state from which to determine adjacent neighbors. 
     * @param mdp -The Markov Decision Process.
     * @return neighbors -A HashMap<State,Double> of state's and their expected discount utility-
     * 	as calculated from the start_state.  
     */
    private Set<State> getNeighboringStates(State start_state, MarkovDecisionProcess mdp) {
    	Set<State> neighbors = new HashSet<State>();
    	State end_state;
    	
    	for (State state : mdp.getStates()) {
    		for (String dir : mdp.getActions()) {
    			double trans_prob = mdp.transProb(start_state, dir, state);
    			if (trans_prob > 0.0) {
    				if (!neighbors.contains(state)) {
    					neighbors.add(state);
    				}
    			}
    		}
    	}
    	return neighbors;
    }
    
    /**
     * Returns a list of terminal states (goal state included).
     * @param mdp -The MarkovDecisionProcess
     * @return terminal_states -A list of terminal states (including the goal state).
     */
    private List<State> getTerminalStates(MarkovDecisionProcess mdp) {
    	List<State> terminal_states = new ArrayList<State>();
    	for (State st : mdp.getStates()) {
    		if (st.isTerminal()) {
    			terminal_states.add(st);
    		}
    	}
    	return terminal_states;
    }
    
    /**
     * Returns the state that is the terminal goal state. 
     * @param mdp -The MarkovDecisionProcess
     * @return goal_state -The positive terminal reward state.
     */
    private State getGoalState(MarkovDecisionProcess mdp) {
    	double max_reward = Integer.MIN_VALUE;
    	State goal_state = null;
    	
    	for (HashMap.Entry<State, Double> state : utility.entrySet()) {
    		if (state.getValue() > max_reward) {
    			max_reward = state.getValue();
    			goal_state = state.getKey();
    		}
    	}
    	return goal_state;
    }
    
    /**
     * Returns the adjacent state with the highest reward. 
     * @param current_state_reward -The reward of the current state. 
     * @param neighboring_states -A HashMap of neighboring states and the-
     * 	action required to reach them from the current state. 
     * @return best_neighbor -The neighboring state with the highest reward. 
     */
    private HashMap.Entry<State, String> getStatePolicy(State current_state, HashMap<State, String> neighboring_states) {
    	double max_reward = Integer.MIN_VALUE;
    	HashMap.Entry<State, String> best_neighbor = null;
    	for (HashMap.Entry<State, String> adj_state : neighboring_states.entrySet()) {
    		if (adj_state.getKey().reward() > max_reward) {
    			max_reward = current_state.reward() + adj_state.getKey().reward();
    			best_neighbor = adj_state;
    		}
    	}
    	return best_neighbor;
    }
    
    /**
     * Determines the current best policy of state, action pairs for a given iteration. 
     * @param mdp -The MarkovDecisionProcess 
     * @return policy -The decided policy for the specific iteration. 
     */
    /*
    private HashMap<State, String> getBackup(MarkovDecisionProcess mdp) {
    	HashMap<State, String> policy = new HashMap<State, String>();
    	State goal_state = getGoalState(mdp);
    	
    	for (State state : mdp.getStates()) {
    		HashMap<State, String> neighbors = getNeighboringStates(state, mdp);
    		HashMap.Entry<State, String> best_neighbor = getOptimalNeighborState(state.reward(), neighbors);
    		policy.put(state, best_neighbor.getValue());
    	}
    	return policy;
    }
    */
    private double determinePolicyDelta(HashMap<State, String> old_policy, HashMap<State, String> new_policy) {
    	//TODO: method body.
    	return Double.NaN;
    }
    
    /**
     * Returns the neighboring state with the highest expected value. 
     * @param start_state -The state of origin.
     * @param mdp -The Markov Decision Process
     * @return optimal_neighbor -A <K,V> pair <State,String> comprised of:
     * 	-State -The optimal state to transition to the terminal goal state from.
     *  -String -The direction to be taken by the State to arrive at the terminal goal state. 
     */
    private HashMap.Entry<State,String> getOptimalNeighbor(State start_state, MarkovDecisionProcess mdp) {
    	HashMap.Entry<State,String> optimal_neighbor = null;
    	double bestValue = Double.MIN_VALUE;
    	double expectedValue = Double.NaN;
    	HashMap<State,String> neighbors = getNeighboringStates(start_state, mdp);
    	
    	//Calculate the optimal expected value.
    	for (HashMap.Entry<State,String> neighboring_state : neighbors.entrySet()) {
    		//calculate the expected value obtained by a transition from-
    		//	the current state to the specified neighboring state. 
    		expectedValue = calculateExpectedValue(mdp.getCurrent(), 
    				neighboring_state.getValue(), neighboring_state.getKey(),mdp);
    		
    		//if the expected value of the neighboring state is the better than the other neighbors:
    		if (expectedValue >= bestValue) {
    			bestValue = expectedValue;
    			optimal_neighbor = neighboring_state;
    		}
    	}
    	return optimal_neighbor;
    	
    }
    
    /**
     * Calculates the expected value obtained by the given action.
     * @param start_state -The starting state from which the action is to be made. 
     * @param action -The action to apply to start_state that results in end_state.
     * @param end_state -The resulting state after the action has been applied. 
     * @param mdp -The Markov Decision Process.
     * @return expectedValue -The expected value obtained by executing the specified action.
     */
    private double calculateExpectedValue(State start_state, String action, State end_state, MarkovDecisionProcess mdp) {
    	double expectedValue;
    	double trans_prob = mdp.transProb(start_state, action, end_state);
    	expectedValue = trans_prob * utility.get(end_state);
    	return expectedValue;
    }
    
    private void calculateGlobalPolicy(MarkovDecisionProcess mdp) {
    	//TODO: finish method body. 
    	GridWorld.display(mdp.getStates(), mdp.getCurrent());
    	State goal_state = getGoalState(mdp);
    	//Find the state with the highest expected value that is directly adjacent to the terminal goal state:
    	HashMap.Entry<State,String> optimal_neighbor = getOptimalNeighbor(goal_state, mdp);
    	//Update the policy structure to reflect the best choice for the neighbor:
    	policy.replace(optimal_neighbor.getKey(), optimal_neighbor.getValue());
    	GridWorld.display(mdp.getStates(), mdp.getCurrent());
    	
    }
    
    /**
     * Computes the AVERAGE optimal policy, given repeated backup iterations.
     * @param mdp -The MarkovDecisionProcess
     * @return -TODO: void
     */
    private HashMap<State, String> getOptimalPolicy(MarkovDecisionProcess mdp) {
    	//TODO: Determine criteria for convergence. Repeatedly call getBackup() until convergence. 
    	HashMap<State, Double> utility;
    	return null;
    }
    
    
    private void getOptimalNeighbor2(State start_state, Set<State> neighbors, MarkovDecisionProcess mdp) {
		//we want the maximum of 
		HashMap<State,Double> 
		for (State state : neighbors) {
			
			for (String dir : mdp.getActions()) {
				
			}
		}
	}

	private Double getStateUtility(State state, MarkovDecisionProcess mdp) {
		//Every state has four directions associated with it. 
		//Each direction is associated with an expected discounted utility. 
		Double state_utility;
		HashMap<String,Double> expected_discount_utility = new HashMap<String,Double>();
		
		for (String dir : mdp.getActions()) {
			double EDU = 0.0;
			for (State neighbor : getNeighboringStates(state, mdp)) {
				EDU += (utility.get(neighbor) * mdp.transProb(state, dir, neighbor));
			}
			expected_discount_utility.put(dir, EDU);
		}
		//return the state's utility as defined in AIMA:17.2.1
		state_utility = Collections.max(expected_discount_utility.values()) + state.reward() + mdp.getGamma();
		return state_utility;
	}
	
	private void calculateGlobalUtility(MarkovDecisionProcess mdp) {
		//TODO: finish method body. 
		State goal_state = getGoalState(mdp);
		Set<State> neighbors = getNeighboringStates(goal_state, mdp);
		//Find the state with the highest expected value that is directly adjacent to the terminal goal state:
		State optimal_neighbor = getOptimalNeighbor(goal_state, neighbors); 
		
		HashMap.Entry<State,String> optimal_neighbor = getOptimalNeighbor(goal_state, mdp);
		double neighbor_utility = calculateExpectedValue(optimal_neighbor.getKey(), optimal_neighbor.getValue(), goal_state, mdp);
		//Update the Utility structure for the optimal neighbor. 
		utility.replace(optimal_neighbor.getKey(), neighbor_utility);
		//Continue on for every state until its utility is fully calculated.
		
		for (State state : mdp.getStates()) {
			
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
			//Given state s, U(s) = 0.0. 
			utility.put(state, 0.0);
			policy.put(state, null);
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
    	//TODO: Is EPS: a. Epsolon? b. The Discount Factor.
        final double EPS = 1e-3;
        
    	initializeStateInformation(mdp);
    	
    	
    	calculateGlobalUtility(mdp);
    	calculateGlobalPolicy(mdp);
    	// if the utility hasn't been computed yet
        // use value iteration to compute the utility of each state.
    	for (HashMap.Entry<State, Double> state : utility.entrySet()) {
    		//if the state's utility hasn't been computed yet:
    		if (state.getValue() == 0.0) {
    			
    		}
    	}
        
        // use the utility to determine the optimal policy.
        // endif
        //
        // return the action for the current state from the policy.
        return "N";
    }
}
