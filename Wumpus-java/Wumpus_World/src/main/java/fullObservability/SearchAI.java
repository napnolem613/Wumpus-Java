package fullObservability;

import wumpus.Agent;
import wumpus.World;

import java.util.*;


public class SearchAI extends Agent {

    private class Node{

        State state;
        Node parent;
        int cost;
        Action action;

        public Node(State s, Node parent, int cost, Action a){
            this.state=s;
            this.parent=parent;
            this.cost=cost;
            this.action=a;
        }

        public State getState(){
            return this.state;
        }
        public void setState(State s){
            this.state = s;
        }

        public int getCost() { return cost; }
        public void setCost(int cost){this.cost=cost; }

        public void setParent(Node n){
            this.parent = n;
        }
        public Node getParent(){
            return this.parent;
        }

        public void setAction(Action a) { this.action = a; }
        public Action getAction() { return this.action; }

    }

    private class State implements Comparable<State>{
        int x;
        int y;
        int colDimension;
        int rowDimension;
        int dir;

        boolean hasGold;  //
        boolean hasArrow;
        boolean hasWumpus;  // it means there exists a wumpus in the entire tile
        boolean hasPit;
        boolean goldLooted;
        boolean bump;

        public State(int x, int y, int column, int row, int dir,
                     boolean hasArrow, boolean wumpus, boolean goldLooted){
            this.x=x;
            this.y=y;
            this.colDimension=column;
            this.rowDimension=row;
            this.dir=dir;
            //this.hasGold=hasGold;
            this.hasArrow=hasArrow;
            this.hasWumpus=wumpus;
            //this.hasPit=pit;
            this.goldLooted=goldLooted;

        }

        @Override
        public int compareTo(State s){
            if (this.x == s.getX() & this.y == s.getY() & this.dir == s.getDir()
                    & this.hasArrow == s.getHasArrow() & this.hasWumpus == s.getHasWumpus()
                    & this.goldLooted == s.getGoldLooted() ) {
                return 0;
            }
            else
                return -1;

        }
        public int getX(){
            return this.x;
        }
        public int getY(){
            return this.y;
        }

        public void setX(int x){
            this.x=x;
        }
        public void setY(int y){
            this.y=y;
        }

        public int getColDimension() {
            return colDimension;
        }
        public int getRowDimension() {
            return this.rowDimension;
        }

        public void setDir(int dir){
            this.dir=dir;
        }
        public int getDir(){
            return this.dir;
        }

        public boolean getHasArrow(){
            return this.hasArrow;
        }
        public void setArrow(boolean hasArrow){
            this.hasArrow=hasArrow;
        }

        public boolean getHasWumpus(){
            return this.hasWumpus;
        }
        public void setHasWumpus(boolean hasWumpus){
            this.hasWumpus=hasWumpus;
        }

        public boolean getGoldLooted(){
            return goldLooted;
        }
        public void setGoldLooted(boolean goldLooted){
            this.goldLooted=goldLooted;
        }

    }



    private Node result(Node n, Action a, World.Tile[][] board){
        State s = n.getState();
        int agentX=n.getState().getX();
        int agentY= n.getState().getY();
        int dir= s.getDir();
        int cost=n.getCost();
        //boolean hasGold=false;
        boolean hasArrow=s.getHasArrow();
        boolean hasWumpus=s.getHasWumpus();
        //boolean hasPit=false;
        boolean goldLooted=s.getGoldLooted();

        cost-=1;

        if(a==Action.TURN_LEFT) {
            if (--dir < 0) {;
                dir=3;
            }
            a=Action.TURN_LEFT;
        }
        if(a==Action.TURN_RIGHT) {
            if (++dir > 3) {
                dir=0;
            }
            a=Action.TURN_RIGHT;
        }
        if(a==Action.FORWARD) {
            if ( dir == 0 && agentX+1 < s.getColDimension() )
                agentX=agentX+1;
            else if (dir == 1 && agentY-1 >= 0 )
                agentY=agentY-1;
            else if ( dir == 2 && agentX-1 >= 0 )
                agentX=agentX-1;
            else if ( dir == 3 && agentY+1 < s.getRowDimension() )
                agentY=agentY+1;

            if ( board[agentX][agentY].getPit())
            {
                cost -= 1000;
            }
            if(board[agentX][agentY].getWumpus() && !hasWumpus)
            {
                cost -= 1000;
            }
            a=Action.FORWARD;
            State new_s = new State(agentX, agentY, s.getColDimension(), s.getRowDimension(), dir,
                    hasArrow, hasWumpus, goldLooted);
            return new Node(new_s, n, cost, a);
        }
        if(a==Action.SHOOT){
            if (s.getHasArrow() )
            {
                hasArrow=false;
                cost=cost-10;
                if ( dir == 0 )
                {
                    for ( int x = agentX; x < s.getColDimension(); ++x )
                        if ( board[x][agentY].getWumpus() && s.getHasWumpus())
                        {
                            hasWumpus=false;
                        }

                }
                else if ( dir == 1 )
                {
                    for ( int y = agentY; y >= 0; --y )
                        if ( board[agentX][y].getWumpus() && s.getHasWumpus())
                        {
                            hasWumpus=false;
                        }
                }
                else if ( dir == 2 )
                {
                    for ( int x = agentX; x >= 0; --x )
                        if ( board[x][agentY].getWumpus() && s.getHasWumpus() )
                        {
                            hasWumpus=false;
                        }
                }
                else if ( dir == 3 )
                {
                    for ( int y = agentY; y < s.getRowDimension(); ++y )
                        if ( board[agentX][y].getWumpus() && s.getHasWumpus())
                        {
                            hasWumpus=false;
                        }
                }
            }
            State new_s = new State(agentX,agentY,s.getColDimension(),s.getRowDimension(),dir,
                    hasArrow,hasWumpus, goldLooted);
            return new Node(new_s, n, cost, a);
        }
        if(a==Action.GRAB){
            if ( board[agentX][agentY].getGold() )
            {
                goldLooted=true;
            }
        }
        if(a==Action.CLIMB){
            if ( agentX == 0 && agentY == 0 )
            {
                if (s.getGoldLooted())
                    cost+=1000;
            }
        }
        State new_s = new State(agentX,agentY,s.getColDimension(),s.getRowDimension(),dir,
                hasArrow,hasWumpus, goldLooted);
        return new Node(new_s, n, cost, a);
    }

    private ArrayList<Node> expand(Node n, World.Tile[][] board){
        ArrayList<Node> nodes = new ArrayList<>();
        State s = n.getState();
        for (Agent.Action a : Agent.Action.values()) {
            Node new_n = result(n,a,board);
            nodes.add(new_n);
        }
        return nodes;

    }

    private boolean isGoal(Node n){

        State s = n.getState();
        if(s.getX()==0 && s.getY()==0 && s.goldLooted && n.getAction()==Action.CLIMB){
            return true;
        }
        else{
            return false;
        }
    }

    // Optional<Node>
    private Optional<Node> best_fist_search(PriorityQueue<Node> frontier,Node initial_node, HashMap<State, Node> reached, World.Tile[][] board){

        Node return_node=null;
        frontier.add(initial_node);
        while(!frontier.isEmpty()){
            Node n=frontier.poll();
            if(isGoal(n)){
                return Optional.ofNullable(n);
            }
            for(Node child_n : expand(n, board)){
                State s =child_n.getState();
                if(!reached.containsKey(s) || child_n.getCost()<reached.get(s).getCost()){
                    reached.put(s,child_n);
                    frontier.add(child_n);
                    child_n.setParent(n);
                }
            }
        }
        return Optional.ofNullable(return_node);

    }

    private ListIterator<Action> planIterator;


    public void SearchAI(World.Tile[][] board) {


        /* The world is board[coloumn][row] with initial position (bottom left) being board[0][0] */

        LinkedList<Action> plan;
        plan = new LinkedList<Action>();

        // Remove the code below //
        for (int i = 0; i<8; i++)
            plan.add(Agent.Action.FORWARD);
        plan.add(Action.TURN_LEFT);
        plan.add(Action.TURN_LEFT);
        for (int i = 10; i<18; i++)
            plan.add(Action.FORWARD);
        plan.add(Action.CLIMB);

        int r=board.length;
        int c=board[0].length;
        Node final_node=null;
        State initial_s = new State(0,0,c,r,0, true,
                true, false);
        Node initial_n = new Node(initial_s,null,0, null);
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        HashMap<State, Node> reached = new HashMap<State, Node>();

        Optional<Node> result = best_fist_search(frontier,initial_n,reached, board);

        // Stack for reversing the actions the final node went through
        Stack<Action> stack = new Stack<>();
        if (result.isPresent()) {
            final_node = result.get();
            Node cur_n=final_node;
            while(cur_n!=null){
                stack.add(cur_n.getAction());
                cur_n=cur_n.getParent();
            }

            while(!stack.isEmpty()){
                System.out.println(stack.peek());
                plan.add(stack.pop());

            }

        }
        else{
            System.out.println("Cannot access to the gold");
        }

        planIterator = plan.listIterator();

    }


    @Override
    public Agent.Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {
        return planIterator.next();
    }

}