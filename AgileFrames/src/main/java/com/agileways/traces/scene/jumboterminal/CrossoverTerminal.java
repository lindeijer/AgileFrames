package com.agileways.traces.scene.jumboterminal;


/**
 * CrossoverTerminal contains all parameters and indices for building of the
 * Jumbo Container Terminal.
 * Change main parameters numberOfStacks, stacksPerLane and parksPerLane for
 * a different terminal-layout.
 * Care should be taken with changing other indices as many are dependent.
 * All measurements are in meters.
 *
 * @author Wierenga,Lindeijer,Evers
 * @version 0.0.1
 */

public class CrossoverTerminal {

  /** Width of all the lanes on the terminal, except for center-, stack and parklanes */
  public final float WIDTH_DRIVING_LANE = 5.5f;
  /** Width of all the centerlanes on the terminal*/
  public final float WIDTH_CENTER_LANE = 6.5f;
  /** Length of all the stacklanes on the terminal*/
  public final float LENGTH_STACKLANE = 17.5f;
  /** Width of all the stacklanes on the terminal*/
  public final float WIDTH_STACKLANE = 6.5f;
  /** Free space on the entrance of all the stacklanes on the terminal, used for driving into the stack*/
  public final float FREE_STACK_ENTRANCE = 8.5f;
  /** Free space on the exit of all the stacklanes on the terminal, used for driving out of the stack*/
  public final float FREE_STACK_EXIT = 8.5f;
  /** Free space on both sides of each stack. Two times this distance will be between stacks */
  public final float FREE_SPACE_BETWEEN_STACKS = 6.5f;
  /** Free space on the entrance of all the parklanes on the terminal, used for driving into the park*/
  public final float FREE_PARK_ENTRANCE = 7.5f;
  /** Length of all the parklanes on the terminal*/
  public final float LENGTH_PARKLANE = 17.5f;
  /** Width of all the parklanes on the terminal*/
  public final float WIDTH_PARKLANE = 6.5f;
  /** Free space on the exit of all the parklanes on the terminal, used for driving out of the park*/
  public final float FREE_PARK_EXIT = 10.6f;
  /** Free space between the southern quaylane and the quay. This space is not used by the vehicles.*/
  public final float FREE_QUAY = 22.8f;
  /** Free space between the western sidelane and the end of the terminal. This space is not used by the vehicles.*/
  public final float FREE_TERMINAL_LEFT = 10.5f;
  /** Free space between the eastern sidelane and the end of the terminal. This space is not used by the vehicles.*/
  public final float FREE_TERMINAL_RIGHT = 10.5f;
  /** Free space between the inner sidelanes and the outer stacks.*/
  public final float FREE_SPACE_AT_SIDES = 27f;
  /** Turn radius of the vehicles in corners of the terminal */
  public final float TURN_RADIUS = 11f;
  /** Distance between the cenetr and the turning point of an AGV */
  public final float AGV_TURNING_POINT = 0f;
  /** Length of track that in minimally needed for an AGV to drive on without danger of collisions*/
  public final float AGV_LENGTH = 20.0f;
  /** Free space on the exit of all the turntables on the terminal, used for driving out of the turntable*/
  public final float FREE_TTABLE_EXIT = 5.3f;
  /** Heigth of all the turntables on the terminal*/
  public final float TTABLE_HEIGHT = 14;
  /** Width of all the turntables on the terminal*/
  public final float TTABLE_WIDTH = 28;
  /** Distance from the top of the terminal to the top of the stack */
  public final float DIST_TOP_STACK = FREE_STACK_ENTRANCE + 2 * WIDTH_DRIVING_LANE;
  /** Distance from the bottom of the stack to the center of the terminal */
  public final float DIST_STACK_CENTER = FREE_STACK_EXIT + 2 * WIDTH_CENTER_LANE;
  /** Distance from the center of the terminal to the top of the park */
  public final float DIST_CENTER_PARK = FREE_PARK_ENTRANCE + 2 * WIDTH_CENTER_LANE;
  /** Distance from the bottom of the park to the bottom of the terminal */
  public final float DIST_PARK_QUAY = FREE_QUAY + 2 * WIDTH_DRIVING_LANE + FREE_TTABLE_EXIT + TTABLE_HEIGHT + FREE_PARK_EXIT;
  /** Distance from the bottom to the top of the terminal */
  public final float DIST_TOP_QUAY = DIST_TOP_STACK + DIST_STACK_CENTER + DIST_CENTER_PARK + DIST_PARK_QUAY + LENGTH_STACKLANE + LENGTH_PARKLANE;

  /** Absolute y-coordinate of all TurnTables */
  public final float TTABLE_Y = getParkLaneY() - FREE_PARK_EXIT - TTABLE_HEIGHT/2;
  /** x-coordinate of the west of the TurnTables relative to the west of the first park*/
  public float[] TTABLE_X = new float[] { 10 , 40 , 84 , 170 , 235 , 300 };

  /** Returns the x-coordinate of the stacklane */
  public float getStacklaneX(int stackNr, int stackLane){
    return (float)(FREE_TERMINAL_LEFT + WIDTH_DRIVING_LANE + FREE_SPACE_AT_SIDES +stackNr * STACK_WIDTH_INCL +
                   FREE_SPACE_BETWEEN_STACKS + stackLane * WIDTH_STACKLANE);
  }
  /** Returns the y-coordinate of the stacklane */
  public float getStackLaneY(){
    return DIST_TOP_QUAY - DIST_TOP_STACK - LENGTH_STACKLANE;
  }
  /** Returns the x-coordinate of the parklane */
  public float getParkLaneX(int parkNr, int parkLane){
    return FREE_TERMINAL_LEFT + WIDTH_DRIVING_LANE + FREE_SPACE_AT_SIDES +(parkNr+1) * STACK_WIDTH_INCL - PARK_WIDTH_EXCL/2 + parkLane * WIDTH_PARKLANE;
  }
  /** Returns the y-coordinate of the parklane */
  public float getParkLaneY(){
    return getStackLaneY() - DIST_STACK_CENTER - DIST_CENTER_PARK - LENGTH_PARKLANE;
  }



  //////////////////////////////////////////////////////////////////////

  /** Number Of Stacks on terminal. Default is 5. The number of parks is always numberOfStacks-1 */
  public int numberOfStacks = 5;
  /** Number of lanes per stack. Default is 4 */
  public int lanesPerStack  = 4;
  /** Number of lanes per park. Default is 5 */
  public int lanesPerPark   = 5;
  /** Number of TurnTables on the terminal. Depends on numberOfStacks. */
  public int NUMBER_OF_TURNTABLES = 3;
  /** Width of each stack including the free spaces at each side */
  public float STACK_WIDTH_INCL;
  /** Width of each stack without the free spaces at each side */
  public float STACK_WIDTH_EXCL;
  /** Width of each park including the free spaces at each side */
  public float PARK_WIDTH_INCL;
  /** Width of each park without the free spaces at each side */
  public float PARK_WIDTH_EXCL;
  /** Total width of the Terminal */
  public float TOTAL_WIDTH;
  /** Free space between each Park */
  public float FREE_SPACE_BETWEENPARKS;
  /** Number of AGVs that fit on a sidelane when queueing */
  public final int NUMBER_OF_AGVS_AT_SIDES;
  /** Number of AGVs that fit on a stacklane when queueing */
  public final int NUMBER_OF_AGVS_AT_STACKLANE;
  /** Rest of stackLane when all AGVs are queueing plus length of one AGV */
  public final float LAST_WIDTH_STACKLANE;


  public CrossoverTerminal(int numberOfStacks) {
    this.numberOfStacks = numberOfStacks;
    this.lanesPerStack = 4;
    this.lanesPerPark = 5;
    this.NUMBER_OF_TURNTABLES = (int) Math.ceil(((float)numberOfStacks)/2);
    STACK_WIDTH_INCL = (lanesPerStack-1) * WIDTH_STACKLANE + 2 * FREE_SPACE_BETWEEN_STACKS;
    STACK_WIDTH_EXCL = (lanesPerStack-1) * WIDTH_STACKLANE;
    PARK_WIDTH_INCL = STACK_WIDTH_INCL;
    PARK_WIDTH_EXCL = (lanesPerPark-1) * WIDTH_PARKLANE;
    TOTAL_WIDTH = numberOfStacks*STACK_WIDTH_INCL + FREE_TERMINAL_LEFT + FREE_TERMINAL_RIGHT + 2 * FREE_SPACE_AT_SIDES + 2*WIDTH_DRIVING_LANE;
    FREE_SPACE_BETWEENPARKS = (STACK_WIDTH_INCL - STACK_WIDTH_EXCL);
    NUMBER_OF_AGVS_AT_SIDES = (int)((DIST_TOP_QUAY - 4*WIDTH_DRIVING_LANE  - 2*TURN_RADIUS - FREE_QUAY)/AGV_LENGTH);
    NUMBER_OF_AGVS_AT_STACKLANE = (int)((STACK_WIDTH_INCL*numberOfStacks + FREE_SPACE_AT_SIDES*2-2*TURN_RADIUS)/AGV_LENGTH);
    LAST_WIDTH_STACKLANE = (int)( STACK_WIDTH_INCL*numberOfStacks + FREE_SPACE_AT_SIDES*2 - 2*TURN_RADIUS - (NUMBER_OF_AGVS_AT_STACKLANE-1)*AGV_LENGTH -1);
  }


}