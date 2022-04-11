/***********************************************************************************************************************
 * Name     : R.K Chanitha Sankalpana                                                                                  *
 * IIT ID   : 20200618                                                                                                 *
 * UoW ID   : w1833590                                                                                                 *
 **********************************************************************************************************************/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class PathFinder extends JFrame {

    // character 2D array for storing the maze/puzzle grid
    private char[][] grid;
    // boolean 2D array for storing the visited nodes
    private boolean[][] visited;
    // an object of the class Nodes
    private Nodes node;
    // a queue to add all the nodes and remove a node them when needed
    private final LinkedList<Nodes> store = new LinkedList<>();
    // boolean for checking if the path is found or not
    private boolean pathFound = false;
    // arraylist inside an arraylist for storing the points of the path ... used only for visualization by swing
    public static ArrayList<ArrayList<Integer>> pathPoints  = new ArrayList<ArrayList<Integer>>();
    //starting node row and column numbers
    int startX = 0;
    int startY = 0;
    // time elapsed for finding the path
    double time;
    // stopwatch for calculating the time elapsed
    Stopwatch stopWatch;
    // scanner for getting the input
    Scanner scanner = new Scanner(System.in);

    //object of the class
    public static PathFinder path = new PathFinder();

    public static void main(String[] args) {
        path.start();
    }

    /**
     * start the program by asking user for the file name
     */
    public void start(){
        System.out.println("Enter the file name or to exit press Q: ");
        //get the input
        String name = scanner.nextLine();
        //output according to users input
        if (name.equals("Q") || name.equals("q")){
            System.out.println("Thank you for using the system");
        }else{
            //creating a new stop watch
            stopWatch = new Stopwatch();
            //calling the file read method with the file name
            fileRead("test/" + name + ".txt");
        }
    }

    /**
     * read the file and store the data in the character array
     * @param name the file name
     */
    public void fileRead(String name) {
        try {
            // create a file reader to read the file
            FileReader fileRead = new FileReader(name);
            // read character from the file
            BufferedReader buffer = new BufferedReader(fileRead);
            //set this buffer’s mark at its position
            buffer.mark(1);

            // store the lines of the file
            String line;

            //getting the length of the text grid
            int rowLength = buffer.readLine().length();
            buffer.reset();

            //initializing the grid and the boolean visited array with the lenght
            grid = new char[rowLength][rowLength];
            visited = new boolean[grid.length][grid.length];

            //while loop to read line by line the characters
            int row = 0;
            while ((line = buffer.readLine()) != null) {
                //getting characters of the line to a character array
                char[] columns = line.toCharArray();
                //inserting the line into the respective row
                grid[row] = columns;
                row++;
            }
            //calling the find path method
            findThePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * find the path using the BFS algorithm
     */
    public void findThePath(){
        //finding the starting position
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Finding source node
                if (grid[i][j] == 'S') {
                    //creating a new node with start row and column
                    node = new Nodes(i, j);
                    startX = i;
                    startY = j;
                }
            }
        }
        //adding the source node to the queue
        store.add(node);
        //making the starting node a visited node
        visited[node.rowNo][node.columnNo] = true;
        //new Nodes object
        Nodes itemVisited = null;

        //while the queue gets empty
        while (!store.isEmpty()) {
            //setting the first index of the queue to the itemVisited
            itemVisited = store.remove(0);

            //checking if the current node is the destination node
            if (grid[itemVisited.rowNo][itemVisited.columnNo] == 'F') {
                //if so stopping the countdown
                time = stopWatch.elapsedTime();
                //setting pathFound bool to true
                pathFound = true;
                break;
            }

            //calling the move function to check the neighbours in each direction
            //the x and y values are the row and column numbers of the movement direction
            move(itemVisited, 0, 1, "bottom");
            move(itemVisited, 0, -1, "up");
            move(itemVisited, -1, 0, "left");
            move(itemVisited, 1, 0, "right");
        }

        if (pathFound) {
            //if path found printing the path
            printPath(itemVisited);
        }else{
            System.out.println("No path found");
        }
    }

    /**
     * move function to check the neighbours in each direction and find the path
     * @param point : the current node
     * @param x : the row number of the movement direction
     * @param y : the column number of the movement direction
     * @param direction : direction of the movement
     */
    public void move(Nodes point, int x, int y, String direction) {
        //storing the current node row and column no
        int row = point.rowNo;
        int column = point.columnNo;

        while(true) {
            //incrementing
            row += y;
            column += x;

            if (!isValid(row, column)) {
                break;
            }

            if (grid[row][column] == 'F') {
                Nodes neighbourItem = new Nodes(row, column);
                neighbourItem.parent = point;
                neighbourItem.direction = direction;

                store.add(0, neighbourItem);
                visited[row][column] = true;
                break;
            }

            int nextRow  = row + y;
            int nextColumn = column + x;

            if ((nextRow < 0 || nextColumn < 0) || (nextRow >= grid.length || nextColumn >= grid.length) || (grid[nextRow][nextColumn] == '0')) {
                Nodes neighbourItem = new Nodes(row, column);
                neighbourItem.parent = point;
                neighbourItem.direction = direction;
                store.add(neighbourItem);
                visited[row][column] = true;
                break;
            }
        }
    }
    public boolean isValid(int row, int column) {
        if (row >= 0 && column >= 0
                && row < grid.length && column < grid[0].length
                && grid[row][column] != '0'
                && !visited[row][column]) {
            return true;
        }
        return false;
    }

    int rowNo = -1;
    int columnNo = -1;

    public void printPath(Nodes visitedNode){
        ArrayList<String> path = new ArrayList<>();

        while (visitedNode.parent != null) {
            //for setting the pathway by words getting the moved direction and the columns and rows of the nodes
            String step = "Move " + visitedNode.direction + " to " + "(" + (visitedNode.columnNo + 1) + ", " + (visitedNode.rowNo + 1) + ")";
            //then adding the steps to an arraylist
            path.add(step);

            //for visualizing the pathway----------------------------------------------------
            midPoints(visitedNode);

            //a temporary array to store the pathway points
            ArrayList<Integer> point = new ArrayList<>();
            point.add(visitedNode.rowNo);
            point.add(visitedNode.columnNo);
            pathPoints.add(point);

            rowNo = visitedNode.rowNo;
            columnNo = visitedNode.columnNo;

            visitedNode = visitedNode.parent;
        }

        path.add("Start at " + "(" + (visitedNode.columnNo + 1) + ", " + (visitedNode.rowNo + 1) + ")");
        midPoints(visitedNode);

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

        System.out.println("Time elapsed : " + time + "ms");
        System.out.println("To visualize the path enter (Y/y) or to end (Q/q) : ");
        String input = scanner.nextLine();

        if (input.equals("Y") || input.equals("y"))
        {
            visualize();
        }else{
            start();
        }

    }

    JFrame f = new JFrame();

    public void visualize(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.setSize(600, 600);
                f.setLayout(new BorderLayout());

                JPanel panelTop = new JPanel(new GridLayout(grid.length,grid.length,1,1));
                JPanel[] panel = new JPanel[grid.length*grid.length];

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        panel[i] = new JPanel();
                        // Finding source
                        if (grid[i][j] == '.') {
                            panel[i].setBackground(Color.decode("#ADD8E6"));
                        }
                        if (grid[i][j] == 'S') {
                            panel[i].setBackground(Color.ORANGE);
                        }
                        else if (grid[i][j] == 'F') {
                            panel[i].setBackground(Color.ORANGE);
                        }
                        else if (grid[i][j] == '0') {
                            panel[i].setBackground(Color.BLACK);
                        }
                        else{
                            for (ArrayList<Integer> p: pathPoints){
                                if (p.get(0) == i && p.get(1) == j) {
                                    panel[i].setBackground(Color.RED);
                                }
                            }
                        }
                        panelTop.add(panel[i]);
                    }
                }

                f.add(panelTop, BorderLayout.CENTER);
                //setting a layout structure and size for the gui

                //setting to be always on top when invoking the method
                f.setAlwaysOnTop(true);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                //for making the frame always stay in the middle
                f.setLocationRelativeTo(null);
                //setting the visibility of the frame to true
                f.setVisible(true);
            }
        });
    }

    public void midPoints(Nodes visitedNode){
        int row = rowNo;
        int column = columnNo;
        boolean valid = false;
        if(rowNo != -1 && columnNo != -1){
            while (!valid) {
                ArrayList<Integer> point1 = new ArrayList<>();
                if (visitedNode.columnNo == columnNo) {
                    if (visitedNode.rowNo > row) {
                        point1.add(row++);
                        point1.add(visitedNode.columnNo);
                        pathPoints.add(point1);
                    }else if(visitedNode.rowNo < row){
                        point1.add(row--);
                        point1.add(visitedNode.columnNo);
                        pathPoints.add(point1);
                    }
                    else {
                        valid = true;
                    }
                }else if (visitedNode.rowNo == rowNo) {
                    if (visitedNode.columnNo > column) {
                        point1.add(visitedNode.rowNo);
                        point1.add(column++);
                        pathPoints.add(point1);
                    }else if(visitedNode.columnNo < column){
                        point1.add(visitedNode.rowNo);
                        point1.add(column--);
                        pathPoints.add(point1);
                    }
                    else {
                        valid = true;
                    }
                }else {
                    valid = true;
                }
            }
        }
    }
}
