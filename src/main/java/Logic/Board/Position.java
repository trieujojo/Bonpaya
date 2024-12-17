package Logic.Board;

import Logic.Parameters;
 // TODO revamp so it has pointers to neighbors
public record Position(int row, int col) implements Comparable<Position> {
    public Position up(){
        if(row+1< Parameters.boardSize()) return new Position(row+1,col);
        return null;
    }
    public Position down(){
        if(row-1>=0) return new Position(row-1,col);
        return null;
    }
    public Position right(){
        if(col+1<Parameters.boardSize()) return new Position(row,col+1);
        return null;
    }
    public Position left(){
        if(col-1>=0) return new Position(row,col-1);
        return null;
    }
    public Position upLeft(){
        if(row+1< Parameters.boardSize()&&col-1>=0) return new Position(row+1,col-1);
        return null;
    }
    public Position upRight(){
        if(row+1< Parameters.boardSize()&&col+1<Parameters.boardSize())
            return new Position(row+1,col+1);
        return null;
    }
    public Position downRight(){
        if(row-1>=0&&col+1<Parameters.boardSize()) return new Position(row-1,col+1);
        return null;
    }
    public Position downLeft(){
        if(row-1>=0&&col-1>=0) return new Position(row-1,col-1);
        return null;
    }
    public Position copy(){
        return new Position(row,col);
    }
    public boolean equals(Object other){
        if(this == other) return true;
        if(other==null) return false;
        if(getClass() != other.getClass()) return false;
        Position p = (Position) other;
        return (col==p.col() && row==p.row());
    }

    @Override
    public int hashCode(){
        return Parameters.boardSize()*row + col;
    }

    @Override
    public int compareTo(Position another) {
        return compare(this.value(), another.value());
    }

    private int value(){
        return row*Parameters.boardSize()+col;
    }

    public static int compare (int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    @Override
     public String toString(){
        return"["+row+","+col+"]";
    }
}
