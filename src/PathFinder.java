import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class PathFinder extends JFrame {

    private char[][] grid;
    private static String fileName;
    private boolean[][] visited;
    private Nodes node;
    private final ArrayList<Nodes> store = new ArrayList<>();
    private boolean pathFound = false;
    public static ArrayList<ArrayList<Integer>> pathPoints  = new ArrayList<ArrayList<Integer>>();
    int startX = 0;
    int startY = 0;

    public static PathFinder path = new PathFinder();

    long startTime;
    long endTime;

    public static void main(String[] args) {
        fileName = "maze25_1.txt";
        path.fileRead(fileName);
    }

    public void fileRead(String name) {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(name);
            assert stream != null;
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
            buffer.mark(1);

            String line;
            int row = 0;

            int rowLength = buffer.readLine().length();
            buffer.reset();

            grid = new char[rowLength][rowLength];
            visited = new boolean[grid.length][grid.length];

            while ((line = buffer.readLine()) != null) {
                char[] columns = line.toCharArray();
                grid[row] = columns;
                row++;
            }
            findThePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findThePath(){
        startTime = System.nanoTime();
        System.out.println(startTime);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Finding source
                if (grid[i][j] == 'S') {
                    node = new Nodes(i, j);
                    startX = i;
                    startY = j;
                }
            }
        }
        store.add(node);
        visited[node.getRowNumber()][node.getColumnNumber()] = true;
        Nodes itemVisited = null;

        while (!store.isEmpty()) {
            itemVisited = store.remove(0);
            int rowNumberVisited = itemVisited.getRowNumber();
            int columnNumberVisited = itemVisited.getColumnNumber();

            if (grid[rowNumberVisited][columnNumberVisited] == 'F') {
                endTime = System.nanoTime();
                System.out.println(endTime);
                pathFound = true;
                break;
            }

            move(itemVisited, 0, 1, "bottom");
            move(itemVisited, 0, -1, "up");
            move(itemVisited, -1, 0, "left");
            move(itemVisited, 1, 0, "right");
        }

        if (pathFound) {
            printPath(itemVisited);
        }else{
            System.out.println("No path found");
        }
    }

    public void move(Nodes point, int x, int y, String direction) {
        int row = point.getRowNumber();
        int column = point.getColumnNumber();

        while(true) {
            row += y;
            column += x;

            if (!isValid(row, column)) {
                break;
            }

            if (grid[row][column] == 'F') {
                Nodes neighbourItem = new Nodes(row, column);
                neighbourItem.setPrevious(point);
                neighbourItem.setMove(direction);

                store.add(0, neighbourItem);
                visited[row][column] = true;
                break;
            }

            int nextRow  = row + y;
            int nextColumn = column + x;

            if ((nextRow < 0 || nextColumn < 0) || (nextRow >= grid.length || nextColumn >= grid.length) || (grid[nextRow][nextColumn] == '0')) {
                Nodes neighbourItem = new Nodes(row, column);
                neighbourItem.setPrevious(point);
                neighbourItem.setMove(direction);
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

        while (visitedNode.getPrevious() != null) {
            //for setting the pathway by words getting the moved direction and the columns and rows of the nodes
            String step = "Move " + visitedNode.getMove() + " to " + "(" + (visitedNode.getColumnNumber() + 1) + ", " + (visitedNode.getRowNumber() + 1) + ")";
            //then adding the steps to an arraylist
            path.add(step);

            //for visualizing the pathway----------------------------------------------------
            midPoints(visitedNode);

            //a temporary array to store the pathway points
            ArrayList<Integer> point = new ArrayList<>();
            point.add(visitedNode.getRowNumber());
            point.add(visitedNode.getColumnNumber());
            pathPoints.add(point);

            rowNo = visitedNode.getRowNumber();
            columnNo = visitedNode.getColumnNumber();

            visitedNode = visitedNode.getPrevious();
        }

        path.add("Start at " + "(" + (visitedNode.getColumnNumber() + 1) + ", " + (visitedNode.getRowNumber() + 1) + ")");
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

        long time = endTime - startTime;
        System.out.println("Time elapsed : " + time + " nano seconds");
        System.out.println("To visualize the path enter (Y/y) or to end (Q/q) : ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();



        if (input.equals("Y") || input.equals("y"))
        {
            visualize();
        }else{
            System.out.println("Ending the program");
        }

    }

    JFrame f = new JFrame();

    public void visualize(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.setSize(600, 600);
                f.setLayout(new BorderLayout());

                JPanel panelTop = new JPanel(new GridLayout(grid.length,grid.length,0,0));
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
                if (visitedNode.getColumnNumber() == columnNo) {
                    if (visitedNode.getRowNumber() > row) {
                        point1.add(row++);
                        point1.add(visitedNode.getColumnNumber());
                        pathPoints.add(point1);
                    }else if(visitedNode.getRowNumber() < row){
                        point1.add(row--);
                        point1.add(visitedNode.getColumnNumber());
                        pathPoints.add(point1);
                    }
                    else {
                        valid = true;
                    }
                }else if (visitedNode.getRowNumber() == rowNo) {
                    if (visitedNode.getColumnNumber() > column) {
                        point1.add(visitedNode.getRowNumber());
                        point1.add(column++);
                        pathPoints.add(point1);
                    }else if(visitedNode.getColumnNumber() < column){
                        point1.add(visitedNode.getRowNumber());
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
