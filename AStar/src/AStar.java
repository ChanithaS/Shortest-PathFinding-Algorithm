/***********************************************************************************************************************
 *                                                                                                                     *
 * Name     : R.K Chanitha Sankalpana                                                                                     *
 * IIT ID   : 20200618                                                                                                *
 * UoW ID   : w1833590                                                                                                 *
 *                                                                                                                     *
 **********************************************************************************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AStar {
    private static String[][] map;
    private static int size = -1;
    //    vertical / horizontal moves
    public static final int V_H_COST = 14;
    //    Cell of our grid
    private static Cell[][] grid;
    //    define a priority queue for open cells.
    //    Open Cells : the set of nodes to be evaluated
    //    put cells with the lowest cost in first
    private static PriorityQueue<Cell> openCells;
    //    Closed Cells : the set of nodes already evaluated
    private static boolean[][] closedCells;
    //    Start Cell
    private static int startI;
    private static int startJ;
    //    End Cell
    private static int finishI;
    private static int finishJ;

    public static void initData(int width, int height, int si, int sj, int ei, int ej, int[][] blocks) {
        grid = new Cell[width][height];
        closedCells = new boolean[width][height];
        openCells = new PriorityQueue<>(Comparator.comparingInt((Cell c) -> c.finalCost));

        startCell(si, sj);
        finishCell(ei, ej);

        //   init heuristic
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Cell(i, j);
                grid[i][j].heuristicCost = Math.abs(i - finishI) + Math.abs(j - finishJ);
                grid[i][j].solution = false;
            }
        }

        grid[startI][startJ].finalCost = 0;

        //   put the blocks on the grid
        for (int[] block : blocks) {
            addBlockOnCell(block[0], block[1]);
        }

        System.out.println(Arrays.deepToString(grid));

        process();
    }

    public static void addBlockOnCell(int i, int j) {
        grid[i][j] = null;
    }

    public static void startCell(int i, int j) {
        startI = i;
        startJ = j;
    }

    public static void finishCell(int i, int j) {
        finishI = i;
        finishJ = j;
    }

    public static void updateCostIfNeeded(Cell current, Cell t, int cost, String direction) {
        if (t == null || closedCells[t.i][t.j])
            return;

        int tFinalCost = t.heuristicCost + cost;
        boolean isOpen = openCells.contains(t);

        if (!isOpen || tFinalCost < t.finalCost) {
            t.finalCost = tFinalCost;
            t.parent = current;
            t.direction = direction;

            if (!isOpen) {
                openCells.add(t);
            }
        }
    }

    public static void process() {
        openCells.add(grid[startI][startJ]);
        Cell current;

        while (true) {
            //gets first cell from openCells
            current = openCells.poll();

            //check if the opencells is empty.. if empty then break
            if (current == null) {
                break;
            }

            //add the current cell to the closed cells
            closedCells[current.i][current.j] = true;

            //check if the current cell is the finish cell
            if (current.equals(grid[finishI][finishJ])) {
                return;
            }

            //create new object t
            Cell t;

            //going to the left and not end of the map
            if (current.i - 1 >= 0) {
                //setting t to the left cell
                t = grid[current.i - 1][current.j];
                //updating the heuristic cost of the cell
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST, "left");
            }

            //going to the right and not end of the map and update the heuristic cost of every cell
            if (current.j - 1 >= 0) {
                t = grid[current.i][current.j - 1];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST, "up");
            }

            if (current.j + 1 < grid[0].length) {
                t = grid[current.i][current.j + 1];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST, "bottom");
            }

            if (current.i + 1 < grid.length) {
                t = grid[current.i + 1][current.j];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST, "right");
            }
        }
    }

    public static void displaySolution() {
        ArrayList<String> path = new ArrayList<>();

        if (closedCells[finishI][finishJ]) {
            //    track back the path
            Cell current = grid[finishI][finishJ];
            grid[current.i][current.j].solution = true;

            while (current.parent != null) {
                String step = "Move " + current.direction + " to " + "(" + (current.parent.i+1) + ", " + (current.parent.j+1) + ")";
                path.add(step);

                grid[current.parent.i][current.parent.j].solution = true;
                current = current.parent;
            }

            System.out.println("\nSolution Visualize :");
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (i == startI && j == startJ) {
                        System.out.print("S  "); //source cell
                    } else if (i == finishI && j == finishJ) {
                        System.out.print("F  "); //destination cell
                    } else if (grid[i][j] != null) {
                        System.out.printf("%-2s ", grid[i][j].solution ? "@" : ".");
                    } else {
                        System.out.print("0  "); //block cell
                    }
                }
                System.out.println();
            }
            System.out.println();

            int count = 1;

            Collections.reverse(path);

            String output = "";
            for (String step : path) {
                System.out.println(count + ". " + step);
                output += count + ". " + step + "\n";
                count += 1;
            }
            System.out.println(count + ". Done!");
            output += count + ". Done!\n\n";

        } else {
            System.out.println("No possible path");
        }
    }

    public static void parser(String filename) {
        try {
            readFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFile(String filename) throws IOException {
        int blockCount = 0;
        InputStream stream = ClassLoader.getSystemResourceAsStream(filename);
        assert stream != null;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

        String line;
        int row = 0;

        while ((line = buffer.readLine()) != null) {
            String[] rowValues = new String[line.length()];
            for (int i = 0; i < line.length(); i++) {
                rowValues[i] = String.valueOf(line.charAt(i));
                if (line.charAt(i) == '0') {
                    blockCount++;
                }
            }

            // Lazy instantiation.
            if (map == null) {
                size = rowValues.length;
                map = new String[size][size];
            }

            if (size >= 0) System.arraycopy(rowValues, 0, map[row], 0, size);
            row++;
        }

        int width = map[0].length;
        int height = map[1].length;
        int startI = 0;
        int startJ = 0;
        int endI = 0;
        int endJ = 0;
        int[][] blocks = new int[blockCount][2];

        int r = 0;

        for (int i = 0; i < map[0].length; i++) {
            for (int j = 0; j < map[1].length; j++) {
                switch (map[i][j].charAt(0)) {
                    case 'S' -> {
                        startI = i;
                        startJ = j;
                    }
                    case 'F' -> {
                        endI = i;
                        endJ = j;
                    }
                    case '0' -> {
                        blocks[r][0] = i;
                        blocks[r][1] = j;
                        r++;
                    }
                }
            }
        }
//        System.out.println(Arrays.deepToString(blocks));
//        System.out.println(startI+ " " + startJ);

        initData(width,height,startI,startJ,endI,endJ,blocks);
    }

    /**

       0 1 2 3 4 5 6 7 8 9
     0 . . . . . 0 . . . S
     1 . . . . 0 . . . . .
     2 0 . . . . . 0 . . 0
     3 . . . 0 . . . . 0 .
     4 . F . . . . . . 0 .
     5 . 0 . . . . . . . .
     6 . . . . . . . 0 . .
     7 . 0 . 0 . . 0 . . 0
     8 0 . . . . . . . . .
     9 . 0 0 . . . . . 0 .

     */

    public static void main(String[] args) {
        parser("maze10_5.txt");
        displaySolution();
    }
}
