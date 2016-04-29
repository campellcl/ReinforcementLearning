import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private HashMap<State, String> getNeighboringStates(State start_state, MarkovDecisionProcess mdp) {
    	HashMap<State, String> neighbors = new HashMap<State, String>();
    	State end_state;
    	
    	for (int i = 0; i < mdp.getStates().size(); i++) {
    		end_state = mdp.getStates().get(i);
    		for (String dir : mdp.getActions()) {
    			double prob = mdp.transProb(start_state, dir, end_state);
    			if (prob > 0.0) {
    				neighbors.put(end_state, dir);
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
    	State goal_state = null;
    	double goal_state_reward = 100.0;
    	for (State state : mdp.getStates()) {
    		if (state.reward() == goal_state_reward) {
    			goal_state = state;
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
    private HashMap.Entry<State, String> getOptimalNeighborState(double current_state_reward, HashMap<State, String> neighboring_states) {
    	double max_reward = Integer.MIN_VALUE;
    	HashMap.Entry<State, String> best_neighbor = null;
    	for (HashMap.Entry<State, String> adj_state : neighboring_states.entrySet()) {
    		if (adj_state.getKey().reward() > max_reward) {
    			max_reward = current_state_reward + adj_state.getKey().reward();
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
    
    /**
     * Computes the AVERAGE optimal policy, given repeated backup iterations.
     * @param mdp -The MarkovDecisionProcess
     * @return -TODO: void
     */
    private HashMap<State, String> getOptimalPolicy(MarkovDecisionProcess mdp) {
    	//TODO: Determine criteria for convergence. Repeatedly call getBackup() until convergence. 
    	return null;
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
        // final double EPS = 1e-3;
    	

        // if the utility hasn't been computed yet
        // use value iteration to compute the utility of each state.
        // use the utility to determine the optimal policy.
        // endif
        //
        // return the action for the current state from the policy.
        return "N";
    }
}
