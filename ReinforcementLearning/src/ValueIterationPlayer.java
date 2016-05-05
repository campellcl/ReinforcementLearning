import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * An agent that uses value iteration to play the game.
 * 
 * @author Mitch Parry
 * @version 2014-03-28
 * 
 */
public class ValueIterationPlayer extends Player
{
    private boolean calculatedStateUtilities = false;
    private boolean stateInformationInitialized = false;

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
     * 
     * @param backup
     *            -A previous utility backup to compare to the current state
     *            utilities.
     * @return policyDelta -The maximum change in the utility of any state.
     */
    private Double getUtilityDelta(HashMap<State, Double> backup)
    {
        double policyDelta = 0.0;

        for (HashMap.Entry<State, Double> backupState : backup.entrySet())
        {
            // System.out.format("CSV: %d", utility.get(backupState.getKey()));
            double currentStateValue =
                Math.abs(utility.get(backupState.getKey()));
            double prevStateValue = Math.abs(backupState.getValue());
            double currStateDelta =
                Math.abs(currentStateValue - prevStateValue);
            if (currStateDelta > policyDelta)
            {
                policyDelta = currStateDelta;
            }
        }
        return policyDelta;
    }

    /**
     * Returns a Set of states that are adjacent to the provided state.
     * 
     * @param startState
     *            -The state from which to determine adjacent neighbors.
     * @param mdp
     *            -The Markov Decision Process.
     * @return neighbors -A HashMap<State,Double> of state's and their expected
     *         discount utility- as calculated from the startState.
     */
    private Set<State> getNeighboringStates(State startState,
        MarkovDecisionProcess mdp)
    {
        Set<State> neighbors = new HashSet<State>();

        for (State state : mdp.getStates())
        {
            for (String dir : mdp.getActions())
            {
                double transProb = mdp.transProb(startState, dir, state);
                if (transProb > 0.0)
                {
                    if (!neighbors.contains(state))
                    {
                        neighbors.add(state);
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Returns the expected reward/utility of a given state, as described in-
     * AIMA: 17.2.1: The Bellman Equation.
     * 
     * @param state
     *            -The state to calculate the utility for.
     * @param mdp
     *            -The Markov Decision Process.
     * @return stateUtility -The utility/expected reward of a given state.
     */
    private Double getStateUtility(State state, MarkovDecisionProcess mdp)
    {
        // Every state has four directions associated with it.
        // Each direction is associated with an expected discounted utility.
        Double stateUtility;
        String optimalPolicy = null;
        Double maxeDU = Double.NEGATIVE_INFINITY;
        HashMap<String, Double> expectedDiscountUtility =
            new HashMap<String, Double>();

        for (String dir : mdp.getActions())
        {
            double eDU = 0.0;
            for (State neighbor : getNeighboringStates(state, mdp))
            {
                eDU += (utility.get(neighbor)
                    * mdp.transProb(state, dir, neighbor));
            }
            expectedDiscountUtility.put(dir, eDU);
            if (eDU > maxeDU)
            {
                maxeDU = eDU;
                optimalPolicy = dir;
            }
        }
        policy.put(state, optimalPolicy);
        // return the state's utility as defined in AIMA: 17.2.1
        stateUtility = (Collections.max(expectedDiscountUtility.values())
            * mdp.getGamma()) + state.reward();
        return stateUtility;
    }

    /**
     * Calculates the utility of every state and updates 'Utility'.
     * 
     * @param mdp
     *            -The Markov Decision Process
     */
    private void calculateGlobalUtility(MarkovDecisionProcess mdp)
    {
        for (State state : mdp.getStates())
        {
            //GridWorld.display(mdp.getStates(), mdp.getCurrent(), utility);
            utility.replace(state, getStateUtility(state, mdp));
            //GridWorld.display(mdp.getStates(), mdp.getCurrent(), utility);
        }
    }

    /**
     * Initializes the utility and policy storage structures for the-
     * ValueIterationPlayer. U(s) is initialized to be 0.0.
     * 
     * @param mdp
     *            -The Markov Decision Process.
     */
    private void initializeStateInformation(MarkovDecisionProcess mdp)
    {
        utility = new HashMap<State, Double>();
        policy = new HashMap<State, String>();
        // Add every state to utility and policy data structures.
        // Initialize the utility of state to be 0.0, and the policy to null:
        for (State state : mdp.getStates())
        {
            // Given a state s, U(s) = 0.0.
            utility.put(state, 0.0);
            policy.put(state, null);
        }
    }

    /**
     * Plays the game using value iteration to pre-compute the policy and then
     * applying the policy in future moves. The Abstract Player class has
     * 
     * @param mdp
     *            the MDP.
     * @return the desired action.
     */
    public String play(MarkovDecisionProcess mdp)
    {
        final double EPS = 1e-3;
        double utilityDelta = 0.0;
        double terminationCoefficient = EPS * ((1 - mdp.getGamma())
            / mdp.getGamma());
        HashMap<State, Double> backup;
        if (!stateInformationInitialized)
        {
            initializeStateInformation(mdp);
            stateInformationInitialized = true;
        }
        if (!calculatedStateUtilities)
        {
            do
            {
                backup = new HashMap<State, Double>(utility);
                calculateGlobalUtility(mdp);
                utilityDelta = getUtilityDelta(backup);
            } while (utilityDelta > terminationCoefficient);
            calculatedStateUtilities = true;
        }
        // return the action for the current state from the optimal policy.
        return policy.get(mdp.getCurrent());
    }
}
