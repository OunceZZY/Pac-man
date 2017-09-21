import java.util.*;

import controllers.osc.OSCPacMan;

public class B00716809PacMan extends OSCPacMan {
  /***********************************************************************************************************************/
  public B00716809PacMan(String[] args) {
    super(args);
  }
  
  @Override
  public void startGame() { //runs once at the start of each game
  }
  
  @Override
  public void endGame() { //runs once at the end of each game
  }
  
  /**
   * Select a direction for Ms. Pac-Man.
   * Runs once for each timestep of the game.
   * Must return one int: 0 (UP),  1 (RIGHT),  2 (DOWN), 3 (LEFT)
   * <p>
   * Note: to check a binary state variable, use less-than or greater-than:
   * if (directedState.get(11) > 0); //test if closest ghost is approaching
   *
   * @return The direction to move in.
   */
  @Override
  public int selectMove() {
    int move = forRegularPill();
    move = (detectThreat(move, 1.0f) == 1) ? changeDirection(move) : move;
    ArrayList<Float> s = getDirectedState(move);
    return move;
  }
  
  int forRegularPill() {
    int move = 0;
    float nearestPillDist = 1.0f;
    
    int directionClosestEdible = 0;
    float distanceFirstEdible = 1.0f;
    
    for (int d = 0; d < NUM_DIR; d++) {
      if (!isTowardWall(d)) {
        
        ArrayList<Float> directedState = getDirectedState(d);
        
        float chase = distanceOfFirstEdibleGhost(directedState);
        if(chase < distanceFirstEdible){
          directionClosestEdible = d;
          distanceFirstEdible = chase;
        }
        
        float dist = directedState.get(7);
        if (dist <= nearestPillDist) {
          move = d;
          nearestPillDist = dist;
        }
      }
    }
    if(distanceFirstEdible <= 0.2)
      return directionClosestEdible;
    
    return move;
  }
  
  int detectThreat(int move, float distance) {
    if (closestApproachingNonEdibleGhostDistance(move) < distance)
      return 1;
    return 0;
  }
  
  float closestApproachingNonEdibleGhostDistance(int move) {
    ArrayList<Float> states = getDirectedState(move);
    if (states.get(11) == 1 && states.get(13) == 0 && states.get(9) - states.get(10) >= -0.02 || (states.get(28) == 0 && states.get(6) != 1 )) {
      return states.get(10);
    }
    return 1.0f;
  }
  
  int changeDirection(int move) {
    int change = move;
    float toPowerPill = 1.0f;
    for (int d = 0; d < NUM_DIR; d++) {
      if (/*d == move ||*/ isTowardWall(d) /*|| detectThreat(d,) == 1*/)
        continue;//{System.out.println("Not plan" + d);continue;}
      ArrayList<Float> states = getDirectedState(d);
      float dToPowerPill = states.get(8);
      if (dToPowerPill < toPowerPill && detectThreat(d, 1.0f) != 1) {
        toPowerPill = dToPowerPill;
        change = d;
      }
    }
    if (move == change) {
      float far = 0.0f;
      for (int d = 0; d < NUM_DIR; d++) {
        if (!isTowardWall(d)) {
          ArrayList<Float> states = getDirectedState(d);
          float clsdies = states.get(10);
          if (clsdies > far) {
            far = clsdies;
            change = d;
          }
        }
      }
      return change;
    } // that means no way to go
    
    return change;
  }

  float distanceOfFirstEdibleGhost(ArrayList<Float> states) {
    if (states.get(13) == 1 ) {
      return states.get(10);
    } else if (states.get(17) == 1 ) {
      return states.get(14);
    } else if (states.get(21) == 1 ) {
      return states.get(18);
    } else if (states.get(25) == 1 ) {
      return states.get(22);
    } else {
      return 1.0f;
    }
  }
  
  /***********************************************************************************************************************/
  public static void main(String[] args) {
    B00716809PacMan pacman = new B00716809PacMan(args);
    int total = 0;
    for (int g = 0; g < pacman.numGamesToPlay(); g++) {
      pacman.initGame();
      while (!pacman.gameOver()) {
        try {
          Thread.sleep(1);
        } catch (Exception e) {
        }
      }
      total += pacman.gameScore;
      System.out.println("Game: " + (g + 1) + " Score: " + pacman.gameScore());
    }
    System.out.println("Average: "+total/pacman.numGamesToPlay());
    pacman.exit();
    
    System.exit(0);
  }
}
